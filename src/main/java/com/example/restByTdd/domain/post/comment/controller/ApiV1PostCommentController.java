package com.example.restByTdd.domain.post.comment.controller;

import com.example.restByTdd.domain.member.member.entity.Member;
import com.example.restByTdd.domain.post.comment.dto.PostCommentDto;
import com.example.restByTdd.domain.post.comment.entity.PostComment;
import com.example.restByTdd.domain.post.post.entity.Post;
import com.example.restByTdd.domain.post.post.service.PostService;
import com.example.restByTdd.global.exceptions.ServiceException;
import com.example.restByTdd.global.rq.Rq;
import com.example.restByTdd.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class ApiV1PostCommentController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping
    public List<PostCommentDto> items(@PathVariable long postId) {
        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        return post
                .getComments()
                .stream()
                .map(PostCommentDto::new)
                .toList();
    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable long postId, @PathVariable long id) {
        Member actor = rq.checkAuthentication();

        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        PostComment postComment = post.getCommentById(id).orElseThrow(
                () -> new ServiceException("404-2", "%d번 댓글은 존재하지 않습니다.".formatted(id))
        );

        postComment.checkActorCanDelete(actor);

        post.removeComment(postComment);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 삭제되었습니다.".formatted(id)
        );
    }

    record PostCommentModifyReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String content
    ) {
    }

    @PutMapping("/{id}")
    public RsData<PostCommentDto> modify(@PathVariable long postId, @PathVariable long id,
                                         @RequestBody @Valid PostCommentModifyReqBody reqBody) {
        Member actor = rq.checkAuthentication();

        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        PostComment postComment = post.getCommentById(id).orElseThrow(
                () -> new ServiceException("404-2", "%d번 댓글은 존재하지 않습니다.".formatted(id))
        );

        postComment.checkActorCanModify(actor);

        postComment.modify(reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 댓글이 수정되었습니다.".formatted(id),
                new PostCommentDto(postComment)
        );
    }

    record PostCommentWriteReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String content
    ) {
    }

    @PostMapping
    @Transactional
    public RsData<PostCommentDto> write(@PathVariable long postId,
                                        @RequestBody @Valid PostCommentWriteReqBody reqBody) {
        Member actor = rq.checkAuthentication();

        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        PostComment postComment = post.addComment(actor, reqBody.content);

        postService.flush();

        return new RsData<>(
                "201-1",
                "%d번 댓글이 생성되었습니다.".formatted(postComment.getId()),
                new PostCommentDto(postComment)
        );
    }
}
