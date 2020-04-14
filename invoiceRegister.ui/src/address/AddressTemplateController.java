package address;

import dataModel.Address;
import db.DataSource;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import static dataModel.Address.HEADOFFICEADDRESS;
import static dataModel.Address.POSTALADDRESS;
import static dataModel.Address.SITEADDDRESS;


public class AddressTemplateController {

    @FXML
    private ComboBox<Address> addressesComboBox;
    @FXML
    private TextField countryField;
    @FXML
    private TextField postcodeField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField houseNrField;
    @FXML
    private TextField mailboxField;
    @FXML
    private ComboBox<String> addressTypeComboBox;
    @FXML
    private TextField idField;

    //getters for binding
    public TextField getCountryField() {
        return countryField;
    }
    public TextField getCityField() {
        return cityField;
    }

    @FXML
    public void  initialize(){
        idField.editableProperty().setValue(false);
        addressTypeComboBox.setItems(FXCollections.observableArrayList(HEADOFFICEADDRESS,POSTALADDRESS,SITEADDDRESS));
        addressTypeComboBox.setValue(HEADOFFICEADDRESS);
        ObservableList<Address> addresses = DataSource.getInstance().getAddresses();
        addressesComboBox.setItems(addresses);
        addressesComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                countryField.disableProperty().setValue(true);
                countryField.setText(newValue.getCountry());
                postcodeField.disableProperty().setValue(true);
                postcodeField.setText(newValue.getPostcode());
                cityField.disableProperty().setValue(true);
                cityField.setText(newValue.getCity());
                streetField.disableProperty().setValue(true);
                streetField.setText(newValue.getStreet());
                houseNrField.disableProperty().setValue(true);
                houseNrField.setText(newValue.getHouseNr());
                mailboxField.disableProperty().setValue(true);
                mailboxField.setText(newValue.getMailbox());
                addressTypeComboBox.setItems(FXCollections.observableArrayList(newValue.getAddressType()));
                addressTypeComboBox.setValue(newValue.getAddressType());
                addressTypeComboBox.disableProperty().setValue(true);
                Integer id = newValue.getAddress_id();
                idField.setText(id.toString());
            }
        });
    }

    @FXML
    public Address processAddressResult(){
        SimpleStringProperty country = new SimpleStringProperty();
        country.set(countryField.getText().trim());
        SimpleStringProperty postcode = new SimpleStringProperty();
        postcode.set(postcodeField.getText().trim());
        SimpleStringProperty city = new SimpleStringProperty();
        city.set(cityField.getText().trim());
        SimpleStringProperty street = new SimpleStringProperty();
        street.set(streetField.getText().trim());
        SimpleStringProperty houseNr = new SimpleStringProperty();
        houseNr.set(houseNrField.getText().trim());
        SimpleStringProperty mailbox = new SimpleStringProperty();
        mailbox.set(mailboxField.getText().trim());
        SimpleStringProperty addressType = new SimpleStringProperty();
        switch (addressTypeComboBox.getSelectionModel().getSelectedItem()){
            case HEADOFFICEADDRESS: addressType.set(HEADOFFICEADDRESS); break;
            case POSTALADDRESS:  addressType.set(POSTALADDRESS); break;
            case SITEADDDRESS: addressType.set(SITEADDDRESS); break;
        }

        SimpleIntegerProperty address_id = new SimpleIntegerProperty();
        address_id.set(Integer.valueOf(idField.getText()));
        return new Address(country,postcode,city,street,houseNr,mailbox,addressType,address_id);
    }
}
