package com.offnal.shifterz.home.service;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.HealthGuideDto;
import com.offnal.shifterz.home.dto.HomeDetailResDto;
import com.offnal.shifterz.home.dto.MealCardDto;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final WorkInstanceRepository workInstanceRepository;

    // 어제/오늘/내일 근무 형태 + 오늘의 루틴 정보를 모두 포함한 통합 API
    public HomeDetailResDto getHomeDetail(Long memberId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        WorkTimeType yesterdayType = findWorkTypeOrNull(yesterday, memberId);
        WorkTimeType todayType = findWorkTypeOrNull(today, memberId);
        WorkTimeType tomorrowType = findWorkTypeOrNull(tomorrow, memberId);

        // 오늘 근무 정보 조회
        WorkInstance todayWork = workInstanceRepository
                .findByWorkDateAndMemberIdThroughWorkCalendar(
                        today, memberId, today, today
                )
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_INSTANCE_NOT_FOUND));

        WorkTime workTime = null;
        if (todayType != WorkTimeType.OFF) {
            String typeKey = todayType.getSymbol();
            workTime = todayWork.getWorkCalendar().getWorkTimes().get(typeKey);
            if (workTime == null) {
                throw new CustomException(ErrorCode.WORK_TIME_NOT_FOUND);
            }
        }

        DailyRoutineResDto routine = getRoutineByWorkType(todayType, workTime, yesterdayType, tomorrowType);

        return HomeDetailResDto.from(yesterdayType, todayType, tomorrowType, routine);
    }

    // 근무 형태에 따른 루틴 구성
    private DailyRoutineResDto getRoutineByWorkType(
            WorkTimeType type,
            WorkTime workTime,
            WorkTimeType yesterdayType,
            WorkTimeType tomorrowType
    ) {
        return switch (type) {
            case OFF -> buildOffRoutine(yesterdayType, tomorrowType);
            case DAY -> buildDayRoutine(workTime);
            case EVENING -> buildEveningRoutine(workTime);
            case NIGHT -> buildNightRoutine(workTime);
        };
    }

    // 특정 날짜의 근무 형태 조회
    private WorkTimeType findWorkTypeOrNull(LocalDate date, Long memberId) {
        return workInstanceRepository
                .findByWorkDateAndMemberIdThroughWorkCalendar(
                        date, memberId, date, date
                )
                .map(WorkInstance::getWorkTimeType)
                .orElse(null);
    }

    // OFF 근무 루틴 구성
    private DailyRoutineResDto buildOffRoutine(WorkTimeType yesterdayType, WorkTimeType tomorrowType) {
        List<String> sleepSchedules = new ArrayList<>();
        List<Map.Entry<LocalTime, LocalTime>> sleepRanges = new ArrayList<>();

        if (yesterdayType == WorkTimeType.NIGHT) {
            sleepSchedules.add("(1) 08:00 ~ 13:00 수면");
            sleepRanges.add(new AbstractMap.SimpleEntry<>(LocalTime.of(8, 0), LocalTime.of(13, 0)));
        }
        if (tomorrowType == WorkTimeType.DAY) {
            sleepSchedules.add("(2) 22:00 ~ 05:00 수면");
            sleepRanges.add(new AbstractMap.SimpleEntry<>(LocalTime.of(22, 0), LocalTime.of(5, 0)));
        }

        String sleepComment = getClosestSleepScheduleComment(sleepRanges);

        return DailyRoutineResDto.from(
                List.of(
                        meal("점심", LocalTime.of(13, 30), "기상 후 체력 회복", List.of("김밥", "칼국수")),
                        meal("저녁", LocalTime.of(17, 30), "밤잠 대비 소화 부담 최소화", List.of("죽", "나물", "연두부"))
                ),
                HealthGuideDto.from(
                        sleepSchedules,
                        sleepComment,
                        "생체 리듬 유지에 집중 야식, 피하고 수면 시간 지키기",
                        "저녁 식사 후 공복 유지"
                )
        );
    }

    // DAY 근무 루틴 구성
    private DailyRoutineResDto buildDayRoutine(WorkTime workTime) {
        LocalTime start = workTime.getStartTime();
        LocalTime end = start.plus(workTime.getDuration());

        LocalTime sleepStart = end.plusHours(6);
        LocalTime sleepEnd = end.plusHours(13);

        return DailyRoutineResDto.from(
                List.of(
                        meal("아침", start.minusHours(1), "기상 직후 에너지 공급", List.of("오트밀", "계란")),
                        meal("점심", start.plusHours(5), " 근무 집중력 유지", List.of("현미밥", "생선", "나물")),
                        meal("저녁", end.plusHours(3), "소화 부담 없는 식사로 수면 대비", List.of("밥", "두부", "나물"))
                ),
                HealthGuideDto.from(
                        List.of("주간 근무 후, 오후 근무 대비해 늦게 수면"),
                        format(sleepStart) + " ~ " + format(sleepEnd) + " 수면",
                        "수면 질 향상 및 조기 기상 위해 저녁 일찍 → 공복 유지 후 수면",
                        format(end.plusHours(4)) + " 이후 공복 유지"
                )
        );
    }

    // EVENING 근무 루틴 구성
    private DailyRoutineResDto buildEveningRoutine(WorkTime workTime) {
        LocalTime start = workTime.getStartTime();
        LocalTime end = start.plus(workTime.getDuration());

        LocalTime sleepStart = end.plusHours(15);
        LocalTime fastingTime = end.plusHours(1);

        return DailyRoutineResDto.from(
                List.of(
                        meal("아침", start.minusHours(7), "리듬 전환 대비", List.of("계란", "토스트")),
                        meal("점심", start.minusHours(2), "근무 전 에너지 확보", List.of("현미밥", "닭가슴살", "채소")),
                        meal("저녁", end.minusHours(3), "과식 피하기", List.of("고구마", "두부 샐러드"))
                ),
                HealthGuideDto.from(
                        List.of("퇴근 후 바로 잠들면 내일 야간 근무에 지장이 갈 수 있어요"),
                        "밤샘 후 " + format(sleepStart) + " 수면",
                        "늦은 기상이므로 퇴근 후 과식 금지",
                        format(fastingTime) + " 이후 공복 유지"
                )
        );
    }

    // NIGHT 근무 루틴 구성
    private DailyRoutineResDto buildNightRoutine(WorkTime workTime) {
        LocalTime start = workTime.getStartTime();
        LocalTime end = start.plus(workTime.getDuration());

        LocalTime preSleepStart = start.minusHours(11);
        LocalTime preSleepEnd = start.minusHours(6);
        LocalTime postSleepStart = end.plusHours(2);
        LocalTime postSleepEnd = end.plusHours(7);

        String sleepComment = getClosestSleepScheduleComment(List.of(
                new AbstractMap.SimpleEntry<>(preSleepStart, preSleepEnd),
                new AbstractMap.SimpleEntry<>(postSleepStart, postSleepEnd)
        ));

        return DailyRoutineResDto.from(
                List.of(
                        meal("점심", LocalTime.of(12, 0), "야근 전 주요 에너지 확보", List.of("현미밥", "생선구이", "채소")),
                        meal("출근 전 간식", start.minusHours(5), "포만감 및 졸림 방지", List.of("고구마", "삶은 달걀", "두유")),
                        meal("근무 중 간식 1", start.plusHours(3), "혈당 안정 및 집중력 유지", List.of("바나나", "견과류")),
                        meal("근무 중 간식 2", start.plusHours(6), "혈당 안정 및 집중력 유지", List.of("삶은 계란", "따뜻한 물")),
                        meal("퇴근 직후 소식", end.plusMinutes(30), "위 부담 줄이며 안정된 수면 유도", List.of("연두부", "물"))
                ),
                HealthGuideDto.from(
                        List.of(
                                "(1) 출근 전 " + format(preSleepStart) + " ~ " + format(preSleepEnd) + " 수면",
                                "(2) 퇴근 후 " + format(postSleepStart) + " ~ " + format(postSleepEnd) + " 수면"
                        ),
                        sleepComment,
                        "퇴근 후 원활한 수면을 위해 " + format(end.minusHours(3)) + " 이후엔 카페인 섭취 금지",
                        format(end.minusHours(3)) + " 이후 공복 유지"
                )
        );
    }

    // 식단 카드 생성 유틸
    private MealCardDto meal(String label, LocalTime time, String desc, List<String> items) {
        return MealCardDto.from(label, format(time), desc, items);
    }

    // 시간 포맷 변환 유틸
    private String format(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // 현재 시간과 가장 가까운 수면 시간 반환
    private String getClosestSleepScheduleComment(List<Map.Entry<LocalTime, LocalTime>> sleepRanges) {
        LocalTime now = LocalTime.now();
        Map.Entry<LocalTime, LocalTime> closest = null;
        long minDiff = Long.MAX_VALUE;

        for (Map.Entry<LocalTime, LocalTime> entry : sleepRanges) {
            long diff = Math.abs(minutesBetween(now, entry.getKey()));
            if (diff < minDiff) {
                minDiff = diff;
                closest = entry;
            }
        }

        if (closest != null) {
            return "수면 " + format(closest.getKey()) + " ~ " + format(closest.getValue());
        } else {
            return "";
        }
    }

    // 분 단위 차이 계산
    private long minutesBetween(LocalTime t1, LocalTime t2) {
        return Math.abs(t1.toSecondOfDay() - t2.toSecondOfDay()) / 60;
    }
}
