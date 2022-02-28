package com.dmtri.client.collectionmanagers;

import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Route;

import java.util.List;

public interface CollectionManager {
    List<Route> getCollection();
    Route getItemById(long id);
    void add(Route route) throws InvalidFieldException;
    void update(Route route);
    void remove(long id);
    void clear();
    long getNextId();
}
