package invoice;

import dataModel.Invoice;
import dataModel.InvoicePayment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.io.IOException;


import static dataModel.Payment.CASH_PAYMENTMETHOD;
import static invoice.InvoicesController.DOUBLE_FORMATTER;

public class InvoiceDetailsController {

    @FXML
    TableView<InvoicePayment> invoicePaymentTableView;
    @FXML
    TextField partnerField;
    @FXML
    TextField contactField;
    @FXML
    TextField invoiceNrField;
    @FXML
    TextField dateOfInvoiceField;
    @FXML
    TextField dateOfPerformanceField;
    @FXML
    TextField dateOfDueField;
    @FXML
    TextField regNrField;
    @FXML
    TextField currencyField;
    @FXML
    TextField vatField;
    @FXML
    TextField paymentMethodField;
    @FXML
    TextField netAmountField;
    @FXML
    TextField grossAmountField;
    @FXML
    TextField balanceField;
    @FXML
    TextArea noteArea;
    @FXML
    Label dateOfDueLabel;
    @FXML
    Label paymentsLabel;
    @FXML
    TableColumn<InvoicePayment,Number> payedOriginalAmountColumn;
    @FXML
    TableColumn<InvoicePayment,Number> payedAmountColumn;

    private static final Callback<TableColumn<InvoicePayment,Number>,TableCell<InvoicePayment,Number>> INVOICEPAYMENT_DOUBLE_CELL_FORMATTER
            = new Callback<>() {
        @Override
        public TableCell<InvoicePayment, Number> call(TableColumn<InvoicePayment, Number> param) {
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

    private Invoice invoice;

    public void initialize(){
        payedAmountColumn.setCellFactory(INVOICEPAYMENT_DOUBLE_CELL_FORMATTER);
        payedOriginalAmountColumn.setCellFactory(INVOICEPAYMENT_DOUBLE_CELL_FORMATTER);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/invoice/invoices.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Couldn't load the invoice.fxml  in InvoiceDetailsController" + e.getMessage());
        }
        InvoicesController invoicesController = loader.getController();
        invoice = invoicesController.getSelectedInvoice();


        if(invoice != null){
            partnerField.setText(invoice.getPartner().toString());
            contactField.setText(invoice.getContact().toString());
            invoiceNrField.setText(invoice.getInvoiceNr());
            dateOfInvoiceField.setText(invoice.getDateOfInvoice().toString());
            dateOfPerformanceField.setText(invoice.getDateOfPerformance().toString());
            dateOfDueField.setText(invoice.getDateOfDue().toString());
            regNrField.setText(invoice.getRegNr());
            currencyField.setText(invoice.getCurrency());
            vatField.setText(invoice.getVat());
            paymentMethodField.setText(invoice.getPaymentMethod());
            netAmountField.setText(DOUBLE_FORMATTER.format(invoice.getNetAmount()));
            grossAmountField.setText(DOUBLE_FORMATTER.format(invoice.getGrossAmount()));
            balanceField.setText(DOUBLE_FORMATTER.format(invoice.getBalance()));
            noteArea.setText(invoice.getNote());
            Task<ObservableList<InvoicePayment>> task = new Task<>() {
                @Override
                protected ObservableList<InvoicePayment> call() {
                    ObservableList<InvoicePayment> invoicePayments = FXCollections.observableArrayList();
                    invoicePayments.addAll(invoice.getInvoicePayments());
                    return invoicePayments;
                }
            };
            invoicePaymentTableView.itemsProperty().bind(task.valueProperty());
            new Thread(task).start();

            if (invoice.getPaymentMethod().equals(CASH_PAYMENTMETHOD)){
                dateOfDueLabel.setVisible(false);
                dateOfDueField.setVisible(false);
                paymentsLabel.setDisable(true);
                invoicePaymentTableView.setDisable(true);
            }
        }
    }
}