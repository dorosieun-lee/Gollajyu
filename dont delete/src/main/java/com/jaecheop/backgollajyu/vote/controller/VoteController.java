package com.jaecheop.backgollajyu.vote.controller;

import com.jaecheop.backgollajyu.vote.entity.Vote;
import com.jaecheop.backgollajyu.vote.model.ChoiceReqDto;
import com.jaecheop.backgollajyu.vote.model.ResponseMessage;
import com.jaecheop.backgollajyu.vote.model.ServiceResult;
import com.jaecheop.backgollajyu.vote.model.VoteReqDto;
import com.jaecheop.backgollajyu.vote.service.VoteService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    /**
     * 투표 생성 - create
     *
     * @return
     */
    @PostMapping("")
    public ResponseEntity<ResponseMessage> addVote(@RequestBody VoteReqDto voteReqDto) {

        // 서비스단으로 넘겨서 로직 처리 -> ServiceResult(result, message, object-data)로 반환
        ServiceResult result = voteService.addVote(voteReqDto);

        // 받아온 결과에 따라 에러 메세지 출력하거나 return 하거나
        if (!result.isResult()) {
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }
        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    @PostMapping("/choices")
    public ResponseEntity<ResponseMessage> choiceMain(@RequestBody ChoiceReqDto choiceReqDto) {

        ServiceResult result = voteService.choiceMain(choiceReqDto);

        if (!result.isResult()) {
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }
        return ResponseEntity.ok().body(ResponseMessage.success());

    }
}
