package com.mountblue.blogProject.service;

import com.mountblue.blogProject.entity.Post;
import com.mountblue.blogProject.entity.PostTag;
import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.repository.PostTagRepository;
import com.mountblue.blogProject.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PostTagService {
    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    TagRepository tagRepository;

    public void createPostTag(int postId, List<Integer> tagIds) {
        tagIds.forEach(tagId -> {
            postTagRepository.createPostTag(postId, tagId);
        });
    }

    public List<Tag> readTagsByPost(int postId) {
         List<PostTag> postTags = postTagRepository.readTagsByPost(postId);

         List<Tag> tags = new ArrayList<>();

         postTags.forEach((postTag) -> {
             tags.add(tagRepository.findById(postTag.getTagId()).get());
         });

         return tags;
    }
}
