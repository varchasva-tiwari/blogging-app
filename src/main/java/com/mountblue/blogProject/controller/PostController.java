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

    private final String JWT_EXAMPLE = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNpZmVyIiwiZXhwIjoxNTk4NzU4Mzc3LCJpYXQiOjE1OTg3MjIzNzd9.jg9TdkOXIXsqLI4-Eyq35j__CCv13Ovvvd1htW04nWw";
    private final String USER_MUST_BE_LOGGED_IN = "You must be logged in to perform this action!";
    private final String POST_REQUIRED_FIELDS_NULL = "Author/Title/Content/Excerpt cannot be null!";
    private final String ONLY_AUTHOR_HAS_PERMISSION = "Only the author can perform this action!";
    private final String INSUFFICIENT_PERMISSIONS = "You do not have permissions to perform this action!";
    private final String POST_DOES_NOT_EXIST_OR_DELETED = "Post does not exist or has been deleted!";
    private final String POST_WRONG_PAGINATION_VALUES = "Incorrect start/limit/sortField/sortOrder value(s)!";
    private final String POST_WRONG_FORMAT = "Post is wrong/wrongly formatted. Please check the provided JSON!";
    private final String POST_KEYWORD_NULL_OR_EMPTY = "Keyword cannot be null or empty!";
    private final String POST_FILTER_FALSE = "Filter cannot be false!";
    private final String POST_KEYWORD_NO_RESULTS = "No results available for given search keyword!";
    private final String POST_FILTER_NO_RESULTS = "No results available for given filters!";
    private final String POST_SAVED = "Post saved successfully!";
    private final String POST_UPDATED = "Post updated successfully!";
    private final String POST_DELETED = "Post deleted successfully!";
    private final String CREATE_POST = "Creates a new post";
    private final String READ_POST = "Returns a specific blog post based on the id";
    private final String READ_ALL_POSTS = "Returns all blog posts";
    private final String UPDATE_POST = "Edits a blog post based on id";
    private final String DELETE_POST = "Deletes a blog post based on id";
    private final String SEARCH_POSTS = "Searches & gets the blog posts having a specific keyword";
    private final String FILTER_POSTS = "Searches & gets the blog posts based on specified filters like author name, date(in YYYY-MM-DD) & tags. Can also search keywords.";

    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = JWT_EXAMPLE)
    @PostMapping ("/posts")
    @ApiOperation(CREATE_POST)
    private ResponseEntity<String> savePost(@RequestBody List<Map<String, ?>> postTags, Principal principal) {
        Post post = null;

        if (principal == null) {
            return new ResponseEntity<>(USER_MUST_BE_LOGGED_IN,
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal)) {
                Map<String, Map> postMap = (Map<String, Map>) postTags.get(0);

                Map<String, String> tagsMap = null;

                if(postTags.size() == 2) {
                    tagsMap = (Map<String, String>) postTags.get(1);
                }

                for(Map.Entry<String, Map> postMapEntry : postMap.entrySet()) {
                    post = new Post(postMapEntry.getValue());

                    if(post.getAuthor() == null || post.getTitle() == null || post.getContent() == null ||
                    post.getExcerpt() == null) {
                        return new ResponseEntity<>(POST_REQUIRED_FIELDS_NULL,
                                HttpStatus.BAD_REQUEST);
                    }

                    post.setAuthor(principal.getName());
                    post.setUserId(userService.getUserId(principal.getName()));

                    post = postService.savePost(post);

                    if(post == null) {
                        return new ResponseEntity<>(POST_WRONG_FORMAT, HttpStatus.BAD_REQUEST);
                    }
                }

                for(Map.Entry<String, String> tagsMapEntry : tagsMap.entrySet()) {
                    List<Integer> newTagIds = tagService.editTags(tagsMapEntry.getValue());
                    postTagService.savePostTag(post.getId(), newTagIds);
                }
            } else {
                return new ResponseEntity<>(INSUFFICIENT_PERMISSIONS,
                        HttpStatus.FORBIDDEN);
            }
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Location", "/posts/"+post.getId());

        return new ResponseEntity<>(POST_SAVED, header, HttpStatus.CREATED);
    }

    @GetMapping("/posts/{id}")
    @ApiOperation(READ_POST)
    private ResponseEntity<?> getPost(@PathVariable("id") int postId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>(POST_DOES_NOT_EXIST_OR_DELETED,
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
    @ApiOperation(READ_ALL_POSTS)
    private ResponseEntity<?> getPosts(@RequestParam(value = "start", defaultValue = "1") Integer pageNo,
                                       @RequestParam(value = "limit", defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                       @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
        List<Post> posts = postService.getPaginatedAndSorted(pageNo, pageSize, sortField, sortOrder);
        List<List<Map>> postTags = postTagService.getPostTags(posts);

        if(posts == null || postTags == null) {
            return new ResponseEntity<String>(POST_WRONG_PAGINATION_VALUES,
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(postTags, HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = JWT_EXAMPLE)
    @PatchMapping("/posts/{id}")
    @ApiOperation(UPDATE_POST)
    private ResponseEntity<String> editPost(@PathVariable("id") int postId,
                                            @RequestBody List<Map<String, ?>> editedPostTags,
                                            Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>(POST_DOES_NOT_EXIST_OR_DELETED,
                    HttpStatus.NOT_FOUND);
        }

        if (principal == null) {
            return new ResponseEntity<>(USER_MUST_BE_LOGGED_IN,
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
                        return new ResponseEntity<>(POST_WRONG_FORMAT,
                                HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity<>(ONLY_AUTHOR_HAS_PERMISSION,
                        HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<String>(POST_UPDATED, HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", required = true, dataType = "String", paramType = "header", example = JWT_EXAMPLE)
    @DeleteMapping(value = "/posts/{id}")
    @ApiOperation(DELETE_POST)
    private ResponseEntity<String> deletePost(@PathVariable("id") int postId, Principal principal) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>(POST_DOES_NOT_EXIST_OR_DELETED, HttpStatus.NOT_FOUND);
        }

        if (principal == null) {
            return new ResponseEntity<>(USER_MUST_BE_LOGGED_IN,
                    HttpStatus.UNAUTHORIZED);
        } else {
            if (isPermitted(principal, postId)) {
                commentService.deleteComments(postId);
                postService.deletePost(postId);
                postTagService.deleteTag(postId);
            } else {
                return new ResponseEntity<>(ONLY_AUTHOR_HAS_PERMISSION,
                        HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<>(POST_DELETED, HttpStatus.OK);
    }

    @GetMapping(value = "/posts", params = "keyword")
    @ApiOperation(SEARCH_POSTS)
    private ResponseEntity<?> search(@RequestParam("keyword") String keyword,
                                     @RequestParam(value = "start", defaultValue = "1") Integer pageNo,
                                     @RequestParam(value = "limit", defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                     @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
        if(keyword == null || keyword.equals("")) {
            return new ResponseEntity<>(POST_KEYWORD_NULL_OR_EMPTY, HttpStatus.BAD_REQUEST);
        }

        Page page = postService.paginatedAndSortedSearch(keyword, pageNo, pageSize, sortField, sortOrder);

        List<Post> posts = page.getContent();

        if(posts.size() == 0) {
            return new ResponseEntity<>(POST_KEYWORD_NO_RESULTS,
                    HttpStatus.NOT_FOUND);
        }

        List<List<Map>> postTags = postTagService.getPostTags(posts);

        return new ResponseEntity<>(postTags, HttpStatus.OK);
    }


   @ApiImplicitParam(name = "filter", required = true, dataType = "boolean", paramType = "query")
   @GetMapping(value = "/posts", params = "filter")
   @ApiOperation(FILTER_POSTS)
   private ResponseEntity<?> filter(@RequestParam(value = "filter") boolean filter,
                                     @RequestParam(value = "search", defaultValue = "") String keyword,
                                     @RequestParam(value = "author", defaultValue = "") String author,
                                     @RequestParam(value = "date", defaultValue = "") String date,
                                     @RequestParam(value = "tags", defaultValue = "") String tags) {
        if(!filter) {
            return new ResponseEntity<>(POST_FILTER_FALSE, HttpStatus.BAD_REQUEST);
        }

        List<Post> posts = postService.searchAndFilterPosts(keyword, author, date, tags);

        if(posts.size() == 0) {
            return new ResponseEntity<>(POST_FILTER_NO_RESULTS, HttpStatus.NOT_FOUND);
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