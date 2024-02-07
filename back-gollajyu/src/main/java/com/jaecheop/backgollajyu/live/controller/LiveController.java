package com.jaecheop.backgollajyu.live.controller;

import com.jaecheop.backgollajyu.live.model.LiveDetailDto;
import com.jaecheop.backgollajyu.live.model.LiveDetailResDto;
import com.jaecheop.backgollajyu.live.model.LiveListDto;
import com.jaecheop.backgollajyu.live.model.LiveStartReqDto;
import com.jaecheop.backgollajyu.live.service.LiveService;
import com.jaecheop.backgollajyu.vote.model.ResponseMessage;
import com.jaecheop.backgollajyu.vote.model.ServiceResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lives")
public class LiveController {

    private final LiveService liveService;

    @Value("${file.dir}")
    private String fileDir;

    @PostMapping("")
    @Operation(summary = "라이브 방송 생성", description = "No body")
    public ResponseEntity<ResponseMessage<?>> startLive(LiveStartReqDto liveStartReqDto) {
        ServiceResult<?> result = liveService.startLive(liveStartReqDto, fileDir);

        ResponseMessage<?> responseMessage = new ResponseMessage<>();

        if (!result.isResult()) {
            return ResponseEntity.ok().body(responseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().body(responseMessage.success());
    }

    @GetMapping("")
    @Operation(summary = "라이브 방송 리스트 조회", description = "returns LiveListDto")
    public ResponseEntity<ResponseMessage<List<LiveListDto>>> listLive() {
        ServiceResult<List<LiveListDto>> result = liveService.findAllLives();

        ResponseMessage<List<LiveListDto>> responseMessage = new ResponseMessage<>();
        if (!result.isResult()) {
            return ResponseEntity.ok().body(responseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().body(responseMessage.success(result.getData()));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "라이브 방송 삭제", description = "No body")
    public ResponseEntity<ResponseMessage<Void>> deleteLive(@PathVariable Long sessionId) {
        ServiceResult<Void> result = liveService.deleteLiveRoom(sessionId);

        ResponseMessage<Void> responseMessage = new ResponseMessage<>();
        if (!result.isResult()) {
            return ResponseEntity.ok().body(responseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().body(responseMessage.success());
    }

    @GetMapping("/{liveId}")
    @Operation(summary = "라이브 방송 상세 조회", description = "returns LiveDetailDto")
    public ResponseEntity<ResponseMessage<LiveDetailResDto>> getLiveDetail(@PathVariable Long liveId) {
        ServiceResult<LiveDetailResDto> result = liveService.findLiveDetail(liveId);

        ResponseMessage<LiveDetailResDto> responseMessage = new ResponseMessage<>();
        if (!result.isResult()) {
            return ResponseEntity.ok().body(responseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().body(responseMessage.success(result.getData()));
    }
}
