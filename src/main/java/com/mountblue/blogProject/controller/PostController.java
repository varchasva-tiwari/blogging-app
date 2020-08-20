package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UserService userService;

    @GetMapping("/showPostForm")
    private String showPostForm(Model model) {
        String newTags = null;
        model.addAttribute("post", new Post());
        model.addAttribute("oldTags", tagService.read());
        model.addAttribute("newTags", newTags);
        return "postForm";
    }

    @PostMapping ("/createPost")
    private String createOrUpdatePost(@ModelAttribute("post") Post post, @ModelAttribute("newTags") String tags, Principal principal, Model model) {
            if(postService.exists(post)) {
                post.setAuthor(principal.getName());
                postService.update(post);
                List<Integer> newTagIds = tagService.update(tags);
                postTagService.create(post.getId(), newTagIds);
            }

            else {
                post.setAuthor(principal.getName());
                post.setUserId(userService.getId(principal.getName()));
                int postId = postService.create(post);

                List<Integer> newTagIds = tagService.update(tags);
                postTagService.create(post.getId(), newTagIds);
            }

        return "redirect:/readPost?id="+post.getId();
    }

    @RequestMapping("/readPost")
    private String readPost(@RequestParam("id")int postId, Model model) {
        model.addAttribute("post", postService.readPost(postId));
        model.addAttribute("tags", postTagService.readTags(postId));
        model.addAttribute("comments", commentService.read(postId));
        List<Comment> comments = commentService.read(postId);

        return "blogPost";
    }

    @RequestMapping("/readPosts")
    private String readPosts(Model model) {
        return getPaginatedPosts(1, 10,"publishedAt", "asc", model);
    }

    @RequestMapping(value = "/readPosts", params = {"start", "limit", "sortField", "sortOrder"})
    private String getPaginatedPosts(@RequestParam(value = "start", required = false) Integer pageNo,
                             @RequestParam(value = "limit", required = false) Integer pageSize,
                             @RequestParam(value = "sortField", required = false) String sortField,
                             @RequestParam(value = "sortOrder", required = false) String sortOrder,
                                Model model) {

        Page page = null;

        List<Post> posts = new ArrayList<>();

        if(pageNo != null) {
            if(sortField != null) {
                page = postService.getPaginatedAndSorted(pageNo, pageSize, sortField, sortOrder);
            }
            else {
                page = postService.getPaginated(pageNo, pageSize);
            }
            posts = page.getContent();
        }

        else {
            if(sortField != null) {
                posts = postService.getSorted(sortField, sortOrder);
            }
        }

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentTotalPosts", page.getNumberOfElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalPosts", page.getTotalElements());
        model.addAttribute("sortField", sortField);

        LinkedHashMap<Post,List<Tag>> postTags = postTagService.getPostTags(posts);

        model.addAttribute("postTags", postTags);
        model.addAttribute("posts", posts);

        return "blogs";
    }

    @RequestMapping(value = "/readPosts", params = "search")
    private String searchPosts(@RequestParam("search") String keyword, Model model) {
        List<Post> posts = postService.getAllByKeyword(keyword);
        LinkedHashMap<Post,List<Tag>> postTags = postTagService.getPostTags(posts);

        model.addAttribute("postTags", postTags);
        model.addAttribute("posts", posts);
        return "blogs";
    }

    @RequestMapping("/updatePost")
    private String updatePostForm(@ModelAttribute("post") Post post, Model model) {
        List<Tag> newTags = postTagService.readTags(post.getId());

        String newTagNames = "";
        for(Tag tag: newTags) {
            newTagNames = newTagNames + tag.getName() + ", ";
        }

        model.addAttribute("post", post);
        model.addAttribute("oldTags", tagService.read());
        model.addAttribute("newTags", newTagNames);

        return "postForm";
    }

    @RequestMapping("/deletePost")
    private String deletePost(@RequestParam("id") int postId) {
        commentService.deleteComments(postId);
        postService.delete(postId);
        postTagService.delete(postId);
        return "redirect:/readPosts";
    }

    @RequestMapping("/filter")
    private String filter(@RequestParam(value = "author", required = false) String author,
                          @RequestParam(value = "dateTime", required = false) String dateTime,
                          @RequestParam(value = "tags", required = false) String tags,
                          Model model) {
        model.addAttribute("posts", postService.filter(author, dateTime, tags));
        return "blogs";
    }
}