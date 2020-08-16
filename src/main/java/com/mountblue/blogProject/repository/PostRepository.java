package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {
    @Query("SELECT p.id from Post p WHERE p.createdAt = :createdAt")
    public int findIdByCreatedAt(@Param("createdAt") LocalDateTime createdAt);
}
