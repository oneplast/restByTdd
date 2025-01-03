package com.example.restByTdd.domain.post.post.repository;

import com.example.restByTdd.domain.post.post.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByIdDesc();
    Optional<Post> findFirstByOrderByIdDesc();
    Page<Post> findByListed(boolean listed, PageRequest pageRequest);
}
