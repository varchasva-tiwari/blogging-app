package com.mountblue.blogProject.service;

import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.repository.PostRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public int create(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        LocalDateTime createdAt = LocalDateTime.now();

        post.setCreatedAt(createdAt);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        return postRepository.getId(createdAt);
    }

    public Post readPost(int id) {
        return postRepository.findById(id).get();
    }

    public List<Post> readPosts() {
       return postRepository.findAll();
    }

    public void update(Post post) {
        Post updatedPost = postRepository.getOne(post.getId());

        updatedPost.setTitle(post.getTitle());
        updatedPost.setContent(post.getContent());
        updatedPost.setExcerpt(post.getExcerpt());
        updatedPost.setAuthor(post.getAuthor());
        updatedPost.setUpdatedAt(LocalDateTime.now());

        postRepository.save(updatedPost);
    }

    public void delete(int id) {
        postRepository.deleteById(id);
    }

    public Page<Post> getPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return postRepository.findAll(pageable);
    }

    public List<Post> getSorted(String sortField, String sortOrder) {
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return postRepository.findAll(sort);
    }

    public Page<Post> getPaginatedAndSorted(int pageNo, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        return postRepository.findAll(pageable);
    }

    public List<Post> getAllByKeyword(String word) {
        return postRepository.getPosts(word);
    }

    public List<Post> filter(String author, String dateTime, String tags) {
        String publishedAt = "";
        List<String> tagNames = new ArrayList<>();

        if(!dateTime.equals("")) {
            publishedAt = dateTime.replace('T', ' ');
        }

        if(!tags.equals("")) {
            tagNames =  Arrays.asList(tags.split(", "));
        }

        return postRepository.filter(author, publishedAt, tagNames);
    }

    public boolean exists(Post post) {
        return postRepository.existsById(post.getId());
    }
}
