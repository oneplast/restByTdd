package com.example.restByTdd.domain.post.comment.controller;

import com.example.restByTdd.domain.post.comment.dto.PostCommentDto;
import com.example.restByTdd.domain.post.post.entity.Post;
import com.example.restByTdd.domain.post.post.service.PostService;
import com.example.restByTdd.global.rq.Rq;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        Post post = postService.findById(postId).get();

        return post
                .getComments()
                .stream()
                .map(PostCommentDto::new)
                .toList();
    }
}