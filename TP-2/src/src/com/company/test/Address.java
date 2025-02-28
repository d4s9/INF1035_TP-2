package src.com.company.test;

// Classe Address pour tester ObjectFieldRef
public class Address {
    private String street;
    private String city;
    public Address() {
        this.street = "";
        this.city = "";
    }
    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "Address : { street: " + street + ", city: " + city + " }";
    }
}
