package org.example.models;

public class Contact {
    private int id;
    private String name;
    private String phone;
    private String email;
    private boolean isFavorite;

    public Contact(String name, String phone, String email, boolean isFavorite) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setId(int id) {
        this.id = id;
    }
}
