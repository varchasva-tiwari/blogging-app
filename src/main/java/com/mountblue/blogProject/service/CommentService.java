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

    public void saveComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public List<Comment> readComments(int postId) {
        return commentRepository.getComments(postId);
    }

    public void editComment(Comment comment) {
        Comment updatedComment = commentRepository.getOne(comment.getId());

        updatedComment.setName(comment.getName());
        updatedComment.setEmail(comment.getEmail());
        updatedComment.setComment(comment.getComment());
        updatedComment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(updatedComment);
    }

    public void deleteComments(int postId) {
        if(commentRepository.getComments(postId).size() > 0)
            commentRepository.deleteById(postId);
    }

    public void deleteComment(int postId, int commentId) {
        commentRepository.deleteComment(postId, commentId);
    }

    public boolean exists(Comment comment) {
        return commentRepository.existsById(comment.getId());
    }
}
