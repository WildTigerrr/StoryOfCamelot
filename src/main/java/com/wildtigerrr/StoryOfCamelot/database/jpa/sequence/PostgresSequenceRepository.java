package com.wildtigerrr.StoryOfCamelot.database.jpa.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgresSequenceMaxValueIncrementer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Profile("!test")
@Repository
public class PostgresSequenceRepository implements SequenceRepository {

    private final DataSource dataSource;

    @Autowired
    public PostgresSequenceRepository(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional(readOnly = true)
    @Override
    public long getNext(String sequenceName) {
        AbstractSequenceMaxValueIncrementer incr = new PostgresSequenceMaxValueIncrementer(this.dataSource, sequenceName);
        return incr.nextLongValue();
    }

}
