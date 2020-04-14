package invoice;

import dataModel.Invoice;
import dataModel.Partner;
import dataModel.Payment;
import db.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static dataModel.Invoice.*;
import static dataModel.Payment.*;


public class InvoicesController {

    @FXML
    DialogPane invoicesDialogPane;
    @FXML
    ComboBox<Partner> partnerComboBox;
    @FXML
    ComboBox<String> currencyComboBox;
    @FXML
    ComboBox<String> paymentMethodComboBox;
    @FXML
    ComboBox<String> filterComboBox;
    @FXML
    DatePicker fromDatePicker;
    @FXML
    DatePicker toDatePicker;
    @FXML
    Label sumLabel;
    @FXML
    Label sumUnPayedLabel;
    @FXML
    Label sumDelayedLabel;
    @FXML
    TableView<Invoice> invoiceTableView;
    @FXML
    TableColumn<Invoice,Number> netAmountColumn;
    @FXML
    TableColumn<Invoice,Number> grossAmountColumn;
    @FXML
    TableColumn<Invoice,Number> balanceColumn;
    @FXML
    Button showInvoiceDetailsButton;
    @FXML
    Button deleteInvoiceButton;

    //double format
    static final DecimalFormat DOUBLE_FORMATTER = new DecimalFormat("#,##0.00");

    private static final Callback<TableColumn<Invoice,Number>,TableCell<Invoice,Number>> INVOICE_DOUBLE_CELL_FORMATTER
            = new Callback<>() {
        @Override
        public TableCell<Invoice, Number> call(TableColumn<Invoice, Number> param) {
            return new TableCell<>() {
                @Override
                public void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setText(null);
                    else setText(DOUBLE_FORMATTER.format(item));
                }
            };
        }
    };

    private ObservableList<Invoice> invoices;

    private static Invoice selectedInvoice;

    //getter in order to reach from other invoiceDetailsController
    Invoice getSelectedInvoice() {
        return selectedInvoice;
    }

    public void initialize(){
        showInvoiceDetailsButton.setDisable(true);
        deleteInvoiceButton.setDisable(true);

        // get data from database
        invoices = DataSource.getInstance().getInvoices();
        ObservableList<Partner> partners = DataSource.getInstance().getPartners();
        //upload invoices with payments
        ObservableList<Payment> payments =  DataSource.getInstance().getPayments();
        for(Payment payment: payments){
            for (Invoice payedInvoice : payment.getInvoices()){
                for (Invoice invoice : invoices) {
                    if (invoice.getInvoice_id() == payedInvoice.getInvoice_id()) {
                        invoice.setInvoicePayments(payedInvoice.getInvoicePayments());
                        break;
                    }
                }
            }
        }

        netAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        grossAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        balanceColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);

        // fill in table with data at first time
        if (!(partners.isEmpty() && invoices.isEmpty())) {
            partnerComboBox.setItems(partners);
            partnerComboBox.getSelectionModel().select(0);
            currencyComboBox.setItems(FXCollections.observableArrayList(FT_CURRENCY,EUR_CURRENCY,GBP_CURRENCY));
            currencyComboBox.getSelectionModel().select(FT_CURRENCY);
            paymentMethodComboBox.setItems(FXCollections.observableArrayList(ALL_PAYMENTMETHOD,CASH_PAYMENTMETHOD,TRANSFER_PAYMENTMETHOD,PREPAY_PAYMENTMETHOD));
            paymentMethodComboBox.getSelectionModel().select(ALL_PAYMENTMETHOD);
            filterComboBox.setItems(FXCollections.observableArrayList(DATEOFINVOICE,DATEOFPERFORMANCE,DATEOFDUE));
            filterComboBox.getSelectionModel().select(DATEOFINVOICE);
            fromDatePicker.setValue(LocalDate.of(2018,1,1));
            toDatePicker.setValue(LocalDate.now().plusDays(1));

            fillInTableView();
        }

        //changeListeners
        partnerComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> fillInTableView());
        currencyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> fillInTableView());
        paymentMethodComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> fillInTableView());
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> fillInTableView());
        fromDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> fillInTableView());
        toDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> fillInTableView());
        invoiceTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            selectedInvoice = newValue;
            showInvoiceDetailsButton. setDisable(false);
            deleteInvoiceButton.setDisable(false);
        });
    }

    private void fillInTableView() {
        Predicate<Invoice> dateFilterPredicate = invoice -> {
            switch (filterComboBox.getSelectionModel().getSelectedItem()){
                case DATEOFINVOICE : return invoice.getDateOfInvoice().isBefore(toDatePicker.getValue().plusDays(1))
                        && invoice.getDateOfInvoice().isAfter(fromDatePicker.getValue().minusDays(1));
                case DATEOFPERFORMANCE : return invoice.getDateOfPerformance().isBefore(toDatePicker.getValue().plusDays(1))
                        && invoice.getDateOfPerformance().isAfter(fromDatePicker.getValue().minusDays(1));
                case DATEOFDUE : return invoice.getDateOfDue().isBefore(toDatePicker.getValue().plusDays(1))
                        && invoice.getDateOfDue().isAfter(fromDatePicker.getValue().minusDays(1));
                default : return false;
            }
        };

        Predicate<Invoice> predicate = invoice ->
                invoice.getPartner().getPartner_id() == partnerComboBox.getSelectionModel().getSelectedItem().getPartner_id()
                        && invoice.getCurrency().equals(currencyComboBox.getSelectionModel().getSelectedItem())
                        && (invoice.getPaymentMethod().equals(paymentMethodComboBox.getSelectionModel().getSelectedItem()) ||
                        (paymentMethodComboBox.getSelectionModel().getSelectedItem().equals(ALL_PAYMENTMETHOD)));

        Task<ObservableList<Invoice>> task = new Task<>() {
            @Override
            protected ObservableList<Invoice> call() {
                return FXCollections.observableArrayList(invoices.stream()
                        .filter(dateFilterPredicate)
                        .filter(predicate)
                        .collect(Collectors.toList()));
            }
        };
        invoiceTableView.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();

        task.setOnSucceeded((event) -> {
                sumLabel.setText("Összesen: "
                        + DOUBLE_FORMATTER.format(task.getValue().stream().mapToDouble(Invoice::getGrossAmount).sum())
                        + " "
                        + currencyComboBox.getSelectionModel().getSelectedItem());
                sumUnPayedLabel.setText("Összes kiegyenlítetlen: "
                        + DOUBLE_FORMATTER.format(task.getValue().stream().mapToDouble(Invoice::getBalance).sum())
                        + " "
                        + currencyComboBox.getSelectionModel().getSelectedItem());
                sumDelayedLabel.setText("Összes lejárt: "
                        + DOUBLE_FORMATTER.format(task.getValue().stream().filter(invoice -> invoice.getDateOfDue().isBefore(LocalDate.now().plusDays(1)))
                            .mapToDouble(Invoice::getBalance).sum())
                        + " "
                        + currencyComboBox.getSelectionModel().getSelectedItem());
        });
    }

    @FXML
    public void showInvoiceDetails() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000,600);
        dialog.initOwner(invoicesDialogPane.getScene().getWindow());
        dialog.setTitle("Számla részletezése");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/invoice/invoiceDetails.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the invoiceDetails.fxml" + e.getMessage());
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    @FXML
    public void deleteInvoice(){
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.OK,megse);
        alert.setTitle("Törlés megerősítése!");
        alert.setHeaderText("Biztosan töröli szeretné?");
        alert.setContentText("Számla sorszáma: " + selectedInvoice.getInvoiceNr() + "\tbruttó:  " + DOUBLE_FORMATTER.format(selectedInvoice.getGrossAmount())
                + " " + getSelectedInvoice().getCurrency() + "\n\n");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            // delete invoicePaymentConnection, invoice, update the payment in database
            DataSource.getInstance().deleteInvoiceCommit(selectedInvoice);
            //re-initialize
            initialize();
            showInvoiceDetailsButton.setDisable(true);
            deleteInvoiceButton.setDisable(true);
        }
    }
}