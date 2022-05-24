package com.dmtri.client.views;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.dmtri.common.models.Route;

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
    private boolean wasSorted = false;

    public RoutesTableView(ObservableSet<Route> routes) {
        tableView = createTable();

        routes.addListener(new SetChangeListener<Route>() {
            public void onChanged(Change<? extends Route> change) {
                if (change.wasAdded()) {
                    // Insertion sort
                    // Use natural ordering if the user did not specify a comparator
                    Comparator<Route> comparator = (tableView.getComparator() == null ? (a, b) -> a.compareTo(b) : tableView.getComparator());
                    for (int i=0; i<orderedRouteList.size(); i++) {
                        if (comparator.compare(change.getElementAdded(), orderedRouteList.get(i)) < 0) {
                            orderedRouteList.add(i, change.getElementAdded());
                            return;
                        }
                    }
                    orderedRouteList.add(change.getElementAdded());
                }
                if (change.wasRemoved()) {
                    orderedRouteList.remove(change.getElementRemoved());
                    if (tableView.getSelectionModel().getSelectedItem() == change.getElementRemoved()) {
                        tableView.getSelectionModel().select(null);
                    }
                }
            }
        });

        // Prevent default sort since we have custom sorting
        tableView.setOnSort(e -> {
            e.consume();
            sortList();
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            // If the selection change was caused by sorting, rollback the change
            if (wasSorted) {
                tableView.getSelectionModel().select(orderedRouteList.contains(oldVal) ? oldVal : newVal);
                wasSorted = false;
            }
        });
    }

    public Parent getView() {
        return tableView;
    }

    private void sortList() {
        // Sorting should be done using stream api :\
        wasSorted = true;
        Stream<Route> s = orderedRouteList.stream();
        if (tableView.getSortOrder().isEmpty()) {
            s = s.sorted();
        } else {
            s = s.sorted(tableView.getComparator());
        }
        orderedRouteList.setAll(s.collect(Collectors.toList()));
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
