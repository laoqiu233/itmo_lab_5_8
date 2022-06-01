package com.dmtri.client.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
    private static final double MIN_RADIUS = 10;
    private static final double MAX_RADIUS = 100;
    private final Image backgroundImage = new Image("/map.png");
    private final ObjectProperty<Route> selectedRouteProperty = new SimpleObjectProperty<>(null);
    private Canvas canvas;
    private ScrollPane view;
    private Pane clickableShapes = new Pane();
    private Set<GraphicRoute> graphicRoutes = new HashSet<>();
    private Map<String, Color> ownerColors = new HashMap<>();

    public RoutesGraphicView(ObservableSet<Route> routeSet) {
        canvas = new Canvas(backgroundImage.getWidth(), backgroundImage.getHeight());
        canvas.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
        canvas.getGraphicsContext2D().setTextBaseline(VPos.CENTER);
        clickableShapes.setMinSize(canvas.getWidth(), canvas.getHeight());
        routeSet.addListener(new SetChangeListener<Route>() {
            @Override
            public void onChanged(Change<? extends Route> change) {
                if (change.wasAdded()) {
                    if (!ownerColors.containsKey(change.getElementAdded().getOwner())) {
                        Random random = new Random();
                        ownerColors.put(change.getElementAdded().getOwner(), Color.rgb(
                            random.nextInt(256),
                            random.nextInt(256),
                            random.nextInt(256)
                        ));
                    }
                    graphicRoutes.add(new GraphicRoute(change.getElementAdded()));
                }

                if (change.wasRemoved()) {
                    Set<String> notRemovedOwners = new HashSet<>();
                    for (Iterator<GraphicRoute> it = graphicRoutes.iterator(); it.hasNext(); ) {
                        GraphicRoute graphicRoute = it.next();
                        if (graphicRoute.getRoute().equals(change.getElementRemoved())) {
                            graphicRoute.removeFromPane();
                            it.remove();
                        } else {
                            notRemovedOwners.add(graphicRoute.getRoute().getOwner());
                        }
                    }
                    ownerColors.keySet().removeIf(x -> !notRemovedOwners.contains(x));
                }

                redrawOnCanvas();
            }
        });
        selectedRouteProperty.addListener((o, oldV, newV) -> redrawOnCanvas());

        Text coordsText = new Text();
        coordsText.setTextAlignment(TextAlignment.LEFT);
        coordsText.setTextOrigin(VPos.BOTTOM);
        clickableShapes.getChildren().add(coordsText);

        view = new ScrollPane();
        view.setContent(new StackPane(canvas, clickableShapes));
        clickableShapes.setOnMouseMoved(e -> {
            coordsText.setX(e.getX());
            coordsText.setY(e.getY());
            coordsText.setText("X: " + (e.getX() - canvas.getWidth() / 2) + "\nY: " + (e.getY() - canvas.getHeight() / 2)); 
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
        double clippedY = Math.max(-50, Math.min(location.getCoordinates().getY(), 50));
        double radius = (MAX_RADIUS - MIN_RADIUS) / 100 * clippedY + MIN_RADIUS;

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
                gc.setLineWidth(startLocation.getRadius() * 0.5);
                gc.strokeOval(startLocation.getCenterX() - startLocation.getRadius(), startLocation.getCenterY() - startLocation.getRadius(), startLocation.getRadius() * 2, startLocation.getRadius() * 2);
                gc.setLineWidth(endLocation.getRadius() * 0.5);
                gc.strokeOval(endLocation.getCenterX() - endLocation.getRadius(), endLocation.getCenterY() - endLocation.getRadius(), endLocation.getRadius() * 2, endLocation.getRadius() * 2);
                gc.setLineWidth(lineBetween.getStrokeWidth() * 2);
                gc.strokeLine(startLocation.getCenterX(), startLocation.getCenterY(), endLocation.getCenterX(), endLocation.getCenterY());
            }
            gc.setFill(ownerColors.get(route.getOwner()));
            gc.setStroke(ownerColors.get(route.getOwner()));
            gc.setLineWidth(lineBetween.getStrokeWidth());
            gc.strokeLine(startLocation.getCenterX(), startLocation.getCenterY(), endLocation.getCenterX(), endLocation.getCenterY()); 
            gc.fillOval(startLocation.getCenterX() - startLocation.getRadius(), startLocation.getCenterY() - startLocation.getRadius(), startLocation.getRadius() * 2, startLocation.getRadius() * 2);
            gc.fillOval(endLocation.getCenterX() - endLocation.getRadius(), endLocation.getCenterY() - endLocation.getRadius(), endLocation.getRadius() * 2, endLocation.getRadius() * 2);
            gc.setFont(new Font(20));
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(3);
            gc.strokeText(route.getFrom().getName(), startLocation.getCenterX(), startLocation.getCenterY());
            gc.strokeText(route.getTo().getName(), endLocation.getCenterX(), endLocation.getCenterY());
            gc.strokeText(route.getName() + "\nDistance: " + route.getDistance() + "\nOwned by: " + route.getOwner(), (startLocation.getCenterX() + endLocation.getCenterX()) / 2, (startLocation.getCenterY() + endLocation.getCenterY()) / 2);
            gc.fillText(route.getFrom().getName(), startLocation.getCenterX(), startLocation.getCenterY());
            gc.fillText(route.getTo().getName(), endLocation.getCenterX(), endLocation.getCenterY());
            gc.fillText(route.getName() + "\nDistance: " + route.getDistance() + "\nOwned by: " + route.getOwner(), (startLocation.getCenterX() + endLocation.getCenterX()) / 2, (startLocation.getCenterY() + endLocation.getCenterY()) / 2);
        }

        void removeFromPane() {
            clickableShapes.getChildren().removeAll(startLocation, endLocation);
        }
    }
}
