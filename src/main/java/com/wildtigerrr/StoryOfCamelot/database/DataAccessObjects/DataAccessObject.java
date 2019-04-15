package com.wildtigerrr.StoryOfCamelot.database.DataAccessObjects;

import java.util.Set;

public interface DataAccessObject {
    Object getRecordById();
    Object getAllRecords();
    boolean insertRecord();
    boolean updateRecord();
    boolean deleteRecord();
}
