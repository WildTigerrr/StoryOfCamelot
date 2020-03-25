package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.jpa.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.ObjectType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.sequence.SequenceRepository;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class IdGenerator extends SequenceStyleGenerator {

    public static final String VALUE_PREFIX_PARAMETER = "valuePrefix";

    private static final String DEFAULT_ID = "00000000000";

    private SequenceRepository sequenceRepository;

    void checkServices() {
        if (this.sequenceRepository == null) this.sequenceRepository = ApplicationContextProvider.bean(SequenceRepository.class);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        checkServices();
        SimpleObject newRecord = (SimpleObject) object;
        ObjectType type = newRecord.type();
        String newId = StringUtils.getNumberWithMaxRadix(sequenceRepository.getNext(type.sequenceName()));
        return type.prefix() + DEFAULT_ID.substring(newId.length()) + newId;
    }

}