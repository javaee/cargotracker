package net.java.cargotracker.interfaces.booking.facade.internal.assembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.java.cargotracker.domain.model.location.Location;

public class LocationDtoAssembler {

    public net.java.cargotracker.interfaces.booking.facade.dto.Location toDto(
            Location location) {
        return new net.java.cargotracker.interfaces.booking.facade.dto.Location(
                location.getUnLocode().getIdString(), location.getName());
    }

    public List<net.java.cargotracker.interfaces.booking.facade.dto.Location> toDtoList(
            List<Location> allLocations) {
        List<net.java.cargotracker.interfaces.booking.facade.dto.Location> dtoList = new ArrayList<>(
                allLocations.size());

        for (Location location : allLocations) {
            dtoList.add(toDto(location));
        }

        Collections.sort(
                dtoList,
                new Comparator<net.java.cargotracker.interfaces.booking.facade.dto.Location>() {

                    @Override
                    public int compare(
                            net.java.cargotracker.interfaces.booking.facade.dto.Location location1,
                            net.java.cargotracker.interfaces.booking.facade.dto.Location location2) {
                                return location1.getName().compareTo(location2.getName());
                            }
                });

        return dtoList;
    }
}
