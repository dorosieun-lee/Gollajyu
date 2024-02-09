package com.jaecheop.backgollajyu.live.service;

import com.jaecheop.backgollajyu.exception.NotEnoughPointException;
import com.jaecheop.backgollajyu.live.entity.Live;
import com.jaecheop.backgollajyu.live.entity.LiveParticipant;
import com.jaecheop.backgollajyu.live.entity.LiveVoteItem;
import com.jaecheop.backgollajyu.live.entity.LiveVoteParticipant;
import com.jaecheop.backgollajyu.live.model.*;
import com.jaecheop.backgollajyu.live.repository.LiveParticipantRepository;
import com.jaecheop.backgollajyu.live.repository.LiveRepository;
import com.jaecheop.backgollajyu.live.repository.LiveVoteItemRepository;
import com.jaecheop.backgollajyu.live.repository.LiveVoteParticipantRepository;
import com.jaecheop.backgollajyu.member.entity.Member;
import com.jaecheop.backgollajyu.member.repostory.MemberRepository;
import com.jaecheop.backgollajyu.vote.model.ServiceResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class LiveService {

    @Value("${img.url}")
    private String imgUrl;

    private final MemberRepository memberRepository;
    private final LiveRepository liveRepository;
    private final LiveVoteItemRepository liveVoteItemRepository;
    private final LiveParticipantRepository liveParticipantRepository;
    private final LiveVoteParticipantRepository liveVoteParticipantRepository;

    @Transactional
    public ServiceResult<LiveStartResDto> startLive(LiveStartReqDto liveStartReqDto, String fileDir) {
        // 멤버 ID로 이미 라이브 방송이 존재하는지 확인
        if (liveRepository.existsByMemberId(liveStartReqDto.getMemberId())) {
            return new ServiceResult<LiveStartResDto>().fail("이미 라이브 방송이 존재합니다.");
        }

        // 멤버 조회
        Member member = memberRepository.findById(liveStartReqDto.getMemberId())
                .orElse(null);

        if (member == null) {
            return new ServiceResult<LiveStartResDto>().fail("존재하지 않는 회원입니다.");
        }

        // 포인트 차감 로직
        try {
            member.minusPoint(10L); // 라이브 방송 시작 시 필요한 포인트 차감
        } catch (NotEnoughPointException e) {
            return new ServiceResult<LiveStartResDto>().fail(e.getMessage());
        }

        // 라이브 방송 이미지 저장
        String liveImagePath = ""; // 라이브 방송 이미지 저장 경로 초기화
        try {
            MultipartFile liveImageFile = liveStartReqDto.getLiveImgUrl();
            if (liveImageFile != null && !liveImageFile.isEmpty()) {
                liveImagePath = saveFile(liveImageFile, fileDir);
            }
        } catch (IOException e) {
            return new ServiceResult<LiveStartResDto>().fail(e.getMessage());
        }

        // 라이브 방송 생성 및 저장
        Live live = liveRepository.save(Live.builder()
                .sessionId(liveStartReqDto.getSessionId())
                .member(member)
                .title(liveStartReqDto.getLiveTitle())
                .imgUrl(liveImagePath)
                .count(0L)
                .build());

        // 라이브 투표 아이템 생성 및 저장
        try {
            for (LiveVoteItemDto itemDto : liveStartReqDto.getLiveVoteItemDtoList()) {
                LiveVoteItem liveVoteItem = new LiveVoteItem();
                liveVoteItem.setLive(live); // 라이브 방송 설정

                // imgUrl이 null이 아닌 경우에만 파일 저장 로직 수행
                if (itemDto.getImgUrl() != null && !itemDto.getImgUrl().isEmpty()) {
                    String fullPath = saveFile(itemDto.getImgUrl(), fileDir);
                    liveVoteItem.setImgUrl(fullPath); // 파일 경로 저장
                } else {
                    liveVoteItem.setImgUrl(null); // imgUrl이 없는 경우 null로 설정
                }

                // description 설정
                liveVoteItem.setDescription(itemDto.getDescription());
                liveVoteItem.setCount(0L);

                // DB에 저장
                liveVoteItemRepository.save(liveVoteItem);
            }
        } catch (IOException e) {
            return new ServiceResult<LiveStartResDto>().fail("파일 저장 중 문제가 발생했습니다: " + e.getMessage());
        }

        LiveStartResDto liveStartResDto = LiveStartResDto.builder().liveId(live.getId()).build();
        return new ServiceResult<LiveStartResDto>().success(liveStartResDto);
    }

    private String saveFile(MultipartFile file, String fileDir) throws IOException {
        String imgPath = "";

        if (!file.isEmpty()) {
            imgPath = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(fileDir + "\\" + imgPath));
        }
        return fileDir + "\\" + imgPath;
    }

//    public ServiceResult<List<LiveListDto>> findAllLives() {
//        List<Live> lives = liveRepository.findAll();
//        List<LiveListDto> liveListDtos = lives.stream()
//                .map(live -> LiveListDto.builder()
//                        .id(live.getId())
//                        .nickname(live.getMember().getNickname())
//                        .title(live.getTitle())
//                        .count(live.getCount())
//                        .imgUrl(live.getImgUrl())
//                        .build())
//                .collect(Collectors.toList());
//        return new ServiceResult<List<LiveListDto>>().success(liveListDtos);
//    }

    public List<LiveListDto> findAllLivesWithTop3() {
        List<Live> allLives = liveRepository.findAll();
        List<Live> top3Lives = liveRepository.findTop3ByOrderByCountDesc();

        // 상위 3개를 제외한 나머지 라이브 방송을 최신순으로 정렬
        List<Live> otherLives = allLives.stream()
                .filter(live -> !top3Lives.contains(live))
                .sorted(Comparator.comparing(Live::getId).reversed())
                .collect(Collectors.toList());

        // DTO 변환 로직 (상위 3개 + 나머지)
        List<LiveListDto> liveListDtos = new ArrayList<>();
        top3Lives.forEach(live -> liveListDtos.add(convertToDto(live)));
        otherLives.forEach(live -> liveListDtos.add(convertToDto(live)));

        return liveListDtos;
    }

    private LiveListDto convertToDto(Live live) {
        return LiveListDto.builder()
                .id(live.getId())
                .title(live.getTitle())
                .nickName(live.getMember().getNickname())
                .count(live.getCount()) // 시청자 수
                .imgUrl(convertFilePathToUrl(live.getImgUrl())) // 라이브 방송 이미지 URL
                .sessionId(live.getSessionId())
                .build();
    }

    @Transactional
    public ServiceResult<Void> deleteLiveRoom(Long liveId) {
        // 라이브 방이 존재하는지 확인
        if (!liveRepository.existsById(liveId)) {
            return new ServiceResult<Void>().fail("Live방이 존재하지 않습니다.");
        }

        // 라이브 방의 투표 참가자들 삭제 (LiveVoteParticipant)
        liveVoteParticipantRepository.deleteByLiveVoteItemLiveId(liveId);

        // 라이브 방과 관련된 아이템들 삭제 (LiveVoteItem)
        liveVoteItemRepository.deleteByLiveId(liveId);

        // 라이브 방에 참여중인 참가자들 삭제 (LiveParticipant)
        liveParticipantRepository.deleteByLiveId(liveId);

        // 라이브 방 삭제
        liveRepository.deleteById(liveId);

        return new ServiceResult<Void>().success();
    }

    public ServiceResult<LiveDetailResDto> findLiveDetail(Long liveId) {
        return liveRepository.findById(liveId)
                .map(live -> {
                    List<LiveVoteItemResDto> voteItems = liveVoteItemRepository.findByLiveId(liveId)
                            .stream()
                            .map(item -> LiveVoteItemResDto.builder()
                                    .id(item.getId())
                                    .imgUrl(convertFilePathToUrl(item.getImgUrl()))
                                    .description(item.getDescription())
                                    .count(item.getCount())
                                    .build())
                            .collect(Collectors.toList());

                    return new ServiceResult<LiveDetailResDto>()
                            .success(LiveDetailResDto.builder()
                                    .title(live.getTitle())
                                    .nickName(live.getMember().getNickname()) // Member의 nickname 설정
                                    .liveCount(live.getCount()) // Live의 count 설정
                                    .liveVoteItemDtoResList(voteItems)
                                    .build());
                })
                .orElseGet(() -> new ServiceResult<LiveDetailResDto>().fail("해당 라이브 방송을 찾을 수 없습니다."));
    }

    // 파일 경로를 웹 URL로 변환
    private String convertFilePathToUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null; // 또는 기본 이미지 URL
        }
        // 이미지 파일명만 추출합니다.
        String fileName = Paths.get(filePath).getFileName().toString();
        // 웹 접근 가능한 URL로 변환합니다.
        return String.format("%s/resources/%s", imgUrl, fileName);
    }

    @Transactional
    public ServiceResult<Void> enterLive(Long liveId, Long memberId) {
        Optional<Live> liveOpt = liveRepository.findById(liveId);
        if (liveOpt.isEmpty()) {
            return new ServiceResult<Void>().fail("해당 라이브 방송이 존재하지 않습니다.");
        }
        Live live = liveOpt.get();

        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return new ServiceResult<Void>().fail("해당 회원이 존재하지 않습니다.");
        }
        Member member = memberOpt.get();

        Optional<LiveParticipant> participantOpt = liveParticipantRepository.findByLiveIdAndMemberId(liveId, memberId);
        if (participantOpt.isPresent()) {
            return new ServiceResult<Void>().fail("이미 라이브 방송에 참여 중입니다.");
        }

        LiveParticipant participant = LiveParticipant.builder()
                .live(live)
                .member(member)
                .build();
        liveParticipantRepository.save(participant);

        // 참여자 수 증가
        live.setCount(live.getCount() + 1);
        liveRepository.save(live);

        return new ServiceResult<Void>().success();
    }

    @Transactional
    public ServiceResult<Void> exitLive(Long liveId, Long memberId) {
        Optional<LiveParticipant> participantOpt = liveParticipantRepository.findByLiveIdAndMemberId(liveId, memberId);

        if (participantOpt.isEmpty()) {
            // 참여자 정보가 존재하지 않는 경우
            return new ServiceResult<Void>().fail("참여자 정보가 존재하지 않습니다.");
        }

        // 참여자 정보가 있는 경우, 참여자 정보 삭제 및 참여자 수 감소
        participantOpt.ifPresent(participant -> {
            liveParticipantRepository.delete(participant);

            Live live = participant.getLive();
            live.setCount(Math.max(0, live.getCount() - 1)); // 참여자 수가 음수가 되지 않도록 조정
            liveRepository.save(live);
        });

        return new ServiceResult<Void>().success();
    }

    @Transactional
    public ServiceResult<?> voteForItem(Long memberId, Long liveId, Long liveVoteItemId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return new ServiceResult<Void>().fail("해당 회원이 존재하지 않습니다.");
        }

        Optional<LiveVoteItem> liveVoteItemOpt = liveVoteItemRepository.findById(liveVoteItemId);
        if (liveVoteItemOpt.isEmpty()) {
            return new ServiceResult<Void>().fail("해당 투표 항목이 존재하지 않습니다.");
        }

        boolean isParticipating = liveParticipantRepository.existsByMemberIdAndLiveId(memberId, liveId);
        if (!isParticipating) {
            return new ServiceResult<Void>().fail("해당 라이브 방송을 시청 중인 사람만 투표할 수 있습니다.");
        }

        // 기존 투표 찾기 및 삭제
        List<LiveVoteParticipant> existingVotes = liveVoteParticipantRepository.findByMemberId(memberId);
        existingVotes.forEach(vote -> {
            LiveVoteItem oldItem = vote.getLiveVoteItem();
            oldItem.setCount(Math.max(0, oldItem.getCount() - 1));
            liveVoteItemRepository.save(oldItem);
            // 기존 투표 항목의 투표 수 감소
            liveVoteParticipantRepository.delete(vote);
        });

        LiveVoteItem liveVoteItem = liveVoteItemOpt.get();
        // 새로운 투표 추가
        LiveVoteParticipant newVote = LiveVoteParticipant.builder()
                .member(memberOpt.get())
                .liveVoteItem(liveVoteItem)
                .build();
        liveVoteParticipantRepository.save(newVote);

        // 투표 항목의 투표 수 증가
        liveVoteItem.setCount(liveVoteItem.getCount() + 1);
        liveVoteItemRepository.save(liveVoteItem);

        return new ServiceResult<Void>().success();
    }
}
