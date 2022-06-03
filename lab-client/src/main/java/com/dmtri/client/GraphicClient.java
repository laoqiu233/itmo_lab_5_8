package com.dmtri.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
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
import com.dmtri.common.userio.BasicUserIO;
import com.dmtri.common.usermanagers.AuthCredentials;
import com.dmtri.common.util.TerminalColors;

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
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GraphicClient extends Application {
    private static final int WINDOW_SIZE = 500;
    private static final int SLEEP_TIME = 100;
    private final LocaleManager localeManager = new LocaleManager(Locale.ENGLISH);
    private Stage mainWindow;
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
        TerminalColors.doColoring(false);
        mainWindow = primaryStage;

        // Create language menu
        languageMenu.textProperty().bind(localeManager.getObservableStringByKey(LocaleKeys.LANGUAGE_MENU_NAME));
        RadioMenuItem englishMenuItem = new RadioMenuItem("English");
        englishMenuItem.setOnAction(e -> localeManager.setLocale(Locale.ENGLISH));
        RadioMenuItem russianMenuItem = new RadioMenuItem("Русский");
        russianMenuItem.setOnAction(e -> localeManager.setLocale(Locale.forLanguageTag("ru-RU")));
        RadioMenuItem romanianMenuItem = new RadioMenuItem("Limba Română");
        romanianMenuItem.setOnAction(e -> localeManager.setLocale(Locale.forLanguageTag("ro")));
        RadioMenuItem latvianMenuItem = new RadioMenuItem("Latviešu Valoda");
        latvianMenuItem.setOnAction(e -> localeManager.setLocale(Locale.forLanguageTag("lv")));
        RadioMenuItem mexicanSpanishMenuItem = new RadioMenuItem("Español Mexicano");
        mexicanSpanishMenuItem.setOnAction(e -> localeManager.setLocale(Locale.forLanguageTag("es-MX")));
        ToggleGroup group = new ToggleGroup();
        englishMenuItem.setToggleGroup(group);
        russianMenuItem.setToggleGroup(group);
        romanianMenuItem.setToggleGroup(group);
        latvianMenuItem.setToggleGroup(group);
        mexicanSpanishMenuItem.setToggleGroup(group);
        englishMenuItem.setSelected(true);
        languageMenu.getItems().addAll(englishMenuItem, russianMenuItem, romanianMenuItem, latvianMenuItem, mexicanSpanishMenuItem);

        routesThread.start();
        primaryStage.titleProperty().bind(localeManager.getObservableStringByKey(LocaleKeys.LOGIN_HEADER));
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

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public ObjectProperty<AuthCredentials> authProperty() {
        return auth;
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

    public void chooseScriptAndExecute() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(localeManager.getObservableStringByKey(LocaleKeys.SCRIPT_CHOOSER_TITLE));
        File selectedFile = fileChooser.showOpenDialog(mainWindow);
        if (selectedFile != null) {
                try (FileInputStream fileInput = new FileInputStream(selectedFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                BasicUserIO scriptIO = new BasicUserIO(fileInput, baos);
                scriptIO.setRepeatInput(true);

                ConsoleClient console = new ConsoleClient(
                    channel.getSocket().getRemoteAddress(),
                    scriptIO
                );
                console.setAuth(getAuth());
                console.run();

                TextArea area = new TextArea();
                area.setEditable(false);
                area.setText(baos.toString());

                Stage resultWindow = new Stage();
                resultWindow.titleProperty().bind(localeManager.getObservableStringByKey(LocaleKeys.SCRIPT_RESULT_TITLE));
                resultWindow.setScene(new Scene(new BorderPane(area)));
                resultWindow.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect(InetSocketAddress address) {
        try {
            SocketChannel socket = SocketChannel.open();
            socket.connect(address);
            socket.configureBlocking(false);
            channel = new ObjectSocketChannelWrapper(socket);
            sceneRoot.setCenter(loginView.getView());
        } catch (UnresolvedAddressException e) {
            new Alert(AlertType.ERROR, localeManager.getObservableStringByKey(LocaleKeys.INVALID_ADDRESS).get()).showAndWait();
        } catch (IOException e) {
            new Alert(AlertType.ERROR, e.getLocalizedMessage()).showAndWait();
            channel = null;
        }
    }

    public void disconnect() {
        routesThread.setWorking(false);
        if (channel != null) {
            try {
                channel.getSocket().close();
            } catch (IOException e) {
                new Alert(AlertType.ERROR, e.getLocalizedMessage()).showAndWait();
            }
        }
        channel = null;
        setAuth(null);
        sceneRoot.setCenter(connectionView.getView());
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
            new Alert(AlertType.ERROR, localeManager.getObservableStringByKey(LocaleKeys.INVALID_RESPONSE).get()).show();
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
            setName("routes-fetching-thread");
            setDaemon(true);
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
                return;
            }
        }
    }
}
