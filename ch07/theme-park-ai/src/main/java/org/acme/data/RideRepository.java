package org.acme.data;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RideRepository implements PanacheRepository<Ride> {

    @Inject
    Logger logger;

    @Tool("get the best ride")
    @Transactional
    public RideRecord getTheBestRideByRatings() {

        logger.info("Get The Best Ride query");

        return findAll(Sort.descending("rating"))
                .project(RideRecord.class)
                .firstResult();
    }

}
