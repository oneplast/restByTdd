package com.example.restByTdd.domain.post.post.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.restByTdd.domain.post.post.entity.Post;
import com.example.restByTdd.domain.post.post.service.PostService;
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
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("1번글 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/1")
                )
                .andDo(print());

        Post post = postService.findById(1).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.createDate").value(
                        Matchers.startsWith(post.getCreateDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.modifyDate").value(
                        Matchers.startsWith(post.getModifyDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.authorId").value(post.getAuthor().getId()))
                .andExpect(jsonPath("$.authorName").value(post.getAuthor().getName()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()));
    }

    @Test
    @DisplayName("존재하지 않는 글 조회, 404")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/1000000")
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));
    }
}
