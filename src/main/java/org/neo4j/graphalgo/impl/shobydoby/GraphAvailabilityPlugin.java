package org.neo4j.graphalgo.impl.shobydoby;

import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.plugins.*;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.ArrayList;

/**
 * Created by schniz on 6/15/14.
 */
public class GraphAvailabilityPlugin extends ServerPlugin {
    @Name( "get_available_path" )
    @Description( "Gets the available path between two nodes" )
    @PluginTarget( Node.class )
    public Path getAvailablePath(
            @Source Node source,
            @Parameter(name="target") @Description("Target") Node target,
            @Parameter(name="trainLength") @Description("The Length of the train") int trainLength,
            @Parameter(name="hour") @Description("The time to start the path") double hour)
    {
        return new GraphAvailabilityBlocker(trainLength).tryBlock(hour, source, target);
    }

    @Name( "block_path" )
    @Description( "Blocks the available path between two nodes" )
    @PluginTarget( Node.class )
    public Path blockPath(
            @Source Node source,
            @Parameter(name="target") @Description("Target") Node target,
            @Parameter(name="trainLength") @Description("The Length of the train") int trainLength,
            @Parameter(name="hour") @Description("The time to start the path") double hour)
    {
        return new GraphAvailabilityBlocker(trainLength).block(hour, source, target);
    }
}
