package com.mountblue.blogProject.service;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    public void createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public List<Comment> readComments(int postId) {
        return commentRepository.readCommentsById(postId);
    }

    public void updateComment(Comment comment) {
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public void deleteComments(int postId) {
        if(commentRepository.readCommentsById(postId).size() > 0)
            commentRepository.deleteById(postId);
    }

    public void deleteComment(int postId, int commentId) {
        commentRepository.deleteByPostAndCommentId(postId, commentId);
    }
}