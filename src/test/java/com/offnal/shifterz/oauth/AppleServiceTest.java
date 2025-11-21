package com.offnal.shifterz.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.global.config.AppleProperties;
import com.offnal.shifterz.member.domain.Provider;
import com.offnal.shifterz.member.dto.MemberResponseDto;
import com.offnal.shifterz.member.repository.MemberRepository;
import com.offnal.shifterz.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AppleServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MemberService memberService;


    @InjectMocks
    private AppleService appleService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        AppleProperties props = new AppleProperties(
                "TEAMID",
                "CLIENTID",
                "KEYID",
                "classpath:key.p8",
                "https://redirect.uri",
                new AppleProperties.Url(
                        "https://appleid.apple.com/auth/token",
                        "https://appleid.apple.com/auth/keys",
                        "https://appleid.apple.com/auth/revoke"
                )
        );

        ReflectionTestUtils.setField(appleService, "appleProperties", props);
    }

    @Test
    void givenValidIdToken_whenGetUserInfo_thenReturnUserInfoAndRegisterMember() throws Exception {

        // === GIVEN ==================================================================
        String idToken = "eyJraWQiOiJhVmVIRmFXeEFaIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnNoaWZ0ZXJ6Lm9mZm5hbCIsImV4cCI6MTc2MzgyODk0OSwiaWF0IjoxNzYzNzQyNTQ5LCJzdWIiOiIwMDEwNDAuMjg2MDQyMjVlOWEyNDg5ZmI2OGMxMGM5NmEzMDQ2MTQuMTUwMCIsIm5vbmNlIjoiMTU1N2RjNTRlZTA4YmNhM2U4NTA0NzdkYTIwY2UzYjYwNGNiOTc2MThhZjdjMDM0MTQ3Zjk1ZmUwMzlhNTZjMiIsImNfaGFzaCI6ImxlYmJaZjdXNHZjLV95bGU2NGs5dUEiLCJlbWFpbCI6ImFuZ3J5X3J5YW5Aa2FrYW8uY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF1dGhfdGltZSI6MTc2Mzc0MjU0OSwibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.F3BQEEnbgZBBmh5BNP_zVhpunBOgUXxr4GedIUkg5p04J_39Q25IkaN1S-rbm3krKx5kWmjY6ndcoOE8_mNIpCZ82aCp2kdwpxHrLQeWa_pi68UC7gGSCwXAop9OP3iUdF27CYyhv7R_CUDgwqywIENxbtt1R_FcWgKpEwNCRP2kN85xjp11aBiIGwAN6ORwOoVohAyqAUcs6gzwS4GT1Aa0AwzqLuhXhu58FxRvuKLne8QCR7PMaA015diI5stCXClw8vm29MPpKAC4LYToA83n1lGomIGdiMbINU7eBVrnqBEx23i3VZq1va8OscYQweTTHjJyC-ESPmMsHCI4ZA";

        // Apple 공개키 mock 응답
        String fakePublicKeyJson = """
            {
              "keys": [
                {
                  "kty": "RSA",
                  "kid": "ABC123",
                  "use": "sig",
                  "n": "dGVzdA",
                  "e": "AQAB"
                }
              ]
            }
        """;

        JsonNode fakeNode = new ObjectMapper().readTree(fakePublicKeyJson);

        given(restTemplate.getForObject(anyString(), eq(String.class)))
                .willReturn(fakePublicKeyJson);

        given(objectMapper.readTree(anyString())).willReturn(fakeNode);

        // MemberService Stub
        given(memberService.registerMemberIfAbsent(
                any(), any(), any(), any(), any(), any()
        )).willReturn(
                MemberResponseDto.MemberRegisterResponseDto.builder()
                        .id(1L)
                        .email("test@test.com")
                        .memberName("김건우")
                        .isNewMember(true)
                        .build()

        );

        AppleLoginRequest req = new AppleLoginRequest();
        req.setIdentityToken(idToken);


        // === WHEN ===================================================================
        AppleUserInfoResponseDto response =
                appleService.getUserInfoFromIdentityToken(req);


        // === THEN ===================================================================
        assertNotNull(response);
        assertNotNull(response.getSub());
        assertNotNull(response.getEmail());

        then(memberService).should(times(1))
                .registerMemberIfAbsent(
                        eq(Provider.APPLE),
                        eq(response.getSub()),
                        eq(response.getEmail()),
                        any(),
                        any(),
                        any()
                );

    }
}