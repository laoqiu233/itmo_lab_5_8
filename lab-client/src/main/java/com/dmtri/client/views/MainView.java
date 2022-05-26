package com.dmtri.client.views;

import java.util.Set;
import java.util.stream.Collectors;

import com.dmtri.client.GraphicClient;
import com.dmtri.common.models.Route;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainView {
    private static final int GAP = 20;
    private Parent view;
    private GraphicClient client;
    private RoutesTableView tableTab;
    private FilterView filterView;
    private ObservableSet<Route> filteredRoutes = FXCollections.observableSet();
    private final InvalidationListener listener = new InvalidationListener() {
        public void invalidated(Observable observable) {
            Set<Route> newFilteredRoutes = client.getRoutes().stream().filter(filterView.getFilter()).collect(Collectors.toSet());
            filteredRoutes.retainAll(newFilteredRoutes);
            filteredRoutes.addAll(newFilteredRoutes);
        }
    };

    public MainView(GraphicClient client) {
        this.client = client;
        filterView = new FilterView();
        filterView.filterProperty().addListener(listener);
        client.routesProperty().addListener(listener);

        BorderPane root = new BorderPane();
        root.setTop(createUserInfoBox());
        root.setLeft(filterView.getView());
        root.setRight(createRouteInspectionBox());
        tableTab = new RoutesTableView(filteredRoutes);
        Tab tab1 = new Tab("Routes (Table)", tableTab.getView());
        Tab tab2 = new Tab("Graph View");
        TabPane center = new TabPane(tab1, tab2);
        center.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        root.setCenter(center);

        this.view = root;
    }

    public Parent getView() {
        return view;
    }

    private Node createRouteInspectionBox() {
        RouteInspectorView inspector = new RouteInspectorView(client, tableTab.selectedRouteProperty());
        Button updateButton = new Button("Update");
        updateButton.disableProperty().bind(Bindings.not(inspector.routeReadyProperty()));
        Button deleteButton = new Button("Delete");
        deleteButton.disableProperty().bind(Bindings.not(inspector.routeReadyProperty()));
        VBox routeInspectionBox = new VBox(GAP);
        routeInspectionBox.getChildren().addAll(inspector.getView(), updateButton, deleteButton);

        return routeInspectionBox;
    }

    private Node createUserInfoBox() {
        Label usernameLabel = new Label();
        usernameLabel.textProperty().bind(Bindings.createStringBinding(
            () -> client.getAuth() == null ? "" : "Logged in as: " + client.getAuth().getLogin(),
            client.authProperty()
        ));
        Button logoutButton = new Button("Logout");
        logoutButton.setOnMouseClicked(e -> client.setAuth(null));
        HBox userInfo = new HBox(GAP);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.setPadding(new Insets(GAP));
        userInfo.getChildren().addAll(usernameLabel, logoutButton);

        Label testLabel = new Label();
        userInfo.getChildren().add(testLabel);
        testLabel.textProperty().bind(Bindings.createStringBinding(() -> ("Selected: " + (tableTab.getSelectedRoute() == null ? "none" : tableTab.getSelectedRoute().getId())), tableTab.selectedRouteProperty()));
        tableTab.selectedRouteProperty().addListener((o, oldVal, newVal) -> {
            System.out.println("Selection changed from " + (oldVal == null ? "null" : oldVal.getId()) + " to " + (newVal == null ? "null" : newVal.getId()));
        });

        return userInfo;
    }
}
