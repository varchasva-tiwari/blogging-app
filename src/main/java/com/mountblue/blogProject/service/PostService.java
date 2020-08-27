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
        System.out.println("title: "+ post.getTitle());
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

        if(updatedPost == null) {
            return null;
        }

        updatedPost.setTitle(post.getTitle());
        updatedPost.setContent(post.getContent());
        updatedPost.setExcerpt(post.getExcerpt());
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

    public List<Post> search(String keyword) {
        return postRepository.search(keyword);
    }

    public Page<Post> paginatedAndSortedSearch(String keyword, int pageNo, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        return postRepository.paginatedAndSortedSearch(keyword, pageable);
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

    public boolean existsById(int postId) {
        return postRepository.existsById(postId);
    }

}
