package invoice;

import dataModel.Contact;
import dataModel.Invoice;
import dataModel.Partner;
import db.DataSource;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import partner.PartnerTemplateController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static dataModel.Invoice.*;
import static dataModel.Payment.CASH_PAYMENTMETHOD;
import static dataModel.Payment.PREPAY_PAYMENTMETHOD;
import static dataModel.Payment.TRANSFER_PAYMENTMETHOD;
import static invoice.InvoicesController.DOUBLE_FORMATTER;

public class InvoiceTemplateController {

    @FXML
    public TextField invoiceNrField;
    @FXML
    ComboBox<Partner> partnerComboBox;
    @FXML
    ComboBox<Contact> contactComboBox;
    @FXML
    ComboBox<String> currencyComboBox;
    @FXML
    ComboBox<String> vatComboBox;
    @FXML
    ComboBox<String> paymentMethodComboBox;
    @FXML
    DatePicker dateOfInvoiceDatePicker;
    @FXML
    DatePicker dateOfPerformanceDatePicker;
    @FXML
    Label dateOfDueDateLabel;
    @FXML
    DatePicker dateOfDueDatePicker;
    @FXML
    public TextField netAmountField;
    @FXML
    Label grossAmountLabel;
    @FXML
    TextField regNrField;
    @FXML
    TextArea noteArea;
    @FXML
    DialogPane partnerDialogPane;

    //getters due to bind
    public TextField getInvoiceNrField() {
        return invoiceNrField;
    }
    public TextField getNetAmountField() {
        return netAmountField;
    }

    private ObservableList<Partner> partners;
    private Double grossAmount;


    public void initialize() {

        // Force netAmount TextField to double
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
        };
        StringConverter<Double> converter = new StringConverter<Double>() {
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
        TextFormatter<Double> doubleTextFormatter = new TextFormatter<>(converter, 0.0, filter);
        netAmountField.setTextFormatter(doubleTextFormatter);
        doubleTextFormatter.valueProperty().addListener((ObservableValue<? extends Double> obs, Double oldDoubleValue, Double newDoubleValue) -> {
            switch (vatComboBox.getSelectionModel().getSelectedItem()){
                case VAT_27:    grossAmount = newDoubleValue*1.27;
                    grossAmountLabel.setText(DOUBLE_FORMATTER.format(grossAmount));
                    break;
                case VAT_0:     grossAmount = newDoubleValue;
                    grossAmountLabel.setText(DOUBLE_FORMATTER.format(grossAmount));
                    break;
            }
        });

        partners = DataSource.getInstance().getPartners();

        partnerComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Partner partner = partnerComboBox.getSelectionModel().getSelectedItem();
                currencyComboBox.setValue(partner.getDefaultCurrency());
                vatComboBox.setValue(partner.getDefaultVat());
                paymentMethodComboBox.setValue(partner.getPaymentMethod());
                dateOfDueDatePicker.setValue(dateOfInvoiceDatePicker.valueProperty().getValue().plusDays(newValue.getPaymentDelay().getDays()));
                contactComboBox.setItems(partner.getContacts());
                if (!partner.getContacts().isEmpty())
                    contactComboBox.setValue(partner.getContacts().get(0));
            }
        });

        currencyComboBox.setItems(FXCollections.observableArrayList(FT_CURRENCY, EUR_CURRENCY, GBP_CURRENCY));
        currencyComboBox.setValue(FT_CURRENCY);

        vatComboBox.setItems(FXCollections.observableArrayList(VAT_27, VAT_0));
        vatComboBox.setValue(VAT_27);
        vatComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Double grossAmount = doubleTextFormatter.getValue();
            switch (newValue) {
                case VAT_27:
                    grossAmount *= 1.27;
                    grossAmountLabel.setText((DOUBLE_FORMATTER.format(grossAmount)));
                    break;
                case VAT_0:
                    grossAmountLabel.setText((DOUBLE_FORMATTER.format(grossAmount)));
                    break;
            }
        });

        paymentMethodComboBox.setItems(FXCollections.observableArrayList(CASH_PAYMENTMETHOD, TRANSFER_PAYMENTMETHOD, PREPAY_PAYMENTMETHOD));
        paymentMethodComboBox.setValue(CASH_PAYMENTMETHOD);

        dateOfInvoiceDatePicker.setValue(LocalDate.now());
        dateOfPerformanceDatePicker.setValue(LocalDate.now());
        dateOfDueDatePicker.setValue(dateOfInvoiceDatePicker.getValue());
        dateOfDueDateLabel.setVisible(false);
        dateOfDueDatePicker.setVisible(false);
        dateOfInvoiceDatePicker.valueProperty().addListener((ov, oldDateValue, newDateValue) -> {
            if (partnerComboBox.getSelectionModel().getSelectedItem() != null) {
                dateOfDueDatePicker.setValue(newDateValue.plusDays(partnerComboBox.getSelectionModel().getSelectedItem()
                        .getPaymentDelay().getDays()));
            } else {
                dateOfDueDatePicker.setValue(LocalDate.now());
            }
        });

        paymentMethodComboBox.valueProperty().addListener((observable,oldValue,newValue) ->  {
            if (newValue.equals(CASH_PAYMENTMETHOD)) {
                dateOfDueDateLabel.setVisible(false);
                dateOfDueDatePicker.setVisible(false);
            } else {
                dateOfDueDateLabel.setVisible(true);
                dateOfDueDatePicker.setVisible(true);
            }
        });

        if(!partners.isEmpty()) {
            partnerComboBox.setItems(partners);
            Partner partner = partners.get(0);
            partnerComboBox.setValue(partner);
            currencyComboBox.setValue(partner.getDefaultCurrency());
            vatComboBox.setValue(partner.getDefaultVat());
            paymentMethodComboBox.setValue(partner.getPaymentMethod());
            dateOfDueDatePicker.setValue(LocalDate.now().plusDays(partner.getPaymentDelay().getDays()));
            contactComboBox.setItems(partner.getContacts());
            if (!partner.getContacts().isEmpty())
                contactComboBox.setValue(partner.getContacts().get(0));
        }
    }


    @FXML
    public void showPartnerTemplate(){
        javafx.scene.control.Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(partnerDialogPane.getScene().getWindow());
        dialog.setTitle("Új partner hozzáadása");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/partner/PartnerTemplate.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the partnerTemplate.fxml" + e.getMessage());
        }

        //Binding DialogPane OK Button check invalid fields
        //get the dialogPane controller
        PartnerTemplateController partnerTemplateController = fxmlLoader.getController();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> partnerTemplateController.getNameField().getText().isEmpty()||
                                partnerTemplateController.getCityField().getText().isEmpty(),
                        partnerTemplateController.getNameField().textProperty(),
                        partnerTemplateController.getCityField().textProperty()
                ));
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            PartnerTemplateController controller = fxmlLoader.getController();
            Partner newPartner = controller.processPartnerResult();
            // WRITE THE ALL NEW PARTNER CLASS TO DATABASE
            DataSource.getInstance().insertPartnerCommit(newPartner);
            //reload database
            partnerComboBox.setItems(DataSource.getInstance().getPartners());
            partnerComboBox.getSelectionModel().select(newPartner);
        }
    }

    @FXML
    public Invoice processInvoiceResult(){
        SimpleStringProperty invoiceNr = new SimpleStringProperty();
        invoiceNr.set(invoiceNrField.getText().trim());
        SimpleStringProperty regNr = new SimpleStringProperty();
        regNr.set(regNrField.getText().trim());
        Partner partner = partnerComboBox.getSelectionModel().getSelectedItem();
        Contact contact = contactComboBox.getSelectionModel().getSelectedItem();
        SimpleStringProperty currency = new SimpleStringProperty();
        currency.set(currencyComboBox.getSelectionModel().getSelectedItem());
        SimpleStringProperty vat = new SimpleStringProperty();
        vat.set(vatComboBox.getSelectionModel().getSelectedItem());
        SimpleStringProperty paymentMethod = new SimpleStringProperty();
        paymentMethod.set(paymentMethodComboBox.getSelectionModel().getSelectedItem());
        LocalDate dateOfInvoice = dateOfInvoiceDatePicker.getValue();
        LocalDate dateOfPerformance = dateOfPerformanceDatePicker.getValue();
        LocalDate dateOfDue = dateOfDueDatePicker.getValue();
        SimpleDoubleProperty netAmount = new SimpleDoubleProperty();
        netAmount.set(Double.valueOf(netAmountField.getText()));
        SimpleDoubleProperty balance = new SimpleDoubleProperty();
        balance.set(paymentMethod.getValue().equals(CASH_PAYMENTMETHOD) ? 0.0 : grossAmount);
        SimpleStringProperty note = new SimpleStringProperty();
        note.set(noteArea.getText());

        return new Invoice(partner,contact,invoiceNr,regNr,netAmount,vat,currency,balance,paymentMethod,
                            dateOfInvoice,dateOfPerformance,dateOfDue,note);
    }
}