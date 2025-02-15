package org.acme.times;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class WaitingTime {

    @Inject
    DurationGenerator durationGenerator;

    @Inject
    Logger logger;

    private final ValueCommands<String, Long> timeCommands;
    private final KeyCommands<String> keyCommands;

    public WaitingTime(RedisDataSource ds) {
        this.timeCommands = ds.value(Long.class);
        this.keyCommands = ds.key();
    }

    public void setRandomWaitingTime(String attraction) {
        this.setWaitingTime(attraction, this.durationGenerator.randomDurantion());
    }

    public void setWaitingTime(String attraction, long waitingTime) {
        this.timeCommands.set(attraction, waitingTime);
    }

    @Tool("get the waiting time for the given ride name")
    public long getWaitingTime(String attraction) {

        logger.infof("Gets waiting time for %s", attraction);

        return this.timeCommands.get(attraction);
    }

    public Map<String, Long> getWaitingTimes() {
        final List<String> keys = this.keyCommands.keys("*")
                .stream()
                .filter(k -> !k.startsWith("embedding"))
                .toList();
        return this.timeCommands.mget(keys.toArray(new String[0]));
    }

}
