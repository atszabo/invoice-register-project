package dataModel;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Contact implements Comparable<Contact> {
    private SimpleStringProperty name;
    private SimpleStringProperty phoneNr;
    private SimpleStringProperty faxNr;
    private SimpleStringProperty email;
    private SimpleStringProperty note;
    private SimpleIntegerProperty contact_id;

    public Contact(SimpleStringProperty name, SimpleStringProperty phoneNr, SimpleStringProperty faxNr, SimpleStringProperty email, SimpleStringProperty note, SimpleIntegerProperty contact_id) {
        this.name = name;
        this.phoneNr = phoneNr;
        this.faxNr = faxNr;
        this.email = email;
        this.note = note;
        this.contact_id = contact_id;
    }

    @Override
    public String toString() {
        return this.name.getValue() + ", " + phoneNr.getValue() + ", " + email.getValue();
    }

    @Override
    public int compareTo(Contact o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }

    public String getName() {
        return name.get();
    }

    public String getPhoneNr() {
        return phoneNr.get();
    }

    public String getFaxNr() {
        return faxNr.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public int getContact_id() {
        return contact_id.get();
    }
}
