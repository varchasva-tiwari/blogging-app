package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Post findById(int id);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %?1%" +
            "OR p.content LIKE %?1%" +
            "OR p.author LIKE %?1%" +
            "OR p.excerpt LIKE %?1%" +
            "OR p.id IN" +
            "(SELECT pt.postId FROM PostTag pt WHERE pt.tagId IN" +
            "(SELECT t.id FROM Tag t WHERE t.name LIKE %?1%))")
    Page<Post> paginatedAndSortedSearch(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM (" +
            "(SELECT * FROM posts p " +
            "WHERE (p.title LIKE %:keyword% " +
            "OR  p.content LIKE %:keyword% " +
            "OR  p.author LIKE %:keyword% " +
            "OR p.excerpt LIKE %:keyword% " +
            "OR p.id IN " +
            "(SELECT pt.post_id FROM post_tags pt WHERE pt.tag_id IN" +
            "(SELECT t.id FROM tags t WHERE t.name LIKE %:keyword%)))) AS post1 INNER JOIN " +
            "(SELECT * FROM posts p2 WHERE ((:author = '') is true or p2.author = :author) AND " +
            "((:publishedAt = '') is true or to_char(p2.published_at,'YYYY-MM-DD') = :publishedAt) AND" +
            "((coalesce(:tagNames, null)) is null or p2.id IN (SELECT pt.post_id FROM post_tags pt WHERE pt.tag_id IN" +
            "(SELECT t.id FROM tags t WHERE t.name IN (:tagNames))))ORDER BY p2.published_at --#pageable\n) AS post2 " +
            "ON post1.id = post2.id) AS post3",
            countQuery = "SELECT count(*) FROM post3",
            nativeQuery = true)
    Page<Post> searchAndFilter(@Param("keyword") String keyword, @Param("author") String author, @Param("publishedAt") String publishedAt,
                               @Param("tagNames") List<String> tagNames, Pageable pageable);

    @Query("SELECT p.author FROM Post p WHERE p.id = :postId")
    String getAuthorById(@Param("postId") int postId);
}
