package com.dmtri.common.network;

import com.dmtri.common.models.Route;

public class ResponseWithRoutes extends Response {
    private static final long serialVersionUID = -802619454694737526L;
    private Route[] routes;

    public ResponseWithRoutes(Route[] routes) {
        this.routes = routes;
    }

    public ResponseWithRoutes(String message, Route[] routes) {
        super(message);
        this.routes = routes;
    }

    public Route getRoute(int i) {
        return routes[i];
    }

    public int getRoutesCount() {
        return routes.length;
    }
}
