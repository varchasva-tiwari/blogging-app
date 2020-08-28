package com.mountblue.blogProject.service;

import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.PostTag;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.repository.PostTagRepository;
import com.mountblue.blogProject.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostTagService {
    @Autowired
    private PostService postService;

    @Autowired
    private PostTagService postTagService;

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private TagRepository tagRepository;

    public void savePostTag(int postId, List<Integer> tagIds) {
        tagIds.forEach(tagId -> {
            if(postTagRepository.containsTag(postId, tagId).isEmpty())
                postTagRepository.savePostTag(postId, tagId, LocalDateTime.now(), LocalDateTime.now());
        });
    }

    public List<Tag> getTags(int postId) {
         List<PostTag> postTags = postTagRepository.getTags(postId);
         List<Tag> tags = new ArrayList<>();

         postTags.forEach((postTag) -> {
             tags.add(tagRepository.findById(postTag.getTagId()).get());
         });

         return tags;
    }

    public List<List<Map>> getPostTags(List<Post> posts) {
        List<List<Map>> postTags = new ArrayList<>();

        for(Post post: posts) {
            Map<String, Post> postMap = new HashMap<>();
            Map<String, List<Tag>> tagsMap = new HashMap<>();

            postMap.put("post", postService.getPost(post.getId()));
            tagsMap.put("tags", postTagService.getTags(post.getId()));

            List<Map> postTag = new ArrayList<>();
            postTag.add(postMap);
            postTag.add(tagsMap);

            postTags.add(postTag);
        }

        return postTags;
    }

    public void deleteTag(int postId) {
        postTagRepository.deleteTag(postId);
    }
}
