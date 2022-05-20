package com.dmtri.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import com.dmtri.client.views.ConnectionView;
import com.dmtri.client.views.LoginView;
import com.dmtri.client.views.MainView;
import com.dmtri.common.network.Response;
import com.dmtri.common.usermanagers.AuthCredentials;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class GraphicClient extends Application {
    private static final int WINDOW_SIZE = 500;
    private static final int SLEEP_TIME = 10;
    private ObjectSocketChannelWrapper channel;
    private Scene scene;
    private ObjectProperty<AuthCredentials> auth = new SimpleObjectProperty<>();
    private ConnectionView connectionView = new ConnectionView(this);
    private LoginView loginView = new LoginView(this);
    private MainView mainView = new MainView(this);

    public void start(Stage primaryStage) {
        scene = new Scene(connectionView.getView());

        primaryStage.setTitle("Route Manager");
        primaryStage.setWidth(WINDOW_SIZE);
        primaryStage.setHeight(WINDOW_SIZE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public ObjectSocketChannelWrapper getChannel() {
        return channel;
    }

    public AuthCredentials getAuth() {
        return auth.get();
    }

    public void setAuth(AuthCredentials auth) {
        if (auth == null) {
            scene.setRoot(loginView.getView());
        } else {
            scene.setRoot(mainView.getView());
        }
        this.auth.set(auth);
    }

    public ObjectProperty<AuthCredentials> authProperty() {
        return auth;
    }

    public void connect(InetSocketAddress address) {
        try {
            SocketChannel socket = SocketChannel.open();
            socket.connect(address);
            socket.configureBlocking(false);
            channel = new ObjectSocketChannelWrapper(socket);
            scene.setRoot(loginView.getView());
        } catch (IOException e) {
            new Alert(AlertType.ERROR, e.getLocalizedMessage()).showAndWait();
            channel = null;
        }
    }

    public void disconnect() {
        if (channel == null) {
            return;
        }
        try {
            channel.getSocket().close();
        } catch (IOException e) {
            new Alert(AlertType.ERROR, e.getLocalizedMessage()).showAndWait();
        }
        channel = null;
        scene.setRoot(connectionView.getView());
    }

    public Response sendMessage(Object msg) {
        try {
            channel.sendMessage(msg);

            while (!channel.checkForMessage()) {
                Thread.sleep(SLEEP_TIME);
            }

            Object payload = channel.getPayload();

            if (payload instanceof Response) {
                Response resp = (Response) payload;
                channel.clearInBuffer();
                return resp;
            }
            new Alert(AlertType.ERROR, "Invalid response").show();
            channel.clearInBuffer();
            return null;
        } catch (IOException | InterruptedException e) {
            new Alert(AlertType.ERROR, e.getLocalizedMessage()).show();
            disconnect();
            return null;
        }
    }
}
