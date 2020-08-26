package com.mountblue.blogProject.service;

import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void savePost(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        LocalDateTime createdAt = LocalDateTime.now();

        post.setCreatedAt(createdAt);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public Post getPost(int id) {
        return postRepository.findById(id).get();
    }

    public void editPost(Post post) {
        Post updatedPost = postRepository.getOne(post.getId());

        updatedPost.setTitle(post.getTitle());
        updatedPost.setContent(post.getContent());
        updatedPost.setExcerpt(post.getExcerpt());
        updatedPost.setAuthor(post.getAuthor());
        updatedPost.setUpdatedAt(LocalDateTime.now());

        postRepository.save(updatedPost);
    }

    public void deletePost(int id) {
        postRepository.deleteById(id);
    }

    public Page<Post> getPaginatedAndSorted(int pageNo, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        return postRepository.findAll(pageable);
    }

    public Page<Post> searchPosts(int pageNo, int pageSize, String sortField, String sortOrder, String word) {
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        return postRepository.search(word, pageable);
    }

    public Page<Post> filterPosts(int pageNo, int pageSize, String author, String dateTime, String tags) {
        String publishedAt = "";
        List<String> tagNames = new ArrayList<>();

        if(!dateTime.equals("")) {
            publishedAt = dateTime.replace('T', ' ');
        }

        if(!tags.equals("")) {
            tagNames =  Arrays.asList(tags.split(", "));
        }

        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        return postRepository.filter(author, publishedAt, tagNames, pageable);
    }

    public Page<Post> searchAndFilterPosts(int pageNo, int pageSize, String keyword, String author, String dateTime, String tags) {
        String publishedAt = "";
        List<String> tagNames = new ArrayList<>();

        if(!dateTime.equals("")) {
            publishedAt = dateTime.replace('T', ' ');
        }

        if(!tags.equals("")) {
            tagNames =  Arrays.asList(tags.split(", "));
        }

        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        return postRepository.searchAndFilter(keyword, author, publishedAt, tagNames, pageable);
    }

    public boolean exists(Post post) {
        return postRepository.existsById(post.getId());
    }

}
