package com.dmtri.client.views;

import java.text.MessageFormat;
import java.util.Optional;

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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CommandsMenu extends Menu {
    private final GraphicClient client;

    public CommandsMenu(GraphicClient client) {
        super("Commands");
        textProperty().bind(client.getLocaleManager().getObservableStringByKey("commandsMenuName"));
        this.client = client;

        MenuItem add = new MenuItem();
        add.textProperty().bind(client.getLocaleManager().getObservableStringByKey("addCommand"));
        add.setOnAction(e -> displayAddRouteWindow());
        MenuItem info = new MenuItem();
        info.textProperty().bind(client.getLocaleManager().getObservableStringByKey("infoCommand"));
        info.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("info")));
        MenuItem printUniqueDistance = new MenuItem();
        printUniqueDistance.textProperty().bind(client.getLocaleManager().getObservableStringByKey("uniqueDistancesCommand"));
        printUniqueDistance.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("print_unique_distance")));
        MenuItem clear = new MenuItem();
        clear.textProperty().bind(client.getLocaleManager().getObservableStringByKey("clearCommand"));
        clear.setOnAction(e -> executeCommandWithEmptyBody("clear"));
        MenuItem removeAllByDistance = new MenuItem();
        removeAllByDistance.textProperty().bind(client.getLocaleManager().getObservableStringByKey("removeAllByDistanceCommand"));
        removeAllByDistance.setOnAction(e -> displayRemoveByDistanceDialog());
        MenuItem sumOfDistance = new MenuItem();
        sumOfDistance.textProperty().bind(client.getLocaleManager().getObservableStringByKey("sumOfDistancesCommand"));
        sumOfDistance.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("sum_of_distance")));

        getItems().addAll(add, info, printUniqueDistance, clear, removeAllByDistance, sumOfDistance);
    }

    private void displayRemoveByDistanceDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.titleProperty().bind(client.getLocaleManager().getObservableStringByKey("removeAllByDistanceCommand"));
        dialog.headerTextProperty().bind(client.getLocaleManager().getObservableStringByKey("distancePrompt"));
        dialog.contentTextProperty().bind(client.getLocaleManager().getObservableStringByKey("distanceLabel"));
        Optional<String> res = dialog.showAndWait();
        if (res.isPresent()) {
            client.sendMessage(new Request(
                "remove_all_by_distance",
                new RequestBody(new String[] {res.get()}),
                client.getAuth()
            ));
        }
    }

    private void displayAddRouteWindow() {
        Route emptyRoute = new Route();
        emptyRoute.setOwner(client.getAuth().getLogin());
        ObjectProperty<Route> routeProperty = new SimpleObjectProperty<>(null);
        RouteInspectorView inspector = new RouteInspectorView(client, routeProperty);
        routeProperty.set(emptyRoute);
        Button addButton = new Button();
        addButton.textProperty().bind(client.getLocaleManager().getObservableStringByKey("addCommand"));
        addButton.disableProperty().bind(Bindings.not(inspector.routeReadyProperty()));
        VBox stageBox = new VBox();
        stageBox.getChildren().addAll(inspector.getView(), addButton);
        stageBox.setAlignment(Pos.TOP_CENTER);
        stageBox.setPadding(new Insets(10));
        Stage routeStage = new Stage();
        routeStage.titleProperty().bind(client.getLocaleManager().getObservableStringByKey("addCommandTitle"));
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
            alert.titleProperty().bind(client.getLocaleManager().getObservableStringByKey("commandResultLabel"));
            alert.headerTextProperty().bind(client.getLocaleManager().getObservableStringByKey("commandSuccessLabel"));
            if (resp.getLocaleKey() == null) {
                alert.setContentText(resp.getMessage());
            } else {
                alert.contentTextProperty().bind(Bindings.createStringBinding(
                    () -> MessageFormat.format(client.getLocaleManager().getObservableStringByKey(resp.getLocaleKey()).get(), resp.getParams()), 
                    client.getLocaleManager().localeProperty())
                );
            }
            alert.show();
        }
    }
}
