package com.dmtri.client.views;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import com.dmtri.client.LocaleManager;
import com.dmtri.client.util.NumberStringConverter;
import com.dmtri.client.util.StringConverter;
import com.dmtri.common.LocaleKeys;
import com.dmtri.common.models.Route;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
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
    private final List<FilterConfigurator> configurators = new LinkedList<>();
    private final ObjectProperty<Predicate<Route>> filterProperty = new SimpleObjectProperty<>();
    private final ChangeListener<Predicate<Route>> filterChangeListener = (o, oldVal, newVal) -> filterProperty.set(newVal);
    private final Parent view;

    public FilterView() {
        Label headerLabel = new Label();
        headerLabel.setFont(new Font(HEADER_SIZE));
        headerLabel.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.FILTER_LABEL));

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
        configurators.add(new FilterConfigurator(LocaleKeys.NONE_LABEL));
        configurators.add(new ComparableFilterConfigurator<Long>(LocaleKeys.ID_LABEL, x -> x.getId(), new NumberStringConverter<>(Long::parseLong)));
        configurators.add(new StringFilterConfigurator(LocaleKeys.NAME_LABEL, x -> x.getName()));
        configurators.add(new ComparableFilterConfigurator<Double>(LocaleKeys.DISTANCE_LABEL, x -> x.getDistance(), new NumberStringConverter<>(Double::parseDouble)));
        configurators.add(new DateFilterConfigurator(LocaleKeys.CREATION_DATE_LABEL));
        configurators.add(new StringFilterConfigurator(LocaleKeys.STARTING_LOCATION_NAME_LABEL, x -> x.getFrom().getName()));
        configurators.add(new ComparableFilterConfigurator<Long>(LocaleKeys.STARTING_LOCATION_X_LABEL, x -> x.getFrom().getCoordinates().getX(), new NumberStringConverter<>(Long::parseLong)));
        configurators.add(new ComparableFilterConfigurator<Double>(LocaleKeys.STARTING_LOCATION_Y_LABEL, x -> x.getFrom().getCoordinates().getY(), new NumberStringConverter<>(Double::parseDouble)));
        configurators.add(new ComparableFilterConfigurator<Long>(LocaleKeys.STARTING_LOCATION_Z_LABEL, x -> x.getFrom().getCoordinates().getZ(), new NumberStringConverter<>(Long::parseLong)));
        configurators.add(new StringFilterConfigurator(LocaleKeys.ENDING_LOCATION_NAME_LABEL, x -> x.getTo().getName()));
        configurators.add(new ComparableFilterConfigurator<Long>(LocaleKeys.ENDING_LOCATION_X_LABEL, x -> x.getTo().getCoordinates().getX(), new NumberStringConverter<>(Long::parseLong)));
        configurators.add(new ComparableFilterConfigurator<Double>(LocaleKeys.ENDING_LOCATION_Y_LABEL, x -> x.getTo().getCoordinates().getY(), new NumberStringConverter<>(Double::parseDouble)));
        configurators.add(new ComparableFilterConfigurator<Long>(LocaleKeys.ENDING_LOCATION_Z_LABEL, x -> x.getTo().getCoordinates().getZ(), new NumberStringConverter<>(Long::parseLong)));
        configurators.add(new StringFilterConfigurator(LocaleKeys.OWNER_LABEL, x -> x.getOwner()));
        ChoiceBox<FilterConfigurator> filterFieldChoice = new ChoiceBox<>();
        filterFieldChoice.getItems().addAll(configurators);

        // Refresh locale
        LocaleManager.localeProperty().addListener((o, oldV, newV) -> {
            FilterConfigurator prevChoice = filterFieldChoice.getValue();
            filterFieldChoice.getItems().clear();
            filterFieldChoice.getItems().addAll(configurators);
            filterFieldChoice.getSelectionModel().select(prevChoice);
        });

        return filterFieldChoice;
    }

    private class FilterConfigurator {
        private Node view;
        private ObjectProperty<Predicate<Route>> filterProperty = new SimpleObjectProperty<>(x -> true);
        private String localeKey;

        FilterConfigurator(String localeKey) {
            this.localeKey = localeKey;
            Label noFilterLabel = new Label();
            noFilterLabel.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.NO_FILTER_LABEL));
            setView(noFilterLabel);
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
            return LocaleManager.getObservableStringByKey(localeKey).get();
        }
    }

    private class ComparableFilterConfigurator<T extends Comparable<T>> extends FilterConfigurator {
        private ChoiceBox<Operation> operationChoice;
        private TextField operandField;
        private Label errorPrompt;
        private Callback<Route, T> valueGetter;
        private StringConverter<T> converter;

        ComparableFilterConfigurator(String localeKey, Callback<Route, T> valueGetter, StringConverter<T> converter) {
            super(localeKey);
            this.valueGetter = valueGetter;
            this.converter = converter;
            operationChoice = new ChoiceBox<>();
            operationChoice.getItems().addAll(Operation.values());
            operationChoice.getSelectionModel().select(0);
            operandField = new TextField();
            operandField.promptTextProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.OPERAND_LABEL));
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
            errorPrompt.textProperty().unbind();
            errorPrompt.setText("");
            T operand = converter.convert(operandField.getText());
            Operation selectedOperation = operationChoice.getSelectionModel().getSelectedItem();

            // If no operand is available then no filter is applied
            if (operand == null) {
                if (!operandField.getText().isEmpty()) {
                    errorPrompt.textProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.INVALID_OPERAND_LABEL));
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
    }

    private class StringFilterConfigurator extends FilterConfigurator {
        private TextField searchField;

        StringFilterConfigurator(String localeKey, Callback<Route, String> valueGetter) {
            super(localeKey);
            searchField = new TextField();
            searchField.promptTextProperty().bind(LocaleManager.getObservableStringByKey(LocaleKeys.SEARCH_PROMPT_LABEL));
            searchField.textProperty().addListener(o -> filterProperty().set(x -> valueGetter.call(x).contains(searchField.getCharacters())));
            setView(searchField);
        }
    }

    private class DateFilterConfigurator extends FilterConfigurator {
        DateFilterConfigurator(String localeKey) {
            super(localeKey);
            DatePicker datePicker = new DatePicker();
            datePicker.valueProperty().addListener((o, oldV, newV) -> {
                if (newV == null) {
                    setFilter(x -> true);
                } else {
                    setFilter(x -> x.getCreationDate().equals(newV));
                }
            });
            setView(datePicker);
        }
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
