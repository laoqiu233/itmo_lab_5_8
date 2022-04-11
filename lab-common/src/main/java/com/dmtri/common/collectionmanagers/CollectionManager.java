package com.dmtri.common.collectionmanagers;
import com.dmtri.common.models.Route;

import java.util.List;
import java.util.function.Predicate;

public interface CollectionManager {
    List<Route> getCollection();
    Route getItemById(long id);
    long add(Route route);
    boolean update(Route route);
    double sumOfDistances();
    List<Double> getUniqueDistances();
    void remove(long id);
    int removeIf(Predicate<? super Route> predicate);
    void clear();
}
