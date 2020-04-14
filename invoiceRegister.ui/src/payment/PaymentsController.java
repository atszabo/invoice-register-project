package payment;

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
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class PaymentsController {
    @FXML
    DialogPane paymentsDialogPane;
    @FXML
    TableView<Payment> pedantPaymentTableView;
    @FXML
    TableView<Payment> settledPaymentTableView;
    @FXML
    Button pedantLookButton;
    @FXML
    Button settledLookButton;
    @FXML
    Button editLookButton;
    @FXML
    Button deleteSettledLookButton;
    @FXML
    Button deletePedantLookButton;
    @FXML
    ComboBox<Partner> partnerComboBox;
    @FXML
    TableColumn<Invoice, Number> pedantAmountColumn;
    @FXML
    TableColumn<Invoice, Number> pedantBalanceColumn;
    @FXML
    TableColumn<Invoice, Number> settledAmountColumn;
    @FXML
    TableColumn<Invoice, Number> settledBalanceColumn;


    private static Payment payment;
    private static Payment pedantPayment;
    private static Payment settledPayment;

    private ObservableList<Payment> payments;

    //double format
    static final DecimalFormat DOUBLE_FORMATTER = new DecimalFormat("#,##0.00");

    static final Callback<TableColumn<Invoice,Number>,TableCell<Invoice,Number>> INVOICE_DOUBLE_CELL_FORMATTER
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



    //getters, reach from other controllers
    public Payment getPayment() {
        return payment;
    }

    public void initialize(){
        pedantAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        pedantBalanceColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        settledAmountColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);
        settledBalanceColumn.setCellFactory(INVOICE_DOUBLE_CELL_FORMATTER);

        pedantLookButton.setDisable(true);
        settledLookButton.setDisable(true);
        editLookButton.setDisable(true);
        deletePedantLookButton.setDisable(true);
        deleteSettledLookButton.setDisable(true);

        pedantPaymentTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null){
                pedantLookButton.setDisable(false);
                editLookButton.setDisable(false);
                deletePedantLookButton.setDisable(false);
                pedantPayment = newValue;
            }

        }));
        settledPaymentTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                settledLookButton.setDisable(false);
                deleteSettledLookButton.setDisable(false);
                settledPayment = newValue;
            }
        }));

        // get data from database
        payments = DataSource.getInstance().getPayments();
        ObservableList<Partner> partners = DataSource.getInstance().getPartners();
        partnerComboBox.setItems(partners);

        fillInTableViews();
        partnerComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            fillInTableViews();
            pedantLookButton.setDisable(true);
            settledLookButton.setDisable(true);
            editLookButton.setDisable(true);
        });
    }

    private void fillInTableViews(){
        Task<ObservableList<Payment>> pedantTask = new Task<>() {
            @Override
            protected ObservableList<Payment> call() {
                if (partnerComboBox.getSelectionModel().getSelectedItem() != null)
                    return FXCollections.observableArrayList(payments.stream()
                            .filter(payment -> payment.getBalance() > 0.0)
                            .filter(payment -> payment.getPartner().getPartner_id() == partnerComboBox.getSelectionModel().getSelectedItem().getPartner_id())
                            .collect(Collectors.toList()));
                return FXCollections.observableArrayList(payments.stream()
                        .filter(payment -> payment.getBalance() > 0.0).collect(Collectors.toList()));
            }
        };

        Task<ObservableList<Payment>> settledTask = new Task<>() {
            @Override
            protected ObservableList<Payment> call() {
                if (partnerComboBox.getSelectionModel().getSelectedItem() != null){
                    return FXCollections.observableArrayList(payments.stream()
                            .filter(payment -> payment.getBalance() == 0.0)
                            .filter(payment -> payment.getPartner().getPartner_id() == partnerComboBox.getSelectionModel().getSelectedItem().getPartner_id())
                            .collect(Collectors.toList()));
                }
                return FXCollections.observableArrayList(payments.stream()
                        .filter(payment -> payment.getBalance() == 0).collect(Collectors.toList()));
            }
        };

        ExecutorService service = null;
        try {
            service = Executors.newSingleThreadExecutor();
            service.submit(pedantTask);
            service.submit(settledTask);
            pedantPaymentTableView.itemsProperty().bind(pedantTask.valueProperty());
            settledPaymentTableView.itemsProperty().bind(settledTask.valueProperty());
        }finally {
            if(service != null) service.shutdown();
        }
    }

    @FXML
    public void showEditPayment(){
        payment = pedantPaymentTableView.getSelectionModel().getSelectedItem();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000,600);
        dialog.initOwner(paymentsDialogPane.getScene().getWindow());
        dialog.setTitle("Befizetés további szerkesztése");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/payment/editPaymentTemplate.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the editPaymentTemplate.fxml" + e.getMessage());
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(megse);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            EditPaymentTemplateController controller = fxmlLoader.getController();
            Payment newPayment = controller.processEditPaymentResult();
            // update teh invoice , invoicePaymentConnection, payments in database
            DataSource.getInstance().updatePaymentCommit(newPayment);
            //re-initialize
            initialize();
        }
    }

    @FXML
    public void showPedantPaymentDetails() { fillInPaymentDetailsTemplate(pedantPaymentTableView); }

    @FXML
    public void showSettledPaymentDetails() {
        fillInPaymentDetailsTemplate(settledPaymentTableView);
    }

    private void fillInPaymentDetailsTemplate(TableView<Payment> tableView) {
        payment = tableView.getSelectionModel().getSelectedItem();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setMaxSize(1000,600);
        dialog.initOwner(paymentsDialogPane.getScene().getWindow());
        dialog.setTitle("Befizetés részeletezése");
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/payment/paymentDetails.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the paymentDetails.fxml" + e.getMessage());
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }


    @FXML
    public void deleteSettledPayment() { deletePayment(settledPayment);
    }
    @FXML
    public void deletePedantPayment() {
        deletePayment(pedantPayment);
    }

    @FXML
    private void deletePayment(Payment payment) {
        ButtonType megse = new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.OK,megse);
        alert.setTitle("Törlés megerősítése!");
        alert.setHeaderText("Biztosan töröli szeretné?");
        alert.setContentText("Befizetés:   " + payment.getDate().toString() + "   " + payment.getAmount()
                                            + " " + payment.getCurrency() + "\n\n");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            // delete invoicePaymentConnection, payments, update the invoice in database
            DataSource.getInstance().deletePaymentCommit(payment);
            //re-initialize
            initialize();
        }
    }
}
