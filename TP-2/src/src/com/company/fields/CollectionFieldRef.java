package src.com.company.fields;

import src.com.company.reflectors.SerialisationReflector;
import src.com.company.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class CollectionFieldRef extends FieldRef {

    private SerialisationReflector reflector;

    public CollectionFieldRef(Field field) {
        super(field); // Appel au constructeur parent
        if (Collection.class.isAssignableFrom(field.getType())) {
            Class<?> genericType = getGenericType(field);
            if (genericType != null) {
                this.reflector = new SerialisationReflector(genericType);
            } else {
                this.reflector = null; // Pas de type générique trouvé
            }
        }
    }

    @Override
    public Object getValue(Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            System.out.println("Impossible to access field " + field.getName());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return aliasAnnotation != null ? aliasAnnotation.value() : name;
    }

    @Override
    public String toJson(Object instance) {
        Object value = getValue(instance);
        if (value == null) {
            return "null";
        }

        // Si c'est une collection ou un tableau, sérialiser comme une liste JSON
        if (value instanceof Collection) {
            StringBuilder json = new StringBuilder("[");
            Collection<?> collection = (Collection<?>) value;
            for (Object item : collection) {
                json.append(reflector != null ? reflector.serialize(item) : serializeValue(item)).append(",");
            }
            if (json.charAt(json.length() - 1) == ',') {
                json.deleteCharAt(json.length() - 1);
            }
            json.append("]");
            return json.toString();
        } else if (value.getClass().isArray()) {
            StringBuilder json = new StringBuilder("[");
            Object[] array = (Object[]) value;
            for (Object item : array) {
                json.append(reflector != null ? reflector.serialize(item) : serializeValue(item)).append(",");
            }
            if (json.charAt(json.length() - 1) == ',') {
                json.deleteCharAt(json.length() - 1);
            }
            json.append("]");
            return json.toString();
        }

        // Si ce n'est ni une collection ni un tableau, retourne une chaîne JSON simple
        return serializeValue(value);
    }




    // Récupérer le type générique des éléments de la collection
    private Class<?> getGenericType(Field field) {
        try {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                return (Class<?>) paramType.getActualTypeArguments()[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Pas de type générique trouvé
    }
}
