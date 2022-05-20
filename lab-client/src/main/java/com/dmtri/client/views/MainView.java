package com.dmtri.client.views;

import com.dmtri.client.GraphicClient;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MainView {
    private static final int GAP = 20;
    private GraphicClient client;
    private Parent view;

    public MainView(GraphicClient client) {
        this.client = client;
        view = createLayout();
    }

    public Parent getView() {
        return view;
    }

    private Parent createLayout() {
        BorderPane root = new BorderPane();
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
        root.setTop(userInfo);

        Tab tab1 = new Tab("Penis");
        Tab tab2 = new Tab("Titties");
        Tab tab3 = new Tab("Big bootie");
        TabPane center = new TabPane(tab1, tab2, tab3);
        center.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        root.setCenter(center);

        return root;
    }
}
