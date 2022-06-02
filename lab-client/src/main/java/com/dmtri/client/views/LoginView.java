package com.dmtri.client.views;

import java.text.MessageFormat;

import com.dmtri.client.GraphicClient;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithAuthCredentials;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {
    private static final double GAP = 10;
    private static final double HEADER_FONT_SIZE = 50;
    private Node view;
    private GraphicClient client;
    private TextField loginField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button registerButton;
    private Button disconnectButton;
    private StringProperty promptMsg = new SimpleStringProperty("");

    public LoginView(GraphicClient client) {
        this.client = client;
        view = createLayout();
    }

    public Node getView() {
        return view;
    }

    private void disableButtons() {
        loginButton.setDisable(true);
        registerButton.setDisable(true);
        disconnectButton.setDisable(true);
    }

    private void enableButtons() {
        loginButton.setDisable(false);
        registerButton.setDisable(false);
        disconnectButton.setDisable(false);
    }

    private void sendLoginRequest(MouseEvent event) {
        event.consume();
        disableButtons();
        promptMsg.unbind();
        promptMsg.set("");
        Response resp = client.sendMessage(new Request(
            "login",
            new RequestBody(new String[] {loginField.getText(), passwordField.getText()}),
            null
        ));

        if (resp != null) {
            if (resp instanceof ResponseWithAuthCredentials) {
                client.setAuth(((ResponseWithAuthCredentials) resp).getAuthCredentials());
            } else {
                if (resp.getLocaleKey() == null) {
                    promptMsg.set(resp.getMessage());
                } else {
                    promptMsg.bind(Bindings.createStringBinding(
                        () -> MessageFormat.format(client.getLocaleManager().getObservableStringByKey(resp.getLocaleKey()).get(), resp.getParams()),
                        client.getLocaleManager().localeProperty())
                    );
                }
            }
        }

        enableButtons();
    }

    private void sendRegisterRequest(MouseEvent event) {
        event.consume();
        disableButtons();
        promptMsg.unbind();
        promptMsg.set("");
        Response resp = client.sendMessage(new Request(
            "register",
            new RequestBody(new String[] {loginField.getText(), passwordField.getText()}),
            null
        ));

        if (resp != null) {
            if (resp instanceof ResponseWithAuthCredentials) {
                client.setAuth(((ResponseWithAuthCredentials) resp).getAuthCredentials());
            } else {
                if (resp.getLocaleKey() == null) {
                    promptMsg.set(resp.getMessage());
                } else {
                    promptMsg.bind(Bindings.createStringBinding(
                        () -> MessageFormat.format(client.getLocaleManager().getObservableStringByKey(resp.getLocaleKey()).get(), resp.getParams()),
                        client.getLocaleManager().localeProperty())
                    );
                }
            }
        }

        enableButtons();
    }

    private Node createLayout() {
        final Label headerLabel = new Label();
        headerLabel.textProperty().bind(client.getLocaleManager().getObservableStringByKey("loginHeader"));
        headerLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, HEADER_FONT_SIZE));
        final Label subheaderLabel = new Label();
        subheaderLabel.textProperty().bind(client.getLocaleManager().getObservableStringByKey("loginSubHeader"));

        loginButton = new Button();
        loginButton.textProperty().bind(client.getLocaleManager().getObservableStringByKey("loginButton"));
        loginButton.setOnMouseClicked(this::sendLoginRequest);
        registerButton = new Button("Register");
        registerButton.textProperty().bind(client.getLocaleManager().getObservableStringByKey("registerButton"));
        registerButton.setOnMouseClicked(this::sendRegisterRequest);
        disconnectButton = new Button("Disconnect");
        disconnectButton.textProperty().bind(client.getLocaleManager().getObservableStringByKey("disconnectButton"));
        disconnectButton.setOnMouseClicked(e -> client.disconnect());
        HBox buttonGroup = new HBox(GAP);
        buttonGroup.getChildren().addAll(loginButton, registerButton, disconnectButton);
        buttonGroup.setAlignment(Pos.CENTER);

        Label prompt = new Label();
        prompt.textProperty().bind(promptMsg);
        prompt.setTextFill(Color.RED);

        VBox box = new VBox(GAP);
        box.getChildren().addAll(headerLabel, subheaderLabel, createLoginPassGrid(), prompt, buttonGroup);
        box.setAlignment(Pos.CENTER);

        return box;
    }

    private Node createLoginPassGrid() {
        final Label loginLabel = new Label();
        loginLabel.textProperty().bind(client.getLocaleManager().getObservableStringByKey("loginLabel"));
        loginField = new TextField();
        loginField.promptTextProperty().bind(client.getLocaleManager().getObservableStringByKey("loginPrompt"));
        final Label passwordLabel = new Label();
        passwordLabel.textProperty().bind(client.getLocaleManager().getObservableStringByKey("passwordLabel"));
        passwordField = new PasswordField();
        passwordField.promptTextProperty().bind(client.getLocaleManager().getObservableStringByKey("passwordPrompt"));

        GridPane loginPassGrid = new GridPane();
        loginPassGrid.add(loginLabel, 0, 0);
        loginPassGrid.add(passwordLabel, 0, 1);
        loginPassGrid.add(loginField, 1, 0);
        loginPassGrid.add(passwordField, 1, 1);
        loginPassGrid.setHgap(GAP);
        loginPassGrid.setVgap(GAP);
        loginPassGrid.setAlignment(Pos.CENTER);

        return loginPassGrid;
    }
}
