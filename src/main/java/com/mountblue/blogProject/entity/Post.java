package com.mountblue.blogProject.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    private static final String TITLE_EMPTY = "Title cannot be empty";
    private static final String EXCERPT_EMPTY = "Excerpt cannot be empty";
    private static final String CONTENT_EMPTY = "Content cannot be empty";
    private static final String AUTHOR_EMPTY = "Author Name cannot be empty";
    private static final String TITLE_EXCEEDED = "Title cannot exceed 50 characters";
    private static final String EXCERPT_EXCEEDED = "Excerpt cannot exceed 300 characters";
    private static final String CONTENT_EXCEEDED = "Content cannot exceed 800 characters";
    private static final String AUTHOR_EXCEEDED = "Author Name cannot exceed 30 characters";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = TITLE_EMPTY)
    @Size(max = 50, message = TITLE_EXCEEDED)
    private String title;

    @Column(name = "excerpt", nullable = false)
    @NotBlank(message = EXCERPT_EMPTY)
    @Size(max = 300, message = EXCERPT_EXCEEDED)
    private String excerpt;

    @Column(name = "content", nullable = false)
    @NotBlank(message = CONTENT_EMPTY)
    @Size(max = 800, message = CONTENT_EXCEEDED)
    private String content;

    @Column(name = "author", nullable = false)
    @NotBlank(message = AUTHOR_EMPTY)
    @Size(max = 30, message = AUTHOR_EXCEEDED)
    private String author;

    @Column(name = "published_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishedAt;

    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    public Post() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Boolean isPublished() {
        return isPublished;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
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
