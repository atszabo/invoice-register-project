package contact;

import dataModel.Contact;
import dataModel.Partner;
import db.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import partner.PartnerAddressContactController;
import java.io.IOException;

public class ContactDetailsController {
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
    private ListView<String> partnerListView;


    //getters for controllers
    public TextField getNameField() {
        return nameField;
    }

    public TextField getPhoneNrField() {
        return phoneNrField;
    }

    public TextField getFaxNrField() {
        return faxNrField;
    }

    public TextField getEmailField() {
        return emailField;
    }

    public TextArea getNoteArea() {
        return noteArea;
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
        Contact contact = partnerAddressContactController.getSelectedContact();
        ObservableList<Integer> partnerIds = DataSource.getInstance().getPartnerIdsByContactId(contact.getContact_id());

        for (Integer partnerId: partnerIds) {
            for (Partner partner : partners) {
                if (partnerId == partner.getPartner_id()) {
                    selectedPartners.add(partner.toString());
                    break;
                }
            }
        }

        nameField.setText(contact.getName());
        phoneNrField.setText(contact.getPhoneNr());
        faxNrField.setText(contact.getFaxNr());
        emailField.setText(contact.getEmail());
        noteArea.setText(contact.getNote());
        partnerListView.setItems(selectedPartners);

    }
}
