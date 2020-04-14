package partner;

import address.AddressTemplateController;
import contact.ContactTemplateController;
import dataModel.Address;
import dataModel.Contact;
import dataModel.Partner;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.io.IOException;
import java.time.Period;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static dataModel.Address.HEADOFFICEADDRESS;
import static dataModel.Invoice.*;
import static dataModel.Payment.CASH_PAYMENTMETHOD;
import static dataModel.Payment.PREPAY_PAYMENTMETHOD;
import static dataModel.Payment.TRANSFER_PAYMENTMETHOD;


public class PartnerTemplateController {


    private ObservableList<Address> addresses;
    private ObservableList<Contact> contacts;

    @FXML
    private TextField nameField;
    @FXML
    private TextField shortNameField;
    @FXML
    private TextField vatNrField;
    @FXML
    private TextField euVatNrField;
    @FXML
    private ComboBox<String> defaultCurrencyComboBox;
    @FXML
    private ComboBox<String> defaultVatComboBox;
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private TextField paymentDelayField;
    @FXML
    private TextField defaultPhoneField;
    @FXML
    private TextField defaultFaxField;
    @FXML
    private TextField defaultEmailField;
    @FXML
    private TextArea noteArea;
    @FXML
    private TextField limitField;
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
    private TextField headOfficeIdField;
    @FXML
    private DialogPane partnerTemplateDialogPane;
    @FXML
    private TableView<Address> addressTableView;
    @FXML
    private TableView<Contact> contactTableView;
    @FXML
    private Button addAddressButton;
    @FXML
    private Button deleteContactButton;
    @FXML
    private Button addContactButton;
    @FXML
    private Button deleteAddressButton;

    void setAddresses(ObservableList<Address> addresses) {
        this.addresses = addresses;
    }

    void setContacts(ObservableList<Contact> contacts) {
        this.contacts = contacts;
    }

    //getters due to binding
    public TextField getNameField() {
        return nameField;
    }
    public TextField getCityField() {
        return cityField;
    }

    TextField getShortNameField() {
        return shortNameField;
    }

    TextField getVatNrField() {
        return vatNrField;
    }

    TextField getEuVatNrField() {
        return euVatNrField;
    }

    ComboBox<String> getDefaultCurrencyComboBox() {
        return defaultCurrencyComboBox;
    }

    ComboBox<String> getDefaultVatComboBox() {
        return defaultVatComboBox;
    }

    ComboBox<String> getPaymentMethodComboBox() {
        return paymentMethodComboBox;
    }

    TextField getPaymentDelayField() {
        return paymentDelayField;
    }

    TextField getDefaultPhoneField() {
        return defaultPhoneField;
    }

    TextField getDefaultFaxField() {
        return defaultFaxField;
    }

    TextField getDefaultEmailField() {
        return defaultEmailField;
    }

    public TextArea getNoteArea() {
        return noteArea;
    }

    TextField getLimitField() {
        return limitField;
    }

    TextField getCountryField() {
        return countryField;
    }

    TextField getPostcodeField() {
        return postcodeField;
    }

    TextField getStreetField() {
        return streetField;
    }

    TextField getHouseNrField() {
        return houseNrField;
    }

    TextField getMailboxField() {
        return mailboxField;
    }

    TextField getHeadOfficeIdField() {
        return headOfficeIdField;
    }

    TableView<Address> getAddressTableView() {
        return addressTableView;
    }

    TableView<Contact> getContactTableView() {
        return contactTableView;
    }

    Button getAddAddressButton() {
        return addAddressButton;
    }

    Button getDeleteContactButton() {
        return deleteContactButton;
    }

    Button getAddContactButton() {
        return addContactButton;
    }

    Button getDeleteAddressButton() {
        return deleteAddressButton;
    }


    public void initialize() {
        countryField.disableProperty().setValue(true);
        postcodeField.disableProperty().setValue(true);
        cityField.disableProperty().setValue(true);
        streetField.disableProperty().setValue(true);
        houseNrField.disableProperty().setValue(true);
        mailboxField.disableProperty().setValue(true);
        headOfficeIdField.disableProperty().setValue(false);
        addresses = FXCollections.observableArrayList();
        addressTableView.setItems(addresses);
        contacts = FXCollections.observableArrayList();
        contactTableView.setItems(contacts);
        deleteContactButton.disableProperty().setValue(true);
        deleteAddressButton.disableProperty().setValue(true);

        // Force netAmount TextField to Integer
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?");
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
        };
        StringConverter<Integer> converter = new StringConverter<>() {
            @Override
            public Integer fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0 ;
                } else {
                    return Integer.valueOf(s);
                }
            }
            @Override
            public String toString(Integer i) {
                return i.toString();
            }
        };

        // Force netAmount TextField to double
        Pattern validEditingStateDouble = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        UnaryOperator<TextFormatter.Change> filterDouble = c -> {
            String text = c.getControlNewText();
            if (validEditingStateDouble.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
        };
        StringConverter<Double> converterDouble = new StringConverter<>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0 ;
                } else {
                    return Double.valueOf(s);
                }
            }
            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };


        TextFormatter<Integer> integerTextFormatter = new TextFormatter<>(converter, 0, filter);
        paymentDelayField.setTextFormatter(integerTextFormatter);
        TextFormatter<Double> doubleTextFormatter = new TextFormatter<>(converterDouble, 0.0, filterDouble);
        limitField.setTextFormatter(doubleTextFormatter);

        defaultCurrencyComboBox.setItems(FXCollections.observableArrayList(FT_CURRENCY,EUR_CURRENCY,GBP_CURRENCY));
        defaultCurrencyComboBox.setValue(FT_CURRENCY);
        defaultVatComboBox.setItems(FXCollections.observableArrayList(VAT_27,VAT_0));
        defaultVatComboBox.setValue(VAT_27);
        paymentMethodComboBox.setItems(FXCollections.observableArrayList(CASH_PAYMENTMETHOD,TRANSFER_PAYMENTMETHOD,PREPAY_PAYMENTMETHOD));
        paymentMethodComboBox.setValue(CASH_PAYMENTMETHOD);

        contactTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
            deleteContactButton.disableProperty().setValue(false));
        addressTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
            deleteAddressButton.disableProperty().setValue(false));
    }


    @FXML
    public Partner processPartnerResult(){
        SimpleStringProperty name = new SimpleStringProperty();
        name.set(nameField.getText().trim());
        SimpleStringProperty shortName = new SimpleStringProperty();
        shortName.set(shortNameField.getText().trim());
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
        SimpleIntegerProperty headOfficeAddress_id = new SimpleIntegerProperty();
        headOfficeAddress_id.set(Integer.valueOf(headOfficeIdField.getText()));
        Address headOfficeAddress = new Address(country, postcode, city, street, houseNr, mailbox,headOfficeAddress_id);
        SimpleStringProperty vatNr = new SimpleStringProperty();
        vatNr.set(vatNrField.getText().trim());
        SimpleStringProperty euVatNr = new SimpleStringProperty();
        euVatNr.set(euVatNrField.getText().trim());
        SimpleStringProperty defaultVat = new SimpleStringProperty();
        defaultVat.set(defaultVatComboBox.getSelectionModel().getSelectedItem());
        SimpleStringProperty defaultCurrency = new SimpleStringProperty();
        defaultCurrency.set(defaultCurrencyComboBox.getSelectionModel().getSelectedItem());
        SimpleStringProperty paymentMethod = new SimpleStringProperty();
        paymentMethod.set(paymentMethodComboBox.getSelectionModel().getSelectedItem());
        Period paymentDelay = Period.ofDays(Integer.valueOf(paymentDelayField.getText()));
        SimpleStringProperty defaultPhone = new SimpleStringProperty();
        defaultPhone.set(defaultPhoneField.getText().trim());
        SimpleStringProperty defaultEmail = new SimpleStringProperty();
        defaultEmail.set(defaultEmailField.getText().trim());
        SimpleStringProperty defaultFax = new SimpleStringProperty();
        defaultFax.set(defaultFaxField.getText().trim());
        SimpleStringProperty note = new SimpleStringProperty();
        note.set(noteArea.getText().trim());
        SimpleDoubleProperty limitValue = new SimpleDoubleProperty();
        limitValue.set(Double.valueOf(limitField.getText()));

        return new Partner(name,shortName,headOfficeAddress,vatNr,euVatNr,defaultVat,defaultCurrency,paymentMethod,
                paymentDelay,defaultPhone,defaultFax,defaultEmail,note,addresses,contacts,limitValue);
    }

    //add a new Address button on action
    @FXML
    public void showAddressTemplate(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(partnerTemplateDialogPane.getScene().getWindow());
        dialog.setTitle("Új cím hozzáadása:");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/address/addressTemplate.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the addressTemplate.fxml" + e.getMessage());
            return;
        }
        //Binding DialogPane OK Button check invalid fields
        //get the dialogPane controller
        AddressTemplateController addressTemplateController = fxmlLoader.getController();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> addressTemplateController.getCountryField().getText().isEmpty()||
                                addressTemplateController.getCityField().getText().isEmpty(),
                        addressTemplateController.getCountryField().textProperty(),
                        addressTemplateController.getCityField().textProperty()
                ));
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            AddressTemplateController controller = fxmlLoader.getController();
            Address newAddress = controller.processAddressResult();
            if(newAddress.getAddressType().equals(HEADOFFICEADDRESS)) {
                countryField.setText(newAddress.getCountry());
                postcodeField.setText(newAddress.getPostcode());
                cityField.setText(newAddress.getCity());
                streetField.setText(newAddress.getStreet());
                houseNrField.setText(newAddress.getHouseNr());
                mailboxField.setText(newAddress.getMailbox());
                Integer address_Id = newAddress.getAddress_id();
                headOfficeIdField.setText(address_Id.toString());
            } else{
                addresses.add(newAddress);
                addressTableView.getSelectionModel().select(newAddress);
            }
        }
    }

    //delete Contact
    @FXML
    public void deleteAddress() {
        addresses.remove(addressTableView.getSelectionModel().getSelectedItem());
        addressTableView.refresh();
        if(addressTableView.getItems().isEmpty())
            deleteAddressButton.disableProperty().setValue(true);
    }

    //add a new Contact button on action
    @FXML
    public void showContactTemplate(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(partnerTemplateDialogPane.getScene().getWindow());
        dialog.setTitle("Új kapcsolat hozzáadása:");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/contact/contactTemplate.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the contactTemplate.fxml" + e.getMessage());
            return;
        }
        //Binding DialogPane OK Button check invalid fields
        //get the dialogPane controller
        ContactTemplateController contactTemplateController = fxmlLoader.getController();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> contactTemplateController.getNameField().getText().isEmpty(),
                        contactTemplateController.getNameField().textProperty()
                ));
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            ContactTemplateController controller = fxmlLoader.getController();
            Contact newContact = controller.processContactResult();
            contacts.add(newContact);
            contactTableView.getSelectionModel().select(newContact);
        }
    }

    //delete Contact
    @FXML
    public void deleteContact() {
        contacts.remove(contactTableView.getSelectionModel().getSelectedItem());
        contactTableView.refresh();
        if(contactTableView.getItems().isEmpty())
            deleteContactButton.disableProperty().setValue(true);
    }
}