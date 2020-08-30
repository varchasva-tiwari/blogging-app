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

    public Post savePost(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        LocalDateTime createdAt = LocalDateTime.now();

        post.setCreatedAt(createdAt);
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public Post getPost(int id) {
        return postRepository.findById(id);
    }

    public Post editPost(Post post) {
        Post updatedPost = postRepository.findById(post.getId());

        if(post.getTitle() != null) {
            updatedPost.setTitle(post.getTitle());
        }

        if(post.getAuthor() != null) {
            updatedPost.setAuthor(post.getAuthor());
        }


        if(post.getExcerpt() != null) {
            updatedPost.setExcerpt(post.getExcerpt());
        }

        if(post.getContent() != null) {
            updatedPost.setContent(post.getContent());
        }

        updatedPost.setAuthor(post.getAuthor());
        updatedPost.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(updatedPost);
    }

    public void deletePost(int id) {
        postRepository.deleteById(id);
    }

    public List<Post> getPaginatedAndSorted(int pageNo, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);

        Page page = postRepository.findAll(pageable);

        List<Post> posts = page.getContent();

        return posts;
    }

    public Page<Post> paginatedAndSortedSearch(String keyword, int pageNo, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        return postRepository.paginatedAndSortedSearch(keyword, pageable);
    }

    public List<Post> searchAndFilterPosts(String keyword, String author, String date, String tags) {
        List<String> tagNames = new ArrayList<>();

        if(!tags.equals("")) {
            tagNames =  Arrays.asList(tags.split(", "));
        }

        return postRepository.searchAndFilter(keyword, author, date, tagNames);
    }

    public boolean exists(Post post) {
        return postRepository.existsById(post.getId());
    }

    public boolean existsById(int postId) {
        return postRepository.existsById(postId);
    }

    public String getAuthor(int postId) {
        return postRepository.getAuthorById(postId);
    }
}
