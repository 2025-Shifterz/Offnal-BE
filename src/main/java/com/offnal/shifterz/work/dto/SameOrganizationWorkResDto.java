package com.offnal.shifterz.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SameOrganizationWorkResDto {

    private String myTeam;
    private List<TeamWorkInstanceResDto> teams;
}
