package com.mountblue.blogProject.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    private static final String NAME_EMPTY = "Name cannot be empty";
    private static final String EMAIL_EMPTY = "Email cannot be empty";
    private static final String EMAIL_WRONG_FORMAT = "Enter email in proper format";
    private static final String COMMENT_EMPTY = "Comment cannot be empty";
    private static final String COMMENT_EXCEEDED = "Comment cannot exceed 500 characters";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotBlank(message = NAME_EMPTY)
    @Size(max = 30)
    private String name;

    @Column(name = "email")
    @NotBlank(message = EMAIL_EMPTY)
    @Email(message = EMAIL_WRONG_FORMAT)
    private String email;

    @Column(name = "comment")
    @NotBlank(message = COMMENT_EMPTY)
    @Size(max = 500, message = COMMENT_EXCEEDED)
    private String comment;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name="user_id")
    private int userId;

    public Comment() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
