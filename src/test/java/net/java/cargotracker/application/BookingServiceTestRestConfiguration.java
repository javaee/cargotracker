package net.java.cargotracker.application;

import net.java.cargotracker.application.util.*;
import javax.ws.rs.ApplicationPath;
import net.java.pathfinder.api.GraphTraversalService;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS configuration.
 */
@ApplicationPath("rest")
public class BookingServiceTestRestConfiguration extends ResourceConfig {

    public BookingServiceTestRestConfiguration() {
        // Resources
        packages(new String[]{GraphTraversalService.class.getPackage().getName()});
        // Providers - JSON.
        register(new MoxyJsonFeature());
        register(new JsonMoxyConfigurationContextResolver()); // TODO See if this can be removed.
    }
}
