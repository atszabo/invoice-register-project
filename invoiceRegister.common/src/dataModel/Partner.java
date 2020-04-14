package dataModel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import java.time.Period;

public class Partner implements Comparable<Partner>{

    private SimpleStringProperty name;
    private SimpleStringProperty shortName;
    private Address headOfficeAddress;
    private SimpleStringProperty vatNr;
    private SimpleStringProperty euVatNr;
    private SimpleStringProperty defaultVat;
    private SimpleStringProperty defaultCurrency;
    private SimpleStringProperty paymentMethod;
    private Period paymentDelay;
    private SimpleStringProperty defaultPhone;
    private SimpleStringProperty defaultFax;
    private SimpleStringProperty defaultEmail;
    private SimpleStringProperty note;
    private ObservableList<Address> addresses;
    private ObservableList<Contact> contacts;
    private SimpleDoubleProperty limitValue;
    private SimpleIntegerProperty partner_id;

    public Partner(SimpleStringProperty name, SimpleStringProperty shortName, Address headOfficeAddress, SimpleStringProperty vatNr,
                   SimpleStringProperty euVatNr, SimpleStringProperty defaultVat, SimpleStringProperty defaultCurrency,
                   SimpleStringProperty paymentMethod, Period paymentDelay, SimpleStringProperty defaultPhone, SimpleStringProperty defaultFax,
                   SimpleStringProperty defaultEmail, SimpleStringProperty note, ObservableList<Address> addresses,
                   ObservableList<Contact> contacts, SimpleDoubleProperty limitValue, SimpleIntegerProperty partner_id) {
        this.name = name;
        this.shortName = shortName;
        this.headOfficeAddress = headOfficeAddress;
        this.vatNr = vatNr;
        this.euVatNr = euVatNr;
        this.defaultVat = defaultVat;
        this.defaultCurrency = defaultCurrency;
        this.paymentMethod = paymentMethod;
        this.paymentDelay = paymentDelay;
        this.defaultPhone = defaultPhone;
        this.defaultFax = defaultFax;
        this.defaultEmail = defaultEmail;
        this.note = note;
        this.addresses = addresses;
        this.contacts = contacts;
        this.limitValue = limitValue;
        this.partner_id = partner_id;
    }

    public Partner(SimpleStringProperty name, SimpleStringProperty shortName, Address headOfficeAddress, SimpleStringProperty vatNr, SimpleStringProperty euVatNr, SimpleStringProperty defaultVat, SimpleStringProperty defaultCurrency, SimpleStringProperty paymentMethod, Period paymentDelay, SimpleStringProperty defaultPhone, SimpleStringProperty defaultFax, SimpleStringProperty defaultEmail, SimpleStringProperty note, ObservableList<Address> addresses, ObservableList<Contact> contacts, SimpleDoubleProperty limitValue) {
        this.name = name;
        this.shortName = shortName;
        this.headOfficeAddress = headOfficeAddress;
        this.vatNr = vatNr;
        this.euVatNr = euVatNr;
        this.defaultVat = defaultVat;
        this.defaultCurrency = defaultCurrency;
        this.paymentMethod = paymentMethod;
        this.paymentDelay = paymentDelay;
        this.defaultPhone = defaultPhone;
        this.defaultFax = defaultFax;
        this.defaultEmail = defaultEmail;
        this.note = note;
        this.addresses = addresses;
        this.contacts = contacts;
        this.limitValue = limitValue;
        this.partner_id = new SimpleIntegerProperty();
    }

    @Override
    public String toString() {
        return name.getValue() + " | " + headOfficeAddress.getCity()+ " | " + vatNr.getValue();
    }

    @Override
    public int compareTo(Partner o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }

    public String getName() {
        return name.get();
    }

    public String getShortName() {
        return shortName.get();
    }

    public Address getHeadOfficeAddress() {
        return headOfficeAddress;
    }

    public String getVatNr() {
        return vatNr.get();
    }

    public String getEuVatNr() {
        return euVatNr.get();
    }

    public String getDefaultVat() {
        return defaultVat.get();
    }

    public String getDefaultCurrency() {
        return defaultCurrency.get();
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public Period getPaymentDelay() {
        return paymentDelay;
    }

    public String getDefaultPhone() {
        return defaultPhone.get();
    }

    public String getDefaultFax() {
        return defaultFax.get();
    }

    public String getDefaultEmail() {
        return defaultEmail.get();
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public ObservableList<Address> getAddresses() {
        return addresses;
    }

    public ObservableList<Contact> getContacts() {
        return contacts;
    }

    public double getLimitValue() {
        return limitValue.get();
    }

    public int getPartner_id() {
        return partner_id.get();
    }

    public void setPartner_id(int partner_id) {
        this.partner_id.set(partner_id);
    }
}
