package dataModel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;


public class Invoice implements Comparable<Invoice>{

    public static final String VAT_27 = "27 %";
    public static final String VAT_0 = "0 %";
    public static final String EUR_CURRENCY = "Euro";
    public static final String FT_CURRENCY = "Forint";
    public static final String GBP_CURRENCY = "Font";
    public static final String DATEOFPERFORMANCE = "Számla teljesítés";
    public static final String DATEOFINVOICE = "Számla kiállítása";
    public static final String DATEOFDUE = "Fizetési határidő";

    private Partner partner;
    private Contact contact;
    private SimpleStringProperty invoiceNr;
    private SimpleStringProperty regNr;
    private SimpleDoubleProperty netAmount;
    private SimpleDoubleProperty grossAmount;
    private SimpleStringProperty vat;
    private SimpleStringProperty currency;
    private SimpleDoubleProperty balance;
    private SimpleStringProperty paymentMethod;
    private LocalDate dateOfInvoice;
    private LocalDate dateOfPerformance;
    private LocalDate dateOfDue;
    private SimpleStringProperty note;
    private SimpleIntegerProperty invoice_id;
    private SimpleDoubleProperty calculatedBalance;
    private SimpleDoubleProperty payedAmount;
    private SimpleDoubleProperty payedOriginalAmount;
    private ObservableList<InvoicePayment> invoicePayments;

    public Invoice(Partner partner, Contact contact, SimpleStringProperty invoiceNr, SimpleStringProperty regNr, SimpleDoubleProperty netAmount,
                   SimpleStringProperty vat, SimpleStringProperty currency, SimpleDoubleProperty balance,
                   SimpleStringProperty paymentMethod, LocalDate dateOfInvoice, LocalDate dateOfPerformance, LocalDate dateOfDue,
                   SimpleStringProperty note) {
        this.partner = partner;
        this.contact = contact;
        this.invoiceNr = invoiceNr;
        this.regNr = regNr;
        this.netAmount = netAmount;
        this.vat = vat;
        switch(vat.getValue()){
            case VAT_27: this.grossAmount = new SimpleDoubleProperty(netAmount.getValue()*1.27); break;
            case VAT_0: this.grossAmount = netAmount; break;
        }
        this.currency = currency;
        this.balance = balance;
        this.paymentMethod = paymentMethod;
        this.dateOfInvoice = dateOfInvoice;
        this.dateOfPerformance = dateOfPerformance;
        this.dateOfDue = dateOfDue;
        this.note = note;
    }

    //setup from database
    public Invoice(Partner partner, Contact contact, SimpleStringProperty invoiceNr, SimpleStringProperty regNr, SimpleDoubleProperty netAmount,
                   SimpleStringProperty vat, SimpleStringProperty currency, SimpleDoubleProperty balance,
                   SimpleStringProperty paymentMethod, LocalDate dateOfInvoice, LocalDate dateOfPerformance, LocalDate dateOfDue,
                   SimpleStringProperty note, SimpleIntegerProperty invoice_id) {
        this.partner = partner;
        this.contact = contact;
        this.invoiceNr = invoiceNr;
        this.regNr = regNr;
        this.netAmount = netAmount;
        this.vat = vat;
        switch(vat.getValue()){
            case VAT_27: this.grossAmount = new SimpleDoubleProperty(netAmount.getValue()*1.27); break;
            case VAT_0: this.grossAmount = netAmount; break;
        }
        this.currency = currency;
        this.balance = balance;
        this.paymentMethod = paymentMethod;
        this.dateOfInvoice = dateOfInvoice;
        this.dateOfPerformance = dateOfPerformance;
        this.dateOfDue = dateOfDue;
        this.note = note;
        this.invoice_id = invoice_id;
        this.calculatedBalance = new SimpleDoubleProperty(0.0);
        this.payedAmount = new SimpleDoubleProperty(0.0);
        this.payedOriginalAmount = new SimpleDoubleProperty(0.0);
        this.invoicePayments = FXCollections.observableArrayList();

    }

    @Override
    public int compareTo(Invoice o) {
        return this.getDateOfDue().compareTo(o.getDateOfDue());
    }

    public void addInvoicePayment (InvoicePayment invoicePayment){
        invoicePayments.add(invoicePayment);
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getInvoiceNr() {
        return invoiceNr.get();
    }

    public String getRegNr() {
        return regNr.get();
    }

    public double getNetAmount() {
        return netAmount.get();
    }

    public double getGrossAmount() {
        return grossAmount.get();
    }

    public String getVat() {
        return vat.get();
    }

    public String getCurrency() {
        return currency.get();
    }

    public void setCurrency(String currency) {
        this.currency.set(currency);
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public LocalDate getDateOfInvoice() {
        return dateOfInvoice;
    }

    public LocalDate getDateOfPerformance() {
        return dateOfPerformance;
    }

    public LocalDate getDateOfDue() {
        return dateOfDue;
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public int getInvoice_id() {
        return invoice_id.get();
    }

    public double getCalculatedBalance() {
        return calculatedBalance.get();
    }

    public void setCalculatedBalance(double calculatedBalance) {this.calculatedBalance.set(calculatedBalance);
    }

    public double getPayedAmount() {
        return payedAmount.get();
    }

    public void setPayedAmount(double payedAmount) {
        this.payedAmount.set(payedAmount);
    }

    public double getPayedOriginalAmount() {
        return payedOriginalAmount.get();
    }

    public void setPayedOriginalAmount(double payedOriginalAmount) {
        this.payedOriginalAmount.set(payedOriginalAmount);
    }

    public ObservableList<InvoicePayment> getInvoicePayments() {
        return invoicePayments;
    }

    public void setInvoicePayments(ObservableList<InvoicePayment> invoicePayments) {
        this.invoicePayments = invoicePayments;
    }

}
