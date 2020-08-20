package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {
    @Query("SELECT p.id FROM Post p WHERE p.createdAt = :createdAt")
    public int getId(@Param("createdAt") LocalDateTime createdAt);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %?1%" +
            "OR  p.content LIKE %?1%" +
            "OR  p.author LIKE %?1%" +
            "OR p.excerpt LIKE %?1%" +
            "OR p.id IN" +
            "(SELECT pt.postId FROM PostTag pt WHERE pt.tagId IN" +
            "(SELECT t.id FROM Tag t WHERE t.name LIKE %?1%))")
    List<Post> getPosts(String keyword);

    @Query(value = "SELECT * FROM posts p WHERE ((:author = '') is true or p.author = :author) AND " +
            "((:publishedAt = '') is true or to_char(p.published_at,'YYYY-MM-DD HH24:MI') = :publishedAt) AND" +
            "((coalesce(:tagNames, null)) is null or p.id IN (SELECT pt.post_id FROM post_tags pt WHERE pt.tag_id IN" +
            "(SELECT t.id FROM tags t WHERE t.name IN (:tagNames))))", nativeQuery = true)
    List<Post> filter(@Param("author") String author, @Param("publishedAt") String publishedAt,
                      @Param("tagNames") List<String> tagNames);
}
