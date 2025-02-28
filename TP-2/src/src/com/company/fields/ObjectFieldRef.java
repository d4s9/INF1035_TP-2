package src.com.company.fields;

import src.com.company.annotations.Ignored;
import src.com.company.reflectors.SerialisationReflector;

import java.lang.reflect.Field;

public class ObjectFieldRef extends FieldRef {

    private SerialisationReflector reflector; // Gère la sérialisation des objets complexes
    private Field field;

    // Constructeur qui initialise le champ et le reflector
    public ObjectFieldRef(Field field) {
        super(field);
        this.field = field;

        // Vérifier si le type n'est pas un type standard avant d'initialiser un reflector
        if (!field.getType().getName().startsWith("java.")) {
            this.reflector = new SerialisationReflector(field.getType());
        }
    }

    // Récupérer la valeur du champ pour une instance donnée
    @Override
    public Object getValue(Object instance) {
        try {
            field.setAccessible(true); // Permet d'accéder aux champs privés
            return field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retourner le nom du champ ou un alias s'il est défini
    @Override
    public String getName() {
        if (aliasAnnotation != null) {
            return aliasAnnotation.value(); // Utilise le nom de l'alias
        }
        return name; // Sinon, utilise le nom par défaut du champ
    }

    // Convertit l'objet du champ en format JSON
    @Override
    public String toJson(Object instance) {
        Object value = getValue(instance);
        if (value == null) {
            return "null";
        }

        // Utiliser le SerialisationReflector pour sérialiser l'objet complet
        if (reflector != null) {
            return reflector.serialize(value);
        }

        // Si aucun reflector, fallback sur toString ou une sérialisation basique
        return "\"" + value.toString() + "\"";
    }
}
