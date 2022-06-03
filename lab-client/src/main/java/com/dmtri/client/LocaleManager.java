package com.dmtri.client;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

/**
 * Class to create observable locales
 */
public final class LocaleManager {
    private static ResourceBundle bundle = ResourceBundle.getBundle("Locales", Locale.ENGLISH);
    private static ObjectProperty<Locale> localePorperty = new SimpleObjectProperty<>(Locale.ENGLISH);
    private static Map<String, StringProperty> observableStrings = new HashMap<>();

    private LocaleManager() { }

    public static ObjectProperty<Locale> localeProperty() {
        return localePorperty;
    }

    public static ObservableStringValue getObservableStringByKey(String key) {
        // Create observable property only when needed
        if (!observableStrings.containsKey(key)) {
            observableStrings.put(key, new SimpleStringProperty(bundle.getString(key)));
        }

        return observableStrings.get(key);
    }

    public static void setLocale(Locale newLocale) {
        Locale.setDefault(newLocale);
        bundle = ResourceBundle.getBundle("Locales", newLocale);
        observableStrings.keySet().forEach(x -> observableStrings.get(x).set(bundle.getString(x)));
        localePorperty.set(newLocale);
    }
}
