package com.dmtri.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import com.dmtri.client.views.CommandsMenu;
import com.dmtri.client.views.ConnectionView;
import com.dmtri.client.views.LoginView;
import com.dmtri.client.views.MainView;
import com.dmtri.common.LocaleKeys;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphicClient extends Application {
    private static final int WINDOW_SIZE = 500;
    private static final long SLEEP_TIME = 100;
    private final Timer routesTimer = new Timer("routes-fetch-thread", true);
    private final TimerTask routeFetcher = new TimerTask() {
        @Override
        public void run() {
            if (doRouteFetch) {
                Response resp = network.sendMessage(new Request(
                    "show",
                    new RequestBody(new String[] {}),
                    getAuth()
                ));

                if (resp instanceof ResponseWithRoutes) {
                    ResponseWithRoutes rwr = (ResponseWithRoutes) resp;
                    Set<Route> newRoutes = new HashSet<>(Arrays.asList(rwr.getRoutes()));

                    Platform.runLater(() -> setRoutes(newRoutes));
                }
            }
        }
    };
    private volatile boolean doRouteFetch = false;
    private final GraphicClientNet network = new GraphicClientNet();
    private final ObjectProperty<AuthCredentials> auth = new SimpleObjectProperty<>();
    private final ObservableSet<Route> routes = FXCollections.observableSet();
    private final ConnectionView connectionView = new ConnectionView(this);
    private final LoginView loginView = new LoginView(this);
    private final MainView mainView = new MainView(this);
    private final Menu languageMenu = new Menu("Language");
    private final Menu commandsMenu = new CommandsMenu(this);
    private final MenuBar menuBar = new MenuBar(languageMenu);
    private final BorderPane sceneRoot = new BorderPane();
    private final Scene scene = new Scene(sceneRoot);
    private Stage mainWindow;

    @Override
    public void init() {
        // Create language menu
        languageMenu.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.LANGUAGE_MENU_NAME));
        RadioMenuItem englishMenuItem = new RadioMenuItem("English");
        englishMenuItem.setOnAction(e -> LocaleManager.setLocale(Locale.ENGLISH));
        RadioMenuItem russianMenuItem = new RadioMenuItem("Русский");
        russianMenuItem.setOnAction(e -> LocaleManager.setLocale(Locale.forLanguageTag("ru-RU")));
        RadioMenuItem romanianMenuItem = new RadioMenuItem("Limba Română");
        romanianMenuItem.setOnAction(e -> LocaleManager.setLocale(Locale.forLanguageTag("ro")));
        RadioMenuItem latvianMenuItem = new RadioMenuItem("Latviešu Valoda");
        latvianMenuItem.setOnAction(e -> LocaleManager.setLocale(Locale.forLanguageTag("lv")));
        RadioMenuItem mexicanSpanishMenuItem = new RadioMenuItem("Español Mexicano");
        mexicanSpanishMenuItem.setOnAction(e -> LocaleManager.setLocale(Locale.forLanguageTag("es-MX")));
        ToggleGroup group = new ToggleGroup();
        englishMenuItem.setToggleGroup(group);
        russianMenuItem.setToggleGroup(group);
        romanianMenuItem.setToggleGroup(group);
        latvianMenuItem.setToggleGroup(group);
        mexicanSpanishMenuItem.setToggleGroup(group);
        englishMenuItem.setSelected(true);
        languageMenu.getItems().addAll(englishMenuItem, russianMenuItem, romanianMenuItem, latvianMenuItem, mexicanSpanishMenuItem);
    }

    @Override
    public void start(Stage primaryStage) {
        mainWindow = primaryStage;

        routesTimer.schedule(routeFetcher, 0L, SLEEP_TIME);

        primaryStage.titleProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.LOGIN_HEADER));
        primaryStage.setWidth(WINDOW_SIZE);
        primaryStage.setHeight(WINDOW_SIZE);
        sceneRoot.setTop(menuBar);
        sceneRoot.setCenter(connectionView.getView());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Attach listeners for connection and disconnection
        network.channelProperty().addListener((o, oldVal, newVal) -> {
            if (newVal == null) {
                setAuth(null);
                sceneRoot.setCenter(connectionView.getView());
            } else {
                sceneRoot.setCenter(loginView.getView());
            }
        });
    }

    @Override
    public void stop() {
        try {
            network.closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GraphicClientNet getNetwork() {
        return network;
    }

    public Stage getMainWindow() {
        return mainWindow;
    }

    public ObjectProperty<AuthCredentials> authProperty() {
        return auth;
    }
    public AuthCredentials getAuth() {
        return auth.get();
    }
    public void setAuth(AuthCredentials auth) {
        if (auth == null) {
            doRouteFetch = false;
            sceneRoot.setCenter(loginView.getView());
            menuBar.getMenus().remove(commandsMenu);
        } else {
            doRouteFetch = true;
            sceneRoot.setCenter(mainView.getView());
            menuBar.getMenus().add(commandsMenu);
        }
        this.auth.set(auth);
    }

    public ObservableSet<Route> routesProperty() {
        return routes;
    }
    public Set<Route> getRoutes() {
        return routes.stream().collect(Collectors.toSet());
    }
    public void setRoutes(Collection<Route> routes) {
        this.routes.retainAll(routes);
        this.routes.addAll(routes);
    }
}
