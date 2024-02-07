package com.jaecheop.backgollajyu.live.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveDetailDto {
    private String title;
    private String nickName;
    private Long liveCount;
    private List<LiveVoteItemDto> liveVoteItemDtoList;
}