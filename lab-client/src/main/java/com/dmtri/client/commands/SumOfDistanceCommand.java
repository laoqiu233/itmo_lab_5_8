package com.dmtri.client.commands;

import com.dmtri.client.collectionmanagers.CollectionManager;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.util.TerminalColors;

public class SumOfDistanceCommand extends AbstractCommand {
    private CollectionManager col;

    public SumOfDistanceCommand(CollectionManager col) {
        super("sum_of_distance");
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("sum_of_distance", TerminalColors.GREEN)
             + " - sums the distances for all routes and outputs the result.";
    }

    @Override
    public Response execute(Request request) {
        Double res = col.getCollection().stream()
                                        .filter(r -> r.getDistance() != null)
                                        .map(r -> r.getDistance())
                                        .reduce((a, b) -> a + b)
                                        .orElse(0d);

        return new Response("Sum of distances: " + res);
    }
}
