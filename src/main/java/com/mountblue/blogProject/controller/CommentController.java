package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.service.CommentService;
import com.mountblue.blogProject.service.PostService;
import com.mountblue.blogProject.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
@Api(description = "Set of endpoints for Creating, Retrieving, Updating and Deleting of Comments")
@RequestMapping("/blogApp")
public class CommentController {
    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping("/posts/{postId}/comments")
    @ApiOperation("Creates a new comment")
    public ResponseEntity<String> saveComment(@PathVariable("postId") int postId,
                                              @RequestBody Map<String, Comment> commentMap,
                                              Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<String>("Post does not exist or has been deleted!",
                    HttpStatus.NOT_FOUND);
        }

        Comment comment = null;
        for(Map.Entry<String, Comment> commentEntry : commentMap.entrySet()) {
            comment = commentEntry.getValue();
        }

        if(comment.getName() == null || comment.getEmail() == null || comment.getComment() == null) {
            return new ResponseEntity<>("Name/Email/Comment cannot be null!", HttpStatus.BAD_REQUEST);
        }

        comment.setPostId(postId);

        if(principal.getName() != null) {
            comment.setUserId(userService.getUserId(principal.getName()));
        }

        if(commentService.saveComment(comment) == null) {
            return new ResponseEntity<>("Comment could not be saved due to server issues! Please try again later!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Location", "/posts/" + postId + "/comments/" + comment.getId());

        return new ResponseEntity<>("Comment saved successfully!", header, HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments/{commentId}")
    @ApiOperation("Returns a specific comment based on post id & comment id")
    public ResponseEntity<?> getComment(@PathVariable("postId") int postId,
                                        @PathVariable("commentId") int commentId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Post does not exist or has been deleted!",
                    HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>("Comment does not exist or has been deleted!",
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
    @ApiOperation("Returns all comments based on post id")
    public ResponseEntity<?> getComments(@PathVariable("postId") int postId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Post does not exist or has been deleted!",
                    HttpStatus.NOT_FOUND);
        }

        Map<String, List<Comment>> commentsMap = new HashMap<>();
        commentsMap.put("comments", commentService.getComments(postId));

        return new ResponseEntity<>(commentsMap, HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNpZmVyIiwiZXhwIjoxNTk4NzU4Mzc3LCJpYXQiOjE1OTg3MjIzNzd9.jg9TdkOXIXsqLI4-Eyq35j__CCv13Ovvvd1htW04nWw")
    @PatchMapping("/posts/{postId}/comments/{commentId}")
    @ApiOperation("Edits a comment based on post id & comment id")
    public ResponseEntity<String> editComment(@PathVariable("postId") int postId,
                                              @PathVariable("commentId") int commentId,
                                              @RequestBody Map<String, Comment> commentMap,
                                              Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Post does not exist or has been deleted!", HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>("Comment does not exist or has been deleted!", HttpStatus.NOT_FOUND);
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

                if(comment.getComment() == null) {
                    return new ResponseEntity<>("Comment field cannot be null!", HttpStatus.BAD_REQUEST);
                }

                comment.setId(commentId);
                comment.setPostId(postId);

                Comment updatedComment = commentService.editComment(comment);

                if (updatedComment == null) {
                    return new ResponseEntity<String>("The comment could not be saved due to server issues! Please try again later!",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<String>("Only the author can perform this action!",
                        HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>("Comment updated successfully!", HttpStatus.OK);
    }


    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNpZmVyIiwiZXhwIjoxNTk4NzU4Mzc3LCJpYXQiOjE1OTg3MjIzNzd9.jg9TdkOXIXsqLI4-Eyq35j__CCv13Ovvvd1htW04nWw")
    @DeleteMapping ("/posts/{postId}/comments/{commentId}")
    @ApiOperation("Delete a comment based on post id & comment id")
    public ResponseEntity<String> deleteComment(@PathVariable("postId") int postId,
                                                @PathVariable("commentId") int commentId,
                                                Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Post does not exist!", HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>("Comment does not exist!", HttpStatus.NOT_FOUND);
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
                return new ResponseEntity<>("Only the author can perform this action!",
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