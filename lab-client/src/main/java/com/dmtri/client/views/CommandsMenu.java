package com.dmtri.client.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

import com.dmtri.client.ConsoleClient;
import com.dmtri.client.GraphicClient;
import com.dmtri.client.LocaleManager;
import com.dmtri.common.LocaleKeys;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.RequestBodyWithRoute;
import com.dmtri.common.network.Response;
import com.dmtri.common.userio.BasicUserIO;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CommandsMenu extends Menu {
    private static final int GAP = 10;
    private final GraphicClient client;

    public CommandsMenu(GraphicClient client) {
        super("Commands");
        textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.COMMANDS_MENU_NAME));
        this.client = client;

        MenuItem add = new MenuItem();
        add.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.ADD_COMMAND));
        add.setOnAction(e -> displayAddRouteWindow());
        MenuItem info = new MenuItem();
        info.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.INFO_COMMAND));
        info.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("info")));
        MenuItem printUniqueDistance = new MenuItem();
        printUniqueDistance.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.UNIQUE_DISTANCES_COMMAND));
        printUniqueDistance.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("print_unique_distance")));
        MenuItem clear = new MenuItem();
        clear.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.CLEAR_COMMAND));
        clear.setOnAction(e -> executeCommandWithEmptyBody("clear"));
        MenuItem removeAllByDistance = new MenuItem();
        removeAllByDistance.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.REMOVE_ALL_BY_DISTANCE_COMMAND));
        removeAllByDistance.setOnAction(e -> displayRemoveByDistanceDialog());
        MenuItem sumOfDistance = new MenuItem();
        sumOfDistance.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.SUM_OF_DISTANCES_COMMAND));
        sumOfDistance.setOnAction(e -> showCommandResultAsAlert(executeCommandWithEmptyBody("sum_of_distance")));
        MenuItem executeScript = new MenuItem();
        executeScript.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.EXECUTE_SCRIPT_COMMAND));
        executeScript.setOnAction(e -> chooseScriptAndExecute());

        getItems().addAll(add, info, printUniqueDistance, clear, removeAllByDistance, sumOfDistance, executeScript);
    }

    private void displayRemoveByDistanceDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.titleProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.REMOVE_ALL_BY_DISTANCE_COMMAND));
        dialog.headerTextProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.DISTANCE_PROMPT));
        dialog.contentTextProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.DISTANCE_LABEL));
        Optional<String> res = dialog.showAndWait();
        if (res.isPresent()) {
            client.getNetwork().sendMessage(new Request(
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
        addButton.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.ADD_COMMAND));
        addButton.disableProperty().bind(Bindings.not(inspector.routeReadyProperty()));
        VBox stageBox = new VBox();
        stageBox.getChildren().addAll(inspector.getView(), addButton);
        stageBox.setAlignment(Pos.TOP_CENTER);
        stageBox.setPadding(new Insets(GAP));
        Stage routeStage = new Stage();
        routeStage.initOwner(client.getMainWindow());
        routeStage.initModality(Modality.WINDOW_MODAL);
        routeStage.titleProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.ADD_COMMAND_TITLE));
        routeStage.setScene(new Scene(stageBox));
        addButton.setOnMouseClicked(e -> {
            client.getNetwork().sendMessage(new Request(
                "add",
                new RequestBodyWithRoute(new String[] {}, inspector.getRoute()),
                client.getAuth()
            ));
            routeStage.close();
        });
        routeStage.showAndWait();
    }

    public void chooseScriptAndExecute() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.SCRIPT_CHOOSER_TITLE));
        File selectedFile = fileChooser.showOpenDialog(client.getMainWindow());
        if (selectedFile != null) {
            try (
                FileInputStream fileInput = new FileInputStream(selectedFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
            ) {
                BasicUserIO scriptIO = new BasicUserIO(fileInput, baos);
                scriptIO.setRepeatInput(true);

                ConsoleClient console = new ConsoleClient(
                    client.getNetwork().channelProperty().get().getSocket().getRemoteAddress(),
                    scriptIO
                );
                console.setAuth(client.getAuth());
                console.run();

                TextArea area = new TextArea();
                area.setEditable(false);
                area.setText(baos.toString());

                Stage resultWindow = new Stage();
                resultWindow.initOwner(client.getMainWindow());
                resultWindow.initModality(Modality.WINDOW_MODAL);
                resultWindow.titleProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.SCRIPT_RESULT_TITLE));
                resultWindow.setScene(new Scene(new BorderPane(area)));
                resultWindow.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR, e.getLocalizedMessage());
                alert.setTitle(LocaleManager.getObservableStringByKey(LocaleKeys.ERROR).get());
                alert.setContentText("");
                alert.showAndWait();
            }
        }
    }

    private Response executeCommandWithEmptyBody(String commandName) {
        return client.getNetwork().sendMessage(new Request(
            commandName,
            new RequestBody(new String[] {}),
            client.getAuth())
        );
    }

    private void showCommandResultAsAlert(Response resp) {
        if (resp != null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.titleProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.COMMAND_RESULT_LABEL));
            alert.headerTextProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.COMMAND_SUCCESS_LABEL));
            if (resp.getLocaleKey() == null) {
                alert.setContentText(resp.getMessage());
            } else {
                alert.contentTextProperty().bind(Bindings.createStringBinding(
                    () -> MessageFormat.format(LocaleManager.getObservableStringByKey(resp.getLocaleKey()).get(), resp.getParams()),
                    LocaleManager.localeProperty())
                );
            }
            alert.show();
        }
    }
}
