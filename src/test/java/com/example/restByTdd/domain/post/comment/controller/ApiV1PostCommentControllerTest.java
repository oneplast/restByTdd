package com.example.restByTdd.domain.post.comment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.restByTdd.domain.member.member.service.MemberService;
import com.example.restByTdd.domain.post.comment.entity.PostComment;
import com.example.restByTdd.domain.post.post.service.PostService;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class ApiV1PostCommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("다건 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/1/comments")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk());

        List<PostComment> comments = postService.findById(1).get().getComments();

        for (int i = 0; i < comments.size(); i++) {
            PostComment postComment = comments.get(i);
            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(postComment.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(
                            Matchers.startsWith(postComment.getCreateDate().toString().substring(0, 25))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(
                            Matchers.startsWith(postComment.getModifyDate().toString().substring(0, 25))))
                    .andExpect(jsonPath("$[%d].authorId".formatted(i)).value(postComment.getAuthor().getId()))
                    .andExpect(jsonPath("$[%d].authorName".formatted(i)).value(postComment.getAuthor().getName()))
                    .andExpect(jsonPath("[%d].content".formatted(i)).value(postComment.getContent()));
        }
    }
}
