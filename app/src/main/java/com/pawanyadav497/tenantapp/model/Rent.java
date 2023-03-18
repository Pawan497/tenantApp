package com.pawanyadav497.tenantapp.model;


public class Rent {

    private int id;
    private String from;
    private String to;
    private String payment_date;
    private String amt_due;
    private String amt_paid;
    private String balance;
    private int tenantID;


    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getAmt_due() {
        return amt_due;
    }

    public void setAmt_due(String amt_due) {
        this.amt_due = amt_due;
    }

    public String getAmt_paid() {
        return amt_paid;
    }

    public void setAmt_paid(String amt_paid) {
        this.amt_paid = amt_paid;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }
}
