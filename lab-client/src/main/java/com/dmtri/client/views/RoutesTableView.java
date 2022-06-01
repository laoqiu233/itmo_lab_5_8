package com.dmtri.client.views;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.text.DateFormatter;

import com.dmtri.client.LocaleManager;
import com.dmtri.common.models.Route;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class RoutesTableView {
    private final LocaleManager localeManager;
    private TableView<Route> tableView;
    private ObservableList<Route> orderedRouteList = FXCollections.observableList(new LinkedList<>());
    private ObjectProperty<Route> selectedRouteProperty = new SimpleObjectProperty<>(null);

    public RoutesTableView(ObservableSet<Route> routes, LocaleManager localeManager) {
        this.localeManager = localeManager;
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

        selectedRouteProperty.addListener((o, oldVal, newVal) -> {
            tableView.getSelectionModel().select(newVal);
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
        TableColumn<Route, Long> idCol = createColumn(
            "idLabel", new PropertyValueFactory<>("id"),
            id -> NumberFormat.getIntegerInstance().format(id)
        );
        TableColumn<Route, String> nameCol = createColumn(
            "nameLabel", new PropertyValueFactory<>("name"),
            x -> x
        );
        TableColumn<Route, LocalDate> creationDateCol = createColumn(
            "creationDateLabel", new PropertyValueFactory<>("creationDate"),
            date -> date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
        );
        TableColumn<Route, Double> distanceCol = createColumn(
            "distanceLabel", new PropertyValueFactory<>("distance"),
            distance -> NumberFormat.getNumberInstance().format(distance)
        );
        TableColumn<Route, String> fromNameCol = createColumn(
            "startingLocationNameLabel", data -> new SimpleStringProperty(data.getValue().getFrom().getName()),
            x -> x
        );
        TableColumn<Route, Long> fromXCol = createColumn(
            "startingLocationXLabel", data -> new SimpleObjectProperty<>(data.getValue().getFrom().getCoordinates().getX()),
            x -> NumberFormat.getIntegerInstance().format(x)
        );
        TableColumn<Route, Double> fromYCol = createColumn(
            "startingLocationYLabel", data -> new SimpleObjectProperty<>(data.getValue().getFrom().getCoordinates().getY()),
            x -> NumberFormat.getNumberInstance().format(x)
        );
        TableColumn<Route, Long> fromZCol = createColumn(
            "startingLocationZLabel", data -> new SimpleObjectProperty<>(data.getValue().getFrom().getCoordinates().getZ()),
            x -> NumberFormat.getIntegerInstance().format(x)
        );
        TableColumn<Route, String> toNameCol = createColumn(
            "endingLocationNameLabel", data -> new SimpleStringProperty(data.getValue().getTo().getName()),
            name -> name
        );
        TableColumn<Route, Long> toXCol = createColumn(
            "endingLocationXLabel", data -> new SimpleObjectProperty<>(data.getValue().getTo().getCoordinates().getX()),
            x -> NumberFormat.getIntegerInstance().format(x)
        );
        TableColumn<Route, Double> toYCol = createColumn(
            "endingLocationYLabel", data -> new SimpleObjectProperty<>(data.getValue().getTo().getCoordinates().getY()),
            x -> NumberFormat.getNumberInstance().format(x)
        );
        TableColumn<Route, Long> toZCol = createColumn(
            "endingLocationZLabel", data -> new SimpleObjectProperty<>(data.getValue().getTo().getCoordinates().getZ()),
            x -> NumberFormat.getIntegerInstance().format(x)
        );
        TableColumn<Route, String> ownerCol = createColumn(
            "ownerLabel", new PropertyValueFactory<>("owner"),
            x -> x
        );
        TableView<Route> table = new TableView<>(orderedRouteList);
        table.getColumns().addAll(idCol, nameCol, creationDateCol, distanceCol, fromNameCol, fromXCol, fromYCol, fromZCol, toNameCol, toXCol, toYCol, toZCol, ownerCol);

        return table;
    }

    private <T> TableColumn<Route, T> createColumn(String localeKey, Callback<CellDataFeatures<Route, T>, ObservableValue<T>> valueFactory, Callback<T, String> formatter) {
        TableColumn<Route, T> newColumn = new TableColumn<>();
        newColumn.textProperty().bind(localeManager.getObservableStringByKey(localeKey));
        newColumn.setCellValueFactory(valueFactory);
        newColumn.setCellFactory(tc -> new LocaleFormattedTableCell<>(formatter));
        return newColumn;
    }

    private class LocaleFormattedTableCell<T> extends TableCell<Route, T> {
        Callback<T, String> formatter;

        LocaleFormattedTableCell(Callback<T, String> formatter) {
            this.formatter = formatter;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                textProperty().unbind();
                setText("");
            } else {
                textProperty().bind(Bindings.createStringBinding(() -> formatter.call(item), localeManager.localeProperty()));
            }
        }
    }
}
