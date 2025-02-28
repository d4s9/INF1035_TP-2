package src.com.company.reflectors;

import src.com.company.fields.CollectionFieldRef;
import src.com.company.fields.FieldRef;
import src.com.company.fields.ObjectFieldRef;
import src.com.company.fields.SimpleFieldRef;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DeSerialisationReflector {

    private List<FieldRef> simpleFields;
    private List<FieldRef> objectFields;
    private List<FieldRef> collectionFields;

    // Constructeur
    public DeSerialisationReflector(Class<?> clazz) {
        this.simpleFields = new ArrayList<>();
        this.objectFields = new ArrayList<>();
        this.collectionFields = new ArrayList<>();

        // Analyse les champs pour les organiser
        analyzeFields(clazz);
    }

    // Analyse les champs de la classe pour les organiser en catégories
    private void analyzeFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(src.com.company.annotations.Ignored.class)) {
                continue; // Ignore les champs annotés avec @Ignored
            }

            if (isSimpleField(field)) {
                simpleFields.add(new SimpleFieldRef(field));
            } else if (isCollectionField(field)) {
                collectionFields.add(new CollectionFieldRef(field) );
            } else {
                objectFields.add(new ObjectFieldRef(field));
            }
        }
    }

    // Vérifie si un champ est un type simple (primitif ou String)
    private boolean isSimpleField(Field field) {
        Class<?> type = field.getType();
        return type.isPrimitive() ||
                type.equals(String.class) ||
                Number.class.isAssignableFrom(type) ||
                type.equals(Boolean.class) ||
                type.equals(Character.class);
    }

    // Vérifie si un champ est une collection
    private boolean isCollectionField(Field field) {
        return java.util.Collection.class.isAssignableFrom(field.getType()) || field.getType().isArray();
    }

    // Reconstruit un objet à partir d'une chaîne JSON
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance(); // Crée une nouvelle instance

            // Nettoyer les retours à la ligne dans le JSON
            json = json.replaceAll("\\s*\\n\\s*", "").trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1); // Retire les accolades externes
            }

            // Analyse manuelle des champs JSON
            int index = 0;
            while (index < json.length()) {
                // Trouver le début et la fin de la clé
                int keyStart = json.indexOf('"', index);
                if (keyStart == -1) break; // Si aucune autre clé n'existe, arrêter
                int keyEnd = json.indexOf('"', keyStart + 1);
                if (keyEnd == -1) throw new RuntimeException("JSON mal formé : clé non terminée");

                String key = json.substring(keyStart + 1, keyEnd).trim();

                // Trouver le séparateur ':'
                int colonIndex = json.indexOf(':', keyEnd + 1);
                if (colonIndex == -1) throw new RuntimeException("JSON mal formé : pas de ':' après la clé");

                // Trouver le début de la valeur
                int valueStart = colonIndex + 1;

                // Trouver la fin de la valeur
                int valueEnd = findValueEnd(json, valueStart);
                if (valueEnd == -1) throw new RuntimeException("JSON mal formé : valeur non terminée");

                String value = json.substring(valueStart, valueEnd).trim();

                // Avancer au prochain champ
                index = valueEnd + 1;

                // Ignorer les champs avec des valeurs non conformes
                if (value.equals("nul") || value.isEmpty()) {
                    value = "null"; // Normaliser les erreurs potentielles
                }

                // Traiter le champ trouvé
                FieldRef fieldRef = findFieldRef(key);
                if (fieldRef != null) {
                    Field field = fieldRef.getField();
                    field.setAccessible(true);

                    if (simpleFields.contains(fieldRef)) {
                        Object simpleValue = parseSimpleValue(cleanJsonValue(value), field.getType());
                        field.set(instance, simpleValue);
                    } else if (objectFields.contains(fieldRef)) {
                        if (!value.equals("null")) {
                            Object nestedObject = new DeSerialisationReflector(field.getType()).fromJson(value, field.getType());
                            field.set(instance, nestedObject);
                        } else {
                            System.out.println("L'objet contient un objet vide qui n'a pas pu être analysé !");
                            field.set(instance, null);
                        }
                    } else if (collectionFields.contains(fieldRef)) {
                        if (field.getType().isArray()) {
                            Object array = parseArray(cleanJsonValue(value), field);
                            field.set(instance, array);
                        } else {
                            List<Object> collection = parseCollection(cleanJsonValue(value), field);
                            field.set(instance, collection);
                        }
                    }
                }
            }

            return instance;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Veuillez ajouter un constructeur sans argument de type : " + clazz.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("La déserialisation a échoué ! " + clazz.getSimpleName());
        }
    }

    private int findValueEnd(String json, int start) {
        int index = start;
        int braceCount = 0;
        int bracketCount = 0;
        boolean inQuotes = false;

        while (index < json.length()) {
            char c = json.charAt(index);
            if (c == '"' && (index == 0 || json.charAt(index - 1) != '\\')) {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '{') braceCount++;
                else if (c == '}') braceCount--;
                else if (c == '[') bracketCount++;
                else if (c == ']') bracketCount--;

                if (braceCount == 0 && bracketCount == 0 && (c == ',' || index == json.length() - 1)) {
                    return index;
                }
            }
            index++;
        }

        return index;
    }


    // Nettoie une valeur JSON en retirant les guillemets extérieurs si nécessaire
    private String cleanJsonValue(String value) {
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1); // Retire les guillemets extérieurs
        }
        return value;
    }


    // Trouve le champ correspondant par nom
    private FieldRef findFieldRef(String key) {
        for (FieldRef fieldRef : simpleFields) {
            if (fieldRef.getName().equals(key)) return fieldRef;
        }
        for (FieldRef fieldRef : objectFields) {
            if (fieldRef.getName().equals(key)) return fieldRef;
        }
        for (FieldRef fieldRef : collectionFields) {
            if (fieldRef.getName().equals(key)) return fieldRef;
        }
        return null;
    }

    // Convertit une valeur JSON simple en type Java
    private Object parseSimpleValue(String value, Class<?> type) {
        if (type.equals(int.class) || type.equals(Integer.class)) {
            if(value == null) return 0;
            if(value.equals("null")) return 0;
            return Integer.parseInt(value);
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            if(value == null) return 0;
            if(value.equals("null")) return 0;
            return Double.parseDouble(value);
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            if(value == null) return 0;
            if(value.equals("null")) return 0;
            return Long.parseLong(value);}
        else if (type.equals(float.class) || type.equals(Float.class)) {
            if(value == null) return 0;
            if(value.equals("null")) return 0;
            return Float.parseFloat(value);
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            if(value == null) return false;
            if(value.equals("null")) return false;
            return Boolean.parseBoolean(value);
        }
        else if (type.equals(Character.class) || type.equals(char.class)) {
            if(value == null) return '0';
            if(value.equals("null")) return '0';
            return Character.valueOf(value.charAt(0));
        }
        else if (type.equals(String.class)) {
            if(value == null) return "null";
            if(value.equals("null")) return "null";
            return value.replace("\"", "");
        }
        throw new IllegalArgumentException("Unsupported simple type: " + type.getName());
    }

    private List<Object> parseCollection(String value, Field field) throws Exception {
        value = value.trim().substring(1, value.length() - 1); // Retire les crochets
        String[] elements = value.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        // Récupérer le type des éléments de la collection
        Class<?> elementType = getGenericType(field);

        List<Object> collection = new ArrayList<>();
        for (String element : elements) {
            String cleanedElement = cleanJsonValue(element.trim());
            if (isSimpleType(elementType)) {
                collection.add(parseSimpleValue(cleanedElement, elementType));
            } else {
                collection.add(new DeSerialisationReflector(elementType).fromJson(cleanedElement, elementType));
            }
        }

        return collection;
    }

    // Récupère le type générique des éléments d'une collection
    private Class<?> getGenericType(Field field) {
        if (field.getGenericType() instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType parameterizedType = (java.lang.reflect.ParameterizedType) field.getGenericType();
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        // Par défaut, retourne Object si le type générique n'est pas précisé
        return Object.class;
    }

    // Vérifie si un type est simple
    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                type.equals(String.class) ||
                Number.class.isAssignableFrom(type) ||
                type.equals(Boolean.class) ||
                type.equals(Character.class);
    }

    private Object parseArray(String value, Field field) throws Exception {
        value = value.trim().substring(1, value.length() - 1); // Retire les crochets
        String[] elements = value.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        Class<?> elementType = field.getType().getComponentType(); // Type des éléments du tableau
        Object array = java.lang.reflect.Array.newInstance(elementType, elements.length);

        for (int i = 0; i < elements.length; i++) {
            String cleanedElement = cleanJsonValue(elements[i].trim());
            if (isSimpleType(elementType)) {
                java.lang.reflect.Array.set(array, i, parseSimpleValue(cleanedElement, elementType));
            } else {
                java.lang.reflect.Array.set(array, i, new DeSerialisationReflector(elementType).fromJson(cleanedElement, elementType));
            }
        }

        return array;
    }

}
