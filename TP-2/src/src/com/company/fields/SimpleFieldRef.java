package src.com.company.fields;

import java.lang.reflect.Field;

public class SimpleFieldRef extends FieldRef {

    private Field field;

    // Constructeur pour initialiser le champ
    public SimpleFieldRef(Field field) {
        super(field); // Appel au constructeur de la classe parent FieldRef
        this.field = field;
    }

    // Retourne la valeur du champ dans l'instance donnée
    @Override
    public Object getValue(Object instance) {
        try {
            field.setAccessible(true); // Permet d'accéder aux champs privés
            return field.get(instance); // Retourne la valeur du champ
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retourne le nom du champ ou un alias s'il est défini
    @Override
    public String getName() {
        if (aliasAnnotation != null) {
            return aliasAnnotation.value(); // Utilise le nom défini par l'annotation Alias
        }
        return name; // Sinon, retourne le nom du champ
    }

    // Sérialise le champ en JSON (valeurs simples ou String)
    @Override
    public String toJson(Object instance) {
        Object value = getValue(instance);
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
}
