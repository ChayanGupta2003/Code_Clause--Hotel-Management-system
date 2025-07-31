package model;

public class Customer {
    private int customer_id;
    private String name;
    private String email;
    private int age;
    private String gender;
    private String password;

    public Customer(String name, String email, int age, String gender, String password) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;
    }

    public int getCustomerId() { return customer_id; }
    public void setCustomerId(int customer_id) { this.customer_id = customer_id; }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPassword() { return password; }
}
