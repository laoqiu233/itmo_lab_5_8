package com.dmtri.common.network;

import com.dmtri.common.models.Route;

public class RequestBodyWithRoute extends RequestBody {
    private static final long serialVersionUID = -5519361223588780166L;
    private Route route;

    public RequestBodyWithRoute(String[] args, Route route) {
        super(args);
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }
}
