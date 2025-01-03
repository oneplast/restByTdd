package com.example.restByTdd.domain.post.post.service;

import com.example.restByTdd.domain.member.member.entity.Member;
import com.example.restByTdd.domain.post.post.entity.Post;
import com.example.restByTdd.domain.post.post.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public Post write(Member author, String title, String content, boolean published) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .content(content)
                .published(published)
                .build();

        return postRepository.save(post);
    }

    public List<Post> findAllByOrderByIdDesc() {
        return postRepository.findAllByOrderByIdDesc();
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public void modify(Post post, String title, String content) {
        post.setTitle(title);
        post.setContent(content);
    }

    public void flush() {
        postRepository.flush();
    }

    public Optional<Post> findLatest() {
        return this.postRepository.findFirstByOrderByIdDesc();
    }
}
