package src.com.company.reflectors;

import src.com.company.annotations.Alias;
import src.com.company.fields.*;
import java.lang.reflect.Field;
import java.util.*;

public class SerialisationReflector {

    private Map<Object, Boolean> memory;

    private List<FieldRef> simpleFields = new ArrayList<>();
    private List<FieldRef> objectFields = new ArrayList<>();
    private List<FieldRef> collectionFields = new ArrayList<>();

    public SerialisationReflector(Class<?> type) {
        this.memory = new IdentityHashMap<>();
        memory.put(type, true); // Ajouter l'objet à la mémoire dès sa détection

        for (Field field : type.getDeclaredFields()) {
            try {
                // Ignorer les classes Java standard
                if (field.getDeclaringClass().getName().startsWith("java.")) {
                    continue;
                }
                // Ignorer les champs annotés avec @Ignored
                else if (field.isAnnotationPresent(src.com.company.annotations.Ignored.class)) {
                    System.out.println("Le champ " + field.getName() + " a été ignoré à cause de l'annotation @Ignored!");
                    continue;
                }
                field.setAccessible(true);
                // Vérifier si le champ est de type simple
                if (field.getType().isPrimitive() ||
                        field.getType().equals(String.class) ||
                        Number.class.isAssignableFrom(field.getType()) ||
                        field.getType().equals(Boolean.class) ||
                        field.getType().equals(Character.class)) {
                    simpleFields.add(new SimpleFieldRef(field));
                }
                // Vérifier si le champ est une collection ou un tableau
                else if (Collection.class.isAssignableFrom(field.getType()) ||
                        field.getType().isArray()) {
                    collectionFields.add(new CollectionFieldRef(field));
                }
                // Sinon, c'est un objet complexe
                else {
                    if (memory.containsKey(field.getType())) {
                        System.out.println("Référence circulaire détectée pour : " + field.getName());
                        continue;
                    }
                    objectFields.add(new ObjectFieldRef(field));
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement du champ : " + field.getName() + ". Raison : " + e.getMessage());
            }
        }
    }

    // Méthode pour sérialiser un objet en JSON
    public String serialize(Object instance) {
        if (instance == null) return "null";
        // Vérifiez si l'objet est déjà dans la mémoire pour éviter une boucle infinie
        if (memory.containsKey(instance)) {
            return "\"circular_reference\""; // Retournez une indication pour les références circulaires
        }

        if (instance instanceof String || instance instanceof Character || instance instanceof Number || instance instanceof Boolean)
            return serializeValue(instance);
        else if (instance instanceof Collection || instance.getClass().isArray()) {
            String serialized = serializeCollectionOrArray(instance);
            memory.put(instance, true); // Mémoriser la collection/array
            return serialized;
        }

        StringBuilder jsonBuilder = new StringBuilder("{");
        StringJoiner fieldJsonJoiner = new StringJoiner(",");

        // Ajouter dans la mémoire avant de sérialiser pour éviter les références circulaires
        memory.put(instance, true);

        // Sérialiser les champs simples
        for (FieldRef fieldRef : simpleFields) {
            if(fieldRef.getValue(instance) == null) {
                System.out.println("La valeur du field " + fieldRef.getField().getName() + " est null !");
                continue;
            }
            String jsonValue = fieldRef.toJson(instance);
            String fieldName = getFieldNameWithAlias(fieldRef.getField());
            fieldJsonJoiner.add("\"" + fieldName + "\":" + jsonValue);
        }
        // Sérialiser les objets
        for (FieldRef fieldRef : objectFields) {
            if(fieldRef.getValue(instance) == null) {
                System.out.println("La valeur du field " + fieldRef.getField().getName() + " est null !");
                continue;
            }
            String jsonValue = fieldRef.toJson(instance);
            String fieldName = getFieldNameWithAlias(fieldRef.getField());
            fieldJsonJoiner.add("\"" + fieldName + "\":" + jsonValue);
        }
        // Sérialiser les collections et tableaux
        for (FieldRef fieldRef : collectionFields) {
            if(fieldRef.getValue(instance) == null) {
                System.out.println("La valeur du field " + fieldRef.getField().getName() + " est null !");
                continue;
            }
            String jsonValue = fieldRef.toJson(instance);
            String fieldName = getFieldNameWithAlias(fieldRef.getField());
            fieldJsonJoiner.add("\"" + fieldName + "\":" + jsonValue);
        }
        jsonBuilder.append(fieldJsonJoiner);
        jsonBuilder.append("}");

        // Mettre à jour la mémoire avec l'objet entièrement sérialisé
        String finalJson = jsonBuilder.toString();
        memory.put(instance, true);
        return finalJson;
    }

    // Méthode utilitaire pour sérialiser une valeur simple
    public String serializeValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String || value instanceof Character) {
            return "\"" + value + "\"";
        }
        if (value instanceof Boolean || value instanceof Number) {
            return value.toString();
        }
        return "\"" + value + "\""; // Cas général pour les types simples
    }


    // Méthode utilitaire pour sérialiser les collections ou tableaux
    private String serializeCollectionOrArray(Object value) {
        if (value == null) {
            return "null";
        }

        StringBuilder jsonBuilder = new StringBuilder("[");
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            StringJoiner collectionJoiner = new StringJoiner(",");
            for (Object item : collection) {
                collectionJoiner.add(item instanceof String || item instanceof Character ? "\"" + item + "\"" : serializeValue(item));
            }
            jsonBuilder.append(collectionJoiner);
        } else if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            StringJoiner arrayJoiner = new StringJoiner(",");
            for (Object item : array) {
                arrayJoiner.add(item instanceof String || item instanceof Character ? "\"" + item + "\"" : serializeValue(item));
            }
            jsonBuilder.append(arrayJoiner);
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    // Méthode pour obtenir le nom du champ avec l'alias si présent
    private String getFieldNameWithAlias(Field field) {
        Alias alias = field.getAnnotation(Alias.class);
        if (alias != null) {
            return alias.value();  // Utiliser l'alias spécifié
        }
        return field.getName();  // Utiliser le nom du champ par défaut
    }
}