package models;

import java.io.Serializable;

public class Passenger implements Serializable {
    private int passengerId;
    private String name;
    private double balance;

    public Passenger(int passengerId, String name, double balance) {
        this.passengerId = passengerId;
        this.name = name;
        this.balance = balance;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void deductBalance(double amount) {
        balance -= amount;
    }

    @Override
    public String toString() {
        return "Passenger [ID=" + passengerId + ", Name=" + name + ", Balance=" + balance + "]";
    }
}
