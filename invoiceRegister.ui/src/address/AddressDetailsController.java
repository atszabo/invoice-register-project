package address;

import dataModel.Address;
import dataModel.Partner;
import db.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import partner.PartnerAddressContactController;

import java.io.IOException;

public class AddressDetailsController {

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
    private TextField addressTypeField;
    @FXML
    private ListView<String> partnerListView;

    //getters for other controllers
    public TextField getCountryField() {
        return countryField;
    }

    public TextField getPostcodeField() {
        return postcodeField;
    }

    public TextField getCityField() {
        return cityField;
    }

    public TextField getStreetField() {
        return streetField;
    }

    public TextField getHouseNrField() {
        return houseNrField;
    }

    public TextField getMailboxField() {
        return mailboxField;
    }

    public TextField getAddressTypeField() {
        return addressTypeField;
    }

    private ObservableList<String> selectedPartners = FXCollections.observableArrayList();

    public void initialize(){
        ObservableList<Partner> partners = DataSource.getInstance().getPartners();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/partner/partnerAddressContact.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Couldn't load the partnerAddressContact.fxml  in AddressDetailsController" + e.getMessage());
        }
        PartnerAddressContactController partnerAddressContactController = loader.getController();
        Address address = partnerAddressContactController.getSelectedAddress();
        ObservableList<Integer> partnerIds = DataSource.getInstance().getPartnerIdsByAddressId(address.getAddress_id());

        for (Integer partnerId: partnerIds) {
            for (Partner partner : partners) {
                if (partnerId == partner.getPartner_id()) {
                    selectedPartners.add(partner.toString());
                    break;
                }
            }
        }

        countryField.setText(address.getCountry());
        postcodeField.setText(address.getPostcode());
        cityField.setText(address.getCity());
        streetField.setText(address.getStreet());
        houseNrField.setText(address.getHouseNr());
        mailboxField.setText(address.getMailbox());
        addressTypeField.setText(address.getAddressType());
        partnerListView.setItems(selectedPartners);

    }

}
