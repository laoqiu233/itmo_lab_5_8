package com.dmtri.client.collectionmanagers;

import com.dmtri.common.models.Route;
import java.util.LinkedList;

public interface CollectionManager {
    LinkedList<Route> getCollection();
    Route getItemById(long id);
    void add(Route route);
    void update(Route route);
    void remove(long id);
    void clear();
}