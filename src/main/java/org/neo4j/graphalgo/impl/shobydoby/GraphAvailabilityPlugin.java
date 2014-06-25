package org.neo4j.graphalgo.impl.shobydoby;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.plugins.*;
import org.neo4j.server.rest.repr.Representation;
import org.neo4j.server.rest.repr.ValueRepresentation;

import java.util.HashMap;

/**
 * Created by Gal Schlezinger on 6/15/14.
 */
public class GraphAvailabilityPlugin extends ServerPlugin {
    @Name("get_available_path")
    @Description("Gets the available path between two nodes")
    @PluginTarget(Node.class)
    public Path getAvailablePath(
            @Source Node source,
            @Parameter(name = "target") @Description("Target") Node target,
            @Parameter(name = "trainLength") @Description("The Length of the train") Double trainLength,
            @Parameter(name = "hour") @Description("The time to start the path") Double hour) {
        Path path = null;

        try (Transaction tx = source.getGraphDatabase().beginTx()) {
            path = new GraphAvailabilityBlocker(trainLength).getAvailablePath(hour, source, target);
            tx.success();
        }

        return path;
    }

    @Name("block_path")
    @Description("Blocks the available path between two nodes")
    @PluginTarget(Node.class)
    public Path blockPath(
            @Source Node source,
            @Parameter(name = "target") @Description("Target") Node target,
            @Parameter(name = "trainLength") @Description("The Length of the train") Double trainLength,
            @Parameter(name = "hour") @Description("The time to start the path") Double hour) {
        Path path = null;

        try (Transaction tx = source.getGraphDatabase().beginTx()) {
            path = new GraphAvailabilityBlocker(trainLength).tryBlock(hour, source, target);
            tx.success();
        }

        return path;
    }

    @Name("block_path_with_alternative_timings")
    @Description("Blocks the available path between two nodes")
    @PluginTarget(Node.class)
    public Double blockPathWithAlternativeTimings(
            @Source Node source,
            @Parameter(name = "target") @Description("Target") Node target,
            @Parameter(name = "trainLength") @Description("The Length of the train") Double trainLength,
            @Parameter(name = "hour") @Description("The time to start the path") Double hour,
            @Parameter(name = "step") @Description("Difference between two checks") Double step,
            @Parameter(name = "steps") @Description("Number of steps") Integer steps) {
        Path path = null;
        Double finalHour = -1.0;

        try (Transaction tx = source.getGraphDatabase().beginTx()) {
            for (double stepToAdd = 0.0; path == null && stepToAdd < steps * step; stepToAdd += step) {
                finalHour = hour + stepToAdd;
                path = new GraphAvailabilityBlocker(trainLength).tryBlock(finalHour, source, target);
            }
            tx.success();
        }

        return path == null ? -1d : finalHour;
    }

    @Name("has_path")
    @Description("Has a path between the two nodes")
    @PluginTarget(Node.class)
    public Boolean hasPath(
            @Source Node source,
            @Parameter(name = "target") @Description("Target") Node target,
            @Parameter(name = "trainLength") @Description("The Length of the train") Double trainLength,
            @Parameter(name = "hour") @Description("The time to start the path") Double hour) {
        Path path = null;

        try (Transaction tx = source.getGraphDatabase().beginTx()) {
            path = new GraphAvailabilityBlocker(trainLength).getAvailablePath(hour, source, target);
            tx.success();
        }

        return path != null;
    }
}
