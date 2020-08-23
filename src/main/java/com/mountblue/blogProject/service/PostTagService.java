package com.mountblue.blogProject.service;

import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.PostTag;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.repository.PostTagRepository;
import com.mountblue.blogProject.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class PostTagService {
    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private TagRepository tagRepository;

    public void create(int postId, List<Integer> tagIds) {
        tagIds.forEach(tagId -> {
            if(postTagRepository.containsTag(postId, tagId).isEmpty())
                postTagRepository.savePostTag(postId, tagId);
        });
    }

    public List<Tag> readTags(int postId) {
         List<PostTag> postTags = postTagRepository.getTags(postId);

         List<Tag> tags = new ArrayList<>();

         postTags.forEach((postTag) -> {
             tags.add(tagRepository.findById(postTag.getTagId()).get());
         });

         return tags;
    }

    public LinkedHashMap<Post, List<Tag>> getPostTags(List<Post> posts) {
        LinkedHashMap<Post,List<Tag>> postTags = new LinkedHashMap<>();

        posts.forEach(post -> {
            postTags.put(post, readTags(post.getId()));
        });

        return postTags;
    }

    public void delete(int postId) {
        postTagRepository.deleteTag(postId);
    }
}
