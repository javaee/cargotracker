package net.java.cargotracker.interfaces.tracking.web;

import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.infrastructure.events.cdi.CargoInspected;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean for tracking cargo. This interface sits immediately on top of
 * the domain layer, unlike the booking interface which has a facade and
 * supporting DTOs in between.
 * <p/>
 * An adapter class, designed for the tracking use case, is used to wrap the
 * domain model to make it easier to work with in a web page rendering context.
 * We do not want to apply view rendering constraints to the design of our
 * domain model, and the adapter helps us shield the domain model classes.
 * <p/>
 * In some very simplistic cases, it may be fine to not use even an adapter.
 */
@Named
@ApplicationScoped
public class HandledCargo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Cargo> cargos = new ArrayList<>();


    public List<Cargo> getCargos(){
        return cargos;
    }

    public void onCargoInspected(@Observes @CargoInspected Cargo cargo){
        cargos.add(cargo);
    }

}