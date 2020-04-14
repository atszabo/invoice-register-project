package main;

import dataModel.*;
import db.DataSource;
import invoice.InvoiceTemplateController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import partner.PartnerTemplateController;
import payment.PaymentTemplateController;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.stream.Collectors;

import static dataModel.Invoice.EUR_CURRENCY;
import static dataModel.Invoice.FT_CURRENCY;
import static dataModel.Invoice.GBP_CURRENCY;


public class Controller {
    //double format
    private static final DecimalFormat DOUBLE_FORMATTER = new DecimalFormat("#,##0.00");

    private ObservableList<Invoice> invoices;


    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private TableView<Invoice> invoicesTableView;
    @FXML
    private ComboBox<String> currencyComboBox;
    @FXML
    TableColumn<Invoice,Double> netAmountColumn;
    @FXML
    TableColumn<Invoice,Double> grossAmountColumn;
    @FXML
    TableColumn<Invoice,Double> balanceColumn;


    public void initialize() {
        netAmountColumn.setCellFactory(col ->
            new TableCell<>() {
                @Override
                public void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setText(null);
                    else setText(DOUBLE_FORMATTER.format(item));
                }
            });
        grossAmountColumn.setCellFactory(col ->
            new TableCell<>() {
                @Override
                public void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setText(null);
                    else setText(DOUBLE_FORMATTER.format(item));
                }
            });
        balanceColumn.setCellFactory(col ->
            new TableCell<>() {
                @Override
                public void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setText(null);
                    else setText(DOUBLE_FORMATTER.format(item));
                }
            });
        currencyComboBox.setItems(FXCollections.observableArrayList(FT_CURRENCY,EUR_CURRENCY,GBP_CURRENCY));
        initializeFtInvoices();
        currencyComboBox.valueProperty().addListener((observable,oldValue,newValue) -> {
            Task<ObservableList<Invoice>> switchTask = new Task<>() {
                @Override
                protected ObservableList<Invoice> call() {
                    switch(newValue){
                    case FT_CURRENCY: return FXCollections.observableArrayList(invoices.parallelStream()
                                        .filter(invoice -> invoice.getBalance()>0)
                                        .filter(invoice -> invoice.getCurrency().equals(FT_CURRENCY))
                                        .collect(Collectors.toList()));
                    case GBP_CURRENCY: return FXCollections.observableArrayList(invoices.parallelStream()
                                        .filter(invoice -> invoice.getCurrency().equals(GBP_CURRENCY))
                                        .filter(invoice -> invoice.getBalance()>0)
                                        .collect(Collectors.toList()));
                    case EUR_CURRENCY: return FXCollections.observableArrayList(invoices.parallelStream()
                                        .filter(invoice -> invoice.getCurrency().equals(EUR_CURRENCY))
                                        .filter(invoice -> invoice.getBalance()>0)
                                        .collect(Collectors.toList()));
                    default: return FXCollections.observableArrayList();
                    }
                }
            };
            invoicesTableView.itemsProperty().bind(switchTask.valueProperty());
            new Thread(switchTask).start();
        });
    }

    private void initializeFtInvoices(){
        currencyComboBox.setValue(FT_CURRENCY);
        invoices = DataSource.getInstance().getInvoices();
        Task<ObservableList<Invoice>> task = new Task<>() {
            @Override
            protected ObservableList<Invoice> call() {
                return FXCollections.observableArrayList(invoices.stream()
                        .filter(invoice -> invoice.getBalance()>0)
                        .filter(invoice -> invoice.getCurrency().equals(FT_CURRENCY))
                        .collect(Collectors.toList()));
            }
        };
        invoicesTableView.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    //Partner button action
    @FXML
    public void showPartners(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000,600);
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Partnerek, címek, kapcsolatok kezelése");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/partner/partnerAddressContact.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the partnerAddressContact.fxml" + e.getMessage());
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            initialize();
        }
    }

    //add a new partner button on action
    @FXML
    public void showPartnerTemplate(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000,600);
        dialog.initOwner(mainBorderPane.getScene().getWindow());
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
        }
    }

    @FXML
    public void showInvoices(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000,600);
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Számlák");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/invoice/invoices.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the invoices.fxml" + e.getMessage());
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            initializeFtInvoices();
        }
    }

    @FXML
    public void showNewInvoice(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000,600);
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Új számla hozzáadása");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/invoice/invoiceTemplate.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the invoiceTemplate.fxml" + e.getMessage());
        }
        //Binding DialogPane OK Button check invalid fields
        //get the dialogPane controller
        InvoiceTemplateController invoiceTemplateController = fxmlLoader.getController();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> invoiceTemplateController.invoiceNrField.getText().isEmpty()||
                                invoiceTemplateController.netAmountField.getText().equals("0.0"),
                                invoiceTemplateController.getInvoiceNrField().textProperty(),
                                invoiceTemplateController.getNetAmountField().textProperty()
                ));
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            InvoiceTemplateController controller = fxmlLoader.getController();
            Invoice newInvoice = controller.processInvoiceResult();
            // WRITE THE INVOICE CLASS TO DATABASE
            try {
                DataSource.getInstance().insertInvoice(newInvoice);
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
            //re-initialize the TableView
            initializeFtInvoices();
        }
    }

    @FXML
    public void showPayments(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000,600);
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Befizetések");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/payment/payments.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the payments.fxml" + e.getMessage());
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            initializeFtInvoices();
        }
    }


    @FXML
    public void showNewPayment() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000, 600);
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Új befizetés");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/payment/paymentTemplate.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the paymentTemplate.fxml" + e.getMessage());
        }
        //Binding DialogPane OK Button check invalid fields
        //get the dialogPane controller
        PaymentTemplateController paymentTemplateController = fxmlLoader.getController();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> !paymentTemplateController.getHoldToggleButton().isSelected(),paymentTemplateController.getHoldToggleButton().selectedProperty()
                ));
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            PaymentTemplateController controller = fxmlLoader.getController();
            Payment newPayment = controller.processPaymentResult();
            // write invoicePayment, payments and update invoice in database
            DataSource.getInstance().insertPaymentCommit(newPayment);
            //re-initialize the TableView
            initializeFtInvoices();
        }
    }
}
