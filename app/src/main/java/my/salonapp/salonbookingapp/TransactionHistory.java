package my.salonapp.salonbookingapp;

import java.util.ArrayList;

public class TransactionHistory {

    private int transactionId;
    private String refNo;
    private String staffName;
    private int clientID;
    private String clientName;
    private long bookingDate;
    private String servicesDesc;
    private Float subTotal;

    private ArrayList<TransactionHistoryService> transactionHistoryServices;

    public TransactionHistory() {
    }

    public TransactionHistory(int transactionId, String refNo,
                              String staffName, int clientID,
                              String clientName, long bookingDate,
                              String servicesDesc, Float subTotal,
                              ArrayList<TransactionHistoryService> transactionHistoryServices) {
        this.transactionId = transactionId;
        this.refNo = refNo;
        this.staffName = staffName;
        this.clientID = clientID;
        this.clientName = clientName;
        this.bookingDate = bookingDate;
        this.servicesDesc = servicesDesc;
        this.subTotal = subTotal;
        this.transactionHistoryServices = transactionHistoryServices;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public long getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(long bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getServicesDesc() {
        return servicesDesc;
    }

    public void setServicesDesc(String servicesDesc) {
        this.servicesDesc = servicesDesc;
    }

    public Float getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Float subTotal) {
        this.subTotal = subTotal;
    }

    public ArrayList<TransactionHistoryService> getTransactionHistoryServices() {
        return transactionHistoryServices;
    }

    public void setTransactionHistoryServices(ArrayList<TransactionHistoryService> transactionHistoryServices) {
        this.transactionHistoryServices = transactionHistoryServices;
    }

    public Float getServicesSubTotal(ArrayList<TransactionHistoryService> transactionHistoryServices) {
        Float subtotal = 0F;

        for (TransactionHistoryService transactionHistoryService : transactionHistoryServices) {
            subtotal += transactionHistoryService.getServicePrice();
        }

        return subtotal;
    }
}
