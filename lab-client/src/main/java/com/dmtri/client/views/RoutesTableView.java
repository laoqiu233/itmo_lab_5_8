package com.dmtri.client.views;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dmtri.common.models.Route;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RoutesTableView {
    private TableView<Route> tableView;
    private ObservableList<Route> orderedRouteList = FXCollections.observableList(new LinkedList<>());
    private ObjectProperty<Route> selectedRouteProperty = new SimpleObjectProperty<>(null);

    public RoutesTableView(ObservableSet<Route> routes) {
        tableView = createTable();

        routes.addListener(new SetChangeListener<Route>() {
            public void onChanged(Change<? extends Route> change) {
                if (change.wasAdded()) {
                    // Insertion sort
                    // Use natural ordering if the user did not specify a comparator
                    // using headers in table view
                    for (int i = 0; i < orderedRouteList.size(); i++) {
                        if (getComparator().compare(change.getElementAdded(), orderedRouteList.get(i)) < 0) {
                            orderedRouteList.add(i, change.getElementAdded());
                            return;
                        }
                    }
                    orderedRouteList.add(change.getElementAdded());
                }
                if (change.wasRemoved()) {
                    if (tableView.getSelectionModel().getSelectedItem() == change.getElementRemoved()) {
                        tableView.getSelectionModel().select(null);
                    }
                    orderedRouteList.remove(change.getElementRemoved());
                }
            }
        });

        // Prevent default sort since we have custom sorting
        tableView.setOnSort(e -> {
            e.consume();
            sortList();
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            setSelectedRoute(newVal);
        });
    }

    public Parent getView() {
        return tableView;
    }

    public ObjectProperty<Route> selectedRouteProperty() {
        return selectedRouteProperty;
    }
    public Route getSelectedRoute() {
        return selectedRouteProperty.get();
    }
    public void setSelectedRoute(Route route) {
        selectedRouteProperty.set(route);
        if (route != tableView.getSelectionModel().getSelectedItem()) {
            tableView.getSelectionModel().select(route);
        }
    }

    private Comparator<Route> getComparator() {
        return (tableView.getComparator() == null ? (a, b) -> a.compareTo(b) : tableView.getComparator());
    }

    private void sortList() {
        // Sorting should be done using stream api :\
        Iterator<Route> it = orderedRouteList.stream().sorted(getComparator()).iterator();
        // Create a permutation index map
        Map<Route, Integer> permMap = IntStream.range(0, orderedRouteList.size())
                                               .boxed().collect(Collectors.toMap(x -> it.next(), x -> x));
        // Here we use the built-in sort of observable list with the permutation map
        // If we instead used setAll(), a list change event with remove and add
        // will be fired instead of a permutation event, which leads to
        // the user's selection being dropped.
        // Of course, we could add a listener to the table's selected item and rollback
        // the change if it was caused by sorting.
        // But somehow that causes the change to happen two times, so the selected row goes like:
        // RouteA -> null (Initial selection drop)
        // null -> RouteA (We roll back the change in the listener since it's caused by sorting)
        // RouteA -> null (For some reason the selection drops again???)
        // null -> RouteA (And restores itself for unknown reasons)
        //
        // I've tried to figure out why this happens by digging into javafx code, but with no success
        // So here it is, my stupid fix so that my code complies with the task requirement to
        // sort with stream API although the table already has the ability to sort.
        // Furthermore, causing a permutation event to fire once instead of two seperate
        // remove and add events makes more sense when sorting.
        orderedRouteList.sort((a, b) -> permMap.get(a).compareTo(permMap.get(b)));
    }

    private TableView<Route> createTable() {
        TableColumn<Route, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Route, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Route, LocalDate> creationDateCol = new TableColumn<>("Creation date");
        creationDateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        TableColumn<Route, Double> distanceCol = new TableColumn<>("Distance");
        distanceCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDistance()));
        TableColumn<Route, String> fromNameCol = new TableColumn<>("Starting location");
        fromNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFrom().getName()));
        TableColumn<Route, Long> fromXCol = new TableColumn<>("Start X");
        fromXCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFrom().getCoordinates().getX()));
        TableColumn<Route, Double> fromYCol = new TableColumn<>("Start Y");
        fromYCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getFrom().getCoordinates().getY()).asObject());
        TableColumn<Route, Long> fromZCol = new TableColumn<>("Start Z");
        fromZCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFrom().getCoordinates().getZ()));
        TableColumn<Route, String> toNameCol = new TableColumn<>("Ending location");
        toNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTo().getName()));
        TableColumn<Route, Long> toXCol = new TableColumn<>("End X");
        toXCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTo().getCoordinates().getX()));
        TableColumn<Route, Double> toYCol = new TableColumn<>("End Y");
        toYCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTo().getCoordinates().getY()).asObject());
        TableColumn<Route, Long> toZCol = new TableColumn<>("End Z");
        toZCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTo().getCoordinates().getZ()));
        TableColumn<Route, String> ownerCol = new TableColumn<>("Owner");
        ownerCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getOwner()));
        TableView<Route> table = new TableView<>(orderedRouteList);
        table.getColumns().addAll(idCol, nameCol, creationDateCol, distanceCol, fromNameCol, fromXCol, fromYCol, fromZCol, toNameCol, toXCol, toYCol, toZCol, ownerCol);

        return table;
    }
}
