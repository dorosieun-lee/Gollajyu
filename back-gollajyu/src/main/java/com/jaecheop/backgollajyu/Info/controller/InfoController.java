package com.jaecheop.backgollajyu.Info.controller;


import com.jaecheop.backgollajyu.Info.model.StatisticsSearchReqDto;
import com.jaecheop.backgollajyu.Info.service.InfoService;
import com.jaecheop.backgollajyu.vote.entity.VoteResult;
import com.jaecheop.backgollajyu.vote.model.CategoryTagDto;
import com.jaecheop.backgollajyu.vote.repository.VoteResultRepository;
import com.jaecheop.backgollajyu.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class InfoController {

    private final VoteService voteService;

    private final VoteResultRepository voteResultRepository;

    private final InfoService infoService;

    @PostMapping("")
    @Operation(summary = "통계결과", description = "returns CategoryTagDtoList")
    public ResponseEntity<List<CategoryTagDto>> resultStatistics(@RequestBody StatisticsSearchReqDto statisticsSearchReqDto) {
        System.out.println("statisticsSearchReqDto = " + statisticsSearchReqDto);
        if (statisticsSearchReqDto == null || statisticsSearchReqDto.getCategoryId() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            List<VoteResult> voteResults;
            if ((statisticsSearchReqDto.getMemberId() != null) && (statisticsSearchReqDto.getMemberId() != 0)) {
                System.out.println("member");
                // memberId is present, include it in the query
                voteResults = voteResultRepository.findAllByCategoryIdAndMemberId(
                        statisticsSearchReqDto.getCategoryId(),
                        statisticsSearchReqDto.getMemberId()
                );
            } else {
//                System.out.println("nomember");
                // memberId is not present, query without considering it
                voteResults = voteResultRepository.findAllByCategoryId(
                        statisticsSearchReqDto.getCategoryId()
                );
            }
//            System.out.println(voteResults+"213124124124//"+statisticsSearchReqDto.getCategoryId());
            List<CategoryTagDto> statistics = voteService.generateStatistics(
                    voteResults,
                    statisticsSearchReqDto
            );
            return ResponseEntity.ok(statistics);
        }
    }
}
