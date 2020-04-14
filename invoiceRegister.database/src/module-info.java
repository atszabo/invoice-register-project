module invoiceRegister.database {
    requires javafx.base;
    requires transitive java.sql;
    requires transitive invoiceRegister.common;

    exports db;
}