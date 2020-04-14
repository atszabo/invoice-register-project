package payment;


import dataModel.Invoice;
import dataModel.InvoicePayment;
import dataModel.Payment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

import static payment.PaymentsController.DOUBLE_FORMATTER;
import static payment.PaymentsController.INVOICE_DOUBLE_CELL_FORMATTER;


public class PaymentDetailsController {
    @FXML
    private TextField partnerField;
    @FXML
    private TextField paymentMethodField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField currencyField;
    @FXML
    private TextField dateField;
    @FXML
    private TextArea noteArea;
    @FXML
    private TableView<Invoice> paymentTableView;
    @FXML
    private TextField balanceField;
    @FXML
    private  TextField balanceCurrencyField;
    @FXML
    TableColumn<Invoice, Number> grossAmountColumn;
    @FXML
    TableColumn<Invoice, Number> payedAmountColumn;
    @FXML
    TableColumn<Invoice, Number> payedOriginalAmountColumn;


    public void initialize(){
        //double table cell format
        grossAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        payedAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        payedOriginalAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/payment/payments.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Couldn't load the payments.fxml  in PaymentDetailsController" + e.getMessage());
        }
        PaymentsController paymentsController = loader.getController();
        Payment payment = paymentsController.getPayment();

        if(payment != null)
        {
            partnerField.setText(payment.getPartner().toString());
            paymentMethodField.setText(payment.getPaymentMethod());
            amountField.setText(DOUBLE_FORMATTER.format(payment.getAmount()));
            currencyField.setText(payment.getCurrency());
            dateField.setText(payment.getDate().toString());
            noteArea.setText(payment.getNote());
            balanceField.setText(DOUBLE_FORMATTER.format(payment.getBalance()));
            balanceCurrencyField.setText(payment.getCurrency());

            Task<ObservableList<Invoice>> task = new Task<>() {
                @Override
                protected ObservableList<Invoice> call() {
                    ObservableList<Invoice> payedInvoices = FXCollections.observableArrayList();
                    for (Invoice invoice : payment.getInvoices()) {
                        for (InvoicePayment invoicePayment: invoice.getInvoicePayments()) {
                            if(invoicePayment.getPayment_id() == payment.getPayment_id()){
                                invoice.setPayedAmount(invoicePayment.getPayedAmount());
                                invoice.setPayedOriginalAmount(invoicePayment.getPayedOriginalAmount());
                                payedInvoices.add(invoice);

                            }
                        }
                    }
                    return payedInvoices;
                }
            };
            paymentTableView.itemsProperty().bind(task.valueProperty());
            new Thread(task).start();
        }
    }
}