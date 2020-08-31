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

    private final String JWT_EXAMPLE = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNpZmVyIiwiZXhwIjoxNTk4NzU4Mzc3LCJpYXQiOjE1OTg3MjIzNzd9.jg9TdkOXIXsqLI4-Eyq35j__CCv13Ovvvd1htW04nWw";
    private final String POST_DOES_NOT_EXIST_OR_DELETED = "Post does not exist or has been deleted!";
    private final String COMMENT_DOES_NOT_EXIST_OR_DELETED = "Comment does not exist or has been deleted!";
    private final String COMMENT_REQUIRED_FIELDS_NULL = "Name/Email/Comment cannot be null!";
    private final String COMMENT_WRONG_FORMAT = "Comment is wrong/wrongly formatted. Please check the provided JSON!";
    private final String COMMENT_POST_MISMATCH = "The comment does not belong to this post!";
    private final String USER_MUST_BE_LOGGED_IN = "You must be logged in to perform this action!";
    private final String ONLY_AUTHOR_HAS_PERMISSION = "Only the author can perform this action!";
    private final String COMMENT_SAVED = "Comment saved successfully!";
    private final String COMMENT_UPDATED = "Comment updated successfully!";
    private final String COMMENT_DELETED = "Comment deleted successfully!";
    private final String CREATE_COMMENT = "Creates a new comment";
    private final String READ_COMMENT = "Returns a specific comment based on post id & comment id";
    private final String READ_ALL_COMMENTS = "Returns all comments based on post id";
    private final String UPDATE_COMMENT = "Edits a comment based on post id & comment id";
    private final String DELETE_COMMENT = "Delete a comment based on post id & comment id";

    @PostMapping("/posts/{postId}/comments")
    @ApiOperation(CREATE_COMMENT)
    public ResponseEntity<String> saveComment(@PathVariable("postId") int postId,
                                              @RequestBody Map<String, Comment> commentMap,
                                              Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<String>(POST_DOES_NOT_EXIST_OR_DELETED,
                    HttpStatus.NOT_FOUND);
        }

        Comment comment = null;
        for(Map.Entry<String, Comment> commentEntry : commentMap.entrySet()) {
            comment = commentEntry.getValue();
        }

        if(comment.getName() == null || comment.getEmail() == null || comment.getComment() == null) {
            return new ResponseEntity<>(COMMENT_REQUIRED_FIELDS_NULL, HttpStatus.BAD_REQUEST);
        }

        comment.setPostId(postId);

        if(principal != null) {
            comment.setUserId(userService.getUserId(principal.getName()));
        }

        if(commentService.saveComment(comment) == null) {
            return new ResponseEntity<>(COMMENT_WRONG_FORMAT,
                    HttpStatus.BAD_REQUEST);
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Location", "/posts/" + postId + "/comments/" + comment.getId());

        return new ResponseEntity<>(COMMENT_SAVED, header, HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments/{commentId}")
    @ApiOperation(READ_COMMENT)
    public ResponseEntity<?> getComment(@PathVariable("postId") int postId,
                                        @PathVariable("commentId") int commentId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>(POST_DOES_NOT_EXIST_OR_DELETED,
                    HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>(COMMENT_DOES_NOT_EXIST_OR_DELETED,
                    HttpStatus.NOT_FOUND);
        }

        if(commentService.getPostId(commentId) != postId) {
            return new ResponseEntity<>(COMMENT_POST_MISMATCH,
                    HttpStatus.NOT_FOUND);
        }

        Comment comment = commentService.getComment(postId, commentId);

        Map<String, Comment> commentMap = new HashMap<>();
        commentMap.put("comment", comment);

        return new ResponseEntity<>(commentMap, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}/comments")
    @ApiOperation(READ_ALL_COMMENTS)
    public ResponseEntity<?> getComments(@PathVariable("postId") int postId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>(POST_DOES_NOT_EXIST_OR_DELETED,
                    HttpStatus.NOT_FOUND);
        }

        Map<String, List<Comment>> commentsMap = new HashMap<>();
        commentsMap.put("comments", commentService.getComments(postId));

        return new ResponseEntity<>(commentsMap, HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = JWT_EXAMPLE)
    @PatchMapping("/posts/{postId}/comments/{commentId}")
    @ApiOperation(UPDATE_COMMENT)
    public ResponseEntity<String> editComment(@PathVariable("postId") int postId,
                                              @PathVariable("commentId") int commentId,
                                              @RequestBody Map<String, Comment> commentMap,
                                              Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>(POST_DOES_NOT_EXIST_OR_DELETED, HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>(COMMENT_DOES_NOT_EXIST_OR_DELETED, HttpStatus.NOT_FOUND);
        }

        if(commentService.getPostId(commentId) != postId) {
            return new ResponseEntity<>(COMMENT_POST_MISMATCH,
                    HttpStatus.NOT_FOUND);
        }

        if (principal == null) {
            return new ResponseEntity<>(USER_MUST_BE_LOGGED_IN,
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal, postId)) {
                Comment comment = null;

                for (Map.Entry<String, Comment> commentEntry : commentMap.entrySet()) {
                    comment = commentEntry.getValue();
                }

                if(comment.getComment() == null) {
                    return new ResponseEntity<>(COMMENT_REQUIRED_FIELDS_NULL, HttpStatus.BAD_REQUEST);
                }

                comment.setId(commentId);
                comment.setPostId(postId);

                Comment updatedComment = commentService.editComment(comment);

                if (updatedComment == null) {
                    return new ResponseEntity<String>(COMMENT_WRONG_FORMAT,
                            HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(ONLY_AUTHOR_HAS_PERMISSION,
                        HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>(COMMENT_UPDATED, HttpStatus.OK);
    }


    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = JWT_EXAMPLE)
    @DeleteMapping ("/posts/{postId}/comments/{commentId}")
    @ApiOperation(DELETE_COMMENT)
    public ResponseEntity<String> deleteComment(@PathVariable("postId") int postId,
                                                @PathVariable("commentId") int commentId,
                                                Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>(POST_DOES_NOT_EXIST_OR_DELETED, HttpStatus.NOT_FOUND);
        }

        if(!commentService.existsById(commentId)) {
            return new ResponseEntity<>(COMMENT_DOES_NOT_EXIST_OR_DELETED, HttpStatus.NOT_FOUND);
        }

        if(commentService.getPostId(commentId) != postId) {
            return new ResponseEntity<>(COMMENT_POST_MISMATCH,
                    HttpStatus.NOT_FOUND);
        }

        if (principal == null) {
            return new ResponseEntity<>(USER_MUST_BE_LOGGED_IN,
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal, postId)) {
                commentService.deleteComment(postId, commentId);
            } else {
                return new ResponseEntity<>(ONLY_AUTHOR_HAS_PERMISSION,
                        HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>(COMMENT_DELETED, HttpStatus.OK);
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