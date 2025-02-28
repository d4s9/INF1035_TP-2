package src.com.company.fields;

import src.com.company.annotations.Alias;
import src.com.company.annotations.Ignored;

import java.lang.reflect.Field;

public abstract class FieldRef {

    protected String name;
    protected Alias aliasAnnotation;
    protected Ignored ignoredAnnotation;
    protected Field field;

    protected FieldRef(Field field) {
        this.field = field;
        this.name = field.getName();
        this.aliasAnnotation = field.getAnnotation(Alias.class);
        this.ignoredAnnotation = field.getAnnotation(Ignored.class);
    }

    public abstract Object getValue(Object instance);

    public abstract String getName();

    public abstract String toJson(Object instance);

    public Field getField() {
        return field;
    }

    // Méthode serializeValue pour sérialiser chaque élément de la collection
    protected String serializeValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        // Ajouter d'autres types de gestion si nécessaire
        return "\"" + value.toString() + "\"";  // Default
    }
}
