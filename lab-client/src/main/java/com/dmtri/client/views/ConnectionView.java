package com.dmtri.client.views;

import java.net.InetSocketAddress;

import com.dmtri.client.GraphicClient;

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
        view = createLayout();
        this.client = client;
    }

    public Parent getView() {
        return view;
    }

    private Parent createLayout() {
        final Label addressLabel = new Label("Address");
        addressField.setPromptText("Enter the address");
        final Label portLabel = new Label("Port");
        portField.setPromptText("Enter the port");
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
