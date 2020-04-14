package payment;

import dataModel.Invoice;
import dataModel.InvoicePayment;
import dataModel.Partner;
import dataModel.Payment;
import db.DataSource;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static dataModel.Invoice.EUR_CURRENCY;
import static dataModel.Invoice.FT_CURRENCY;
import static dataModel.Invoice.GBP_CURRENCY;
import static payment.PaymentsController.INVOICE_DOUBLE_CELL_FORMATTER;


public class EditPaymentTemplateController {

    @FXML
    DialogPane editPaymentDialogPane;

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
    private TextField eurFtField;
    @FXML
    private Label eurFtLabel;
    @FXML
    private TextField gbpFtField;
    @FXML
    private Label gbpFtLabel;
    @FXML
    private TextField gbpEurField;
    @FXML
    private Label gbpEurLabel;
    @FXML
    private Label paymentBalanceLabel;
    @FXML
    private Label paymentBalanceCurrencyLabel;
    @FXML
    private TableView<Invoice> unPayedInvoicesTableView;
    @FXML
    private Button payInButton;
    @FXML
    private TableView<Invoice> payedInvoicesTableView;
    @FXML
    ToggleButton holdToggleButton;
    @FXML
    private TableColumn<Invoice, Number> grossAmountColumn;
    @FXML
    private TableColumn<Invoice, Number> balanceColumn;
    @FXML
    private  TableColumn<Invoice, Number> balanceInCurrencyColumn;
    @FXML
    private TableColumn<Invoice, Number> payedAmountColumn;
    @FXML
    private TableColumn<Invoice, Number> payedBalanceColumn;

    private ObservableList<Invoice> invoices;
    private ObservableList<Invoice> payedInvoices = FXCollections.observableArrayList();
    private ObservableList<Invoice> newPayedInvoices = FXCollections.observableArrayList();
    private Payment payment;
    private Double paymentBalance;

    public void initialize(){
        grossAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        balanceColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        balanceInCurrencyColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        payedBalanceColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        payedAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);

        // Force ( eurFtField, gbpFtField, gbpEurField) TextFields to double
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };
        StringConverter<Double> converter = new StringConverter<>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };

        TextFormatter<Double> eurFieldTextFormatter = new TextFormatter<>(converter, 0.0, filter);
        eurFtField.setTextFormatter(eurFieldTextFormatter);
        TextFormatter<Double> gbpFieldTextFormatter = new TextFormatter<>(converter, 0.0, filter);
        gbpFtField.setTextFormatter(gbpFieldTextFormatter);
        TextFormatter<Double> gbpEurFieldTextFormatter = new TextFormatter<>(converter, 0.0, filter);
        gbpEurField.setTextFormatter(gbpEurFieldTextFormatter);



        invoices = DataSource.getInstance().getInvoices();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/payment/payments.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            System.out.println("Couldn't load the payments.fxml in EditPaymentTemplateController" + e.getMessage());
        }
        PaymentsController paymentsController = loader.getController();
        payment = paymentsController.getPayment();

        partnerField.setText(payment.getPartner().toString());
        partnerField.setDisable(true);
        paymentMethodField.setText(payment.getPaymentMethod());
        paymentMethodField.setDisable(true);
        amountField.setText(((Double) payment.getAmount()).toString());
        amountField.setDisable(true);
        currencyField.setText(payment.getCurrency());
        currencyField.setDisable(true);
        dateField.setText(payment.getDate().toString());
        dateField.setDisable(true);
        noteArea.setText(payment.getNote());
        currencyVisibilitySetter(payment.getCurrency());
        paymentBalance = payment.getBalance();
        paymentBalanceLabel.setText(PaymentsController.DOUBLE_FORMATTER.format(paymentBalance));
        paymentBalanceCurrencyLabel.setText(" " + payment.getCurrency());
        eurFtField.setText("0.0");
        gbpFtField.setText("0.0");
        gbpEurField.setText("0.0");
        unPayedInvoicesTableView.setDisable(true);
        payInButton.setDisable(true);
        payedInvoicesTableView.setDisable(true);


        Task<ObservableList<Invoice>> task = new Task<>() {
            @Override
            protected ObservableList<Invoice> call() {
                for (Invoice invoice : payment.getInvoices()) {
                    for (InvoicePayment invoicePayment: invoice.getInvoicePayments()) {
                        if(invoicePayment.getPayment_id() == payment.getPayment_id()){
                            invoice.setPayedAmount(invoicePayment.getPayedAmount());
                            invoice.setPayedOriginalAmount(invoicePayment.getPayedOriginalAmount());
                            payedInvoices.add(invoice);

                            // set the currency rates from previous payments
                            if(!payment.getCurrency().equals(invoice.getCurrency())){
                                if(payment.getCurrency().equals(FT_CURRENCY)){
                                    double currencyRate = Math.round(invoicePayment.getPayedOriginalAmount() / invoicePayment.getPayedAmount() * 100);
                                    if(invoice.getCurrency().equals(EUR_CURRENCY)) {
                                        eurFtField.setText(((Double) (currencyRate/100)).toString());
                                        eurFtField.setDisable(true);
                                    }
                                    if(invoice.getCurrency().equals(GBP_CURRENCY)) {
                                        gbpFtField.setText(((Double) (currencyRate/100)).toString());
                                        gbpFtField.setDisable(true);
                                    }
                                }
                                if(payment.getCurrency().equals(EUR_CURRENCY)){
                                    double currencyRate;
                                    if(invoice.getCurrency().equals(FT_CURRENCY)){
                                        currencyRate = Math.round(invoicePayment.getPayedAmount() / invoicePayment.getPayedOriginalAmount() * 100);
                                        eurFtField.setText(((Double) (currencyRate/100)).toString());
                                        eurFtField.setDisable(true);
                                    }
                                    if(invoice.getCurrency().equals(GBP_CURRENCY)){
                                        currencyRate = Math.round(invoicePayment.getPayedOriginalAmount() / invoicePayment.getPayedAmount() * 100);
                                        gbpEurField.setText(((Double) (currencyRate/100)).toString());
                                        gbpEurField.setDisable(true);
                                    }
                                }
                                if(payment.getCurrency().equals(GBP_CURRENCY)){
                                    double currencyRate = Math.round(invoicePayment.getPayedAmount() / invoicePayment.getPayedOriginalAmount() * 100);
                                    if(invoice.getCurrency().equals(FT_CURRENCY)) {
                                        gbpFtField.setText(((Double) (currencyRate/100)).toString());
                                        gbpFtField.setDisable(true);
                                    }
                                    if(invoice.getCurrency().equals(EUR_CURRENCY)) {
                                        gbpEurField.setText(((Double) (currencyRate/100)).toString());
                                        gbpEurField.setDisable(true);
                                    }
                                }
                            }

                        }
                    }
                }
                return payedInvoices;
            }
        };
        payedInvoicesTableView.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
        task.setOnSucceeded((event )-> fillInUnPayedInvoiceTableView());
    }

    @FXML
    private void holdToggleButtonPressed() {
        holdToggleButton.visibleProperty().setValue(false); //set invisible - avoid press again
        eurFtField.setDisable(true);
        gbpFtField.setDisable(true);
        gbpEurField.setDisable(true);
        unPayedInvoicesTableView.setDisable(false);
        payInButton.setDisable(false);
        payedInvoicesTableView.setDisable(false);

        //fill in the unPayedInvoiceTableView
        fillInUnPayedInvoiceTableView();
    }



    //fill in the unPayedInvoiceTableView
    private void fillInUnPayedInvoiceTableView(){
        Task<ObservableList<Invoice>> partnerSelectedTask = new Task<>() {
            @Override
            protected ObservableList<Invoice> call() {
                ObservableList<Invoice> calculatedInvoices = FXCollections.observableArrayList();
                invoices.stream().filter(invoice -> invoice.getBalance() > 0 &&
                        invoice.getPartner().getPartner_id() == payment.getPartner().getPartner_id())
                        .forEach(invoice -> {
                            double eurFt = Double.valueOf(eurFtField.getText());
                            double gbpFt = Double.valueOf(gbpFtField.getText());
                            double gbpEur = Double.valueOf(gbpEurField.getText());
                            double currentBalance;
                            if (invoice.getCurrency().equals(payment.getCurrency())) {
                                invoice.setCalculatedBalance(invoice.getBalance());
                                calculatedInvoices.add(invoice);
                            }
                            if (payment.getCurrency().equals(FT_CURRENCY) &&
                                    eurFt != 0.0 && invoice.getCurrency().equals(EUR_CURRENCY)) {
                                invoice.setCalculatedBalance(Math.round(invoice.getBalance() * eurFt)); //round Integer
                                calculatedInvoices.add(invoice);
                            }
                            if (payment.getCurrency().equals(FT_CURRENCY) &&
                                    gbpFt != 0.0 && invoice.getCurrency().equals(GBP_CURRENCY)) {
                                invoice.setCalculatedBalance(Math.round(invoice.getBalance() * gbpFt)); //round Integer
                                calculatedInvoices.add(invoice);
                            }
                            if (payment.getCurrency().equals(EUR_CURRENCY) &&
                                    eurFt != 0.0 && invoice.getCurrency().equals(FT_CURRENCY)) {
                                currentBalance = Math.round(invoice.getBalance() / eurFt * 100);
                                invoice.setCalculatedBalance(currentBalance/100);
                                calculatedInvoices.add(invoice);
                            }
                            if (payment.getCurrency().equals(EUR_CURRENCY) &&
                                    gbpEur != 0.0 && invoice.getCurrency().equals(GBP_CURRENCY)) {
                                invoice.setCalculatedBalance(invoice.getBalance() * gbpEur);
                                calculatedInvoices.add(invoice);
                            }
                            if (payment.getCurrency().equals(GBP_CURRENCY) &&
                                    gbpFt != 0.0 && invoice.getCurrency().equals(FT_CURRENCY)) {
                                currentBalance = Math.round(invoice.getBalance() / gbpFt * 100);
                                invoice.setCalculatedBalance(currentBalance / 100);
                                calculatedInvoices.add(invoice);
                            }
                            if (payment.getCurrency().equals(GBP_CURRENCY) &&
                                    gbpEur != 0.0 && invoice.getCurrency().equals(EUR_CURRENCY)) {
                                currentBalance = Math.round(invoice.getBalance() / gbpEur * 100);
                                invoice.setCalculatedBalance(currentBalance / 100);
                                calculatedInvoices.add(invoice);
                            }
                        });
                return calculatedInvoices;
            }
        };
        unPayedInvoicesTableView.itemsProperty().bind(partnerSelectedTask.valueProperty());
        new Thread(partnerSelectedTask).start();
    }

    private void currencyVisibilitySetter(String newValue) {
        if (newValue.equals(FT_CURRENCY)) {
            gbpEurField.visibleProperty().setValue(false);
            gbpEurLabel.visibleProperty().setValue(false);
            gbpFtField.visibleProperty().setValue(true);
            gbpFtLabel.visibleProperty().setValue(true);
            eurFtField.visibleProperty().setValue(true);
            eurFtLabel.visibleProperty().setValue(true);
        }
        if (newValue.equals(EUR_CURRENCY)) {
            gbpEurField.visibleProperty().setValue(true);
            gbpEurLabel.visibleProperty().setValue(true);
            gbpFtField.visibleProperty().setValue(false);
            gbpFtLabel.visibleProperty().setValue(false);
            eurFtField.visibleProperty().setValue(true);
            eurFtLabel.visibleProperty().setValue(true);
        }
        if (newValue.equals(GBP_CURRENCY)) {
            gbpEurField.visibleProperty().setValue(true);
            gbpEurLabel.visibleProperty().setValue(true);
            gbpFtField.visibleProperty().setValue(true);
            gbpFtLabel.visibleProperty().setValue(true);
            eurFtField.visibleProperty().setValue(false);
            eurFtLabel.visibleProperty().setValue(false);
        }
    }


    @FXML
    public void payInButtonPressed(){
        if (unPayedInvoicesTableView.getSelectionModel().getSelectedItem() != null) {
            Task<ObservableList<Invoice>> task = new Task<>() {
                @Override
                protected ObservableList<Invoice> call() {
                    Invoice payedInvoice = unPayedInvoicesTableView.getSelectionModel().getSelectedItem();
                    double exchangeRate, payedOriginalAmount, roundedResultHelper;
                    if (payedInvoice.getCalculatedBalance() <= paymentBalance) {
                        if (payedInvoice.getCurrency().equals(payment.getCurrency())) {
                            //setPayedAmount
                            payedInvoice.setPayedAmount(payedInvoice.getCalculatedBalance());
                        } else {
                            exchangeRate = payedInvoice.getBalance() / payedInvoice.getCalculatedBalance();
                            //setPayedAmount
                            roundedResultHelper = Math.round(payedInvoice.getCalculatedBalance() * exchangeRate * 100);
                            if (payedInvoice.getCurrency().equals(FT_CURRENCY)) // if Ft currency round Integer value
                                payedInvoice.setPayedAmount(Math.round(roundedResultHelper / 100));
                            else
                                payedInvoice.setPayedAmount(roundedResultHelper / 100);
                        }
                        paymentBalance = paymentBalance - payedInvoice.getCalculatedBalance();
                        payedOriginalAmount = payedInvoice.getCalculatedBalance();
                        payedInvoice.setBalance(0.0);
                        payedInvoice.setCalculatedBalance(0.0);
                    } else {
                        if (payedInvoice.getCurrency().equals(payment.getCurrency())) {
                            //setBalance
                            if (payedInvoice.getCurrency().equals(FT_CURRENCY)) // if FT_CURRENCY round Integer value
                                payedInvoice.setBalance(Math.round(payedInvoice.getCalculatedBalance() - paymentBalance));
                            else
                                payedInvoice.setBalance(payedInvoice.getCalculatedBalance() - paymentBalance);
                            //setPayedAmount
                            payedInvoice.setPayedAmount(paymentBalance);
                        } else {
                            exchangeRate = payedInvoice.getBalance() / payedInvoice.getCalculatedBalance();
                            //setBalance
                            roundedResultHelper = Math.round((payedInvoice.getCalculatedBalance() * exchangeRate -
                                    paymentBalance * exchangeRate) * 100);
                            if (payedInvoice.getCurrency().equals(FT_CURRENCY)) // if FT_CURRENCY round Integer value
                                payedInvoice.setBalance(Math.round(roundedResultHelper / 100));
                            else
                                payedInvoice.setBalance(roundedResultHelper / 100);
                            //setPayedAmount
                            roundedResultHelper = Math.round(paymentBalance * exchangeRate * 100);
                            if (payedInvoice.getCurrency().equals(FT_CURRENCY)) // if FT_CURRENCY round Integer value
                                payedInvoice.setPayedAmount(Math.round(roundedResultHelper / 100));
                            else
                                payedInvoice.setPayedAmount(roundedResultHelper / 100);
                        }
                        payedOriginalAmount = paymentBalance;
                        paymentBalance = 0.0;
                    }

                    roundedResultHelper = Math.round(payedOriginalAmount * 100);
                    payedInvoice.setPayedOriginalAmount(roundedResultHelper / 100);
                    payedInvoices.add(payedInvoice);
                    newPayedInvoices.add(payedInvoice);
                    return payedInvoices;
                }
            };
            payedInvoicesTableView.itemsProperty().bind(task.valueProperty());
            new Thread(task).start();
            //refresh the unPayedInvoiceTableView
            task.setOnSucceeded((event) -> {
                    paymentBalanceLabel.setText(PaymentsController.DOUBLE_FORMATTER.format(paymentBalance));
                    fillInUnPayedInvoiceTableView();
                    if(paymentBalance<=0)
                        payInButton.disableProperty().setValue(true);
            });
        }
    }

    @FXML
    Payment processEditPaymentResult(){
        SimpleIntegerProperty payment_id= new SimpleIntegerProperty();
        payment_id.set(payment.getPayment_id());
        Partner partner = payment.getPartner();
        SimpleDoubleProperty amount = new SimpleDoubleProperty();
        amount.set(payment.getAmount());
        SimpleDoubleProperty balance = new SimpleDoubleProperty();
        double roundHelper = Math.round(paymentBalance * 100);
        balance.set(roundHelper / 100);
        SimpleStringProperty currency = new SimpleStringProperty();
        currency.set(payment.getCurrency());
        LocalDate date = payment.getDate();
        SimpleStringProperty paymentMethod = new SimpleStringProperty();
        paymentMethod.set(payment.getPaymentMethod());
        SimpleStringProperty note = new SimpleStringProperty();
        note.set(noteArea.getText().trim());
        return new Payment(partner,newPayedInvoices,amount,balance,currency,date,paymentMethod,note,payment_id);
    }
}