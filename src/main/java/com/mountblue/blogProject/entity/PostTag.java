package com.mountblue.blogProject.entity;

import com.mountblue.blogProject.PostTagId;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@IdClass(PostTagId.class)
@Table(name = "post_tags")
public class PostTag {
    @Id
    @Column(name = "post_id")
    private int postId;

    @Id
    @Column(name = "tag_id")
    private int tagId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PostTag() {}

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
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
}