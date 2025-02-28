package src.com.company;

import src.com.company.reflectors.SerialisationReflector;
import src.com.company.reflectors.DeSerialisationReflector;

public class Json1035 {

    // Méthode pour sérialiser un objet en JSON
    public String serialize(Object o) {
        if (o == null) {
            return "null";
        }

        // Utilisation du SerialisationReflector pour gérer tous les cas
        SerialisationReflector reflector = new SerialisationReflector(o.getClass());
        return reflector.serialize(o);
    }

    // Méthode pour désérialiser une chaîne JSON en objet
    public <T> T deserialize(String json, Class<T> toType) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        // Utilisation du DeSerialisationReflector pour reconstruire l'objet
        DeSerialisationReflector reflector = new DeSerialisationReflector(toType);
        return reflector.fromJson(json, toType);
    }
}
