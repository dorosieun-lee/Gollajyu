package com.jaecheop.backgollajyu.vote.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@ToString
@Getter
public class VoteInfoDto {
    private Long voteId;
    private Long memberId;
    private String memberNickname;
    private String title;
    private String description;
    private Integer categoryId;

    private LocalDateTime createAt;

    private Long likesCnt;

    // 멤버의 해당 투표 좋아요 여부
    private boolean isLiked;

    private Long totalChoiceCnt;

    private int itemCnt;

    public void updateIsLiked(){
        this.isLiked = true;
    }

    public static VoteCloseInfoDto convertToVoteCloseDto(VoteInfoDto voteInfo){
        return VoteCloseInfoDto.builder()
                .voteId(voteInfo.voteId)
                .memberId(voteInfo.getMemberId())
                .title(voteInfo.getTitle())
                .description(voteInfo.getDescription())
                .createAt(voteInfo.getCreateAt())
                .totalChoiceCnt(voteInfo.getTotalChoiceCnt())
                .build();
    }
}
