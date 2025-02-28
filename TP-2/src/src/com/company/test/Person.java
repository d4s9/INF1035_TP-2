package src.com.company.test;
import src.com.company.annotations.Alias;
import src.com.company.annotations.Ignored;

import java.util.List;

public class Person {
    @Alias("Nom_De_Personne")
    private String name;
    @Ignored private String lastName;
    private int age;
    private boolean isActive;
    private double salary;
    private long id;
    private char initial;
    private Integer weight;

    // Champs pour tester ObjectFieldRef et CollectionFieldRef
    private Address address; // Objet (test ObjectFieldRef)
    private List<String> friends; // Collection (test CollectionFieldRef)
    private String[] phoneNumbers; // Tableau (test CollectionFieldRef)

    //Référence circulaire
    private Person bestFriend;

    // Constructeur sans arguments (par défaut)
    public Person() {
        // Initialiser les valeurs par défaut si nécessaire
        this.name = "";
        this.lastName = "";
        this.age = 0;
        this.isActive = false;
        this.salary = 0.0;
        this.id = 0;
        this.initial = ' ';
        this.weight = 0;
        this.address = new Address("DEFAULT", "DEFAULT"); // Valeur par défaut pour l'objet Address
        this.friends = List.of("DEFAULT", "DEFAULT", "DEFAULT"); // Valeur par défaut pour la collection
        this.phoneNumbers = new String[]{"DEFAULT", "DEFAULT"}; // Valeur par défaut pour le tableau
        this.bestFriend = new Person("Joe","Mama", 69, false, 1, 69420, 'L', 380, new Address("Mom's house", "idk"), List.of("Nobody"), new String[]{"123-456-7890"}, null);
    }

    // Constructeur avec paramètres
    public Person(String name, String lastName, int age, boolean isActive, double salary, long id, char initial, Integer weight, Address address, List<String> friends, String[] phoneNumbers, Person bestFriend) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.isActive = isActive;
        this.salary = salary;
        this.id = id;
        this.initial = initial;
        this.weight = weight;
        this.address = address;
        this.friends = friends;
        this.phoneNumbers = phoneNumbers;
        this.bestFriend = bestFriend;
    }

    // Getters et Setters pour tous les champs
    public String getName() {
        return name;
    }

    public String getLastName() {return lastName;}

    public int getAge() {
        return age;
    }

    public boolean isActive() {
        return isActive;
    }

    public double getSalary() {
        return salary;
    }

    public long getId() {
        return id;
    }

    public char getInitial() {
        return initial;
    }

    public Address getAddress() {
        return address;
    }

    public List<String> getFriends() {
        return friends;
    }

    public String[] getPhoneNumbers() {
        return phoneNumbers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Person {");
        sb.append("\n  name: ").append(name);
        sb.append("\n  lastName: ").append(lastName);
        sb.append("\n  age: ").append(age);
        sb.append("\n  isActive: ").append(isActive);
        sb.append("\n  salary: ").append(salary);
        sb.append("\n  id: ").append(id);
        sb.append("\n  initial: ").append(initial);
        sb.append("\n  weight: ").append(weight);
        sb.append("\n  ").append(address); // Utilise toString() de Address
        sb.append("\n  friends: ").append(friends);
        sb.append("\n  phoneNumbers: ").append(phoneNumbers != null ? String.join(", ", phoneNumbers) : "null");
        sb.append("\n}");
        return sb.toString();
    }

    public void setBestFriend(Person p2) {
        this.bestFriend = p2;
    }
}

