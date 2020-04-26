package my.salonapp.salonbookingapp;

public class BookingService implements Comparable<BookingService> {

    private int bookingServiceId;
    private int bookingId;
    private String serviceName;
    private Float servicePrice;
    private int serviceDuration;
    private int displayOrder;

    public BookingService() {
    }

    public BookingService(int bookingServiceId, int bookingId,
                          String serviceName, Float servicePrice,
                          int serviceDuration, int displayOrder) {
        this.bookingServiceId = bookingServiceId;
        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceDuration = serviceDuration;
        this.displayOrder = displayOrder;
    }

    public int getBookingServiceId() {
        return bookingServiceId;
    }

    public void setBookingServiceId(int bookingServiceId) {
        this.bookingServiceId = bookingServiceId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
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

    public void setServicePrice(Float servicePrice) {
        this.servicePrice = servicePrice;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(int serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public String toString() {
        return serviceName;
    }

    @Override
    public int compareTo(BookingService o) {
        return this.getServiceName().compareTo(o.getServiceName());
    }
}
