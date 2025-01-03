package com.example.restByTdd.domain.post.post.controller;

import com.example.restByTdd.domain.member.member.entity.Member;
import com.example.restByTdd.domain.post.post.dto.PostDto;
import com.example.restByTdd.domain.post.post.dto.PostWithContentDto;
import com.example.restByTdd.domain.post.post.entity.Post;
import com.example.restByTdd.domain.post.post.service.PostService;
import com.example.restByTdd.global.dto.PageDto;
import com.example.restByTdd.global.rq.Rq;
import com.example.restByTdd.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping
    public PageDto<PostDto> items(@RequestParam(defaultValue = "1") int page
            , @RequestParam(defaultValue = "10") int pageSize) {
        return new PageDto<>(
                postService.findByListedPaged(true, page, pageSize)
                        .map(PostDto::new)
        );
    }

    @GetMapping("/{id}")
    public PostWithContentDto item(@PathVariable long id) {
        Post post = postService.findById(id).get();

        if (!post.isPublished()) {
            Member actor = rq.checkAuthentication();

            post.checkActorCanRead(actor);
        }

        return new PostWithContentDto(post);
    }

    record PostWriteReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String title,
            @NotBlank
            @Length(min = 2, max = 10000000)
            String content,
            boolean published,
            boolean listed
    ) {
    }

    @PostMapping
    public RsData<PostWithContentDto> write(@RequestBody @Valid PostWriteReqBody reqBody) {
        Member actor = rq.checkAuthentication();

        Post post = this.postService.write(actor, reqBody.title, reqBody.content, reqBody.published, reqBody.listed);

        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostWithContentDto(post)
        );
    }

    record PostModifyReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String title,
            @NotBlank
            @Length(min = 2, max = 10000000)
            String content,
            boolean published,
            boolean listed
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    public RsData<PostWithContentDto> modify(@PathVariable long id, @RequestBody @Valid PostModifyReqBody reqBody) {
        Member actor = this.rq.checkAuthentication();

        Post post = this.postService.findById(id).get();

        post.checkActorCanModify(actor);

        postService.modify(post, reqBody.title, reqBody.content, reqBody.published, reqBody.listed);

        postService.flush();

        return new RsData<>(
                "200-1",
                "%d번 글이 수정되었습니다.".formatted(id),
                new PostWithContentDto(post)
        );
    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable long id) {
        Member member = rq.checkAuthentication();

        Post post = postService.findById(id).get();

        post.checkActorCanDelete(member);

        postService.delete(post);

        return new RsData<>("200-1", "%d번 글이 삭제되었습니다.".formatted(id));
    }
}
