package src.CollectionManagers;

import src.Exceptions.ItemNotFoundException;
import src.Models.Route;
import java.util.LinkedList;

public interface CollectionManager {
    LinkedList<Route> getCollection();
    void add(Route route);
    void update(Route route) throws ItemNotFoundException;
    void remove(long id) throws ItemNotFoundException;
    void clear();
}