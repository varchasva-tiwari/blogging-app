package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.service.CommentService;
import com.mountblue.blogProject.service.PostService;
import com.mountblue.blogProject.service.PostTagService;
import com.mountblue.blogProject.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostTagService postTagService;

    @Autowired
    private CommentService commentService;

    @RequestMapping("/home")
    private String showHome() {
        return "home";
    }

    @GetMapping("/createPost")
    private String showForm(Model model) {
        String tags = null;
        model.addAttribute("post", new Post());
        model.addAttribute("oldTags", tagService.readTags());
        model.addAttribute("newTags", tags);
        return "postForm";
    }

    @PostMapping ("/createPost")
    private String createPost(@ModelAttribute("post") Post post, @ModelAttribute("newTags") String tags) {
        int postId = postService.createPost(post);

        List<Integer> newTagIds = tagService.updateTags(tags);

        postTagService.createPostTag(post.getId(), newTagIds);

        return "redirect:/createPost";
    }

    @RequestMapping("/readPost")
    private String readPost(@RequestParam("id")int postId, Model model) {
        model.addAttribute("post", postService.readPost(postId));
        model.addAttribute("tags", postTagService.readTagsByPost(postId));
        model.addAttribute("comments", commentService.readComments(postId));
        return "blogPost";
    }

    @RequestMapping("/readPosts")
    private String readPosts(Model model) {
        model.addAttribute("posts", postService.readPosts());
        return "blogs";
    }

    @RequestMapping("/updatePost")
    private String updatePost(@ModelAttribute("post") Post post) {
        postService.updatePost(post);
        return "redirect:/readPost?id=" + post.getId();
    }

    @RequestMapping("/deletePost")
    private String deletePost(@RequestParam("id") int postId) {
        postService.deletePost(postId);
        return "redirect:/readPosts";
    }
}