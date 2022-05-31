package com.dmtri.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.dmtri.client.views.CommandsMenu;
import com.dmtri.client.views.ConnectionView;
import com.dmtri.client.views.LoginView;
import com.dmtri.client.views.MainView;
import com.dmtri.common.models.Route;
import com.dmtri.common.network.Request;
import com.dmtri.common.network.RequestBody;
import com.dmtri.common.network.Response;
import com.dmtri.common.network.ResponseWithRoutes;
import com.dmtri.common.usermanagers.AuthCredentials;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphicClient extends Application {
    private static final int WINDOW_SIZE = 500;
    private static final int SLEEP_TIME = 100;
    private ObjectSocketChannelWrapper channel;
    private ObjectProperty<AuthCredentials> auth = new SimpleObjectProperty<>();
    private ObservableSet<Route> routes = FXCollections.observableSet();
    private RoutesThread routesThread = new RoutesThread();
    private ConnectionView connectionView = new ConnectionView(this);
    private LoginView loginView = new LoginView(this);
    private MainView mainView = new MainView(this);
    private Menu languageMenu = new Menu("Language");
    private Menu commandsMenu = new CommandsMenu(this);
    private MenuBar menuBar = new MenuBar(languageMenu);
    private BorderPane sceneRoot = new BorderPane();
    private Scene scene = new Scene(sceneRoot);

    public void start(Stage primaryStage) {
        routesThread.start();
        primaryStage.setTitle("Route Manager");
        primaryStage.setWidth(WINDOW_SIZE);
        primaryStage.setHeight(WINDOW_SIZE);
        sceneRoot.setTop(menuBar);
        sceneRoot.setCenter(connectionView.getView());
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
            routesThread.setWorking(false);
            sceneRoot.setCenter(loginView.getView());
            menuBar.getMenus().remove(commandsMenu);
        } else {
            routesThread.setWorking(true);
            sceneRoot.setCenter(mainView.getView());
            menuBar.getMenus().add(commandsMenu);
        }
        this.auth.set(auth);
    }

    public ObjectProperty<AuthCredentials> authProperty() {
        return auth;
    }

    public Set<Route> getRoutes() {
        return routes.stream().collect(Collectors.toSet());
    }

    public void setRoutes(Collection<Route> routes) {
        this.routes.retainAll(routes);
        this.routes.addAll(routes);
    }

    public ObservableSet<Route> routesProperty() {
        return routes;
    }

    public void connect(InetSocketAddress address) {
        try {
            SocketChannel socket = SocketChannel.open();
            socket.connect(address);
            socket.configureBlocking(false);
            channel = new ObjectSocketChannelWrapper(socket);
            sceneRoot.setCenter(loginView.getView());
        } catch (UnresolvedAddressException e) {
            new Alert(AlertType.ERROR, "The address you provided is invalid").showAndWait();
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
        sceneRoot.setCenter(connectionView.getView());
        setAuth(null);
        routesThread.setWorking(false);
    }

    public synchronized Response sendMessage(Object msg) {
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
            Platform.runLater(() -> {
                new Alert(AlertType.ERROR, e.getLocalizedMessage()).show();
                disconnect();
            });
            return null;
        }
    }

    private class RoutesThread extends Thread {
        private volatile boolean workFlag = false;

        RoutesThread() {
            this.setName("routes-fetching-thread");
            this.setDaemon(true);
        }

        public void setWorking(boolean flag) {
            workFlag = flag;
        }

        public void run() {
            try {
                while (true) {
                    if (workFlag) {
                        Response resp = sendMessage(new Request(
                            "show",
                            new RequestBody(new String[] {}),
                            getAuth()
                        ));

                        if (resp instanceof ResponseWithRoutes) {
                            ResponseWithRoutes rwr = (ResponseWithRoutes) resp;
                            Set<Route> newRoutes = new HashSet<>();

                            for (int i = 0; i < rwr.getRoutesCount(); i++) {
                                newRoutes.add(rwr.getRoute(i));
                            }

                            Platform.runLater(() -> setRoutes(newRoutes));
                        }

                        Thread.sleep(SLEEP_TIME);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
