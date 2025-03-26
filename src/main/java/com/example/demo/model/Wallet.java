package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Wallet {
    @Id
    private Long id;
    private String balance;

    // Default constructor for JPA
    public Wallet() {
        this.balance = "0";
    }

    // Constructor with initial balance
    public Wallet(Long id, String balance) {
        this.id = id;
        this.balance = balance;
    }

    // Getter for balance
    public String getBalance() {
        return balance;
    }

    // Setter for balance
    public void setBalance(String balance) {
        this.balance = balance;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }
}
