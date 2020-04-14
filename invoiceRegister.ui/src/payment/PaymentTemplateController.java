package payment;

import dataModel.Invoice;
import dataModel.Partner;
import dataModel.Payment;
import db.DataSource;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;


import java.time.LocalDate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static dataModel.Invoice.EUR_CURRENCY;
import static dataModel.Invoice.FT_CURRENCY;
import static dataModel.Invoice.GBP_CURRENCY;
import static payment.PaymentsController.DOUBLE_FORMATTER;
import static payment.PaymentsController.INVOICE_DOUBLE_CELL_FORMATTER;


public class PaymentTemplateController {

    @FXML
    DialogPane paymentDialogPane;
    @FXML
    ComboBox<Partner> partnerComboBox;
    @FXML
    TextField paymentMethodField;
    @FXML
    TextField amountField;
    @FXML
    ComboBox<String> currencyComboBox;
    @FXML
    DatePicker dateDatePicker;
    @FXML
    TextArea noteArea;
    @FXML
    TextField eurFtField;
    @FXML
    Label eurFtLabel;
    @FXML
    TextField gbpFtField;
    @FXML
    Label gbpFtLabel;
    @FXML
    TextField gbpEurField;
    @FXML
    Label gbpEurLabel;
    @FXML
    Label paymentBalanceLabel;
    @FXML
    Label paymentBalanceCurrencyLabel;
    @FXML
    TableView<Invoice> unPayedInvoicesTableView;
    @FXML
    Button payInButton;
    @FXML
    TableView<Invoice> payedInvoicesTableView;
    @FXML
    ToggleButton holdToggleButton;
    @FXML
    TableColumn<Invoice, Number> grossAmountColumn;
    @FXML
    TableColumn<Invoice, Number> balanceColumn;
    @FXML
    TableColumn<Invoice, Number> balanceInCurrencyColumn;
    @FXML
    TableColumn<Invoice, Number> payedAmountColumn;
    @FXML
    TableColumn<Invoice, Number> payedBalanceColumn;

    private ObservableList<Invoice> invoices;
    private ObservableList<Invoice> payedInvoices = FXCollections.observableArrayList();
    private Double paymentBalance;

    // public getter in order to reach from the Controller
    public ToggleButton getHoldToggleButton() {
        return holdToggleButton;
    }


    public void initialize() {
        grossAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        balanceColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        balanceInCurrencyColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        payedBalanceColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        payedAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);

        // Force (amountField, eurFtField, gbpFtField, gbpEurField) TextFields to double
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

        TextFormatter<Double> amountFieldTextFormatter = new TextFormatter<>(converter, 0.0, filter);
        amountField.setTextFormatter(amountFieldTextFormatter);

        TextFormatter<Double> eurFieldTextFormatter = new TextFormatter<>(converter, 0.0, filter);
        eurFtField.setTextFormatter(eurFieldTextFormatter);
        TextFormatter<Double> gbpFieldTextFormatter = new TextFormatter<>(converter, 0.0, filter);
        gbpFtField.setTextFormatter(gbpFieldTextFormatter);
        TextFormatter<Double> gbpEurFieldTextFormatter = new TextFormatter<>(converter, 0.0, filter);
        gbpEurField.setTextFormatter(gbpEurFieldTextFormatter);

        // get data from database
        ObservableList<Partner> partners = DataSource.getInstance().getPartners();
        invoices = DataSource.getInstance().getInvoices();

        unPayedInvoicesTableView.disableProperty().setValue(true);
        payInButton.disableProperty().setValue(true);
        payedInvoicesTableView.disableProperty().setValue(true);

        currencyComboBox.setItems(FXCollections.observableArrayList(FT_CURRENCY, EUR_CURRENCY, GBP_CURRENCY));
        currencyComboBox.setValue(FT_CURRENCY);
        currencyComboBox.valueProperty().addListener(((observable, oldValue, newValue) -> currencyVisibilitySetter(newValue)));

        dateDatePicker.setValue(LocalDate.now());

        //insert partners into the ComboBox
        if (!partners.isEmpty()) {
            partnerComboBox.setItems(partners);
            Partner partner = partners.get(0);
            partnerComboBox.setValue(partner);
            currencyComboBox.setValue(partner.getDefaultCurrency());
            currencyVisibilitySetter(currencyComboBox.getSelectionModel().getSelectedItem());
        }
        partnerComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null)
                currencyComboBox.setValue(newValue.getDefaultCurrency());
        }));

        //Binding - check invalid fields
        holdToggleButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> amountField.getText().isEmpty() ||
                        Double.valueOf(amountField.getText()) == 0.0 ||
                        partnerComboBox.getSelectionModel().isEmpty(),
                amountField.textProperty(), partnerComboBox.itemsProperty()
        ));
    }

    @FXML
    private void holdToggleButtonPressed() {
        holdToggleButton.visibleProperty().setValue(false); //set invisible - avoid press again
        partnerComboBox.disableProperty().setValue(true);
        paymentMethodField.disableProperty().setValue(true);
        amountField.disableProperty().setValue(true);
        currencyComboBox.disableProperty().setValue(true);
        dateDatePicker.disableProperty().setValue(true);
        eurFtField.disableProperty().setValue(true);
        gbpFtField.disableProperty().setValue(true);
        gbpEurField.disableProperty().setValue(true);
        unPayedInvoicesTableView.disableProperty().setValue(false);
        payInButton.disableProperty().setValue(false);
        payedInvoicesTableView.disableProperty().setValue(false);

        paymentBalance = Double.valueOf(amountField.getText());
        paymentBalanceLabel.setText(DOUBLE_FORMATTER.format(paymentBalance));
        paymentBalanceCurrencyLabel.setText(currencyComboBox.getSelectionModel().getSelectedItem().equals(FT_CURRENCY) ? " " + FT_CURRENCY :
                currencyComboBox.getSelectionModel().getSelectedItem().equals(EUR_CURRENCY) ? " " + EUR_CURRENCY :
                        " " + GBP_CURRENCY);
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
                invoice.getPartner().getPartner_id() == partnerComboBox.getSelectionModel().getSelectedItem().getPartner_id())
                .forEach(invoice -> {
                    double eurFt = Double.valueOf(eurFtField.getText());
                    double gbpFt = Double.valueOf(gbpFtField.getText());
                    double gbpEur = Double.valueOf(gbpEurField.getText());
                    double currentBalance;
                    if (invoice.getCurrency().equals(currencyComboBox.getSelectionModel().getSelectedItem())) {
                        invoice.setCalculatedBalance(invoice.getBalance());
                        calculatedInvoices.add(invoice);
                    }
                    if (currencyComboBox.getSelectionModel().getSelectedItem().equals(FT_CURRENCY) &&
                            eurFt != 0.0 && invoice.getCurrency().equals(EUR_CURRENCY)) {
                        invoice.setCalculatedBalance(Math.round(invoice.getBalance() * eurFt)); //round Integer
                        calculatedInvoices.add(invoice);
                    }
                    if (currencyComboBox.getSelectionModel().getSelectedItem().equals(FT_CURRENCY) &&
                            gbpFt != 0.0 && invoice.getCurrency().equals(GBP_CURRENCY)) {
                        invoice.setCalculatedBalance(Math.round(invoice.getBalance() * gbpFt)); //round Integer
                        calculatedInvoices.add(invoice);
                    }
                    if (currencyComboBox.getSelectionModel().getSelectedItem().equals(EUR_CURRENCY) &&
                            eurFt != 0.0 && invoice.getCurrency().equals(FT_CURRENCY)) {
                        currentBalance = Math.round(invoice.getBalance() / eurFt * 100);
                        invoice.setCalculatedBalance(currentBalance/100);

                        calculatedInvoices.add(invoice);
                    }
                    if (currencyComboBox.getSelectionModel().getSelectedItem().equals(EUR_CURRENCY) &&
                            gbpEur != 0.0 && invoice.getCurrency().equals(GBP_CURRENCY)) {
                        invoice.setCalculatedBalance(invoice.getBalance() * gbpEur);
                        calculatedInvoices.add(invoice);
                    }
                    if (currencyComboBox.getSelectionModel().getSelectedItem().equals(GBP_CURRENCY) &&
                            gbpFt != 0.0 && invoice.getCurrency().equals(FT_CURRENCY)) {
                        currentBalance = Math.round(invoice.getBalance() / gbpFt * 100);
                        invoice.setCalculatedBalance(currentBalance / 100);
                        calculatedInvoices.add(invoice);
                    }
                    if (currencyComboBox.getSelectionModel().getSelectedItem().equals(GBP_CURRENCY) &&
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
    private void payInButtonPressed() {
        if (unPayedInvoicesTableView.getSelectionModel().getSelectedItem() != null) {
            Task<ObservableList<Invoice>> task = new Task<>() {
                @Override
                protected ObservableList<Invoice> call() {
                    Invoice payedInvoice = unPayedInvoicesTableView.getSelectionModel().getSelectedItem();
                    double exchangeRate, payedOriginalAmount, roundedResultHelper;
                    if (payedInvoice.getCalculatedBalance() <= paymentBalance) {
                        if (payedInvoice.getCurrency().equals(currencyComboBox.getSelectionModel().getSelectedItem())) {
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
                        if (payedInvoice.getCurrency().equals(currencyComboBox.getSelectionModel().getSelectedItem())) {
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
                    return payedInvoices;
                }
            };
            payedInvoicesTableView.itemsProperty().bind(task.valueProperty());
            new Thread(task).start();
            //refresh the unPayedInvoiceTableView
            task.setOnSucceeded((event) -> {
                    paymentBalanceLabel.setText(DOUBLE_FORMATTER.format(paymentBalance));
                    fillInUnPayedInvoiceTableView();
                    if(paymentBalance<=0)
                        payInButton.disableProperty().setValue(true);
            });
        }
    }

    @FXML
    public Payment processPaymentResult(){
        Partner partner = partnerComboBox.getSelectionModel().getSelectedItem();
        SimpleDoubleProperty amount = new SimpleDoubleProperty();
        amount.set(Double.valueOf(amountField.getText().trim()));
        SimpleDoubleProperty balance = new SimpleDoubleProperty();
        double roundHelper = Math.round(paymentBalance * 100);
        balance.set(roundHelper / 100);
        SimpleStringProperty currency = new SimpleStringProperty();
        currency.set(currencyComboBox.getSelectionModel().getSelectedItem());
        LocalDate date = dateDatePicker.getValue();
        SimpleStringProperty paymentMethod = new SimpleStringProperty();
        paymentMethod.set(paymentMethodField.getText().trim());
        SimpleStringProperty note = new SimpleStringProperty();
        note.set(noteArea.getText().trim());
        return new Payment(partner,payedInvoices,amount,balance,currency,date,paymentMethod,note);
    }
}