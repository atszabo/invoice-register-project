package contact;

import dataModel.Contact;
import db.DataSource;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ContactTemplateController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneNrField;
    @FXML
    private TextField faxNrField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea noteArea;
    @FXML
    private TextField idField;
    @FXML
    private ComboBox<Contact> contactsComboBox;

    //getter for binding


    public TextField getNameField() {
        return nameField;
    }

    @FXML
    public void  initialize(){
        idField.editableProperty().setValue(false);
        ObservableList<Contact> contacts = DataSource.getInstance().getContacts();
        contactsComboBox.setItems(contacts);
        contactsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                nameField.disableProperty().setValue(true);
                nameField.setText(newValue.getName());
                phoneNrField.disableProperty().setValue(true);
                phoneNrField.setText(newValue.getPhoneNr());
                faxNrField.disableProperty().setValue(true);
                faxNrField.setText(newValue.getFaxNr());
                emailField.disableProperty().setValue(true);
                emailField.setText(newValue.getEmail());
                noteArea.disableProperty().setValue(true);
                noteArea.setText(newValue.getNote());
                Integer id = newValue.getContact_id();
                idField.setText(id.toString());
            }
        });
    }

    @FXML
    public Contact processContactResult(){
        SimpleStringProperty name = new SimpleStringProperty();
        name.set(nameField.getText().trim());
        SimpleStringProperty phoneNr = new SimpleStringProperty();
        phoneNr.set(phoneNrField.getText().trim());
        SimpleStringProperty faxNr = new SimpleStringProperty();
        faxNr.set(faxNrField.getText().trim());
        SimpleStringProperty email = new SimpleStringProperty();
        email.set(emailField.getText().trim());
        SimpleStringProperty note = new SimpleStringProperty();
        note.set(noteArea.getText().trim());
        SimpleIntegerProperty contact_id = new SimpleIntegerProperty();
        contact_id.set(Integer.valueOf(idField.getText()));

        return new Contact(name,phoneNr,faxNr,email,note,contact_id);
    }
}
