package dataModel;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Address implements Comparable<Address>{

    public static final String POSTALADDRESS = "Postacím";
    public static final String SITEADDDRESS = "Telephely";
    public static final String HEADOFFICEADDRESS ="Székhely";

    private SimpleStringProperty country;
    private SimpleStringProperty postcode;
    private SimpleStringProperty city;
    private SimpleStringProperty street;
    private SimpleStringProperty houseNr;
    private SimpleStringProperty mailbox;
    private SimpleStringProperty addressType;
    private SimpleIntegerProperty address_id;

    @Override
    public String toString() {
        return addressType.getValue() + ": " + city.getValue() + ", " + postcode.getValue() + ", " + country.getValue() +
                ", " +street.getValue() + " " + houseNr.getValue() + ", Pf.: " + mailbox.getValue();
    }

    @Override
    public int compareTo(Address o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }

    public Address(SimpleStringProperty country, SimpleStringProperty postcode, SimpleStringProperty city, SimpleStringProperty street, SimpleStringProperty houseNr, SimpleStringProperty mailbox, SimpleIntegerProperty address_id) {
        this.country = country;
        this.postcode = postcode;
        this.city = city;
        this.street = street;
        this.houseNr = houseNr;
        this.mailbox = mailbox;
        this.addressType = new SimpleStringProperty();
        this.addressType.set(HEADOFFICEADDRESS);
        this.address_id= address_id;
    }

    public Address(SimpleStringProperty country, SimpleStringProperty postcode, SimpleStringProperty city, SimpleStringProperty street, SimpleStringProperty houseNr, SimpleStringProperty mailbox, SimpleStringProperty addressType, SimpleIntegerProperty address_id) {
        this.country = country;
        this.postcode = postcode;
        this.city = city;
        this.street = street;
        this.houseNr = houseNr;
        this.mailbox = mailbox;
        this.addressType = addressType;
        this.address_id= address_id;
    }

    public String getCountry() {
        return country.get();
    }

    public String getPostcode() {
        return postcode.get();
    }

    public String getCity() {
        return city.get();
    }


    public String getStreet() {
        return street.get();
    }


    public String getHouseNr() {
        return houseNr.get();
    }

    public String getMailbox() {
        return mailbox.get();
    }

    public String getAddressType() {
        return addressType.get();
    }

    public int getAddress_id() {
        return address_id.get();
    }

}
