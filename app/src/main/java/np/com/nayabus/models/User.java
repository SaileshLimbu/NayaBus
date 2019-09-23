package np.com.nayabus.models;

import java.io.Serializable;

public class User implements Serializable {
    private String email;
    private String fullName;
    private String phoneNumber;
    private int balance;

    public User() {
    }

    public User(String email, String fullName, String phoneNumber, int balance) {
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
