package partner;

import address.AddressDetailsController;
import contact.ContactDetailsController;
import dataModel.Address;
import dataModel.Contact;
import dataModel.Partner;
import db.DataSource;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dataModel.Address.HEADOFFICEADDRESS;


public class PartnerAddressContactController {

    @FXML
    private DialogPane partnerAddressContactDialogPane;
    @FXML
    private TableView<Partner> partnerTableView;
    @FXML
    private TableView<Address> addressTableView;
    @FXML
    private TableView<Contact> contactTableView;
    @FXML
    private Button showPartnerButton;
    @FXML
    private Button editPartnerButton;
    @FXML
    private Button deletePartnerButton;
    @FXML
    private Button showAddressButton;
    @FXML
    private Button editAddressButton;
    @FXML
    private Button deleteAddressButton;
    @FXML
    private Button showContactButton;
    @FXML
    private Button editContactButton;
    @FXML
    private Button deleteContactButton;

    private ObservableList<Partner> partners;
    private ObservableList<Address> addresses;
    private ObservableList<Contact> contacts;

    //for other Controller
    private static Address selectedAddress;

    public static Address getSelectedAddress() {
        return selectedAddress;
    }

    private static Contact selectedContact;

    public static Contact getSelectedContact() {
        return selectedContact;
    }

    public void initialize() {
        showPartnerButton.setDisable(true);
        editPartnerButton.setDisable(true);
        deletePartnerButton.setDisable(true);
        showAddressButton.setDisable(true);
        editAddressButton.setDisable(true);
        deleteAddressButton.setDisable(true);
        showContactButton.setDisable(true);
        editContactButton.setDisable(true);
        deleteContactButton.setDisable(true);
        fillInPartnersAddressesContactsTables();
        partnerTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showPartnerButton.setDisable(false);
            editPartnerButton.setDisable(false);
            deletePartnerButton.setDisable(false);
        });
        addressTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showAddressButton.setDisable(false);
            editAddressButton.setDisable(false);
            deleteAddressButton.setDisable(false);
            selectedAddress = addressTableView.getSelectionModel().getSelectedItem();
        });
        contactTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showContactButton.setDisable(false);
            editContactButton.setDisable(false);
            deleteContactButton.setDisable(false);
            selectedContact = contactTableView.getSelectionModel().getSelectedItem();
        });
    }

    private void fillInPartnersAddressesContactsTables() {

        Task<ObservableList<Partner>> partnerTask = new Task<>() {
            @Override
            protected ObservableList<Partner> call() {
                partners = FXCollections.observableArrayList
                        (DataSource.getInstance().getPartners());
                return partners;
            }
        };

        Task<ObservableList<Address>> addressTask = new Task<>() {
            @Override
            protected ObservableList<Address> call() {
                addresses = FXCollections.observableArrayList
                        (DataSource.getInstance().getAddresses());
                return addresses;
            }
        };
        Task<ObservableList<Contact>> contactTask = new Task<>() {
            @Override
            protected ObservableList<Contact> call()  {
                contacts = FXCollections.observableArrayList
                        (DataSource.getInstance().getContacts());
                return contacts;
            }
        };

        ExecutorService service = null;
        try

        {
            service = Executors.newSingleThreadExecutor();
            service.submit(partnerTask);
            service.submit(addressTask);
            service.submit(contactTask);
            partnerTableView.itemsProperty().bind(partnerTask.valueProperty());
            addressTableView.itemsProperty().bind(addressTask.valueProperty());
            contactTableView.itemsProperty().bind(contactTask.valueProperty());
        } finally

        {
            if (service != null)
                service.shutdown();
        }

    }

    @FXML
    public void showPartner() {
        Partner partner = partnerTableView.getSelectionModel().getSelectedItem();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000, 600);
        dialog.initOwner(partnerAddressContactDialogPane.getScene().getWindow());
        dialog.setTitle("Partner");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/partner/partnerTemplate.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the partnerTemplate.fxml" + e.getMessage());
        }
        PartnerTemplateController partnerTemplateController = fxmlLoader.getController();
        fillInPartnerTemplate(partner, partnerTemplateController);
        partnerTemplateController.getNameField().setEditable(false);
        partnerTemplateController.getShortNameField().setEditable(false);
        partnerTemplateController.getAddAddressButton().setVisible(false);
        partnerTemplateController.getDeleteAddressButton().setVisible(false);
        partnerTemplateController.getVatNrField().setEditable(false);
        partnerTemplateController.getEuVatNrField().setEditable(false);
        partnerTemplateController.getDefaultCurrencyComboBox().setDisable(true);
        partnerTemplateController.getDefaultVatComboBox().setDisable(true);
        partnerTemplateController.getPaymentMethodComboBox().setDisable(true);
        partnerTemplateController.getPaymentDelayField().setEditable(false);
        partnerTemplateController.getLimitField().setEditable(false);
        partnerTemplateController.getDefaultPhoneField().setEditable(false);
        partnerTemplateController.getDefaultEmailField().setEditable(false);
        partnerTemplateController.getDefaultFaxField().setEditable(false);
        partnerTemplateController.getAddContactButton().setVisible(false);
        partnerTemplateController.getDeleteContactButton().setVisible(false);
        partnerTemplateController.getNoteArea().setEditable(false);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    @FXML
    public void editPartner() {
        Partner partner = partnerTableView.getSelectionModel().getSelectedItem();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000, 600);
        dialog.initOwner(partnerAddressContactDialogPane.getScene().getWindow());
        dialog.setTitle("Partner");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/partner/partnerTemplate.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the partnerTemplate.fxml" + e.getMessage());
        }
        PartnerTemplateController partnerTemplateController = fxmlLoader.getController();
        fillInPartnerTemplate(partner, partnerTemplateController);
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> partnerTemplateController.getNameField().getText().isEmpty() ||
                                partnerTemplateController.getCityField().getText().isEmpty(),
                        partnerTemplateController.getNameField().textProperty(),
                        partnerTemplateController.getCityField().textProperty()
                ));
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            PartnerTemplateController controller = fxmlLoader.getController();
            Partner updatedPartner = controller.processPartnerResult();
            updatedPartner.setPartner_id(partner.getPartner_id());
            // Update Partner
            DataSource.getInstance().updatePartnerCommit(updatedPartner);
            //re-initialize
            fillInPartnersAddressesContactsTables();
            showPartnerButton.setDisable(true);
            editPartnerButton.setDisable(true);
            deletePartnerButton.setDisable(true);
        }
    }


    private void fillInPartnerTemplate(Partner partner, PartnerTemplateController partnerTemplateController) {
        partnerTemplateController.getNameField().setText(partner.getName());
        partnerTemplateController.getShortNameField().setText(partner.getShortName());
        partnerTemplateController.getCountryField().setText(partner.getHeadOfficeAddress().getCountry());
        partnerTemplateController.getPostcodeField().setText(partner.getHeadOfficeAddress().getPostcode());
        partnerTemplateController.getCityField().setText(partner.getHeadOfficeAddress().getCity());
        partnerTemplateController.getStreetField().setText(partner.getHeadOfficeAddress().getStreet());
        partnerTemplateController.getHouseNrField().setText(partner.getHeadOfficeAddress().getHouseNr());
        partnerTemplateController.getMailboxField().setText(partner.getHeadOfficeAddress().getMailbox());
        partnerTemplateController.getHeadOfficeIdField().setText(((Integer) partner.getHeadOfficeAddress().getAddress_id()).toString());
        partnerTemplateController.setAddresses(partner.getAddresses());
        partnerTemplateController.getAddressTableView().setItems(partner.getAddresses());
        partnerTemplateController.getVatNrField().setText(partner.getVatNr());
        partnerTemplateController.getEuVatNrField().setText(partner.getEuVatNr());
        partnerTemplateController.getDefaultCurrencyComboBox().getSelectionModel().select(partner.getDefaultCurrency());
        partnerTemplateController.getDefaultVatComboBox().getSelectionModel().select(partner.getDefaultVat());
        partnerTemplateController.getPaymentMethodComboBox().getSelectionModel().select(partner.getPaymentMethod());
        partnerTemplateController.getPaymentDelayField().setText(((Integer) partner.getPaymentDelay().getDays()).toString());
        partnerTemplateController.getLimitField().setText(((Double) partner.getLimitValue()).toString());
        partnerTemplateController.getDefaultPhoneField().setText(partner.getDefaultPhone());
        partnerTemplateController.getDefaultEmailField().setText(partner.getDefaultEmail());
        partnerTemplateController.getDefaultFaxField().setText(partner.getDefaultFax());
        partnerTemplateController.setContacts(partner.getContacts());
        partnerTemplateController.getContactTableView().setItems(partner.getContacts());
        partnerTemplateController.getNoteArea().setText(partner.getNote());
    }

    @FXML
    public void deletePartner() {
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.OK,megse);
        alert.setTitle("Törlés megerősítése!");
        alert.setHeaderText("Biztosan töröli szeretné?");
        alert.setContentText(partnerTableView.getSelectionModel().getSelectedItem().toString() + "\n\n" +
                "!!! A partner összes számlája és befizetése is törlésre kerül !!!!");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // delete partner, invoice, payment, partnerAddressConnection, partnerContactConnection, invoicePaymentConnection
            DataSource.getInstance().deletePartnerCommit(partnerTableView.getSelectionModel().getSelectedItem());
            fillInPartnersAddressesContactsTables();
            showPartnerButton.setDisable(true);
            editPartnerButton.setDisable(true);
            deletePartnerButton.setDisable(true);
        }
    }

    @FXML
    public void showAddress() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(partnerAddressContactDialogPane.getScene().getWindow());
        dialog.setTitle("Cím részletezése:");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/address/addressDetails.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the addressDetails.fxml" + e.getMessage());
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        AddressDetailsController addressDetailsController = fxmlLoader.getController();
        addressDetailsController.getCountryField().setEditable(false);
        addressDetailsController.getCityField().setEditable(false);
        addressDetailsController.getPostcodeField().setEditable(false);
        addressDetailsController.getStreetField().setEditable(false);
        addressDetailsController.getHouseNrField().setEditable(false);
        addressDetailsController.getMailboxField().setEditable(false);
        addressDetailsController.getAddressTypeField().setEditable(false);
        dialog.showAndWait();
    }

    @FXML
    public void editAddress() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(partnerAddressContactDialogPane.getScene().getWindow());
        dialog.setTitle("Cím részeletezése:");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/address/addressDetails.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the addressDetails.fxml" + e.getMessage());
        }
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        AddressDetailsController addressDetailsController = fxmlLoader.getController();
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> addressDetailsController.getCountryField().getText().isEmpty() ||
                                addressDetailsController.getCityField().getText().isEmpty(),
                        addressDetailsController.getCountryField().textProperty(),
                        addressDetailsController.getCityField().textProperty()
                ));
        addressDetailsController.getAddressTypeField().setDisable(true);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Update Address
            try {
                DataSource.getInstance().updateAddress(
                        addressDetailsController.getCountryField().getText(),
                        addressDetailsController.getPostcodeField().getText(),
                        addressDetailsController.getCityField().getText(),
                        addressDetailsController.getStreetField().getText(),
                        addressDetailsController.getHouseNrField().getText(),
                        addressDetailsController.getMailboxField().getText(),
                        addressTableView.getSelectionModel().getSelectedItem().getAddress_id()
                );
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            //re-initialize
            fillInPartnersAddressesContactsTables();
            showAddressButton.setDisable(true);
            editAddressButton.setDisable(true);
            deleteAddressButton.setDisable(true);
        }
    }

    @FXML
    public void deleteAddress() {
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.OK,megse);
        alert.setTitle("Törlés megerősítése!");
        alert.setHeaderText("Biztosan töröli szeretné?\n\n" +
                "Székhelycím csak abban az esetben törölődik, ha egyik céghez sem kapcsolódik!");
        alert.setContentText(addressTableView.getSelectionModel().getSelectedItem().toString() + "\n\n");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // delete address
            Address selectedAddress = addressTableView.getSelectionModel().getSelectedItem();
            int counter = 0;
            //if headOffice address is used, delete is denied
            if (selectedAddress.getAddressType().equals(HEADOFFICEADDRESS)) {
                for (Partner partner : partners) {
                    if (partner.getHeadOfficeAddress().getAddress_id() == selectedAddress.getAddress_id()) {
                        counter++;
                        break;
                    }
                }
            }
            if (counter == 0) {
                DataSource.getInstance().deleteAddressCommit(selectedAddress.getAddress_id());
                fillInPartnersAddressesContactsTables();
                showAddressButton.setDisable(true);
                editAddressButton.setDisable(true);
                deleteAddressButton.setDisable(true);
            }
        }
    }

    @FXML
    public void showContact() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(partnerAddressContactDialogPane.getScene().getWindow());
        dialog.setTitle("Kapcsolat részeletezése:");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/contact/contactDetails.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the contactDetails.fxml" + e.getMessage());
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        ContactDetailsController contactDetailsController = fxmlLoader.getController();
        contactDetailsController.getNameField().setEditable(false);
        contactDetailsController.getPhoneNrField().setEditable(false);
        contactDetailsController.getFaxNrField().setEditable(false);
        contactDetailsController.getEmailField().setEditable(false);
        contactDetailsController.getNoteArea().setEditable(false);
        dialog.showAndWait();
    }

    @FXML
    public void editContact() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(partnerAddressContactDialogPane.getScene().getWindow());
        dialog.setTitle("Kapcsolat szerkesztése:");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/contact/contactDetails.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the contactDetails.fxml" + e.getMessage());
            return;
        }
        //Binding DialogPane OK Button check invalid fields
        //get the dialogPane controller
        ContactDetailsController contactDetailsController = fxmlLoader.getController();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> contactDetailsController.getNameField().getText().isEmpty(),
                        contactDetailsController.getNameField().textProperty()
                ));
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Update Contact
            try {
                DataSource.getInstance().updateContact(
                        contactDetailsController.getNameField().getText(),
                        contactDetailsController.getPhoneNrField().getText(),
                        contactDetailsController.getFaxNrField().getText(),
                        contactDetailsController.getEmailField().getText(),
                        contactDetailsController.getNoteArea().getText(),
                        contactTableView.getSelectionModel().getSelectedItem().getContact_id()
                );
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            //re-initialize
            fillInPartnersAddressesContactsTables();
            showContactButton.setDisable(true);
            editContactButton.setDisable(true);
            deleteContactButton.setDisable(true);
        }
    }

    @FXML
    public void deleteContact() {
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.OK,megse);
        alert.setTitle("Törlés megerősítése!");
        alert.setHeaderText("Biztosan töröli szeretné?\n\n");
        alert.setContentText(contactTableView.getSelectionModel().getSelectedItem().toString() + "\n\n");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // delete contact
            Contact selectedContact = contactTableView.getSelectionModel().getSelectedItem();
            DataSource.getInstance().deleteContactCommit(selectedContact.getContact_id());
            fillInPartnersAddressesContactsTables();
            showContactButton.setDisable(true);
            editContactButton.setDisable(true);
            deleteContactButton.setDisable(true);
        }
    }
}