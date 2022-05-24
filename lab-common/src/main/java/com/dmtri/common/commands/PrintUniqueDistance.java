package com.dmtri.common.commands;

import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.InvalidRequestException;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.Response;
import com.dmtri.common.util.TerminalColors;

public class PrintUniqueDistance extends AbstractCommand {
    private CollectionManager col;

    public PrintUniqueDistance(CollectionManager col) {
        super("print_unique_distance");
        this.col = col;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("print_unique_distance", TerminalColors.GREEN)
             + " - prints all distinct distances";
    }

    @Override
    public Response execute(Request request, String username) throws InvalidRequestException {
        StringBuilder sb = new StringBuilder();

        for (Double distance : col.getUniqueDistances()) {
            sb.append(distance.toString() + '\n');
        }

        return new Response(sb.toString());
    }
}
