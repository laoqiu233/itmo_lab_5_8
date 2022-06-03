package com.dmtri.client.views;

import java.net.InetSocketAddress;

import com.dmtri.client.GraphicClient;
import com.dmtri.common.LocaleKeys;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;

public class ConnectionView {
    private static final int MAX_PORT = 25565;
    private static final double GAP = 10;
    private final Parent view;
    private final TextField addressField = new TextField();
    private final TextField portField = new TextField();
    private final Button connectButton = new Button("Connect");
    private final GraphicClient client;

    public ConnectionView(GraphicClient client) {
        this.client = client;
        view = createLayout();
    }

    public Parent getView() {
        return view;
    }

    private Parent createLayout() {
        connectButton.textProperty().bind(client.getLocaleManager().getObservableStringByKey(LocaleKeys.CONNECT_BUTTON));
        final Label addressLabel = new Label();
        addressLabel.textProperty().bind(client.getLocaleManager().getObservableStringByKey(LocaleKeys.ADDRESS_LABEL));
        addressField.promptTextProperty().bind(client.getLocaleManager().getObservableStringByKey(LocaleKeys.ADDRESS_PROMPT));
        final Label portLabel = new Label();
        portLabel.textProperty().bind(client.getLocaleManager().getObservableStringByKey(LocaleKeys.PORT_LABEL));
        portField.promptTextProperty().bind(client.getLocaleManager().getObservableStringByKey(LocaleKeys.PORT_PROMPT));
        portField.setTextFormatter(new TextFormatter<>(change -> {
            if (!change.getControlNewText().equals("")) {
                try {
                    int port = Integer.parseInt(change.getControlNewText());
                    if (port < 0 || port > MAX_PORT) {
                        change.setText("");
                        change.setRange(0, 0);
                    }
                } catch (NumberFormatException e) {
                    change.setText("");
                    change.setRange(0, 0);
                }
            }
            return change;
        }));
        connectButton.disableProperty().bind(Bindings.or(addressField.textProperty().isEmpty(), portField.textProperty().isEmpty()));
        connectButton.setOnMouseClicked(e -> client.connect(new InetSocketAddress(addressField.getText(), Integer.parseInt(portField.getText()))));

        GridPane connectionLayout = new GridPane();

        connectionLayout.add(addressLabel, 0, 0);
        connectionLayout.add(portLabel, 0, 1);
        connectionLayout.add(addressField, 1, 0);
        connectionLayout.add(portField, 1, 1);
        connectionLayout.setHgap(GAP);
        connectionLayout.setVgap(GAP);
        connectionLayout.setAlignment(Pos.CENTER);
        connectionLayout.add(connectButton, 0, 2, 2, 1);
        connectButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        return connectionLayout;
    }
}
