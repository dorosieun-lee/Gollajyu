package com.jaecheop.backgollajyu.vote.repository;

import com.jaecheop.backgollajyu.vote.entity.Category;
import com.jaecheop.backgollajyu.vote.entity.VoteItem;
import com.jaecheop.backgollajyu.vote.entity.VoteResult;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteResultRepository extends JpaRepository<VoteResult, Long> {

    Optional<VoteResult> findByMemberIdAndVoteId(Long memberId, Long voteId);

    @Query("SELECT vr FROM VoteResult vr WHERE vr.voteItem = :voteItem")
    List<VoteResult> findByVoteItem(VoteItem voteItem);

    Long countByVoteItemIdAndTagId(Long voteItemId, int tagId);
    @Query("SELECT vr FROM VoteResult vr")
    List<VoteResult> findByAll();

    List<VoteResult> findAllByMemberId(Long memberId);

    List<VoteResult> findAllByMemberIdAndCategoryId(Long memberId, int categoryId);

    List<VoteResult> findAllByCategoryId(Integer categoryId);

    List<VoteResult> findAllByCategoryIdAndMemberId(Integer categoryId, Long memberId);

    List<VoteResult> findAllByVoteItemId(Long voteItemId);
}
