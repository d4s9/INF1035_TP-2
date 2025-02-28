package src.com.company;

import java.util.List;

import src.com.company.test.Person;
import src.com.company.test.Address;

public class Main {
    public static void main(String[] args) {
        Json1035 json1035 = new Json1035();

        // Crée un objet Address et un objet Personne avec une collection et un tableau
        Address address = new Address("406 rue du trou perdu", "Au lac à poche");
        Person person = new Person("Jimmy","Neutron", 30, true,
                50000.5, 123456789, 'j', null, address,
                List.of("Kelly", "Julien", "Jay"), new String[]{"819-446-7770", "418-664-5555"},
                new Person("Joe","Mama", 69, false,
                        1, 69420, 'L', 380,
                        new Address("Mom's house", "idk"),
                        List.of("Nobody"), new String[]{"911"}, null));

        // Sérialise l'objet Person
        String json = json1035.serialize(person);
        System.out.println("Serialized JSON: " + json);
        // Sérialise le même objet Person
        String json2 = json1035.serialize(person);
        System.out.println("\nSerialized JSON: " + json2);

        // Désérialise le JSON en objet Person
        Person deserializedPerson = json1035.deserialize(json, Person.class);
        System.out.println(deserializedPerson.toString());


//Références circulaires complexes
        System.out.println("\nRéférences circulaire complexe: ");
        Person p1 = new Person("Alice", "Wonderland", 25, true, 30000.0, 123, 'A', 55,
                new Address("1st Street", "Wonderland"),
                List.of("Bob", "Charlie"),
                new String[] {"123-456"}, null);

        Person p2 = new Person("Bob", "Builder", 30, false, 40000.0, 456, 'B', 70,
                new Address("2nd Street", "Builderland"),
                List.of("Alice"),
                new String[] {"987-654"}, p1);

// Références croisées
        System.out.println("\nRéférences croisée: ");
        p1.setBestFriend(p2);
        p2.setBestFriend(p1);

// Test
        System.out.println(new Json1035().serialize(p1));
        System.out.println();

//Collections contenant des objets
        System.out.println("\nCollections contenant des objets: ");
        List<Object> mixedList = List.of(
                new Person("John", "Doe", 35, true, 50000.0, 789, 'J', 80, null, null, null, null),
                "A simple string",
                12345, // Un entier
                true, // Un booléen
                new Person("Jane", "Smith", 28, false, 60000.0, 101, 'S', 65, null, null, null, null) // Un autre objet
        );

// Test
        System.out.println(new Json1035().serialize(mixedList));
        System.out.println();

//Objets imbriqués profondément
        System.out.println("\nObjets imbriqués profondément: ");
        Address mainAddress = new Address("Main Street", "Metropolis");
        Address secondaryAddress = new Address("Secondary Street", "Smallville");
        Person p = new Person("Clark", "Kent", 29, true, 100000.0, 789, 'C', 90,
                mainAddress, List.of("Lois", "Jimmy"),
                new String[] {"123-456"},
                new Person("Lex", "Luthor", 40, false, 90000.0, 987, 'L', 75, secondaryAddress, null, null, null));

// Test
        System.out.println(new Json1035().serialize(p));
        System.out.println();

//Collections et tableaux vides
        System.out.println("\nCollections et tableaux vides: ");
        Person person3 = new Person("Empty", "Fields", 0, false, 0, 0, 'E', 0,
                null, List.of(), new String[] {}, null);

// Test
        System.out.println(new Json1035().serialize(person3));
        System.out.println();

//Objets avec des champs null
        System.out.println("\nObjets avec des champs null: ");
        Person person4 = new Person("Null", null, 0, false, 0, 0, 'N', null,
                null, null, null, null);

// Test
        System.out.println(new Json1035().serialize(person4));
        System.out.println();

//Alias et annotations @Ignored
        System.out.println("\nAlias et annotations @Ignored: ");
        Person person5 = new Person("AliasTest", "ShouldBeIgnored", 22, true, 1000, 1, 'A', 50,
                new Address("Alias Street", "City"),
                null, null, null);

// Test
// Vérifie que :
//name est remplacé par Nom_De_Personne.
//lastName n'apparaît pas dans le JSON.
        System.out.println(new Json1035().serialize(person5));
        System.out.println();

//Objets avec des primitives et des wrappers
        System.out.println("\nObjets avec des primitives et des wrappers: ");
        Person person6 = new Person("Primitive", "Wrapper", 25, true, 5000.5, 123456, 'P', 100,
                null, null, null, null);

// Test
        System.out.println(new Json1035().serialize(person6));
        System.out.println();

//Collections imbriquées
        System.out.println("\nCollections imbriquées: ");
        List<List<String>> nestedList = List.of(
                List.of("A", "B", "C"),
                List.of("D", "E", "F")
        );

// Test
        System.out.println(new Json1035().serialize(nestedList));


        //Désérialisation d'un objet simple
        System.out.println("\nDésérialisation d'un objet simple");
        String json3 = "{\"Nom_De_Personne\":\"Alice\",\"age\":25,\"isActive\":true,\"salary\":3000.0,\"id\":12345,\"initial\":\"A\",\"weight\":60}";
        Person person2 = new Json1035().deserialize(json3, Person.class);
        System.out.println(person2);

        //Désérialisation avec des champs imbriqués
        System.out.println("\nDésérialisation avec des champs imbriqués");
        String json4 = "{\"Nom_De_Personne\":\"Bob\",\"age\":30,\"isActive\":false,\"address\":{\"street\":\"Main Street\",\"city\":\"Metropolis\"}}";
        Person person7 = new Json1035().deserialize(json4, Person.class);
        System.out.println(person7);

        //Désérialisation d'un tableau ou collection
        System.out.println("\nDésérialisation d'un tableau ou collection");
        String json5 = "{\"Nom_De_Personne\":\"Charlie\",\"friends\":[\"Alice\",\"Bob\"]}";
        Person person8 =  new Json1035().deserialize(json5, Person.class);
        System.out.println(person8);

        //Désérialisation avec tableau de primitives
        System.out.println("\nDésérialisation avec tableau de primitives");
        String json6 = "{\"Nom_De_Personne\":\"Dave\",\"phoneNumbers\":[\"123-456-7890\",\"987-654-3210\"]}";
        Person person9 = new Json1035().deserialize(json6, Person.class);
        System.out.println(person9);

        //Désérialisation avec références circulaires
        System.out.println("\nDésérialisation avec références circulaires");
        String json7 = "{\"Nom_De_Personne\":\"Eve\",\"bestFriend\":{\"Nom_De_Personne\":\"Mallory\",\"bestFriend\":null}}";
        Person person10 = new Json1035().deserialize(json7, Person.class);
        System.out.println(person10);

        //Désérialisation avec des champs ignorés
        System.out.println("\nDésérialisation avec des champs ignorés");
        String json8 = "{\"Nom_De_Personne\":\"Frank\",\"lastName\":\"IgnoredField\",\"age\":40}";
        Person person11 = new Json1035().deserialize(json8, Person.class);
        System.out.println(person11);

        //Désérialisation avec alias
        System.out.println("\nDésérialisation avec alias");
        String json9 = "{\"Nom_De_Personne\":\"Grace\",\"age\":35}";
        Person person12 = new Json1035().deserialize(json9, Person.class);
        System.out.println(person12);


        //Désérialisation avec valeurs nulles
        System.out.println("\nDésérialisation avec valeurs nulles");
        String json10 = "{\"Nom_De_Personne\":null,\"age\":null,\"address\":null}";
        Person person13 = new Json1035().deserialize(json10, Person.class);
        System.out.println(person13);

        //Désérialisation d'un objet complexe
        System.out.println("\nDésérialisation d'un objet complexe");
        String json11 = "{\"Nom_De_Personne\":\"Hannah\",\"age\":28,\"isActive\":true,\"salary\":4500.5,\"address\":{\"street\":\"Broadway\",\"city\":\"New York\"},\"friends\":[\"Alice\",\"Bob\",\"Charlie\"],\"phoneNumbers\":[\"123-456-7890\",\"987-654-3210\"]}";
        Person person14 = new Json1035().deserialize(json11, Person.class);
        System.out.println(person14);


    }
}

