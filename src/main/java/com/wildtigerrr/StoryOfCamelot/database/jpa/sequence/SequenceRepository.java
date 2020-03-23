package com.wildtigerrr.StoryOfCamelot.database.jpa.sequence;

public interface SequenceRepository {
    long getNext(String sequenceName);
}
