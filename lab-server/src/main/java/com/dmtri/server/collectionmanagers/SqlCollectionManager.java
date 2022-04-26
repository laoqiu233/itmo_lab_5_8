package com.dmtri.server.collectionmanagers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.dmtri.common.collectionmanagers.CollectionManager;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.Coordinates;
import com.dmtri.common.models.Location;
import com.dmtri.common.models.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlCollectionManager implements CollectionManager {
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS routes ("
                                                   + "   id serial PRIMARY KEY,"
                                                   + "   name varchar(100) NOT NULL,"
                                                   + "   creation_date TIMESTAMP NOT NULL,"
                                                   + "   from_name varchar(100) NOT NULL,"
                                                   + "   from_coordinates_x bigint,"
                                                   + "   from_coordinates_y double precision NOT NULL,"
                                                   + "   from_coordinates_z bigint,"
                                                   + "   to_name varchar(100) NOT NULL,"
                                                   + "   to_coordinates_x bigint,"
                                                   + "   to_coordinates_y double precision NOT NULL,"
                                                   + "   to_coordinates_z bigint,"
                                                   + "   distance double precision,"
                                                   + "   owner_id integer NOT NULL,"
                                                   + "   CONSTRAINT fk_owner"
                                                   + "      FOREIGN KEY(owner_id) REFERENCES users(id) ON DELETE CASCADE)";
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlCollectionManager.class);
    private final Connection conn;
    private final List<Route> collection = new LinkedList<>();
    private final Lock lock = new ReentrantLock();

    public SqlCollectionManager(Connection conn) {
        this.conn = conn;
    }

    public void initTable() throws SQLException {
        // Setup tables
        try (Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            s.execute(CREATE_TABLE_QUERY);

            try (ResultSet res = s.executeQuery("SELECT * FROM routes")) {
                int invalidRoutes = 0;

                while (res.next()) {
                    Route newRoute = mapRowToRoute(res);
                    if (newRoute != null) {
                        collection.add(newRoute);
                    } else {
                        invalidRoutes++;
                    }
                }

                LOGGER.info("Loaded " + collection.size() + " routes from DB, removed " + invalidRoutes + " invalid routes.");
            }
        }
    }

    private Route mapRowToRoute(ResultSet res) throws SQLException {
        try {
            Route route = new Route(
                res.getLong("id"),
                res.getString("name"),
                res.getTimestamp("creation_date").toLocalDateTime().toLocalDate(),
                new Location(
                    res.getString("from_name"),
                    new Coordinates(
                        res.getObject("from_coordinates_x") == null ? null : res.getLong("from_coordinates_x"),
                        res.getDouble("from_coordinates_y"),
                        res.getObject("from_coordinates_z") == null ? null : res.getLong("from_coordinates_z")
                    )
                ),
                new Location(
                    res.getString("to_name"),
                    new Coordinates(
                        res.getObject("to_coordinates_x") == null ? null : res.getLong("to_coordinates_x"),
                        res.getDouble("to_coordinates_y"),
                        res.getObject("to_coordinates_z") == null ? null : res.getLong("to_coordinates_z")
                    )
                ),
                res.getObject("distance") == null ? null : res.getDouble("distance")
            );

            route.setOwnerId(res.getLong("owner_id"));

            return route;
        } catch (InvalidFieldException e) {
            return null;
        }
    }

    private void prepareRouteStatement(PreparedStatement s, Route route, int paramOffset) throws SQLException {
        int i = 0;
        s.setString(paramOffset + ++i, route.getName());
        s.setTimestamp(paramOffset + ++i, Timestamp.valueOf(route.getCreationDate().atStartOfDay()));
        s.setString(paramOffset + ++i, route.getFrom().getName());
        if (route.getFrom().getCoordinates().getX() != null) {
            s.setLong(paramOffset + ++i, route.getFrom().getCoordinates().getX());
        } else {
            s.setNull(paramOffset + ++i, Types.BIGINT);
        }
        s.setDouble(paramOffset + ++i, route.getFrom().getCoordinates().getY());
        if (route.getFrom().getCoordinates().getZ() != null) {
            s.setLong(paramOffset + ++i, route.getFrom().getCoordinates().getZ());
        } else {
            s.setNull(paramOffset + ++i, Types.BIGINT);
        }
        s.setString(paramOffset + ++i, route.getTo().getName());
        if (route.getTo().getCoordinates().getX() != null) {
            s.setLong(paramOffset + ++i, route.getTo().getCoordinates().getX());
        } else {
            s.setNull(paramOffset + ++i, Types.BIGINT);
        }
        s.setDouble(paramOffset + ++i, route.getTo().getCoordinates().getY());
        if (route.getTo().getCoordinates().getZ() != null) {
            s.setLong(paramOffset + ++i, route.getTo().getCoordinates().getZ());
        } else {
            s.setNull(paramOffset + ++i, Types.BIGINT);
        }
        if (route.getDistance() != null) {
            s.setDouble(paramOffset + ++i, route.getDistance());
        } else {
            s.setNull(paramOffset + ++i, Types.DOUBLE);
        }
        s.setLong(paramOffset + ++i, route.getOwnerId());
    }

    @Override
    public List<Route> getCollection() {
        try {
            lock.lock();
            return new LinkedList<>(collection);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Route getItemById(long id) {
        try {
            lock.lock();
            return collection.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long add(Route route) {
        String query = "INSERT INTO routes VALUES ("
                     + "    default,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";

        try (PreparedStatement s = conn.prepareStatement(query)) {
            lock.lock();
            prepareRouteStatement(s, route, 0);
            try (ResultSet res = s.executeQuery()) {
                res.next();
                Long id = res.getLong("id");
                route.setId(id);
                collection.add(route);
                return id;
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to insert element into DB", e);
            return 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean update(Route route) {
        final int idOffset = 12;
        String query = "UPDATE routes SET "
                     + "name=?, "
                     + "creation_date=?, "
                     + "from_name=?, "
                     + "from_coordinates_x=?, "
                     + "from_coordinates_y=?, "
                     + "from_coordinates_z=?, "
                     + "to_name=?, "
                     + "to_coordinates_x=?, "
                     + "to_coordinates_y=?, "
                     + "to_coordinates_z=?, "
                     + "distance=? "
                     + "WHERE id=?";

        try (PreparedStatement s = conn.prepareStatement(query)) {
            lock.lock();
            prepareRouteStatement(s, route, 0);
            s.setLong(idOffset, route.getId());
            int count = s.executeUpdate();
            if (count > 0) {
                collection.removeIf(x -> x.getId() == route.getId());
                collection.add(route);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to update route", e);
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double sumOfDistances() {
        try {
            lock.lock();
            return collection.stream()
                        .filter(r -> r.getDistance() != null)
                        .map(r -> r.getDistance())
                        .reduce((a, b) -> a + b)
                        .orElse(0d);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Double> getUniqueDistances() {
        try {
            lock.lock();
            return collection.stream()
                .filter(x -> x.getDistance() != null)
                .map(x -> x.getDistance())
                .distinct()
                .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(long id) {
        String query = "DELETE FROM routes WHERE id=?";

        try (PreparedStatement s = conn.prepareStatement(query)) {
            lock.lock();
            s.setLong(1, id);
            s.executeUpdate();
            collection.removeIf(x -> x.getId() == id);
        } catch (SQLException e) {
            LOGGER.error("Failed to delete row", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int removeIf(Predicate<? super Route> predicate) {
        try {
            lock.lock();
            List<Long> ids = collection.stream().filter(predicate).map(x -> x.getId()).collect(Collectors.toList());
            ids.forEach(this::remove);
            return ids.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try (Statement s = conn.createStatement()) {
            lock.lock();
            boolean prev = conn.getAutoCommit();
            conn.setAutoCommit(false);
            s.execute("DROP TABLE routes");
            s.execute(CREATE_TABLE_QUERY);
            conn.commit();
            conn.setAutoCommit(prev);
            collection.clear();
        } catch (SQLException e) {
            try {
                LOGGER.error("Failed to clear table, rolling back...", e);
                conn.rollback();
            } catch (SQLException e_) {
                LOGGER.error("Failed to rollback", e);
            }
        } finally {
            lock.unlock();
        }
    }
}
