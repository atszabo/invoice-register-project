module invoiceRegister.ui {
    requires javafx.fxml;
    requires javafx.controls;
    requires invoiceRegister.database;

    exports main to javafx.graphics, javafx.fxml;
    exports partner to javafx.graphics,javafx.fxml;
    exports address to javafx.graphics,javafx.fxml;
    exports contact to javafx.graphics,javafx.fxml;
    exports invoice to javafx.graphics,javafx.fxml;
    exports payment to javafx.graphics,javafx.fxml;

    opens main to javafx.fxml;
    opens partner to javafx.fxml;
    opens address to javafx.fxml;
    opens contact to javafx.fxml;
    opens invoice to javafx.fxml;
    opens payment to javafx.fxml;

}