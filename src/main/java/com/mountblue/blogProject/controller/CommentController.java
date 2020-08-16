package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CommentController {
    @Autowired
    CommentService commentService;

    @GetMapping("/createComment")
    public String showCommentForm(@RequestParam("id") int postId, Model model) {
        Comment comment = new Comment();
        comment.setPostId(postId);

        model.addAttribute("comment", comment);
        return "commentForm";
    }

    @PostMapping("/createComment")
    public String createComment(@ModelAttribute("newComment") Comment comment) {
        commentService.createComment(comment);
        return "redirect:/readPost?id=" + comment.getPostId();
    }

    @GetMapping("/updateComment")
    public String updateComment(@ModelAttribute("comment") Comment comment, Model model) {
        model.addAttribute("comment", comment);
        return "commentForm";
    }

    @PostMapping("/updateComment")
    public String updateComment(@ModelAttribute("comment") Comment comment) {
        commentService.updateComment(comment);
        return "home";
    }

    @RequestMapping("/deleteComment")
    public String deleteComment(@RequestParam("postId") int postId, @RequestParam("commentId") int commentId) {
        commentService.deleteComment(postId, commentId);
        return "redirect:/readPost?id=" + postId;
    }

}
