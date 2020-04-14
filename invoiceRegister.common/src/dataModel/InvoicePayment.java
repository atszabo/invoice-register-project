package dataModel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;


public class InvoicePayment {
    private SimpleIntegerProperty payment_id;
    private SimpleDoubleProperty payedAmount;
    private SimpleDoubleProperty payedOriginalAmount;
    LocalDate date;
    private SimpleStringProperty paymentMethod;
    private SimpleStringProperty currency;

    public InvoicePayment(SimpleIntegerProperty payment_id, SimpleDoubleProperty payedAmount, SimpleDoubleProperty payedOriginalAmount,
                          LocalDate date, SimpleStringProperty paymentMethod, SimpleStringProperty currency) {
        this.payment_id = payment_id;
        this.payedAmount = payedAmount;
        this.payedOriginalAmount = payedOriginalAmount;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
    }

    public int getPayment_id() {
        return payment_id.get();
    }

    public double getPayedAmount() {
        return payedAmount.get();
    }

    public double getPayedOriginalAmount() {
        return payedOriginalAmount.get();
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public LocalDate getDate() {
        return date;
    } //FXML table column use this

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public String getCurrency() {
        return currency.get();
    }

    public void setCurrency(String currency) {
        this.currency.set(currency);
    }
}
