package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {
    @Query("SELECT p.id FROM Post p WHERE p.createdAt = :createdAt")
    public int findIdByCreatedAt(@Param("createdAt") LocalDateTime createdAt);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %?1%" +
            "OR  p.content LIKE %?1%" +
            "OR  p.author LIKE %?1%" +
            "OR p.excerpt LIKE %?1%" +
            "OR p.id IN" +
            "(SELECT pt.postId FROM PostTag pt WHERE pt.tagId IN" +
            "(SELECT t.id FROM Tag t WHERE t.name LIKE %?1%))")
    List<Post> search(String keyword);
}
