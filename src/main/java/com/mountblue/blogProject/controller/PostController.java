package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
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

    @PostMapping ("/posts")
    private ResponseEntity<String> savePost(@RequestBody List<Map<String, ? extends Object>> postTags) {
        Post post = null;
        Map<String, Map> postMap = (Map<String, Map>) postTags.get(0);
        Map<String, String> tagsMap = (Map<String, String>) postTags.get(1);

        for(Map.Entry<String, Map> postMapEntry : postMap.entrySet()) {
            post = postService.savePost(new Post(postMapEntry.getValue()));
        }

        for(Map.Entry<String, String> tagsMapEntry : tagsMap.entrySet()) {
            List<Integer> newTagIds = tagService.editTags(tagsMapEntry.getValue());
            postTagService.savePostTag(post.getId(), newTagIds);
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Location", "/posts/{"+post.getId()+"}");

        return new ResponseEntity<>("Post saved successfully!", header, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    private ResponseEntity<? extends Object> getPost(@PathVariable("id") int postId) {
        if(postService.getPost(postId) == null) {
            return new ResponseEntity<String>("This post has either been deleted or does not exist!", HttpStatus.NOT_FOUND);
        }

        Map<String, Post> postMap = new HashMap<>();
        Map<String, List<Tag>> tagsMap = new HashMap<>();
        Map<String, List<Comment>> commentsMap = new HashMap<>();

        postMap.put("post", postService.getPost(postId));
        tagsMap.put("tags", postTagService.getTags(postId));
        commentsMap.put("comments", commentService.readComments(postId));

        List<Map> post = new ArrayList<>();
        post.add(postMap);
        post.add(tagsMap);
        post.add(commentsMap);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/posts")
    private ResponseEntity<List<List<Map>>> getPosts() {
        return getPaginatedPosts(1, 10,"publishedAt", "asc");
    }

    @GetMapping(value = "/posts", params = {"start", "limit", "sortField", "sortOrder"})
    private ResponseEntity<List<List<Map>>> getPaginatedPosts(@RequestParam(value = "start", required = false) Integer pageNo,
                             @RequestParam(value = "limit", required = false) Integer pageSize,
                             @RequestParam(value = "sortField", required = false) String sortField,
                             @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        List<Post> posts = postService.getPaginatedAndSorted(pageNo, pageSize, sortField, sortOrder);
        List<List<Map>> postTags = postTagService.getPostTags(posts);

        return new ResponseEntity<>(postTags, HttpStatus.OK);
    }

    @GetMapping(value = "/posts", params = {"keyword"})
    private ResponseEntity<List<List<Map>>> searchPosts(@RequestParam("keyword") String keyword) {

        List<Post> posts = postService.search(keyword);
        List<List<Map>> postTags = postTagService.getPostTags(posts);

        return new ResponseEntity<>(postTags, HttpStatus.OK);
    }

    @GetMapping(value = "/posts", params = {"keyword", "start", "limit", "sortField", "sortOrder"})
    private ResponseEntity<List<List<Map>>> paginatedAndSortedSearch(@RequestParam("keyword") String keyword,
                          @RequestParam(value = "start", required = false) Integer pageNo,
                          @RequestParam(value = "limit", required = false) Integer pageSize,
                          @RequestParam(value = "sortField", required = false) String sortField,
                          @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        Page page = postService.paginatedAndSortedSearch(keyword, pageNo, pageSize, sortField, sortOrder);

        List<Post> posts = page.getContent();
        List<List<Map>> postTags = postTagService.getPostTags(posts);

        return new ResponseEntity<>(postTags, HttpStatus.OK);
    }

    @PatchMapping("/posts/{id}")
    private ResponseEntity<String> editPost(@PathVariable("id") int postId, @RequestBody List<Map<String, ? extends Object>> editedPost) {
        Post updatedPost = null;

        Map<String, Map> postMap = (Map<String, Map>) editedPost.get(0);

        for(Map.Entry<String, Map> postEntry : postMap.entrySet()) {
            updatedPost = postService.editPost(new Post(postEntry.getValue()));

            if(updatedPost == null) {
                return new ResponseEntity<>("Could find post with given id!", HttpStatus.NOT_FOUND);
            }
        }

        if(editedPost.size() == 2) {
            Map<String, String> tagsMap = (Map<String, String>) editedPost.get(1);

            for(Map.Entry<String, String> tagsEntry : tagsMap.entrySet()) {
                List<Integer> newTagIds = tagService.editTags(tagsEntry.getValue());
                postTagService.savePostTag(postId, newTagIds);
            }
        }

        return new ResponseEntity<String>("Post updated successfully!", HttpStatus.OK);
    }

    @DeleteMapping(value = "/posts/{id}")
    private ResponseEntity<String> delete(@PathVariable("id") int postId) {
        if(!postService.existsById(postId)) {
            return new ResponseEntity<>("Incorrect id!", HttpStatus.NOT_FOUND);
        }

        commentService.deleteComments(postId);
        postService.deletePost(postId);
        postTagService.deleteTag(postId);

        return new ResponseEntity<>("Post deleted successfully!", HttpStatus.OK);
    }

    /*@RequestMapping("/filter")
    private String filter(@RequestParam(value = "start", required = false) Integer pageNo,
                          @RequestParam(value = "limit", required = false) Integer pageSize,
                          @RequestParam(value = "search", required = false) String keyword,
                          @RequestParam(value = "author", required = false) String author,
                          @RequestParam(value = "dateTime", required = false) String dateTime,
                          @RequestParam(value = "tags", required = false) String tags,
                          Model model) {

        Page page = null;

        if(keyword != null) {
            page = postService.searchAndFilterPosts(pageNo, pageSize, keyword, author, dateTime, tags);
        } else {
            page = postService.filterPosts(pageNo, pageSize, author, dateTime, tags);
        }

        List<Post> posts = page.getContent();

        LinkedHashMap<Post,List<Tag>> postTags = postTagService.getPostTags(posts);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentTotalPosts", page.getNumberOfElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalPosts", page.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("author", author);
        model.addAttribute("dateTime", dateTime);
        model.addAttribute("tags", tags);
        model.addAttribute("postTags", postTags);

        return "blogs";
    }*/
}