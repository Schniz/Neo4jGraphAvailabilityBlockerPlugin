package org.neo4j.graphalgo.impl.shobydoby;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.plugins.*;

/**
 * Created by Gal Schlezinger on 6/15/14.
 */
public class GraphAvailabilityPlugin extends ServerPlugin {
    @Name( "get_available_path" )
    @Description( "Gets the available path between two nodes" )
    @PluginTarget( Node.class )
    public Path getAvailablePath(
            @Source Node source,
            @Parameter(name="target") @Description("Target") Node target,
            @Parameter(name="trainLength") @Description("The Length of the train") Integer trainLength,
            @Parameter(name="hour") @Description("The time to start the path") Double hour)
    {
        Path path = null;

        try (Transaction tx = source.getGraphDatabase().beginTx())
        {
            path = new GraphAvailabilityBlocker(trainLength).getAvailablePath(hour, source, target);
            tx.success();
        }

        return path;
    }

    @Name( "block_path" )
    @Description( "Blocks the available path between two nodes" )
    @PluginTarget( Node.class )
    public Path blockPath(
            @Source Node source,
            @Parameter(name="target") @Description("Target") Node target,
            @Parameter(name="trainLength") @Description("The Length of the train") Integer trainLength,
            @Parameter(name="hour") @Description("The time to start the path") Double hour)
    {
        Path path = null;

        try (Transaction tx = source.getGraphDatabase().beginTx())
        {
            path = new GraphAvailabilityBlocker(trainLength).tryBlock(hour, source, target);
            tx.success();
        }

        return path;
    }
}
