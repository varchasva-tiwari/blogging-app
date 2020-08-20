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

    public void create(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public List<Comment> read(int postId) {
        return commentRepository.read(postId);
    }

    public void update(Comment comment) {
        Comment updatedComment = commentRepository.getOne(comment.getId());

        updatedComment.setName(comment.getName());
        updatedComment.setEmail(comment.getEmail());
        updatedComment.setComment(comment.getComment());
        updatedComment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(updatedComment);
    }

    public void deleteComments(int postId) {
        if(commentRepository.read(postId).size() > 0)
            commentRepository.deleteById(postId);
    }

    public void delete(int postId, int commentId) {
        commentRepository.delete(postId, commentId);
    }

    public boolean exists(Comment comment) {
        return commentRepository.existsById(comment.getId());
    }
}
