package dataModel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class Payment {

    public static final String CASH_PAYMENTMETHOD = "Készpénz";
    public static final String TRANSFER_PAYMENTMETHOD = "Átutalás";
    public static final String PREPAY_PAYMENTMETHOD = "Díjbekérő";
    public static final String ALL_PAYMENTMETHOD = "Mind";

    private Partner partner;
    private ObservableList<Invoice> invoices;
    private SimpleDoubleProperty amount;
    private SimpleDoubleProperty balance;
    private SimpleStringProperty currency;
    private LocalDate date;
    private SimpleStringProperty paymentMethod;
    private SimpleStringProperty note;
    private SimpleIntegerProperty payment_id;

    public Payment(Partner partner, ObservableList<Invoice> invoices, SimpleDoubleProperty amount,
                   SimpleDoubleProperty balance, SimpleStringProperty currency, LocalDate date,
                   SimpleStringProperty paymentMethod, SimpleStringProperty note) {
        this.partner = partner;
        this.invoices = invoices;
        this.amount = amount;
        this.balance = balance;
        this.currency = currency;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.note = note;
    }

    //setup from database
    public Payment(Partner partner, ObservableList<Invoice> invoices, SimpleDoubleProperty amount,
                   SimpleDoubleProperty balance, SimpleStringProperty currency, LocalDate date,
                   SimpleStringProperty paymentMethod, SimpleStringProperty note, SimpleIntegerProperty payment_id) {
        this.partner = partner;
        this.invoices = invoices;
        this.amount = amount;
        this.balance = balance;
        this.currency = currency;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.payment_id = payment_id;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public ObservableList<Invoice> getInvoices() {
        return invoices;
    }

    public double getAmount() {
        return amount.get();
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public String getCurrency() {
        return currency.get();
    }

    public void setCurrency(String currency) {
        this.currency.set(currency);
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public int getPayment_id() {
        return payment_id.get();
    }
}
