package com.example.restByTdd.domain.post.post.entity;

import com.example.restByTdd.domain.member.member.entity.Member;
import com.example.restByTdd.domain.post.comment.entity.PostComment;
import com.example.restByTdd.global.exceptions.ServiceException;
import com.example.restByTdd.global.jpa.entity.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<PostComment> comments = new ArrayList<>();

    public PostComment addComment(Member author, String content) {
        PostComment comment = PostComment.builder()
                .post(this)
                .author(author)
                .content(content)
                .build();

        comments.add(comment);

        return comment;
    }

    public List<PostComment> getCommentsByOrderByIdDesc() {
        return comments.reversed();
    }

    public Optional<PostComment> getCommentById(long commentId) {
        return comments.stream()
                .filter(comment -> comment.getId() == commentId)
                .findFirst();
    }

    public void removeComment(PostComment postComment) {
        comments.remove(postComment);
    }

    public void checkActorCanDelete(Member actor) {
        if (actor == null) {
            throw new ServiceException("403-1", "로그인 후 이용해주세요.");
        }

        if (actor.isAdmin()) {
            return;
        }

        if (actor.equals(author)) {
            return;
        }

        throw new ServiceException("403-2", "작성자만 글을 삭제할 수 있습니다.");
    }

    public void checkActorCanModify(Member actor) {
        if (actor == null) {
            throw new ServiceException("403-1", "로그인 후 이용해주세요.");
        }

        if (actor.equals(author)) {
            return;
        }

        throw new ServiceException("403-2", "작성자만 글을 수정할 수 있습니다.");
    }
}