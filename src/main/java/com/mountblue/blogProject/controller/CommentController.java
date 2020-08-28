package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.service.CommentService;
import com.mountblue.blogProject.service.PostService;
import com.mountblue.blogProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
public class CommentController {
    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<String> saveComment(@PathVariable("postId") int postId,
                                              @RequestBody Map<String, Comment> commentMap,
                                              Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<String>("Incorrect post id!",
                    HttpStatus.NOT_FOUND);
        }

        Comment comment = null;
        for(Map.Entry<String, Comment> commentEntry : commentMap.entrySet()) {
            comment = commentEntry.getValue();
        }

        comment.setPostId(postId);

        if(principal.getName() != null) {
            comment.setUserId(userService.getUserId(principal.getName()));
        }

        if(commentService.saveComment(comment) == null) {
            return new ResponseEntity<>("The comment could not be saved due to server issues! Please try again later!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders header = new HttpHeaders();
        header.add("link", "/posts/" + postId + "/comments/" + comment.getId());

        return new ResponseEntity<>("Comment saved successfully!", header, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable("postId") int postId,
                                                       @PathVariable("commentId") int commentId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Incorrect post id!",
                    HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>("Incorrect comment id!",
                    HttpStatus.NOT_FOUND);
        }

        if(commentService.getPostId(commentId) != postId) {
            return new ResponseEntity<>("The comment does not belong to this post!",
                    HttpStatus.NOT_FOUND);
        }

        Comment comment = commentService.getComment(postId, commentId);

        Map<String, Comment> commentMap = new HashMap<>();
        commentMap.put("comment", comment);

        return new ResponseEntity<>(commentMap, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable("postId") int postId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Incorrect post id!",
                    HttpStatus.NOT_FOUND);
        }

        Map<String, List<Comment>> commentsMap = new HashMap<>();
        commentsMap.put("comments", commentService.getComments(postId));

        return new ResponseEntity<>(commentsMap, HttpStatus.OK);
    }

    @PatchMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<String> editComment(@PathVariable("postId") int postId,
                                              @PathVariable("commentId") int commentId,
                                              @RequestBody Map<String, Comment> commentMap,
                                              Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Incorrect post id!", HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>("Incorrect comment id!", HttpStatus.NOT_FOUND);
        }

        if(commentService.getPostId(commentId) != postId) {
            return new ResponseEntity<>("The comment does not belong to this post!",
                    HttpStatus.NOT_FOUND);
        }

        if (principal.getName() == null) {
            return new ResponseEntity<>("You are not authorized to perform this action! Please login first!",
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal, postId)) {
                Comment comment = null;

                for (Map.Entry<String, Comment> commentEntry : commentMap.entrySet()) {
                    comment = commentEntry.getValue();
                }

                comment.setId(commentId);
                comment.setPostId(postId);

                Comment updatedComment = commentService.editComment(comment);
                if (updatedComment == null) {
                    return new ResponseEntity<String>("The comment could not be saved due to server issues! Please try again later!",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<String>("You are not authorized to perform this action!",
                        HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>("Comment updated successfully!", HttpStatus.OK);
    }

    @DeleteMapping ("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("postId") int postId,
                                                @PathVariable("commentId") int commentId,
                                                Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Incorrect post id!", HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>("Incorrect comment id!", HttpStatus.NOT_FOUND);
        }

        if(commentService.getPostId(commentId) != postId) {
            return new ResponseEntity<>("The comment does not belong to this post!",
                    HttpStatus.NOT_FOUND);
        }

        if (principal.getName() == null) {
            return new ResponseEntity<>("You are not authorized to perform this action! Please login first!",
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal, postId)) {
                commentService.deleteComment(postId, commentId);
            } else {
                return new ResponseEntity<>("You are not authorized to perform this action!",
                        HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>("Comment deleted successfully!", HttpStatus.OK);
    }

    public boolean isPermitted(Principal principal, int postId) {
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_admin");
        SimpleGrantedAuthority authorAuthority = new SimpleGrantedAuthority("ROLE_author");

        if (userService.loadUserByUsername(principal.getName())
                .getAuthorities().contains(adminAuthority) ||
                (userService.loadUserByUsername(principal.getName()).getAuthorities()
                .contains(authorAuthority) && postService.getAuthor(postId).equals(principal.getName()))) {
            return true;
        }

        return false;
    }
}