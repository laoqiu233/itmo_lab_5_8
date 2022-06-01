package com.dmtri.client.views;

import java.util.Set;
import java.util.stream.Collectors;

import com.dmtri.client.GraphicClient;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.RequestBodyWithRoute;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainView {
    private static final int GAP = 20;
    private static final int SMALL_GAP = 7;
    private Parent view;
    private GraphicClient client;
    private RoutesTableView tableTab;
    private RoutesGraphicView graphicTab;
    private FilterView filterView;
    private RouteInspectorView inspector;
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
        filterView = new FilterView(client.getLocaleManager());
        filterView.filterProperty().addListener(listener);
        client.routesProperty().addListener(listener);

        BorderPane root = new BorderPane();
        root.setLeft(filterView.getView());
        tableTab = new RoutesTableView(filteredRoutes, client.getLocaleManager());
        graphicTab = new RoutesGraphicView(filteredRoutes, client.getLocaleManager());
        tableTab.selectedRouteProperty().bindBidirectional(graphicTab.selectedRouteProperty());
        Tab tab1 = new Tab("Routes (Table)", tableTab.getView());
        tab1.textProperty().bind(client.getLocaleManager().getObservableStringByKey("routesTable"));
        Tab tab2 = new Tab("Graph View", graphicTab.getView());
        tab2.textProperty().bind(client.getLocaleManager().getObservableStringByKey("routesGraph"));
        TabPane center = new TabPane(tab1, tab2);
        center.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        root.setCenter(center);
        root.setTop(createUserInfoBox());
        root.setRight(createRouteInspectionBox());

        this.view = root;
    }

    public Parent getView() {
        return view;
    }

    private void sendUpdatedRoute(MouseEvent e) {
        client.sendMessage(new Request(
            "update",
            new RequestBodyWithRoute(new String[] {}, inspector.getRoute()),
            client.getAuth()
        ));
    }

    private void deleteSelectedRoute(MouseEvent e) {
        client.sendMessage(new Request(
            "remove_by_id",
            new RequestBody(new String[] {Long.toString(inspector.getRoute().getId())}),
            client.getAuth()
        ));
    }

    private Node createRouteInspectionBox() {
        inspector = new RouteInspectorView(client, tableTab.selectedRouteProperty());
        Button updateButton = new Button("Update");
        updateButton.disableProperty().bind(Bindings.not(inspector.routeReadyProperty()));
        updateButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(updateButton, Priority.ALWAYS);
        updateButton.setOnMouseClicked(this::sendUpdatedRoute);
        Button deleteButton = new Button("Delete");
        deleteButton.disableProperty().bind(Bindings.not(inspector.routeReadyProperty()));
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(deleteButton, Priority.ALWAYS);
        deleteButton.setOnMouseClicked(this::deleteSelectedRoute);
        HBox buttonBox = new HBox(SMALL_GAP);
        buttonBox.setPadding(new Insets(0, SMALL_GAP, 0, SMALL_GAP));
        buttonBox.setMaxWidth(Double.MAX_VALUE);
        buttonBox.getChildren().addAll(updateButton, deleteButton);
        VBox routeInspectionBox = new VBox(GAP);
        routeInspectionBox.getChildren().addAll(inspector.getView(), buttonBox);

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
