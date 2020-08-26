package com.mountblue.blogProject.service;

import com.mountblue.blogProject.entity.Tag;
import com.mountblue.blogProject.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public void saveTags(List<Tag> tags) {
        tagRepository.saveAll(tags);
    }

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public List<Integer> editTags(String tags) {
        List<String> tagNames = Arrays.asList(tags.split(", "));

        List<Integer> newTagIds = new ArrayList<>();

        tagNames.forEach(name -> {
            Tag tag = tagRepository.findTagByName(name);

            if(tag == null) {
                tag = new Tag();
                tag.setName(name);
                tag.setCreatedAt(LocalDateTime.now());

                tagRepository.save(tag);
            }
            newTagIds.add(tagRepository.getId(name));
        });

        return newTagIds;
    }
}
