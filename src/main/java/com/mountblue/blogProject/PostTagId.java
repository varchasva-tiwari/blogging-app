package com.mountblue.blogProject;

import java.io.Serializable;
import java.util.Objects;

public class PostTagId implements Serializable {
    private int postId;
    private int tagId;

    public PostTagId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostTagId postTagId = (PostTagId) o;
        return postId == postTagId.postId &&
                tagId == postTagId.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, tagId);
    }
}
