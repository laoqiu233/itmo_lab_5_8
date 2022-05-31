package com.dmtri.client.views;

import java.time.LocalDate;

import com.dmtri.client.GraphicClient;
import com.dmtri.client.util.NumberStringConverter;
import com.dmtri.client.util.StringConverter;
import com.dmtri.common.exceptions.InvalidFieldException;
import com.dmtri.common.models.AbstractValidator;
import com.dmtri.common.models.Coordinates;
import com.dmtri.common.models.Location;
import com.dmtri.common.models.Route;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class RouteInspectorView {
    private static final int GAP = 3;
    private static final int HORIZONTAL_PADDING = 8;
    private GraphicClient client;
    private ObjectProperty<Route> routeToInspectProperty;
    private ReadOnlyBooleanWrapper routeReadyProperty = new ReadOnlyBooleanWrapper(false);
    private BooleanProperty routeIsEditableProperty = new SimpleBooleanProperty(false);
    private Parent view;
    private ValidationField<String> nameField;
    private ValidationField<Double> distanceField;
    private ValidationField<String> fromNameField;
    private ValidationField<Long> fromXField;
    private ValidationField<Double> fromYField;
    private ValidationField<Long> fromZField;
    private ValidationField<String> toNameField;
    private ValidationField<Long> toXField;
    private ValidationField<Double> toYField;
    private ValidationField<Long> toZField;

    public RouteInspectorView(GraphicClient client, ObjectProperty<Route> routeToInspectProperty) {
        this.client = client;
        this.routeToInspectProperty = routeToInspectProperty;
        routeToInspectProperty.addListener((o, oldV, newV) -> fillFields(newV));
        routeIsEditableProperty.bind(Bindings.createBooleanBinding(RouteInspectorView.this::routeIsEditable, client.authProperty(), routeToInspectProperty));
        createAllFields();

        routeReadyProperty.bind(Bindings.and(nameField.valueReadyProperty,
                                Bindings.and(distanceField.valueReadyProperty,
                                Bindings.and(fromNameField.valueReadyProperty,
                                Bindings.and(fromXField.valueReadyProperty,
                                Bindings.and(fromYField.valueReadyProperty,
                                Bindings.and(fromZField.valueReadyProperty,
                                Bindings.and(toNameField.valueReadyProperty,
                                Bindings.and(toXField.valueReadyProperty,
                                Bindings.and(toYField.valueReadyProperty,
                                Bindings.and(toZField.valueReadyProperty,
                                routeIsEditableProperty)))))))))));

        VBox box = new VBox(GAP);
        box.setPadding(new Insets(GAP, HORIZONTAL_PADDING, GAP, HORIZONTAL_PADDING));
        box.getChildren().addAll(
            nameField.getComponent(),
            distanceField.getComponent(),
            fromNameField.getComponent(),
            fromXField.getComponent(),
            fromYField.getComponent(),
            fromZField.getComponent(),
            toNameField.getComponent(),
            toXField.getComponent(),
            toYField.getComponent(),
            toZField.getComponent()
        );
        this.view = box;
    }

    public Parent getView() {
        return view;
    }

    public ReadOnlyBooleanProperty routeReadyProperty() {
        return routeReadyProperty.getReadOnlyProperty();
    }

    public Route getRoute() {
        try {
            Route newRoute = new Route(
                routeToInspectProperty.get().getId(),
                nameField.getValue(),
                LocalDate.now(),
                new Location(
                    fromNameField.getValue(),
                    new Coordinates(
                        fromXField.getValue(),
                        fromYField.getValue(),
                        fromZField.getValue()
                    )
                ),
                new Location(
                    toNameField.getValue(),
                    new Coordinates(
                        toXField.getValue(),
                        toYField.getValue(),
                        toZField.getValue()
                    )
                ),
                distanceField.getValue()
            );
            newRoute.setOwner(routeToInspectProperty.get().getOwner());
            return newRoute;
        } catch (InvalidFieldException e) {
            return routeToInspectProperty.get();
        }
    }

    private void createAllFields() {
        nameField = new ValidationField<>("Name", x -> (x.isEmpty() ? null : x), Route.VALIDATOR::validateName);
        distanceField = new ValidationField<>("Distance", new NumberStringConverter<>(Double::parseDouble), Route.VALIDATOR::validateDistance);
        fromNameField = new ValidationField<>("Starting Name", x -> (x.isEmpty() ? null : x), Location.VALIDATOR::validateName);
        fromXField = new ValidationField<>("Starting X", new NumberStringConverter<>(Long::parseLong), Coordinates.VALIDATOR::validateX);
        fromYField = new ValidationField<>("Starting Y", new NumberStringConverter<>(Double::parseDouble), Coordinates.VALIDATOR::validateY);
        fromZField = new ValidationField<>("Starting Z", new NumberStringConverter<>(Long::parseLong), Coordinates.VALIDATOR::validateZ);
        toNameField = new ValidationField<>("Ending Name", x -> (x.isEmpty() ? null : x), Location.VALIDATOR::validateName);
        toXField = new ValidationField<>("Ending X", new NumberStringConverter<>(Long::parseLong), Coordinates.VALIDATOR::validateX);
        toYField = new ValidationField<>("Ending Y", new NumberStringConverter<>(Double::parseDouble), Coordinates.VALIDATOR::validateY);
        toZField = new ValidationField<>("Ending Z", new NumberStringConverter<>(Long::parseLong), Coordinates.VALIDATOR::validateZ);
    }

    private void fillFields(Route route) {
        if (route == null) {
            nameField.emptyValue();
            distanceField.emptyValue();
            fromNameField.emptyValue();
            fromXField.emptyValue();
            fromYField.emptyValue();
            fromZField.emptyValue();
            toNameField.emptyValue();
            toXField.emptyValue();
            toYField.emptyValue();
            toZField.emptyValue();
            return;
        }

        nameField.setValue(route.getName());
        distanceField.setValue(route.getDistance());
        fromNameField.setValue(route.getFrom().getName());
        fromXField.setValue(route.getFrom().getCoordinates().getX());
        fromYField.setValue(route.getFrom().getCoordinates().getY());
        fromZField.setValue(route.getFrom().getCoordinates().getZ());
        toNameField.setValue(route.getTo().getName());
        toXField.setValue(route.getTo().getCoordinates().getX());
        toYField.setValue(route.getTo().getCoordinates().getY());
        toZField.setValue(route.getTo().getCoordinates().getZ());
    }

    private boolean routeIsEditable() {
        if (routeToInspectProperty.get() == null || client.getAuth() == null) {
            return false;
        }

        return routeToInspectProperty.get().getOwner().equals(client.getAuth().getLogin());
    }

    private class ValidationField<T> {
        private StringConverter<T> converter;
        private AbstractValidator<T> validator;
        private Node component;
        private TextField valueField;
        private Label promptLabel = new Label();
        private BooleanProperty valueReadyProperty = new SimpleBooleanProperty(false);

        ValidationField(String fieldName, StringConverter<T> converter, AbstractValidator<T> validator) {
            this.converter = converter;
            this.validator = validator;
            Label fieldLabel = new Label(fieldName);
            promptLabel.setTextFill(Color.RED);
            valueField = new TextField();
            valueField.editableProperty().bind(routeIsEditableProperty);
            VBox mainBox = new VBox(GAP);
            mainBox.getChildren().addAll(fieldLabel, valueField, promptLabel);
            component = mainBox;

            valueField.textProperty().addListener((o, oldV, newV) -> validateAndUpdate(newV));
        }

        void validateAndUpdate(String newV) {
            promptLabel.setText("");
            valueReadyProperty.set(false);

            // If we aren't editing any routes just exit
            if (routeToInspectProperty.get() == null) {
                return;
            }

            T value = converter.convert(newV);
            if (value == null && !newV.isEmpty()) {
                promptLabel.setText("Invalid value provided");
                return;
            }

            try {
                validator.validate(value);
            } catch (InvalidFieldException e) {
                promptLabel.setText(e.getMessage());
                return;
            }

            valueReadyProperty.set(true);
        }

        void emptyValue() {
            valueField.setText("");
        }

        void setValue(T value) {
            valueField.setText(value == null ? "" : value.toString());
            // If we provide a route with null parameters, the text field
            // will not fire a changed event, in that case 
            // we manually call the method to prompt the user
            validateAndUpdate(valueField.getText());
        }

        T getValue() {
            return converter.convert(valueField.getText());
        }

        Node getComponent() {
            return component;
        }
    }
}
