package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {

    @Query("SELECT c from Comment c WHERE c.postId = :postId")
    List<Comment> getComments(@Param("postId") int postId);

    @Modifying
    @Transactional
    @Query(value = "DELETE from comments c WHERE c.id = :commentId AND c.post_id = :postId", nativeQuery = true)
    void deleteComment(@Param("postId") int postId, @Param("commentId") int commentId);

    @Query(value = "SELECT c from Comment c WHERE c.postId = :postId AND c.id = :commentId")
    Comment getComment(@Param("postId") int postId, @Param("commentId") int commentId);

    @Query("SELECT c.postId from Comment c WHERE c.id = :commentId")
    int getPostId(int commentId);

    Comment findById(int commentId);
}
