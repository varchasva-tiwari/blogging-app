package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@Api(description = "Set of endpoints for Creating, Retrieving, Updating, Searching, Filtering and Deleting of Posts")
@RequestMapping("/blogApp")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostTagService postTagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNpZmVyIiwiZXhwIjoxNTk4NzU4Mzc3LCJpYXQiOjE1OTg3MjIzNzd9.jg9TdkOXIXsqLI4-Eyq35j__CCv13Ovvvd1htW04nWw")
    @PostMapping ("/posts")
    @ApiOperation("Creates a new post")
    private ResponseEntity<String> savePost(@RequestBody List<Map<String, ?>> postTags, Principal principal) {
        Post post = null;

        if (principal == null) {
            return new ResponseEntity<>("You must be logged in to perform this action!",
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal)) {
                if(postTags.size() < 1) {
                    return new ResponseEntity<>("Request body cannot be empty!", HttpStatus.BAD_REQUEST);
                }

                Map<String, Map> postMap = (Map<String, Map>) postTags.get(0);

                Map<String, String> tagsMap = null;

                if(postTags.size() == 2) {
                    tagsMap = (Map<String, String>) postTags.get(1);
                }

                for(Map.Entry<String, Map> postMapEntry : postMap.entrySet()) {
                    post = new Post(postMapEntry.getValue());

                    if(post.getAuthor() == null || post.getTitle() == null || post.getContent() == null ||
                    post.getExcerpt() == null) {
                        return new ResponseEntity<>("Author/Title/Content/Excerpt cannot be null!",
                                HttpStatus.BAD_REQUEST);
                    }

                    post.setAuthor(principal.getName());
                    post.setUserId(userService.getUserId(principal.getName()));

                    post = postService.savePost(post);
                }

                for(Map.Entry<String, String> tagsMapEntry : tagsMap.entrySet()) {
                    List<Integer> newTagIds = tagService.editTags(tagsMapEntry.getValue());
                    postTagService.savePostTag(post.getId(), newTagIds);
                }
            } else {
                return new ResponseEntity<>("You do not have permissions to perform this action!",
                        HttpStatus.FORBIDDEN);
            }
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Location", "/posts/"+post.getId());

        return new ResponseEntity<>("Post saved successfully!", header, HttpStatus.CREATED);
    }

    @GetMapping("/posts/{id}")
    @ApiOperation("Returns a specific blog post based on the id")
    private ResponseEntity<?> getPost(@PathVariable("id") int postId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Post does not exist or has been deleted!",
                    HttpStatus.NOT_FOUND);
        }

        Map<String, Post> postMap = new HashMap<>();
        Map<String, List<Tag>> tagsMap = new HashMap<>();
        Map<String, List<Comment>> commentsMap = new HashMap<>();

        postMap.put("post", postService.getPost(postId));
        tagsMap.put("tags", postTagService.getTags(postId));
        commentsMap.put("comments", commentService.getComments(postId));

        List<Map> post = new ArrayList<>();
        post.add(postMap);
        post.add(tagsMap);
        post.add(commentsMap);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping(value = "/posts")
    @ApiOperation("Returns all blog posts")
    private ResponseEntity<?> getPosts(@RequestParam(value = "start", defaultValue = "1") Integer pageNo,
                                       @RequestParam(value = "limit", defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                       @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
        List<Post> posts = postService.getPaginatedAndSorted(pageNo, pageSize, sortField, sortOrder);
        List<List<Map>> postTags = postTagService.getPostTags(posts);

        if(posts == null || postTags == null) {
            return new ResponseEntity<String>("Posts could not be retrieved due to server issues! Please try again later!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(postTags, HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNpZmVyIiwiZXhwIjoxNTk4NzU4Mzc3LCJpYXQiOjE1OTg3MjIzNzd9.jg9TdkOXIXsqLI4-Eyq35j__CCv13Ovvvd1htW04nWw")
    @PatchMapping("/posts/{id}")
    @ApiOperation("Edits a blog post based on id")
    private ResponseEntity<String> editPost(@PathVariable("id") int postId,
                                            @RequestBody List<Map<String, ?>> editedPostTags,
                                            Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Post does not exist or has been deleted!",
                    HttpStatus.NOT_FOUND);
        }

        if (principal == null) {
            return new ResponseEntity<>("You need to be logged in to perform this action!",
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal, postId)) {
                Post updatedPost = null;

                Map<String, Map> postMap = (Map<String, Map>) editedPostTags.get(0);

                for(Map.Entry<String, Map> postEntry : postMap.entrySet()) {
                    updatedPost = new Post(postEntry.getValue());

                    updatedPost.setId(postId);
                    updatedPost = postService.editPost(updatedPost);

                    if(updatedPost == null) {
                        return new ResponseEntity<>("The post could not be updated due to server issues! Please try again later!",
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }

                if(editedPostTags.size() == 2) {
                    Map<String, String> tagsMap = (Map<String, String>) editedPostTags.get(1);

                    for(Map.Entry<String, String> tagsEntry : tagsMap.entrySet()) {
                        List<Integer> newTagIds = tagService.editTags(tagsEntry.getValue());
                        postTagService.savePostTag(postId, newTagIds);
                    }
                }
            } else {
                return new ResponseEntity<>("Only the author can perform this action!",
                        HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<String>("Post updated successfully!", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNpZmVyIiwiZXhwIjoxNTk4NzU4Mzc3LCJpYXQiOjE1OTg3MjIzNzd9.jg9TdkOXIXsqLI4-Eyq35j__CCv13Ovvvd1htW04nWw")
    @DeleteMapping(value = "/posts/{id}")
    @ApiOperation("Deletes a blog post based on id")
    private ResponseEntity<String> deletePost(@PathVariable("id") int postId, Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Post does not exist!", HttpStatus.NOT_FOUND);
        }

        if (principal == null) {
            return new ResponseEntity<>("You need to be logged in to perform this action!",
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal, postId)) {
                commentService.deleteComments(postId);
                postService.deletePost(postId);
                postTagService.deleteTag(postId);
            } else {
                return new ResponseEntity<>("Only the author can perform this action!",
                        HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<>("Post deleted successfully!", HttpStatus.OK);
    }

    @GetMapping(value = "/posts", params = "keyword")
    @ApiOperation("Searches & gets the blog posts having a specific keyword")
    private ResponseEntity<?> search(@RequestParam("keyword") String keyword,
                                     @RequestParam(value = "start", defaultValue = "1") Integer pageNo,
                                     @RequestParam(value = "limit", defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                     @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
        if(keyword == null || keyword.equals("")) {
            return new ResponseEntity<>("Keyword cannot be null or empty!", HttpStatus.BAD_REQUEST);
        }

        Page page = postService.paginatedAndSortedSearch(keyword, pageNo, pageSize, sortField, sortOrder);

        if(page == null) {
            return new ResponseEntity<>("Posts could not be retrieved due to server issues! Please try again later!",
                    HttpStatus.NOT_FOUND);
        }

        List<Post> posts = page.getContent();

        List<List<Map>> postTags = postTagService.getPostTags(posts);

        return new ResponseEntity<>(postTags, HttpStatus.OK);
    }


   @ApiImplicitParam(name = "filter", required = true, dataType = "boolean", paramType = "query")
   @GetMapping(value = "/posts", params = "filter")
   @ApiOperation("Searches & gets the blog posts based on specified filters like author name, date(in YYYY-MM-DD) & tags")
   private ResponseEntity<?> filter(@RequestParam(value = "filter") boolean filter,
                                     @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                     @RequestParam(value = "author", defaultValue = "") String author,
                                     @RequestParam(value = "date", defaultValue = "") String date,
                                     @RequestParam(value = "tags", defaultValue = "") String tags) {
        if(!filter) {
            return new ResponseEntity<>("Filter cannot be false or null!", HttpStatus.BAD_REQUEST);
        }

        List<Post> posts = postService.searchAndFilterPosts(keyword, author, date, tags);

        if(posts == null) {
            return new ResponseEntity<>("No results available for given filters!", HttpStatus.NOT_FOUND);
        }

        List<List<Map>> postTags = postTagService.getPostTags(posts);

        return new ResponseEntity<>(postTags, HttpStatus.OK);
    }

    public boolean isPermitted(Principal principal) {
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_admin");
        SimpleGrantedAuthority authorAuthority = new SimpleGrantedAuthority("ROLE_author");

        if (userService.loadUserByUsername(principal.getName()).
                getAuthorities().contains(adminAuthority) ||
                userService.loadUserByUsername(principal.getName()).
                getAuthorities().contains(authorAuthority)) {
            return true;
        }

        return false;
    }

    public boolean isPermitted(Principal principal, int postId) {
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_admin");
        SimpleGrantedAuthority authorAuthority = new SimpleGrantedAuthority("ROLE_author");


        if (userService.loadUserByUsername(principal.getName()).
                getAuthorities().contains(adminAuthority)
                || (userService.loadUserByUsername(principal.getName()).getAuthorities().
                contains(authorAuthority) && postService.getAuthor(postId).equals(principal.getName()))) {
            return true;
        }

        return false;
    }
}