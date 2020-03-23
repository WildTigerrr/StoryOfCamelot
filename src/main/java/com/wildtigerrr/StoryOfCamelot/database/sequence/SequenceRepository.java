package com.wildtigerrr.StoryOfCamelot.database.sequence;

public interface SequenceRepository {
    long getNext(String sequenceName);
}
