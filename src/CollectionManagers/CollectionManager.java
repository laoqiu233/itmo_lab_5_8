package src.CollectionManagers;

import src.Models.Route;
import java.util.LinkedList;

public interface CollectionManager {
    LinkedList<Route> getCollection();
    void add(Route route);
    void update(Route route);
    void remove(long id);
    void clear();
}
