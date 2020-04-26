package my.salonapp.salonbookingapp;

public class TransactionHistoryService {

    private int transactionId;
    private int bookingServiceId;
    private String serviceName;
    private Float servicePrice;
    private int displayOrder;

    public TransactionHistoryService() {
    }

    public TransactionHistoryService(int transactionId, int bookingServiceId,
                                     String serviceName, Float servicePrice,
                                     int displayOrder) {
        this.transactionId = transactionId;
        this.bookingServiceId = bookingServiceId;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.displayOrder = displayOrder;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getBookingServiceId() {
        return bookingServiceId;
    }

    public void setBookingServiceId(int bookingServiceId) {
        this.bookingServiceId = bookingServiceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Float getServicePrice() {
        return servicePrice;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setServicePrice(Float servicePrice) {
        this.servicePrice = servicePrice;
    }
}
