package com.dmtri.client.views;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.dmtri.client.LocaleManager;
import com.dmtri.common.models.Location;
import com.dmtri.common.models.Route;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class RoutesGraphicView {
    private static final int MAX_RGB = 256;
    private static final double Y_LO = -50;
    private static final double Y_HI = 50;
    private static final double MIN_RADIUS = 50;
    private static final double MAX_RADIUS = 200;
    private static final double GRAPH_FONT_SIZE = 20;
    private static final double GRAPH_TEXT_OUTLINE_STROKE = 3;
    private final Image backgroundImage = new Image("/map.png");
    private final ObjectProperty<Route> selectedRouteProperty = new SimpleObjectProperty<>(null);
    private final LocaleManager localeManager;
    private final Canvas canvas;
    private final ScrollPane view;
    private final Pane clickableShapes = new Pane();
    private final Set<GraphicRoute> graphicRoutes = new HashSet<>();
    private final SetChangeListener<Route> listener = new SetChangeListener<Route>() {
        @Override
        public void onChanged(Change<? extends Route> change) {
            if (change.wasAdded()) {
                graphicRoutes.add(new GraphicRoute(change.getElementAdded()));
            }

            if (change.wasRemoved()) {
                for (Iterator<GraphicRoute> it = graphicRoutes.iterator(); it.hasNext();) {
                    GraphicRoute graphicRoute = it.next();
                    if (graphicRoute.getRoute().equals(change.getElementRemoved())) {
                        graphicRoute.removeFromPane();
                        it.remove();
                    }
                }
            }

            redrawOnCanvas();
        }
    };

    public RoutesGraphicView(ObservableSet<Route> routeSet, LocaleManager localeManager) {
        this.localeManager = localeManager;
        canvas = new Canvas(backgroundImage.getWidth(), backgroundImage.getHeight());
        canvas.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
        canvas.getGraphicsContext2D().setTextBaseline(VPos.CENTER);
        clickableShapes.setMinSize(canvas.getWidth(), canvas.getHeight());
        routeSet.addListener(listener);
        selectedRouteProperty.addListener((o, oldV, newV) -> redrawOnCanvas());
        localeManager.localeProperty().addListener((o, oldV, newV) -> redrawOnCanvas());

        setupCoordsText();

        view = new ScrollPane();
        view.setContent(new StackPane(canvas, clickableShapes));

        redrawOnCanvas();
    }

    private void setupCoordsText() {
        Text coordsText = new Text();
        coordsText.setTextAlignment(TextAlignment.LEFT);
        coordsText.setTextOrigin(VPos.BOTTOM);
        clickableShapes.getChildren().add(coordsText);

        clickableShapes.setOnMouseMoved(e -> {
            coordsText.setX(e.getX());
            coordsText.setY(e.getY());
            coordsText.setText("X: " + (e.getX() - canvas.getWidth() / 2) + "\nZ: " + (e.getY() - canvas.getHeight() / 2));
        });
    }

    public Node getView() {
        return view;
    }

    public ObjectProperty<Route> selectedRouteProperty() {
        return selectedRouteProperty;
    }
    public Route getSelectedRoute() {
        return selectedRouteProperty.get();
    }
    public void setSelectedRoute(Route route) {
        selectedRouteProperty.set(route);
    }

    private void redrawOnCanvas() {
        canvas.getGraphicsContext2D().drawImage(backgroundImage, 0, 0);
        graphicRoutes.forEach(x -> x.draw());
    }

    private Circle createCircleFromLocation(Location location) {
        double centerX = canvas.getWidth() / 2;
        if (location.getCoordinates().getX() != null) {
            centerX += location.getCoordinates().getX();
            centerX = ((centerX % canvas.getWidth()) + canvas.getWidth()) % canvas.getWidth();
        }
        double centerY = canvas.getHeight() / 2;
        if (location.getCoordinates().getZ() != null) {
            centerY += location.getCoordinates().getZ();
            centerY = ((centerY % canvas.getHeight()) + canvas.getHeight()) % canvas.getHeight();
        }
        double clippedY = Math.max(Y_LO, Math.min(location.getCoordinates().getY(), Y_HI));
        double radius = (MAX_RADIUS - MIN_RADIUS) / (Y_HI - Y_LO) * clippedY + MIN_RADIUS;

        Circle newCircle = new Circle(centerX, centerY, radius);
        newCircle.setFill(Color.TRANSPARENT);

        return newCircle;
    }

    private class GraphicRoute {
        private final Route route;
        private final Circle startLocation;
        private final Circle endLocation;
        private final Line lineBetween;

        private final EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Clicked on " + route.getId());
                setSelectedRoute(route);
            }
        };

        GraphicRoute(Route route) {
            this.route = route;
            startLocation = createCircleFromLocation(route.getFrom());
            startLocation.setOnMouseClicked(clickHandler);
            endLocation = createCircleFromLocation(route.getTo());
            endLocation.setOnMouseClicked(clickHandler);
            lineBetween = new Line(startLocation.getCenterX(), startLocation.getCenterY(), endLocation.getCenterX(), endLocation.getCenterY());
            lineBetween.setStrokeWidth(
                (route.getDistance() == null ? 1 : Math.min(route.getDistance(), MIN_RADIUS))
            );
            lineBetween.setOnMouseClicked(clickHandler);
            lineBetween.setStroke(Color.TRANSPARENT);
            clickableShapes.getChildren().addAll(startLocation, endLocation, lineBetween);
        }

        Route getRoute() {
            return route;
        }

        void draw() {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            // Make a black outline if the route is selected
            if (route.equals(getSelectedRoute())) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(startLocation.getRadius() / 2);
                gc.strokeOval(startLocation.getCenterX() - startLocation.getRadius(), startLocation.getCenterY() - startLocation.getRadius(), startLocation.getRadius() * 2, startLocation.getRadius() * 2);
                gc.setLineWidth(endLocation.getRadius() / 2);
                gc.strokeOval(endLocation.getCenterX() - endLocation.getRadius(), endLocation.getCenterY() - endLocation.getRadius(), endLocation.getRadius() * 2, endLocation.getRadius() * 2);
                gc.setLineWidth(lineBetween.getStrokeWidth() * 2);
                gc.strokeLine(startLocation.getCenterX(), startLocation.getCenterY(), endLocation.getCenterX(), endLocation.getCenterY());
            }
            Random random = new Random(route.getOwner().hashCode());
            Color color = Color.rgb(
                random.nextInt(MAX_RGB),
                random.nextInt(MAX_RGB),
                random.nextInt(MAX_RGB)
            );  
            gc.setFill(color);
            gc.setStroke(color);
            gc.setLineWidth(lineBetween.getStrokeWidth());
            gc.strokeLine(startLocation.getCenterX(), startLocation.getCenterY(), endLocation.getCenterX(), endLocation.getCenterY());
            gc.fillOval(startLocation.getCenterX() - startLocation.getRadius(), startLocation.getCenterY() - startLocation.getRadius(), startLocation.getRadius() * 2, startLocation.getRadius() * 2);
            gc.fillOval(endLocation.getCenterX() - endLocation.getRadius(), endLocation.getCenterY() - endLocation.getRadius(), endLocation.getRadius() * 2, endLocation.getRadius() * 2);
            gc.setFont(new Font(GRAPH_FONT_SIZE));
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(GRAPH_TEXT_OUTLINE_STROKE);
            gc.strokeText(route.getFrom().getName(), startLocation.getCenterX(), startLocation.getCenterY());
            gc.strokeText(route.getTo().getName(), endLocation.getCenterX(), endLocation.getCenterY());
            gc.strokeText(route.getName()
                         + "\n" + localeManager.getObservableStringByKey("distanceLabel").get() + ": " + (route.getDistance() == null ? "null" : NumberFormat.getNumberInstance().format(route.getDistance()))
                         + "\n" + localeManager.getObservableStringByKey("ownerLabel").get() + ": " + route.getOwner(),
                         (startLocation.getCenterX() + endLocation.getCenterX()) / 2,
                         (startLocation.getCenterY() + endLocation.getCenterY()) / 2
            );
            gc.fillText(route.getFrom().getName(), startLocation.getCenterX(), startLocation.getCenterY());
            gc.fillText(route.getTo().getName(), endLocation.getCenterX(), endLocation.getCenterY());
            gc.fillText(route.getName()
                         + "\n" + localeManager.getObservableStringByKey("distanceLabel").get() + ": " + (route.getDistance() == null ? "null" : NumberFormat.getNumberInstance().format(route.getDistance()))
                         + "\n" + localeManager.getObservableStringByKey("ownerLabel").get() + ": " + route.getOwner(),
                         (startLocation.getCenterX() + endLocation.getCenterX()) / 2,
                         (startLocation.getCenterY() + endLocation.getCenterY()) / 2
            );
        }

        void removeFromPane() {
            clickableShapes.getChildren().removeAll(startLocation, endLocation);
        }
    }
}
