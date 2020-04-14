package db;

import dataModel.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static dataModel.Address.HEADOFFICEADDRESS;



public class DataSource {
    // database path
    private static final String DB_NAME = "InvoiceRegister.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;
    // database tables structures
    //table addresses structure
    private static final String TABLE_ADDRESSES = "addresses";
    private static final String COLUMN_ADDRESS_ID = "_id";
    private static final String COLUMN_ADDRESS_COUNTRY = "country";
    private static final String COLUMN_ADDRESS_POSTCODE = "postcode";
    private static final String COLUMN_ADDRESS_CITY = "city";
    private static final String COLUMN_ADDRESS_STREET = "street";
    private static final String COLUMN_ADDRESS_HOUSENR = "houseNr";
    private static final String COLUMN_ADDRESS_MAILBOX = "mailbox";
    private static final String COLUMN_ADDRESS_ADDRESSTYPE = "addressType";

    //table partnerAddressConnection structure
    private static final String TABLE_PARTNERADDRESSES = "partnerAddressConnection";
    private static final String COLUMN_PARTNERADDRESS_PARTNER_ID = "partner_id";
    private static final String COLUMN_PARTNERADDRESS_ADDRESS_ID = "address_id";

    //table contacts structure
    private static final String TABLE_CONTACTS = "contacts";
    private static final String COLUMN_CONTACT_ID = "_id";
    private static final String COLUMN_CONTACT_NAME = "name";
    private static final String COLUMN_CONTACT_PHONENR = "phoneNr";
    private static final String COLUMN_CONTACT_FAXNR = "faxNr";
    private static final String COLUMN_CONTACT_EMAIL = "email";
    private static final String COLUMN_CONTACT_NOTE = "note";

    //table partnerContactConnection structure
    private static final String TABLE_PARTNERCONTACTS = "partnerContactConnection";
    private static final String COLUMN_PARTNERCONTACT_PARTNER_ID = "partner_id";
    private static final String COLUMN_PARTNERCONTACT_CONTACT_ID = "contact_id";

    //table partner structure
    private static final String TABLE_PARTNERS = "partners";
    private static final String COLUMN_PARTNER_ID = "_id";
    private static final String COLUMN_PARTNER_NAME = "name";
    private static final String COLUMN_PARTNER_SHORTNAME = "shortName";
    private static final String COLUMN_PARTNER_VATNR = "vatNr";
    private static final String COLUMN_PARTNER_EUVATNR = "euVatNr";
    private static final String COLUMN_PARTNER_DEFAULTVAT = "defaultVat";
    private static final String COLUMN_PARTNER_DEFAULTCURRENCY = "defaultCurrency";
    private static final String COLUMN_PARTNER_PAYMENTMETHOD = "paymentMethod";
    private static final String COLUMN_PARTNER_PAYMENTDELAY = "paymentDelay";
    private static final String COLUMN_PARTNER_LIMITVALUE = "limitValue";
    private static final String COLUMN_PARTNER_DEFAULTPHONE = "defaultPhone";
    private static final String COLUMN_PARTNER_DEFAULTFAX = "defaultFax";
    private static final String COLUMN_PARTNER_DEFAULTEMAIL = "defaultEmail";
    private static final String COLUMN_PARTNER_NOTE = "note";


    //table invoice structure
    private static final String TABLE_INVOICES = "invoices";
    private static final String COLUMN_INVOICE_ID = "_id";
    private static final String COLUMN_INVOICE_PARTNER_ID = "partner_id";
    private static final String COLUMN_INVOICE_CONTACT_ID = "contact_id";
    private static final String COLUMN_INVOICE_INVOICENR = "invoiceNr";
    private static final String COLUMN_INVOICE_REGNR = "regNr";
    private static final String COLUMN_INVOICE_NETAMOUNT = "netAmount";
    private static final String COLUMN_INVOICE_VAT = "vat";
    private static final String COLUMN_INVOICE_CURRENCY = "currency";
    private static final String COLUMN_INVOICE_BALANCE = "balance";
    private static final String COLUMN_INVOICE_PAYMENTMETHOD = "paymentMethod";
    private static final String COLUMN_INVOICE_DATEOFINVOICE = "dateOfInvoice";
    private static final String COLUMN_INVOICE_DATEOFPERFORMANCE = "dateOfPerformance";
    private static final String COLUMN_INVOICE_DATEOFDUE = "dateOfDue";
    private static final String COLUMN_INVOICE_NOTE = "note";

    //table payment structure
    private static final String TABLE_PAYMENTS = "payments";
    private static final String COLUMN_PAYMENT_ID = "_id";
    private static final String COLUMN_PAYMENT_PARTNER_ID = "partner_id";
    private static final String COLUMN_PAYMENT_AMOUNT = "amount";
    private static final String COLUMN_PAYMENT_BALANCE = "balance";
    private static final String COLUMN_PAYMENT_CURRENCY = "currency";
    private static final String COLUMN_PAYMENT_DATE = "date";
    private static final String COLUMN_PAYMENT_PAYMENTMETHOD = "paymentMethod";
    private static final String COLUMN_PAYMENT_NOTE = "note";

    //table invoicePaymentConnection structure
    private static final String TABLE_INVOICEPAYMENT = "invoicePaymentConnection";
    private static final String COLUMN_INVOICEPAYMENT_ID = "_id";
    private static final String COLUMN_INVOICEPAYMENT_INVOICE_ID = "invoice_id";
    private static final String COLUMN_INVOICEPAYMENT_PAYMENT_ID = "payment_id";
    private static final String COLUMN_INVOICEPAYMENT_PAYEDAMOUNT = "payedAmount";
    private static final String COLUMN_INVOICEPAYMENT_PAYEDORIGINALAMOUNT ="payedOriginalAmount";

    //inserts
    private static final String INSERT_CONTACTS = "INSERT INTO " + TABLE_CONTACTS +
            '(' + COLUMN_CONTACT_NAME + ", " + COLUMN_CONTACT_PHONENR + ", " + COLUMN_CONTACT_FAXNR + ", "
            + COLUMN_CONTACT_EMAIL + ", " + COLUMN_CONTACT_NOTE + ") VALUES(?, ?, ?, ?, ?)";

    private static final String INSERT_PARTNERCONTACTS = "INSERT INTO " + TABLE_PARTNERCONTACTS +
            '(' + COLUMN_PARTNERCONTACT_PARTNER_ID + ", " + COLUMN_PARTNERCONTACT_CONTACT_ID + ") VALUES(?, ?)";

    private static final String INSERT_PARTNERS = "INSERT INTO " + TABLE_PARTNERS +
            '(' + COLUMN_PARTNER_NAME + ", " + COLUMN_PARTNER_SHORTNAME +
            ", " + COLUMN_PARTNER_VATNR + ", " + COLUMN_PARTNER_EUVATNR + ", " + COLUMN_PARTNER_DEFAULTVAT +
            ", " + COLUMN_PARTNER_DEFAULTCURRENCY + ", " + COLUMN_PARTNER_PAYMENTMETHOD + ", " +
            COLUMN_PARTNER_PAYMENTDELAY + ", " + COLUMN_PARTNER_LIMITVALUE + ", " + COLUMN_PARTNER_DEFAULTPHONE +
            ", " + COLUMN_PARTNER_DEFAULTFAX + ", " + COLUMN_PARTNER_DEFAULTEMAIL + ", " + COLUMN_PARTNER_NOTE +
            ") VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";

    private static final String INSERT_ADDRESSES = "INSERT INTO " + TABLE_ADDRESSES +
            '(' + COLUMN_ADDRESS_COUNTRY + ", " + COLUMN_ADDRESS_POSTCODE + ", " + COLUMN_ADDRESS_CITY + ", "
            + COLUMN_ADDRESS_STREET + ", " + COLUMN_ADDRESS_HOUSENR + ", " + COLUMN_ADDRESS_MAILBOX + ", "
            + COLUMN_ADDRESS_ADDRESSTYPE + ") VALUES(?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_PARTNERADDRESSES = "INSERT INTO " + TABLE_PARTNERADDRESSES +
            '(' + COLUMN_PARTNERADDRESS_PARTNER_ID + ", " + COLUMN_PARTNERADDRESS_ADDRESS_ID + ") VALUES(?, ?)";

    private static final String INSERT_INVOICES = "INSERT INTO " + TABLE_INVOICES + '('
            + COLUMN_INVOICE_PARTNER_ID + ", " + COLUMN_INVOICE_CONTACT_ID + ", " + COLUMN_INVOICE_INVOICENR + ", "
            + COLUMN_INVOICE_REGNR + ", " + COLUMN_INVOICE_NETAMOUNT + ", " + COLUMN_INVOICE_VAT + ", " + COLUMN_INVOICE_CURRENCY + ", "
            + COLUMN_INVOICE_BALANCE + ", " + COLUMN_INVOICE_PAYMENTMETHOD + ", " + COLUMN_INVOICE_DATEOFINVOICE + ", "
            + COLUMN_INVOICE_DATEOFPERFORMANCE + ", " + COLUMN_INVOICE_DATEOFDUE + ", "
            + COLUMN_INVOICE_NOTE + " ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String INSERT_PAYMENTS = "INSERT INTO " + TABLE_PAYMENTS + '('
            + COLUMN_PAYMENT_PARTNER_ID + ", " + COLUMN_PAYMENT_AMOUNT + ", " + COLUMN_PAYMENT_BALANCE + ", "
            + COLUMN_PAYMENT_CURRENCY + ", " + COLUMN_PAYMENT_DATE + ", " + COLUMN_PAYMENT_PAYMENTMETHOD + ", "
            + COLUMN_PAYMENT_NOTE + " ) VALUES (?,?,?,?,?,?,?)";

    private static final String INSERT_INVOICEPAYMENTS = "INSERT INTO " + TABLE_INVOICEPAYMENT +
            '(' + COLUMN_INVOICEPAYMENT_INVOICE_ID + ", " + COLUMN_INVOICEPAYMENT_PAYMENT_ID + ", " +
            COLUMN_INVOICEPAYMENT_PAYEDAMOUNT + ", " + COLUMN_INVOICEPAYMENT_PAYEDORIGINALAMOUNT +") VALUES(?,?,?,?)";

    //update tables
    private static final String UPDATE_INVOICE_PAYED = "UPDATE " + TABLE_INVOICES + " SET " + COLUMN_INVOICE_BALANCE + "= ? WHERE "
                                                        + COLUMN_INVOICE_ID + "= ?";

    private static final String UPDATE_PAYMENTS = "UPDATE " + TABLE_PAYMENTS + " SET " + COLUMN_PAYMENT_BALANCE + "= ?, " +
            COLUMN_PAYMENT_NOTE + "= ? WHERE " + COLUMN_PAYMENT_ID + "= ?";

    private static final String UPDATE_PAYMENTS_BY_INVOICE = "UPDATE " + TABLE_PAYMENTS + " SET " + COLUMN_PAYMENT_BALANCE + "= ?" +
            " WHERE " + COLUMN_PAYMENT_ID + "= ?";

    private static final String UPDATE_PARTNERS = "UPDATE " + TABLE_PARTNERS + " SET " + COLUMN_PARTNER_NAME + "= ?, " +
            COLUMN_PARTNER_SHORTNAME + "= ?, " + COLUMN_PARTNER_VATNR + "= ?, " + COLUMN_PARTNER_EUVATNR + "= ?, " +
            COLUMN_PARTNER_DEFAULTVAT + "= ?, " + COLUMN_PARTNER_DEFAULTCURRENCY + "= ?, " + COLUMN_PARTNER_PAYMENTMETHOD + "= ?, " +
            COLUMN_PARTNER_PAYMENTDELAY + "= ?, " + COLUMN_PARTNER_LIMITVALUE + "= ?, " + COLUMN_PARTNER_DEFAULTPHONE + "= ?, " +
            COLUMN_PARTNER_DEFAULTFAX + "= ?, " + COLUMN_PARTNER_DEFAULTEMAIL + "= ?, " + COLUMN_PARTNER_NOTE + "= ? " +
            " WHERE " + COLUMN_PARTNER_ID + "= ?";

    private static final String UPDATE_ADDRESSES = "UPDATE " + TABLE_ADDRESSES + " SET " + COLUMN_ADDRESS_COUNTRY + "=?, " +
            COLUMN_ADDRESS_POSTCODE + "=?, " + COLUMN_ADDRESS_CITY + "=?, " + COLUMN_ADDRESS_STREET + "=?, " +
            COLUMN_ADDRESS_HOUSENR + "=?, " + COLUMN_ADDRESS_MAILBOX + "=? WHERE " + COLUMN_ADDRESS_ID + "=? ";

    private static final String UPDATE_CONTACT = "UPDATE " + TABLE_CONTACTS + " SET " + COLUMN_CONTACT_NAME + "=?, " +
            COLUMN_CONTACT_PHONENR + "=?, " + COLUMN_CONTACT_FAXNR + "=?, "  + COLUMN_CONTACT_EMAIL + "=?, " +
            COLUMN_CONTACT_NOTE + "=? WHERE " + COLUMN_CONTACT_ID + "=? ";

    //delete payment
    private static final String DELETE_PAYMENTS = "DELETE FROM " + TABLE_PAYMENTS + " WHERE " + COLUMN_PAYMENT_ID + " = ?";

    private static final String DELETE_PAYMENTS_BY_PARTNERID = "DELETE FROM " + TABLE_PAYMENTS + " WHERE " + COLUMN_PAYMENT_PARTNER_ID + " = ?";

    private static final String DELETE_INVOICES = "DELETE FROM " + TABLE_INVOICES + " WHERE " + COLUMN_INVOICE_ID + " = ?";

    private static final String DELETE_INVOICES_BY_PARTNERID = "DELETE FROM " + TABLE_INVOICES + " WHERE " + COLUMN_INVOICE_PARTNER_ID + " = ?";

    private static final String DELETE_INVOICEPAYMENTS ="DELETE FROM " + TABLE_INVOICEPAYMENT + " WHERE " + COLUMN_INVOICEPAYMENT_ID +
            " = ?";

    private static final String DELETE_INVOICEPAYMENTS_BY_PAYMENTID ="DELETE FROM " + TABLE_INVOICEPAYMENT + " WHERE " + COLUMN_INVOICEPAYMENT_PAYMENT_ID +
            " = ?";

    private static final String DELETE_PARTNERADDRESS_BY_PARTNERID = "DELETE FROM " + TABLE_PARTNERADDRESSES + " WHERE " +
            COLUMN_PARTNERADDRESS_PARTNER_ID + " = ?";

    private static final String DELETE_PARTNERADDRESS_BY_ADDRESSID = "DELETE FROM " + TABLE_PARTNERADDRESSES + " WHERE " +
    COLUMN_PARTNERADDRESS_ADDRESS_ID + " = ?";

    private static final String DELETE_PARTNERCONTACT_BY_PARTNERID = "DELETE FROM " + TABLE_PARTNERCONTACTS + " WHERE " +
            COLUMN_PARTNERCONTACT_PARTNER_ID + " = ?";

    private static final String DELETE_PARTNERCONTACT_BY_CONTACTID = "DELETE FROM " + TABLE_PARTNERCONTACTS + " WHERE " +
            COLUMN_PARTNERCONTACT_CONTACT_ID + " = ?";

    private static final String DELETE_PARTNER = "DELETE FROM " + TABLE_PARTNERS + " WHERE " +
            COLUMN_PARTNER_ID + " = ?";

    private static final String DELETE_ADDRESS = "DELETE FROM " + TABLE_ADDRESSES + " WHERE " +
            COLUMN_ADDRESS_ID + " = ?";

    private static final String DELETE_CONTACT = "DELETE FROM " + TABLE_CONTACTS + " WHERE " +
            COLUMN_CONTACT_ID + " = ?";

    //query tables
    private static final String QUERY_PARTNERS = "SELECT * FROM " + TABLE_PARTNERS;

    private static final String QUERY_CONTACTS = "SELECT * FROM " +TABLE_CONTACTS;

    private static final String QUERY_ADDRESSES = "SELECT * FROM " +TABLE_ADDRESSES;

    private static final String QUERY_INVOICES = "SELECT * FROM " +TABLE_INVOICES;

    private static final String QUERY_PAYMENTS = "SELECT * FROM " +TABLE_PAYMENTS;

    private static final String QUERY_ADDRESSIDS_BY_PARTNERID = "SELECT " + COLUMN_PARTNERADDRESS_ADDRESS_ID +
            " FROM " + TABLE_PARTNERADDRESSES + " WHERE " + COLUMN_PARTNERADDRESS_PARTNER_ID + "=";

    private static final String QUERY_PARTNERIDS_BY_ADDRESSIDS = "SELECT " + COLUMN_PARTNERADDRESS_PARTNER_ID +
            " FROM " + TABLE_PARTNERADDRESSES + " WHERE " + COLUMN_PARTNERADDRESS_ADDRESS_ID + "=";

    private static final String QUERY_CONTACTIDS_BY_PARTNERID = "SELECT " + COLUMN_PARTNERCONTACT_CONTACT_ID +
            " FROM " + TABLE_PARTNERCONTACTS + " WHERE " + COLUMN_PARTNERCONTACT_PARTNER_ID + "=";

    private static final String QUERY_PARTNERIDS_BY_CONTACTID = "SELECT " + COLUMN_PARTNERCONTACT_PARTNER_ID +
            " FROM " + TABLE_PARTNERCONTACTS + " WHERE " + COLUMN_PARTNERCONTACT_CONTACT_ID + "=";

    private static final String QUERY_DATA_BY_PAYMENTID = "SELECT * FROM " + TABLE_INVOICEPAYMENT +
            " WHERE " + COLUMN_INVOICEPAYMENT_PAYMENT_ID + "=";

    private static final String QUERY_DATA_BY_INVOICEID = "SELECT * FROM " + TABLE_INVOICEPAYMENT +
            " WHERE " + COLUMN_INVOICEPAYMENT_INVOICE_ID + "=";



    // connection declaration
    private Connection conn;


    // statements declarations
    private PreparedStatement insertIntoContacts;
    private PreparedStatement insertIntoPartnerContacts;
    private PreparedStatement insertIntoPartners;
    private PreparedStatement insertIntoAddresses;
    private PreparedStatement insertIntoPartnerAddresses;
    private PreparedStatement insertIntoInvoices;
    private PreparedStatement insertIntoPayments;
    private PreparedStatement insertIntoInvoicePayment;

    private PreparedStatement updateInvoicePayedStatement;
    private PreparedStatement updatePayments;
    private PreparedStatement updatePaymentByInvoices;
    private PreparedStatement updatePartners;
    private PreparedStatement updateAddresses;
    private PreparedStatement updateContact;

    private PreparedStatement deletePayments;
    private PreparedStatement deleteInvoices;
    private PreparedStatement deleteInvoicePayments;
    private PreparedStatement deletePartnerAddressByPartnerId;
    private PreparedStatement deletePartnerAddressByAddressId;
    private PreparedStatement deletePartnerContactByPartnerId;
    private PreparedStatement deletePartnerContactByContactId;
    private PreparedStatement deletePartners;
    private PreparedStatement deleteInvoiceByPartnerId;
    private PreparedStatement deletePaymentByPartnerId;
    private PreparedStatement deleteInvoicePaymentsByPartnerId;
    private PreparedStatement deleteAddress;
    private PreparedStatement deleteContact;

    private PreparedStatement queryPartners;
    private PreparedStatement queryAddressIdsByPartnerId;
    private PreparedStatement queryPartnerIdsByAddressId;
    private PreparedStatement queryContactIdsByPartnerId;
    private PreparedStatement queryPartnerIdsByContactId;
    private PreparedStatement queryAddresses;
    private PreparedStatement queryContacts;
    private PreparedStatement queryInvoices;
    private PreparedStatement queryPayments;
    private PreparedStatement queryDataByPaymentId;




    //Create DataSource singleton
    private static DataSource instance = new DataSource();

    private DataSource() {
    }

    public static DataSource getInstance() {
        return instance;
    }


    // open/close methods for create/close connection & prepareStatements
    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);

            insertIntoContacts = conn.prepareStatement(INSERT_CONTACTS, Statement.RETURN_GENERATED_KEYS);
            insertIntoAddresses = conn.prepareStatement(INSERT_ADDRESSES, Statement.RETURN_GENERATED_KEYS);
            insertIntoPartners = conn.prepareStatement(INSERT_PARTNERS, Statement.RETURN_GENERATED_KEYS);
            insertIntoPartnerContacts = conn.prepareStatement(INSERT_PARTNERCONTACTS);
            insertIntoPartnerAddresses = conn.prepareStatement(INSERT_PARTNERADDRESSES);
            insertIntoInvoices = conn.prepareStatement(INSERT_INVOICES);
            insertIntoPayments = conn.prepareStatement(INSERT_PAYMENTS);
            insertIntoInvoicePayment = conn.prepareStatement(INSERT_INVOICEPAYMENTS);

            updateInvoicePayedStatement = conn.prepareStatement(UPDATE_INVOICE_PAYED);
            updatePayments = conn.prepareStatement(UPDATE_PAYMENTS);
            updatePaymentByInvoices =conn.prepareStatement(UPDATE_PAYMENTS_BY_INVOICE);
            updatePartners = conn.prepareStatement(UPDATE_PARTNERS);
            updateAddresses = conn.prepareStatement(UPDATE_ADDRESSES);
            updateContact = conn.prepareStatement(UPDATE_CONTACT);

            deletePayments = conn.prepareStatement(DELETE_PAYMENTS);
            deleteInvoices = conn.prepareStatement(DELETE_INVOICES);
            deleteInvoicePayments = conn.prepareStatement(DELETE_INVOICEPAYMENTS);
            deletePartnerAddressByPartnerId = conn.prepareStatement(DELETE_PARTNERADDRESS_BY_PARTNERID);
            deletePartnerAddressByAddressId = conn.prepareStatement(DELETE_PARTNERADDRESS_BY_ADDRESSID);
            deletePartnerContactByPartnerId = conn.prepareStatement(DELETE_PARTNERCONTACT_BY_PARTNERID);
            deletePartnerContactByContactId = conn.prepareStatement(DELETE_PARTNERCONTACT_BY_CONTACTID);
            deletePartners = conn.prepareStatement(DELETE_PARTNER);
            deleteInvoiceByPartnerId = conn.prepareStatement(DELETE_INVOICES_BY_PARTNERID);
            deletePaymentByPartnerId = conn.prepareStatement(DELETE_PAYMENTS_BY_PARTNERID);
            deleteInvoicePaymentsByPartnerId = conn.prepareStatement(DELETE_INVOICEPAYMENTS_BY_PAYMENTID);
            deleteAddress = conn.prepareStatement(DELETE_ADDRESS);
            deleteContact = conn.prepareStatement(DELETE_CONTACT);

            queryPartners = conn.prepareStatement(QUERY_PARTNERS);
            queryAddresses = conn.prepareStatement(QUERY_ADDRESSES);
            queryContacts = conn.prepareStatement(QUERY_CONTACTS);
            queryInvoices = conn.prepareStatement(QUERY_INVOICES);
            queryPayments = conn.prepareStatement(QUERY_PAYMENTS);

            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (insertIntoContacts != null) insertIntoContacts.close();
            if (insertIntoAddresses != null) insertIntoAddresses.close();
            if (insertIntoPartners != null) insertIntoPartners.close();
            if (insertIntoPartnerContacts != null) insertIntoPartnerContacts.close();
            if (insertIntoPartnerAddresses != null) insertIntoPartnerAddresses.close();
            if (insertIntoInvoices != null) insertIntoInvoices.close();
            if (insertIntoPayments != null) insertIntoPayments.close();
            if (insertIntoInvoicePayment != null) insertIntoInvoicePayment.close();

            if (updateInvoicePayedStatement != null) updateInvoicePayedStatement.close();
            if (updatePayments != null) updatePayments.close();
            if (updatePaymentByInvoices != null) updatePaymentByInvoices.close();
            if (updatePartners != null) updatePartners.close();
            if (updateAddresses != null) updateAddresses.close();
            if (updateContact != null) updateContact.close();

            if (deletePayments != null) deletePayments.close();
            if (deleteInvoices != null) deleteInvoices.close();
            if (deleteInvoicePayments != null) deleteInvoicePayments.close();
            if (deletePartnerAddressByPartnerId != null) deletePartnerAddressByPartnerId.close();
            if (deletePartnerAddressByAddressId != null) deletePartnerAddressByAddressId.close();
            if (deletePartnerContactByPartnerId != null) deletePartnerContactByPartnerId.close();
            if (deletePartnerContactByContactId != null) deletePartnerContactByContactId.close();
            if (deletePartners != null) deletePartners.close();
            if (deleteInvoiceByPartnerId != null) deleteInvoiceByPartnerId.close();
            if (deletePaymentByPartnerId != null) deletePaymentByPartnerId.close();
            if (deleteInvoicePaymentsByPartnerId != null) deleteInvoicePaymentsByPartnerId.close();
            if (deleteAddress != null) deleteAddress.close();
            if (deleteContact != null) deleteContact.close();

            if (queryPartners != null) queryPartners.close();
            if (queryAddressIdsByPartnerId != null) queryAddressIdsByPartnerId.close();
            if (queryPartnerIdsByAddressId != null) queryPartnerIdsByAddressId.close();
            if (queryContactIdsByPartnerId != null) queryContactIdsByPartnerId.close();
            if (queryPartnerIdsByContactId != null) queryPartnerIdsByContactId.close();
            if (queryAddresses != null) queryAddresses.close();
            if (queryContacts != null) queryContacts.close();
            if (queryInvoices != null) queryInvoices.close();
            if (queryPayments != null) queryPayments.close();
            if (queryDataByPaymentId != null) queryDataByPaymentId.close();

            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }


    // -----PAYMENT-----
    // commit insert payment
    public void insertPaymentCommit(Payment payment) {
        try {
            conn.setAutoCommit(false);
            int payment_id = insertPayment(payment);
            for (Invoice invoice : payment.getInvoices()) {
                int invoice_id = invoice.getInvoice_id();
                double payedAmount = invoice.getPayedAmount();
                double payedOriginalAmount = invoice.getPayedOriginalAmount();
                insertInvoicePayment(invoice_id,payment_id,payedAmount,payedOriginalAmount);
                double newBalance = invoice.getBalance();
                updateInvoicePayed(invoice_id,newBalance);
            }
            conn.commit();

        } catch (Exception e) {
            System.out.println("Insert payment, invoicePaymentConnection update Invoice exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println(" Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }

    // insert payment method
    private int insertPayment(Payment payment) throws SQLException{
        insertIntoPayments.setInt(1,payment.getPartner().getPartner_id());
        insertIntoPayments.setDouble(2,payment.getAmount());
        insertIntoPayments.setDouble(3,payment.getBalance());
        insertIntoPayments.setString(4,payment.getCurrency());
        insertIntoPayments.setString(5,payment.getDate().toString());
        insertIntoPayments.setString(6,payment.getPaymentMethod());
        insertIntoPayments.setString(7,payment.getNote());
        int affectedRows = insertIntoPayments.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert payments!");
        }
        ResultSet generatedKeys = insertIntoPayments.getGeneratedKeys();
        if(generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Couldn't get _id for payment");
        }
    }

    // commit update payment
    public void updatePaymentCommit(Payment payment) {
        try {
            conn.setAutoCommit(false);
            updatePayment(payment);
            int payment_id = payment.getPayment_id();
            for (Invoice invoice : payment.getInvoices()) {
                int invoice_id = invoice.getInvoice_id();
                double payedAmount = invoice.getPayedAmount();
                double payedOriginalAmount = invoice.getPayedOriginalAmount();
                insertInvoicePayment(invoice_id,payment_id,payedAmount,payedOriginalAmount);
                double newBalance = invoice.getBalance();
                updateInvoicePayed(invoice_id,newBalance);
            }
            conn.commit();

        } catch (Exception e) {
            System.out.println("Insert payment, invoicePaymentConnection update Invoice exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }


    //update payment method
    private void updatePayment(Payment payment) throws SQLException{
        updatePayments.setDouble(1,payment.getBalance());
        updatePayments.setString(2,payment.getNote());
        updatePayments.setInt(3,payment.getPayment_id());
        int affectedRows = updatePayments.executeUpdate();
        if(affectedRows != 1) {
            throw new SQLException("Couldn't update payments!");
        }
    }

    //update payment due to delete invoice

    private void updatePaymentByInvoice(int payment_id, double amount) throws SQLException{
        updatePaymentByInvoices.setDouble(1,amount);
        updatePaymentByInvoices.setDouble(2,payment_id);
        int affectedRows = updatePaymentByInvoices.executeUpdate();
        if(affectedRows != 1) {
            throw new SQLException("Couldn't update payments (delete invoice)!");
        }
    }

    // commit delete payment
    public void deletePaymentCommit(Payment payment) {
        ObservableList<Invoice> invoices = getInvoices();
        try {
            conn.setAutoCommit(false);
            deletePayment(payment);
            int payment_id = payment.getPayment_id();
            queryDataByPaymentId = conn.prepareStatement(QUERY_DATA_BY_PAYMENTID + payment_id);
            ResultSet invoicePaymentResult = queryDataByPaymentId.executeQuery();
            while (invoicePaymentResult.next()) {
                int invoice_id = invoicePaymentResult.getInt(COLUMN_INVOICEPAYMENT_INVOICE_ID);
                int invoicePayment_id = invoicePaymentResult.getInt(COLUMN_INVOICEPAYMENT_ID);
                int j = 0;
                while(invoices.get(j).getInvoice_id()!=invoice_id) {
                    j++;
                }
                Invoice invoice = invoices.get(j);
                //add the payed amount to the invoice
                double amount = (invoice.getBalance()) + (invoicePaymentResult.getDouble(COLUMN_INVOICEPAYMENT_PAYEDAMOUNT));
                updateInvoicePayed(invoice_id,amount);
                //delete from invoicePaymentConnection table
                deleteInvoicePayment(invoicePayment_id);
            }
            conn.commit();

        } catch (Exception e) {
            System.out.println("delete payment, invoicePaymentConnection update Invoice exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }


    //delete payment method
    private void deletePayment(Payment payment) throws SQLException{
        deletePayments.setInt(1,payment.getPayment_id());
        int affectedRows = deletePayments.executeUpdate();
        if(affectedRows != 1) {
            throw new SQLException("Couldn't delete payments!");
        }
    }

    //delete payment by PartnerId
    private void deletePaymentByPartner(Partner partner) throws SQLException{
        deletePaymentByPartnerId.setInt(1,partner.getPartner_id());
        deletePaymentByPartnerId.executeUpdate();
    }

    //query payments
    public ObservableList<Payment> getPayments() {
        ObservableList<Partner> partners = getPartners();
        ObservableList<Invoice> invoices = getInvoices();
        ObservableList<Payment> payments = FXCollections.observableArrayList();
        try {
            ResultSet paymentResult = queryPayments.executeQuery();
            while (paymentResult.next()) {
                ObservableList<Invoice> payedInvoices = FXCollections.observableArrayList();
                SimpleIntegerProperty payment_id = new SimpleIntegerProperty();
                payment_id.set(paymentResult.getInt(COLUMN_PAYMENT_ID));
                int partner_id = paymentResult.getInt(COLUMN_PAYMENT_PARTNER_ID);
                int i = 0;
                while(partners.get(i).getPartner_id()!=partner_id) {
                    i++;
                }
                Partner partner = partners.get(i);
                SimpleDoubleProperty amount = new SimpleDoubleProperty();
                amount.set(paymentResult.getDouble(COLUMN_PAYMENT_AMOUNT));
                SimpleDoubleProperty balance = new SimpleDoubleProperty();
                balance.set(paymentResult.getDouble(COLUMN_PAYMENT_BALANCE));
                SimpleStringProperty currency = new SimpleStringProperty();
                currency.set(paymentResult.getString(COLUMN_PAYMENT_CURRENCY));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(paymentResult.getString(COLUMN_PAYMENT_DATE), dtf);
                SimpleStringProperty paymentMethod = new SimpleStringProperty();
                paymentMethod.set(paymentResult.getString(COLUMN_PAYMENT_PAYMENTMETHOD));
                SimpleStringProperty note = new SimpleStringProperty();
                note.set(paymentResult.getString(COLUMN_PAYMENT_NOTE));
                queryDataByPaymentId = conn.prepareStatement(QUERY_DATA_BY_PAYMENTID + payment_id.getValue());
                ResultSet invoicePaymentResult = queryDataByPaymentId.executeQuery();

                while (invoicePaymentResult.next()) {
                    int invoice_id = invoicePaymentResult.getInt(COLUMN_INVOICEPAYMENT_INVOICE_ID);
                    int j = 0;
                    while(invoices.get(j).getInvoice_id()!=invoice_id) {
                        j++;
                    }
                    invoices.get(j).addInvoicePayment(new InvoicePayment(payment_id,
                            new SimpleDoubleProperty(invoicePaymentResult.getDouble(COLUMN_INVOICEPAYMENT_PAYEDAMOUNT)),
                            new SimpleDoubleProperty(invoicePaymentResult.getDouble(COLUMN_INVOICEPAYMENT_PAYEDORIGINALAMOUNT)),
                            date, paymentMethod, currency));
                    payedInvoices.add(invoices.get(j));
                }
                payments.add(new Payment(partner,payedInvoices,amount,balance,currency,date,paymentMethod,note,payment_id));
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong loading payment / dataSource " + e.getMessage());
        }
        return payments;
    }


    // -----INVOICE-----
    // insert invoice method
    public void insertInvoice(Invoice invoice) throws SQLException{
        insertIntoInvoices.setInt(1,invoice.getPartner().getPartner_id());
        insertIntoInvoices.setInt(2,invoice.getContact().getContact_id());
        insertIntoInvoices.setString(3,invoice.getInvoiceNr());
        insertIntoInvoices.setString(4,invoice.getRegNr());
        insertIntoInvoices.setDouble(5,invoice.getNetAmount());
        insertIntoInvoices.setString(6,invoice.getVat());
        insertIntoInvoices.setString(7,invoice.getCurrency());
        insertIntoInvoices.setDouble(8,invoice.getBalance());
        insertIntoInvoices.setString(9,invoice.getPaymentMethod());
        insertIntoInvoices.setString(10,invoice.getDateOfInvoice().toString());
        insertIntoInvoices.setString(11,invoice.getDateOfPerformance().toString());
        insertIntoInvoices.setString(12,invoice.getDateOfDue().toString());
        insertIntoInvoices.setString(13,invoice.getNote());
        int affectedRows = insertIntoInvoices.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert Invoices!");
        }
    }

    //update updateInvoicePayedStatement method
    private void updateInvoicePayed(int invoice_id, double newBalance) throws SQLException {
        updateInvoicePayedStatement.setDouble(1,newBalance);
        updateInvoicePayedStatement.setInt(2,invoice_id);
        int affectedRows = updateInvoicePayedStatement.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert invoicePayment!");
        }
    }

    // insert invoicePayment method
    private void insertInvoicePayment(int invoice_id, int payment_id, double payedAmount, double payedOriginalAmount) throws SQLException{
        insertIntoInvoicePayment.setInt(1,invoice_id);
        insertIntoInvoicePayment.setInt(2,payment_id);
        insertIntoInvoicePayment.setDouble(3,payedAmount);
        insertIntoInvoicePayment.setDouble(4,payedOriginalAmount);
        int affectedRows = insertIntoInvoicePayment.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert invoicePayment!");
        }
    }

    // delete invoicePayment method
    private void deleteInvoicePayment(int invoicePayment_id) throws SQLException{
        deleteInvoicePayments.setInt(1,invoicePayment_id);
        deleteInvoicePayments.executeUpdate();
    }

    // delete invoicePaymentByPaymentId method
    private void deleteInvoicePaymentByPaymentId(int payment_id) throws SQLException{
        deleteInvoicePaymentsByPartnerId.setInt(1,payment_id);
        deleteInvoicePaymentsByPartnerId.executeUpdate();
    }

    // commit delete invoice
    public void deleteInvoiceCommit(Invoice invoice) {
        ObservableList<Payment> payments = getPayments();
        try {
            conn.setAutoCommit(false);
            deleteInvoice(invoice);
            int invoice_id = invoice.getInvoice_id();
            queryDataByPaymentId = conn.prepareStatement(QUERY_DATA_BY_INVOICEID + invoice_id);
            ResultSet invoicePaymentResult = queryDataByPaymentId.executeQuery();
            while (invoicePaymentResult.next()) {
                int payment_id = invoicePaymentResult.getInt(COLUMN_INVOICEPAYMENT_PAYMENT_ID);
                int invoicePayment_id = invoicePaymentResult.getInt(COLUMN_INVOICEPAYMENT_ID);
                int j = 0;
                while(payments.get(j).getPayment_id() != payment_id) {
                    j++;
                }
                Payment payment = payments.get(j);
                //add the original payed amount to the payment
                double amount = (payment.getBalance()) + (invoicePaymentResult.getDouble(COLUMN_INVOICEPAYMENT_PAYEDORIGINALAMOUNT));
                updatePaymentByInvoice(payment_id,amount);
                //delete from invoicePaymentConnection table
                deleteInvoicePayment(invoicePayment_id);
            }
            conn.commit();

        } catch (Exception e) {
            System.out.println("delete payment, invoicePaymentConnection update Invoice exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }

    //delete invoice method
    private void deleteInvoice(Invoice invoice) throws SQLException{
        deleteInvoices.setInt(1,invoice.getInvoice_id());
        int affectedRows = deleteInvoices.executeUpdate();
        if(affectedRows != 1) {
            throw new SQLException("Couldn't delete invoices!");
        }
    }

    //delete invoice by PartnerId
    private void deleteInvoiceByPartner(Partner partner) throws SQLException{
        deleteInvoiceByPartnerId.setInt(1,partner.getPartner_id());
        deleteInvoiceByPartnerId.executeUpdate();
    }

    // invoice getter
    public ObservableList<Invoice> getInvoices() {
        ObservableList<Partner> partners = getPartners();
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();
        try {
            ResultSet invoiceResult = queryInvoices.executeQuery();
            while (invoiceResult.next()) {
                SimpleIntegerProperty invoice_id = new SimpleIntegerProperty();
                invoice_id.set(invoiceResult.getInt(COLUMN_INVOICE_ID));
                int partner_id = invoiceResult.getInt(COLUMN_INVOICE_PARTNER_ID);
                int i = 0;
                while(partners.get(i).getPartner_id()!=partner_id) {
                    i++;
                }
                Partner partner = partners.get(i);
                int contact_id = invoiceResult.getInt(COLUMN_INVOICE_CONTACT_ID);
                //set contact default as deleted Partner
                Contact contact = new Contact(new SimpleStringProperty("Törölt kapcsolat"),
                        new SimpleStringProperty(""),new SimpleStringProperty(""),new SimpleStringProperty(""),
                        new SimpleStringProperty(""),new SimpleIntegerProperty(-1));
                //if contact is present in contacts Table
                for (Contact partnerContact : partner.getContacts()) {
                    if (partnerContact.getContact_id()==contact_id){
                        contact = partnerContact;
                        break;
                    }
                }
                SimpleStringProperty invoiceNr = new SimpleStringProperty();
                invoiceNr.set(invoiceResult.getString(COLUMN_INVOICE_INVOICENR));
                SimpleStringProperty regNr = new SimpleStringProperty();
                regNr.set(invoiceResult.getString(COLUMN_INVOICE_REGNR));
                SimpleDoubleProperty netAmount = new SimpleDoubleProperty();
                netAmount.set(invoiceResult.getDouble(COLUMN_INVOICE_NETAMOUNT));
                SimpleStringProperty vat = new SimpleStringProperty();
                vat.set(invoiceResult.getString(COLUMN_INVOICE_VAT));
                SimpleStringProperty paymetMethod = new SimpleStringProperty();
                paymetMethod.set(invoiceResult.getString(COLUMN_INVOICE_PAYMENTMETHOD));
                SimpleStringProperty currency = new SimpleStringProperty();
                currency.set(invoiceResult.getString(COLUMN_INVOICE_CURRENCY));
                SimpleDoubleProperty balance = new SimpleDoubleProperty();
                balance.set(invoiceResult.getDouble(COLUMN_INVOICE_BALANCE));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateOfInvoice = LocalDate.parse(invoiceResult.getString(COLUMN_INVOICE_DATEOFINVOICE), dtf);
                LocalDate dateOfPerformance = LocalDate.parse(invoiceResult.getString(COLUMN_INVOICE_DATEOFPERFORMANCE), dtf);
                LocalDate dateOfDue = LocalDate.parse(invoiceResult.getString(COLUMN_INVOICE_DATEOFDUE), dtf);
                SimpleStringProperty note = new SimpleStringProperty();
                note.set(invoiceResult.getString(COLUMN_INVOICE_NOTE));

                invoices.add(new Invoice(partner,contact,invoiceNr,regNr,netAmount,vat,currency,balance,paymetMethod,
                        dateOfInvoice,dateOfPerformance,dateOfDue,note,invoice_id));
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong loading invoices / dataSource " + e.getMessage());
        }
        return invoices;
    }

    // -----CONTACT-----
    // contacts getter
    public ObservableList<Contact> getContacts(){
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        try {
            ResultSet contactResult = queryContacts.executeQuery();
            while (contactResult.next()) {
                SimpleIntegerProperty contact_id = new SimpleIntegerProperty();
                contact_id.set(contactResult.getInt(COLUMN_CONTACT_ID));
                SimpleStringProperty name = new SimpleStringProperty();
                name.set(contactResult.getString(COLUMN_CONTACT_NAME));
                SimpleStringProperty phoneNr = new SimpleStringProperty();
                phoneNr.set(contactResult.getString(COLUMN_CONTACT_PHONENR));
                SimpleStringProperty faxNr = new SimpleStringProperty();
                faxNr.set(contactResult.getString(COLUMN_CONTACT_FAXNR));
                SimpleStringProperty email = new SimpleStringProperty();
                email.set(contactResult.getString(COLUMN_CONTACT_EMAIL));
                SimpleStringProperty note = new SimpleStringProperty();
                note.set(contactResult.getString(COLUMN_CONTACT_NOTE));

                contacts.add(new Contact(name,phoneNr,faxNr,email,note,contact_id));
            }
        }catch (SQLException e) {
            System.out.println("Something went wrong loading contacts / dataSource " + e.getMessage());
        }
        return contacts.sorted();
    }

    //insert contact method
    private int insertContact(String name, String phoneNr, String faxNr, String email,
                              String note) throws SQLException {
        insertIntoContacts.setString(1, name);
        insertIntoContacts.setString(2,phoneNr);
        insertIntoContacts.setString(3,faxNr);
        insertIntoContacts.setString(4,email);
        insertIntoContacts.setString(5,note);
        int affectedRows = insertIntoContacts.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert contact!");
        }

        ResultSet generatedKeys = insertIntoContacts.getGeneratedKeys();
        if(generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Couldn't get _id for contacts");
        }
    }

    //update contact method
    public void updateContact(String name, String phoneNr, String faxNr, String email,
                              String note, int contactId) throws SQLException {
        updateContact.setString(1,name);
        updateContact.setString(2,phoneNr);
        updateContact.setString(3,faxNr);
        updateContact.setString(4,email);
        updateContact.setString(5,note);
        updateContact.setInt(6,contactId);
        int affectedRows = updateContact.executeUpdate();
        if(affectedRows != 1) {
            throw new SQLException("Couldn't update Contact!");
        }
    }

    //delete contact commit
    public void deleteContactCommit(int contact_Id){
        try {
            conn.setAutoCommit(false);
            deleteContact(contact_Id);
            deletePartnerContactByContactId(contact_Id);
            conn.commit();

        } catch (Exception e) {
            System.out.println("delete contact, partnerContactConnection,exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }


    //delete contact
    private void deleteContact(int contact_id) throws SQLException{
        deleteContact.setInt(1,contact_id);
        deleteContact.executeUpdate();
    }

    // -----ADDRESS-----
    // addresses getter
    public ObservableList<Address> getAddresses(){
        ObservableList<Address> addresses = FXCollections.observableArrayList();
        try {
            ResultSet addressResult = queryAddresses.executeQuery();
            while (addressResult.next()) {
                SimpleIntegerProperty address_id = new SimpleIntegerProperty();
                address_id.set(addressResult.getInt(COLUMN_ADDRESS_ID));
                SimpleStringProperty country = new SimpleStringProperty();
                country.set(addressResult.getString(COLUMN_ADDRESS_COUNTRY));
                SimpleStringProperty postcode = new SimpleStringProperty();
                postcode.set(addressResult.getString(COLUMN_ADDRESS_POSTCODE));
                SimpleStringProperty city = new SimpleStringProperty();
                city.set(addressResult.getString(COLUMN_ADDRESS_CITY));
                SimpleStringProperty street = new SimpleStringProperty();
                street.set(addressResult.getString(COLUMN_ADDRESS_STREET));
                SimpleStringProperty houseNr = new SimpleStringProperty();
                houseNr.set(addressResult.getString(COLUMN_ADDRESS_HOUSENR));
                SimpleStringProperty mailbox = new SimpleStringProperty();
                mailbox.set(addressResult.getString(COLUMN_ADDRESS_MAILBOX));
                SimpleStringProperty addressType = new SimpleStringProperty();
                addressType.set(addressResult.getString(COLUMN_ADDRESS_ADDRESSTYPE));

                addresses.add(new Address(country,postcode,city,street,houseNr,mailbox,addressType,address_id));
            }
        }catch (SQLException e) {
            System.out.println("Something went wrong loading addresses / dataSource " + e.getMessage());
        }
        return addresses.sorted();
    }

    //insert address method
    private int insertAddress(String country, String postcode, String city, String street,
                              String houseNr, String mailbox, String addressType) throws SQLException {
        insertIntoAddresses.setString(1,country);
        insertIntoAddresses.setString(2,postcode);
        insertIntoAddresses.setString(3,city);
        insertIntoAddresses.setString(4,street);
        insertIntoAddresses.setString(5,houseNr);
        insertIntoAddresses.setString(6,mailbox);
        insertIntoAddresses.setString(7,addressType);
        int affectedRows = insertIntoAddresses.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert addresses!");
        }

        ResultSet generatedKeys = insertIntoAddresses.getGeneratedKeys();
        if(generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Couldn't get _id for addresses");
        }
    }

    //update address method
    public void updateAddress(String country, String postcode, String city, String street,
                              String houseNr, String mailbox, int addressId) throws SQLException {
        updateAddresses.setString(1,country);
        updateAddresses.setString(2,postcode);
        updateAddresses.setString(3,city);
        updateAddresses.setString(4,street);
        updateAddresses.setString(5,houseNr);
        updateAddresses.setString(6,mailbox);
        updateAddresses.setInt(7,addressId);
        int affectedRows = updateAddresses.executeUpdate();
        if(affectedRows != 1) {
            throw new SQLException("Couldn't update addresses!");
        }
    }

    //delete address commit
    public void deleteAddressCommit(int addressId){
        try {
            conn.setAutoCommit(false);
            deleteAddress(addressId);
            deletePartnerAddressByAddressId(addressId);
            conn.commit();

        } catch (Exception e) {
            System.out.println("delete address, partnerAdderssConnection,exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }


    //delete address
    private void deleteAddress(int address_id) throws SQLException{
        deleteAddress.setInt(1,address_id);
        deleteAddress.executeUpdate();
    }


    // -----PARTNER-----
    // commit insert partner
    public void insertPartnerCommit(Partner partner) {
        try {
            conn.setAutoCommit(false);
            int partner_id = insertPartner(partner);
            for (Contact contact : partner.getContacts()) {
                int contact_id;
                if(contact.getContact_id() != 0)
                    contact_id = contact.getContact_id();
                else
                    contact_id = insertContact(contact.getName(), contact.getPhoneNr(), contact.getFaxNr(),
                        contact.getEmail(), contact.getNote());
                insertPartnerContact(partner_id, contact_id);
            }
            int headOfficeAddress_id;
            if(partner.getHeadOfficeAddress().getAddress_id()!=0)
                headOfficeAddress_id = partner.getHeadOfficeAddress().getAddress_id();
            else
                headOfficeAddress_id = insertAddress(partner.getHeadOfficeAddress().getCountry(),
                    partner.getHeadOfficeAddress().getPostcode(),
                    partner.getHeadOfficeAddress().getCity(),
                    partner.getHeadOfficeAddress().getStreet(),
                    partner.getHeadOfficeAddress().getHouseNr(),
                    partner.getHeadOfficeAddress().getMailbox(),
                    partner.getHeadOfficeAddress().getAddressType());
            insertPartnerAddress(partner_id, headOfficeAddress_id);

            for (Address address : partner.getAddresses()) {
                int address_id;
                if(address.getAddress_id()!=0)
                    address_id = address.getAddress_id();
                else
                    address_id = insertAddress(address.getCountry(), address.getPostcode(), address.getCity(),
                        address.getStreet(), address.getHouseNr(), address.getMailbox(),
                        address.getAddressType());
                insertPartnerAddress(partner_id, address_id);
            }

            conn.commit();

        } catch (Exception e) {
            System.out.println("Insert partner,address,contact,partnerContact,partnerAddress exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }

    // commit update partner
    public void updatePartnerCommit(Partner partner) {
        try {
            conn.setAutoCommit(false);
            int partner_id = partner.getPartner_id();
            updatePartner(partner);
            deletePartnerContactByPartnerId(partner_id);
            deletePartnerAddressByPartnerId(partner_id);
            for (Contact contact : partner.getContacts()) {
                int contact_id;
                if (contact.getContact_id() != 0)
                    contact_id = contact.getContact_id();
                else
                    contact_id = insertContact(contact.getName(), contact.getPhoneNr(), contact.getFaxNr(),
                            contact.getEmail(), contact.getNote());
                insertPartnerContact(partner_id, contact_id);
            }
            int headOfficeAddress_id;
            if (partner.getHeadOfficeAddress().getAddress_id() != 0)
                headOfficeAddress_id = partner.getHeadOfficeAddress().getAddress_id();
            else
                headOfficeAddress_id = insertAddress(partner.getHeadOfficeAddress().getCountry(),
                        partner.getHeadOfficeAddress().getPostcode(),
                        partner.getHeadOfficeAddress().getCity(),
                        partner.getHeadOfficeAddress().getStreet(),
                        partner.getHeadOfficeAddress().getHouseNr(),
                        partner.getHeadOfficeAddress().getMailbox(),
                        partner.getHeadOfficeAddress().getAddressType());
            insertPartnerAddress(partner_id, headOfficeAddress_id);

            for (Address address : partner.getAddresses()) {
                int address_id;
                if (address.getAddress_id() != 0)
                    address_id = address.getAddress_id();
                else
                    address_id = insertAddress(address.getCountry(), address.getPostcode(), address.getCity(),
                            address.getStreet(), address.getHouseNr(), address.getMailbox(),
                            address.getAddressType());
                insertPartnerAddress(partner_id, address_id);
            }

            conn.commit();

        } catch (Exception e) {
            System.out.println("update partner,address,contact,partnerContact,partnerAddress exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }

    // commit delete partner
    public void deletePartnerCommit(Partner partner) {
        ObservableList<Payment> payments = getPayments();
        try {
            conn.setAutoCommit(false);
            deletePartner(partner);
            ObservableList<Integer> payment_Ids = FXCollections.observableArrayList();
            for (Payment payment: payments) {
                if (payment.getPartner().getPartner_id() == partner.getPartner_id())
                    payment_Ids.add(payment.getPayment_id());
            }
            for (int payment_Id: payment_Ids) {
                deleteInvoicePaymentByPaymentId(payment_Id);
            }

            deleteInvoiceByPartner(partner);
            deletePaymentByPartner(partner);
            deletePartnerAddressByPartnerId(partner.getPartner_id());
            deletePartnerContactByPartnerId(partner.getPartner_id());

            conn.commit();

        } catch (Exception e) {
            System.out.println("delete partner, invoice, payment, partnerAdderssConnection, partnerContactConnection, " +
                    "invoicePaymentConnection  exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }

    //insert partner method
    private int insertPartner(Partner partner) throws SQLException {
        insertIntoPartners.setString(1,partner.getName());
        insertIntoPartners.setString(2,partner.getShortName());
        insertIntoPartners.setString(3,partner.getVatNr());
        insertIntoPartners.setString(4,partner.getEuVatNr());
        insertIntoPartners.setString(5,partner.getDefaultVat());
        insertIntoPartners.setString(6,partner.getDefaultCurrency());
        insertIntoPartners.setString(7,partner.getPaymentMethod());
        insertIntoPartners.setInt(8,partner.getPaymentDelay().getDays());
        insertIntoPartners.setDouble(9,partner.getLimitValue());
        insertIntoPartners.setString(10,partner.getDefaultPhone());
        insertIntoPartners.setString(11,partner.getDefaultFax());
        insertIntoPartners.setString(12,partner.getDefaultEmail());
        insertIntoPartners.setString(13,partner.getNote());
        int affectedRows = insertIntoPartners.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert partners!");
        }

        ResultSet generatedKeys = insertIntoPartners.getGeneratedKeys();
        if(generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Couldn't get _id for partners");
        }
    }

    //update partner method
    private void updatePartner(Partner partner) throws SQLException {
        updatePartners.setString(1,partner.getName());
        updatePartners.setString(2,partner.getShortName());
        updatePartners.setString(3,partner.getVatNr());
        updatePartners.setString(4,partner.getEuVatNr());
        updatePartners.setString(5,partner.getDefaultVat());
        updatePartners.setString(6,partner.getDefaultCurrency());
        updatePartners.setString(7,partner.getPaymentMethod());
        updatePartners.setInt(8,partner.getPaymentDelay().getDays());
        updatePartners.setDouble(9,partner.getLimitValue());
        updatePartners.setString(10,partner.getDefaultPhone());
        updatePartners.setString(11,partner.getDefaultFax());
        updatePartners.setString(12,partner.getDefaultEmail());
        updatePartners.setString(13,partner.getNote());
        updatePartners.setInt(14,partner.getPartner_id());
        int affectedRows = updatePartners.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't update partners!");
        }
    }

    //delete partner method
    private void deletePartner(Partner partner) throws SQLException{
        deletePartners.setInt(1,partner.getPartner_id());
        int affectedRows = deletePartners.executeUpdate();
        if(affectedRows != 1) {
            throw new SQLException("Couldn't delete partners!");
        }
    }

    //insert partnerContact method
    private void insertPartnerContact(int partner_id, int contact_id) throws SQLException {
        insertIntoPartnerContacts.setInt(1, partner_id);
        insertIntoPartnerContacts.setInt(2, contact_id);
        int affectedRows = insertIntoPartnerContacts.executeUpdate();

        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert partnerContact!");
        }
    }

    //delete partnerContact method
    private void deletePartnerContactByPartnerId(int partner_id) throws SQLException{
        deletePartnerContactByPartnerId.setInt(1,partner_id);
        deletePartnerContactByPartnerId.executeUpdate();
    }

    //delete partnerContact method
    private void deletePartnerContactByContactId(int contact_id) throws SQLException{
        deletePartnerContactByContactId.setInt(1,contact_id);
        deletePartnerContactByContactId.executeUpdate();
    }

    //query partnerId by ContactId
    public ObservableList<Integer> getPartnerIdsByContactId(int contactId){
        ObservableList<Integer> partnerIdsList = FXCollections.observableArrayList();
        try {
            queryPartnerIdsByContactId = conn.prepareStatement(QUERY_PARTNERIDS_BY_CONTACTID + contactId);
            ResultSet partnerIdsResult = queryPartnerIdsByContactId.executeQuery();
            while (partnerIdsResult.next()){
                int partnerId = partnerIdsResult.getInt(COLUMN_PARTNERCONTACT_PARTNER_ID);
                partnerIdsList.add(partnerId);
            }
        }catch (SQLException e){
            System.out.println("Something went wrong loading partnerIds / dataSource " + e.getMessage());
        }
        return partnerIdsList;
    }

    //insert partnerAddress method
    private void insertPartnerAddress(int partner_id, int address_id) throws SQLException {
        insertIntoPartnerAddresses.setInt(1, partner_id);
        insertIntoPartnerAddresses.setInt(2, address_id);

        int affectedRows = insertIntoPartnerAddresses.executeUpdate();
        if(affectedRows != 1) {
            throw new SQLException("Couldn't insert partnerAddress!");
        }
    }

    //delete partnerAddress method
    private void deletePartnerAddressByPartnerId(int partner_id) throws SQLException{
        deletePartnerAddressByPartnerId.setInt(1,partner_id);
        deletePartnerAddressByPartnerId.executeUpdate();
    }

    //delete partnerAddress method
    private void deletePartnerAddressByAddressId(int address_id) throws SQLException{
        deletePartnerAddressByAddressId.setInt(1,address_id);
        deletePartnerAddressByAddressId.executeUpdate();
    }

    //query partnerId by AddressId
    public ObservableList<Integer> getPartnerIdsByAddressId(int addressId){
        ObservableList<Integer> partnerIdsList = FXCollections.observableArrayList();
        try {
            queryPartnerIdsByAddressId = conn.prepareStatement(QUERY_PARTNERIDS_BY_ADDRESSIDS + addressId);
            ResultSet partnerIdsResult = queryPartnerIdsByAddressId.executeQuery();
            while (partnerIdsResult.next()){
                int partnerId = partnerIdsResult.getInt(COLUMN_PARTNERADDRESS_PARTNER_ID);
                partnerIdsList.add(partnerId);
            }
        }catch (SQLException e){
            System.out.println("Something went wrong loading partnerIds / dataSource " + e.getMessage());
        }
        return partnerIdsList;
    }

    //partner getter
    public ObservableList<Partner> getPartners(){
        ObservableList<Address> addresses = DataSource.getInstance().getAddresses();
        ObservableList<Contact> contacts = DataSource.getInstance().getContacts();
        ObservableList<Partner> partners = FXCollections.observableArrayList();
        try {
            ResultSet partnerResult = queryPartners.executeQuery();
            while (partnerResult.next()) {
                int partnerId = partnerResult.getInt(COLUMN_PARTNER_ID);
                SimpleStringProperty partnerName = new SimpleStringProperty();
                partnerName.set(partnerResult.getString(COLUMN_PARTNER_NAME));
                SimpleStringProperty shortName = new SimpleStringProperty();
                shortName.set(partnerResult.getString(COLUMN_PARTNER_SHORTNAME));
                Address headOfficeAddress = null;
                SimpleStringProperty vatNr = new SimpleStringProperty();
                vatNr.set(partnerResult.getString(COLUMN_PARTNER_VATNR));
                SimpleStringProperty euVatNr = new SimpleStringProperty();
                euVatNr.set(partnerResult.getString(COLUMN_PARTNER_EUVATNR));
                SimpleStringProperty defaultVat = new SimpleStringProperty();
                defaultVat.set(partnerResult.getString(COLUMN_PARTNER_DEFAULTVAT));
                SimpleStringProperty defaultCurrency = new SimpleStringProperty();
                defaultCurrency.set(partnerResult.getString(COLUMN_PARTNER_DEFAULTCURRENCY));
                SimpleStringProperty paymentMethod = new SimpleStringProperty();
                paymentMethod.set(partnerResult.getString(COLUMN_PARTNER_PAYMENTMETHOD));
                Period paymentDelay = Period.ofDays(partnerResult.getInt(COLUMN_PARTNER_PAYMENTDELAY));
                SimpleStringProperty defaultPhone = new SimpleStringProperty();
                defaultPhone.set(partnerResult.getString(COLUMN_PARTNER_DEFAULTPHONE));
                SimpleStringProperty defaultFax = new SimpleStringProperty();
                defaultFax.set(partnerResult.getString(COLUMN_PARTNER_DEFAULTFAX));
                SimpleStringProperty defaultEmail = new SimpleStringProperty();
                defaultEmail.set(partnerResult.getString(COLUMN_PARTNER_DEFAULTEMAIL));
                SimpleStringProperty partnerNote = new SimpleStringProperty();
                partnerNote.set(partnerResult.getString(COLUMN_PARTNER_NOTE));
                ObservableList<Address> filteredAddresses = FXCollections.observableArrayList();
                ObservableList<Contact> filteredContacts = FXCollections.observableArrayList();
                SimpleDoubleProperty limitValue = new SimpleDoubleProperty();
                limitValue.set(partnerResult.getDouble(COLUMN_PARTNER_LIMITVALUE));
                queryAddressIdsByPartnerId =conn.prepareStatement(QUERY_ADDRESSIDS_BY_PARTNERID + partnerId);
                ResultSet addressIdsResult = queryAddressIdsByPartnerId.executeQuery();
                while (addressIdsResult.next()) {
                    int addressId = addressIdsResult.getInt(COLUMN_PARTNERADDRESS_ADDRESS_ID);
                    for (Address address:addresses) {
                        if (address.getAddress_id()==addressId){
                            if (address.getAddressType().equals(HEADOFFICEADDRESS))
                                headOfficeAddress = address;
                            else
                                filteredAddresses.add(address);
                        }
                    }
                }
                queryContactIdsByPartnerId = conn.prepareStatement(QUERY_CONTACTIDS_BY_PARTNERID+ partnerId);
                ResultSet contactIdsResult = queryContactIdsByPartnerId.executeQuery();
                while (contactIdsResult.next()) {
                    int contactId = contactIdsResult.getInt(COLUMN_PARTNERCONTACT_CONTACT_ID);
                    for (Contact contact:contacts){
                        if(contact.getContact_id()==contactId){
                            filteredContacts.add(contact);
                        }
                    }
                }
                Partner newPartner = new Partner(partnerName, shortName, headOfficeAddress, vatNr,
                        euVatNr, defaultVat, defaultCurrency, paymentMethod,
                        paymentDelay, defaultPhone, defaultFax, defaultEmail,
                        partnerNote, filteredAddresses, filteredContacts, limitValue, new SimpleIntegerProperty(partnerId));
                partners.add(newPartner);
            }
        }catch (SQLException e) {
            System.out.println("Something went wrong loading partners / dataSource " + e.getMessage());
        }
        return partners.sorted();
    }
}