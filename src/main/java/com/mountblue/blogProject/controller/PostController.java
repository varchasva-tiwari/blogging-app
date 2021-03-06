package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;

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

    @PostMapping ("/createPost")
    private String saveOrUpdatePost(@ModelAttribute("post") Post post,
                                  @ModelAttribute("newTags") String tags,
                                  Principal principal, Model model) {
            if(postService.exists(post)) {
                post.setAuthor(principal.getName());
                postService.editPost(post);
            } else {
                post.setAuthor(principal.getName());
                post.setUserId(userService.getUserId(principal.getName()));
                postService.savePost(post);
            }

        List<Integer> newTagIds = tagService.editTags(tags);
        postTagService.savePostTag(post.getId(), newTagIds);

        return "redirect:/readPost?id="+post.getId();
    }

    @GetMapping("/posts/{id}")
    private ResponseEntity<Post> getPostById(@PathVariable("id")int postId) {
        Post post = postService.getPost(postId);
        List<Tag> tags = postTagService.getTags(postId);
        List<Comment> comments = commentService.readComments(postId);

        return new ResponseEntity<Post>(post, HttpStatus.OK);
    }

    @RequestMapping("/readPosts")
    private String getPosts(Model model) {
        return getPaginatedPosts(1, 10,"publishedAt", "asc", model);
    }

    @RequestMapping(value = "/readPosts", params = {"start", "limit", "sortField", "sortOrder"})
    private String getPaginatedPosts(@RequestParam(value = "start", required = false) Integer pageNo,
                             @RequestParam(value = "limit", required = false) Integer pageSize,
                             @RequestParam(value = "sortField", required = false) String sortField,
                             @RequestParam(value = "sortOrder", required = false) String sortOrder,
                                Model model) {

        Page page = postService.getPaginatedAndSorted(pageNo, pageSize, sortField, sortOrder);

        List<Post> posts = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentTotalPosts", page.getNumberOfElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalPosts", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("keyword", null);

        LinkedHashMap<Post,List<Tag>> postTags = postTagService.readPostTags(posts);

        model.addAttribute("postTags", postTags);

        return "blogs";
    }

    @RequestMapping(value = "/searchPosts")
    private String getPaginatedSearch(@RequestParam("search") String keyword,
                          @RequestParam(value = "start", required = false) Integer pageNo,
                          @RequestParam(value = "limit", required = false) Integer pageSize,
                          @RequestParam(value = "sortField", required = false) String sortField,
                          @RequestParam(value = "sortOrder", required = false) String sortOrder,
                          Model model) {

        Page page = postService.searchPosts(pageNo, pageSize, sortField, sortOrder, keyword);

        List<Post> posts = page.getContent();

        LinkedHashMap<Post,List<Tag>> postTags = postTagService.readPostTags(posts);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentTotalPosts", page.getNumberOfElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalPosts", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("postTags", postTags);
        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);

        return "blogs";
    }

    @RequestMapping("/updatePost")
    private String updateForm(@ModelAttribute("post") Post post, Model model) {
        List<Tag> newTags = postTagService.getTags(post.getId());

        String newTagNames = "";
        for(Tag tag: newTags) {
            newTagNames = newTagNames + tag.getName() + ", ";
        }

        model.addAttribute("post", post);
        model.addAttribute("oldTags", tagService.getTags());
        model.addAttribute("newTags", newTagNames);

        return "postForm";
    }

    @RequestMapping("/deletePost")
    private String deletePost(@RequestParam("id") int postId) {
        commentService.deleteComments(postId);
        postService.deletePost(postId);
        postTagService.deleteTag(postId);
        return "redirect:/readPosts";
    }

    @RequestMapping("/filter")
    private String filterPost(@RequestParam(value = "start", required = false) Integer pageNo,
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

        LinkedHashMap<Post,List<Tag>> postTags = postTagService.readPostTags(posts);

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
    }
}