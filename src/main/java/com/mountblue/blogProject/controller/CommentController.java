package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.Comment;
import com.mountblue.blogProject.service.CommentService;
import com.mountblue.blogProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/createComment")
    public String showCommentsForm(@RequestParam("id") int postId, Model model) {
        Comment comment = new Comment();
        comment.setPostId(postId);

        model.addAttribute("newComment", comment);
        return "commentForm";
    }

    @PostMapping("/createComment")
    public String saveComment(@ModelAttribute("newComment") Comment newComment, Principal principal) {
        if(commentService.exists(newComment)) {
            commentService.update(newComment);
        }

        else
        {
            if(principal != null) {
                newComment.setUserId(userService.getId(principal.getName()));
                newComment.setName(principal.getName());
            }

            commentService.create(newComment);
        }

        return "redirect:/readPost?id=" + newComment.getPostId();
    }

    @RequestMapping("/updateComment")
    public String editCommentForm(@ModelAttribute("oldComment") Comment comment, Model model) {
        model.addAttribute("newComment", comment);
        return "commentForm";
    }

    @RequestMapping("/deleteComment")
    public String deleteComment(@RequestParam("postId") int postId, @RequestParam("commentId") int commentId) {
        commentService.delete(postId, commentId);
        return "redirect:/readPost?id=" + postId;
    }
}
