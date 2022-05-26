package com.dmtri.client.views;

import java.util.function.Predicate;

import com.dmtri.client.util.NumberStringConverter;
import com.dmtri.client.util.StringConverter;
import com.dmtri.common.models.Route;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class FilterView {
    private static final int GAP = 10;
    private static final int HEADER_SIZE = 20;
    private static final int MIN_WIDTH = 100;
    private final ObjectProperty<Predicate<Route>> filterProperty = new SimpleObjectProperty<>();
    private final ChangeListener<Predicate<Route>> filterChangeListener = (o, oldVal, newVal) -> filterProperty.set(newVal);
    private final Parent view;

    public FilterView() {
        Label headerLabel = new Label("Filter");
        headerLabel.setFont(new Font(HEADER_SIZE));

        ChoiceBox<FilterConfigurator> filterFieldChoice = createChoiceBox();

        VBox box = new VBox(GAP);
        box.getChildren().addAll(headerLabel, filterFieldChoice);
        box.setPadding(new Insets(GAP));
        box.setMinWidth(MIN_WIDTH);

        filterFieldChoice.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                box.getChildren().remove(oldValue.getView());
                oldValue.filterProperty().removeListener(filterChangeListener);
            }
            if (newValue != null) {
                box.getChildren().add(newValue.getView());
                filterProperty.set(newValue.filterProperty.get());
                newValue.filterProperty().addListener(filterChangeListener);
            }
        });
        filterFieldChoice.getSelectionModel().select(0);

        this.view = box;
    }

    public Parent getView() {
        return view;
    }

    public Predicate<Route> getFilter() {
        return filterProperty.get();
    }

    public void setFilter(Predicate<Route> filter) {
        filterProperty.set(filter);
    }

    public ObjectProperty<Predicate<Route>> filterProperty() {
        return filterProperty;
    }

    private ChoiceBox<FilterConfigurator> createChoiceBox() {
        ChoiceBox<FilterConfigurator> filterFieldChoice = new ChoiceBox<>();
        filterFieldChoice.getItems().addAll(
            new FilterConfigurator("None"),
            new ComparableFilterConfigurator<Long>("ID", x -> x.getId(), new NumberStringConverter<>(Long::parseLong)),
            new StringFilterConfigurator("Name", x -> x.getName()),
            new ComparableFilterConfigurator<Double>("Distance", x -> x.getDistance(), new NumberStringConverter<>(Double::parseDouble)),
            new StringFilterConfigurator("Starting location", x -> x.getFrom().getName()),
            new ComparableFilterConfigurator<Long>("Start X", x -> x.getFrom().getCoordinates().getX(), new NumberStringConverter<>(Long::parseLong)),
            new ComparableFilterConfigurator<Double>("Start Y", x -> x.getFrom().getCoordinates().getY(), new NumberStringConverter<>(Double::parseDouble)),
            new ComparableFilterConfigurator<Long>("Start Z", x -> x.getFrom().getCoordinates().getZ(), new NumberStringConverter<>(Long::parseLong)),
            new StringFilterConfigurator("Ending location", x -> x.getTo().getName()),
            new ComparableFilterConfigurator<Long>("End X", x -> x.getTo().getCoordinates().getX(), new NumberStringConverter<>(Long::parseLong)),
            new ComparableFilterConfigurator<Double>("End Y", x -> x.getTo().getCoordinates().getY(), new NumberStringConverter<>(Double::parseDouble)),
            new ComparableFilterConfigurator<Long>("End Z", x -> x.getTo().getCoordinates().getZ(), new NumberStringConverter<>(Long::parseLong)),
            new StringFilterConfigurator("Owner", x -> x.getOwner())
        );

        return filterFieldChoice;
    }

    private static class FilterConfigurator {
        private Node view;
        private ObjectProperty<Predicate<Route>> filterProperty = new SimpleObjectProperty<>(x -> true);
        private String fieldName;

        FilterConfigurator(String fieldName) {
            this.fieldName = fieldName;
            setView(new Label("No filter applied"));
        }

        Node getView() {
            return view;
        }

        void setView(Node view) {
            this.view = view;
        }

        ObjectProperty<Predicate<Route>> filterProperty() {
            return filterProperty;
        }

        @Override
        public String toString() {
            return fieldName;
        }
    }

    private static class ComparableFilterConfigurator<T extends Comparable<T>> extends FilterConfigurator {
        private ChoiceBox<Operation> operationChoice;
        private TextField operandField;
        private Label errorPrompt;
        private Callback<Route, T> valueGetter;
        private StringConverter<T> converter;

        ComparableFilterConfigurator(String fieldName, Callback<Route, T> valueGetter, StringConverter<T> converter) {
            super(fieldName);
            this.valueGetter = valueGetter;
            this.converter = converter;
            operationChoice = new ChoiceBox<>();
            operationChoice.getItems().addAll(Operation.values());
            operationChoice.getSelectionModel().select(0);
            operandField = new TextField();
            operandField.setPromptText("Operand");
            errorPrompt = new Label();
            errorPrompt.setTextFill(Color.RED);

            VBox box = new VBox(GAP);
            box.getChildren().addAll(operationChoice, operandField, errorPrompt);
            setView(box);

            // If anything changes trigger the property set event
            operationChoice.getSelectionModel().selectedItemProperty().addListener(x -> {
                filterProperty().set(createFilter());
            });
            operandField.textProperty().addListener(x -> filterProperty().set(createFilter()));
        }

        Predicate<Route> createFilter() {
            errorPrompt.setText("");
            T operand = converter.convert(operandField.getText());
            Operation selectedOperation = operationChoice.getSelectionModel().getSelectedItem();

            // If no operand is available then no filter is applied
            if (operand == null) {
                if (!operandField.getText().isEmpty()) {
                    errorPrompt.setText("Invalid operand value");
                }
                return x -> true;
            }

            return x -> {
                T value = valueGetter.call(x);
                // Remove empty cells
                if (value == null) {
                    return false;
                }

                return selectedOperation.check(value.compareTo(operand));
            };
        }

        private enum Operation {
            GT(">", x -> x > 0), GE(">=", x -> x >= 0),
            EQ("=", x -> x == 0), LE("<=", x -> x <= 0),
            LT("<", x -> x < 0);

            private String verbal;
            private Predicate<Integer> operation;

            Operation(String verbal, Predicate<Integer> operation) {
                this.verbal = verbal;
                this.operation = operation;
            }

            boolean check(int x) {
                return operation.test(x);
            }

            @Override
            public String toString() {
                return verbal;
            }
        }
    }

    private class StringFilterConfigurator extends FilterConfigurator {
        private TextField searchField;

        StringFilterConfigurator(String fieldName, Callback<Route, String> valueGetter) {
            super(fieldName);
            searchField = new TextField();
            searchField.setPromptText("Search for...");
            searchField.textProperty().addListener(o -> filterProperty().set(x -> valueGetter.call(x).contains(searchField.getCharacters())));
            setView(searchField);
        }
    }
}
