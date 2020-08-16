package com.mountblue.blogProject.service;

import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public int createPost(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        LocalDateTime createdAt = LocalDateTime.now();

        post.setCreatedAt(createdAt);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        return postRepository.findIdByCreatedAt(createdAt);
    }

    public Post readPost(int id) {
        return postRepository.findById(id).get();
    }

    public List<Post> readPosts() {
       return postRepository.findAll();
    }

    public void updatePost(Post post) {
        Post updatedPost = postRepository.getOne(post.getId());

        updatedPost.setTitle(post.getTitle());
        updatedPost.setContent(post.getContent());
        updatedPost.setAuthor(post.getAuthor());
        updatedPost.setUpdatedAt(LocalDateTime.now());

        postRepository.save(updatedPost);
    }

    public void deletePost(int id) {
        postRepository.deleteById(id);
    }

    public Page<Post> findPaginated(int pageNo, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        return postRepository.findAll(pageable);
    }
}
