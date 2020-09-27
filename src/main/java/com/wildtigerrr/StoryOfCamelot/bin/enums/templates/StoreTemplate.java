package com.wildtigerrr.StoryOfCamelot.bin.enums.templates;

import com.wildtigerrr.StoryOfCamelot.bin.enums.NameTranslation;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.StoreType;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Getter
@Log4j2
public enum StoreTemplate {
    TRADING_SQUARE_MERCHANT(
            LocationTemplate.TRADING_SQUARE.name(),
            NameTranslation.STORE_MERCHANT, new HashSet<>() {{
                add(StoreType.ARMORER);
                add(StoreType.BLACKSMITH);
            }}
    ),
    TRADING_SQUARE_GROCERY(
            LocationTemplate.TRADING_SQUARE.name(),
            NameTranslation.STORE_GROCERY, new HashSet<>() {{
                add(StoreType.GROCERY);
            }}
    );

    private final String location;
    private final NameTranslation name;
    private final Set<StoreType> storeTypes;

    StoreTemplate(String location, NameTranslation name, Set<StoreType> storeTypes) {
        this.location = location;
        this.name = name;
        this.storeTypes = storeTypes;
    }

    public static Map<String, List<StoreTemplate>> getStoreMapping() {
        Map<String, List<StoreTemplate>> storeMapping = new HashMap<>();
        for (StoreTemplate storeTemplate : StoreTemplate.values()) {
            if (!storeMapping.containsKey(storeTemplate.getLocation())) storeMapping.put(storeTemplate.getLocation(), new ArrayList<>());
            storeMapping.get(storeTemplate.getLocation()).add(storeTemplate);
        }
        return storeMapping;
    }

}
