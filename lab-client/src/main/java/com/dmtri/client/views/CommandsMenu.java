package com.dmtri.client.views;

import com.dmtri.client.GraphicClient;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.RequestBodyWithRoute;
import com.dmtri.common.network.Response;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CommandsMenu extends Menu {
    private final GraphicClient client;

    public CommandsMenu(GraphicClient client) {
        super("Commands");
        this.client = client;

        MenuItem add = new MenuItem("Add");
        add.setOnAction(e -> displayAddRouteWindow());
        MenuItem info = new MenuItem("Info");
        info.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("info")));
        MenuItem printUniqueDistance = new MenuItem("Print unique distances");
        printUniqueDistance.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("print_unique_distance")));
        MenuItem clear = new MenuItem("Clear");
        clear.setOnAction(e -> executeCommandWithEmptyBody("clear"));
        MenuItem sumOfDistance = new MenuItem("Sum of distances");
        sumOfDistance.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("sum_of_distance")));

        getItems().addAll(add, info, printUniqueDistance, clear, sumOfDistance);
    }

    private void displayAddRouteWindow() {
        Route emptyRoute = new Route();
        emptyRoute.setOwner(client.getAuth().getLogin());
        ObjectProperty<Route> routeProperty = new SimpleObjectProperty<>(null);
        RouteInspectorView inspector = new RouteInspectorView(client, routeProperty);
        routeProperty.set(emptyRoute);
        Button addButton = new Button("Add");
        addButton.disableProperty().bind(Bindings.not(inspector.routeReadyProperty()));
        VBox stageBox = new VBox();
        stageBox.getChildren().addAll(inspector.getView(), addButton);
        stageBox.setAlignment(Pos.TOP_CENTER);
        stageBox.setPadding(new Insets(10));
        Stage routeStage = new Stage();
        routeStage.setTitle("Add a route");
        routeStage.setScene(new Scene(stageBox));
        addButton.setOnMouseClicked(e -> {
            client.sendMessage(new Request(
                "add",
                new RequestBodyWithRoute(new String[] {}, inspector.getRoute()),
                client.getAuth()
            ));
            routeStage.close();
        });
        routeStage.show();
    }

    private Response executeCommandWithEmptyBody(String commandName) {
        return client.sendMessage(new Request(
            commandName,
            new RequestBody(new String[] {}), 
            client.getAuth())
        );
    }

    private void showCommandResultAsAlert(Response resp) {
        if (resp != null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Command Result");
            alert.setHeaderText("Command success");
            alert.setContentText(resp.getMessage());
            alert.show();
        }
    }
}
