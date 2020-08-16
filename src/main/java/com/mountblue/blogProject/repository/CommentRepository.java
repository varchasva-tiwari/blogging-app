package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {

    @Query("SELECT c from Comment c WHERE c.postId = :postId")
    List<Comment> readCommentsById(@Param("postId") int postId);
}
