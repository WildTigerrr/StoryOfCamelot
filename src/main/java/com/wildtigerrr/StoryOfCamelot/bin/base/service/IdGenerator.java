package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.database.interfaces.SimpleObject;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.ObjectType;
import com.wildtigerrr.StoryOfCamelot.database.sequence.SequenceRepository;
import com.wildtigerrr.StoryOfCamelot.exception.IdValueExceededException;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Service
public class IdGenerator extends SequenceStyleGenerator {

    public static final String VALUE_PREFIX_PARAMETER = "valuePrefix";
    private String valuePrefix;

    private static final String DEFAULT_ID = "00000000000";

    private static Map<String, String> lastIds;

    private SequenceRepository sequenceRepository;

//    @Autowired
//    public IdGenerator(SequenceRepository sequenceRepository) {
//        this.sequenceRepository = sequenceRepository;
//    }

    void checkServices() {
        if (this.sequenceRepository == null) this.sequenceRepository = ApplicationContextProvider.getBean(SequenceRepository.class);
    }

    @PostConstruct
    void postConstruct() {
        lastIds = new HashMap<>();
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        checkServices();
        SimpleObject newRecord = (SimpleObject) object;
        ObjectType type = newRecord.type();
        String newId = StringUtils.getNumberWithMaxRadix(sequenceRepository.getNext(type.sequenceName()));
        return type.prefix() + DEFAULT_ID.substring(newId.length()) + newId;
    }

    public String generateId(Type type) {
        String typeName = StringUtils.getClassName(type);
        String keyPrefix = getKeyPrefix(typeName);
        String newId;
        if (lastIds.containsKey(typeName)) {
            newId = keyPrefix + next(lastIds.get(typeName).substring(4));
        } else {
            newId = keyPrefix + next(DEFAULT_ID);
        }
        lastIds.put(typeName, newId);
        return newId;
    }

    public String getKeyPrefix(String typeName) {
        switch (typeName) {
            case "Player": return "a0p0";
            case "Backpack": return "a0b0";
            case "FileLink": return "a0f0";
            case "Item": return "a0i0";
            case "Location": return "a0l0";
            case "LocationNear": return "a0ln";
            case "Mob": return "a0m0";
            case "MobDrop": return "a0md";
            case "Npc": return "a0n0";
            case "PossibleLocation": return "a0pl";
            default: throw new InvalidInputException("Id for type " + typeName + " cannot be constructed");
        }
    }


    private String next(String s) {
        int length = s.length();
        char c = s.charAt(length - 1);

        if (c == '9') {
            return s.substring(0, length - 1) + 'a';
        } else if (c == 'z') {
            if (length > 1) {
                return next(s.substring(0, length - 1)) + '0';
            } else {
                throw new IdValueExceededException();
            }
        }

        return s.substring(0, length - 1) + ++c;
    }

}