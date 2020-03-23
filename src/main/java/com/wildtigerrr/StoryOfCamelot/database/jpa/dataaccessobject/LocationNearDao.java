package com.wildtigerrr.StoryOfCamelot.database.jpa.dataaccessobject;

import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.LocationNear;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationNearDao extends CrudRepository<LocationNear, String> {
    List<LocationNear> findByStartLocation(Location location);
    LocationNear findByStartLocationAndFinishLocation(Location startLocation, Location finishLocation);
}
