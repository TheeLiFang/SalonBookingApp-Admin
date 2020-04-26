package my.salonapp.salonbookingapp;

public class UpcomingBooking {

    private int bookingId;
    private String bookingTime;
    private String staffName;
    private String clientName;

    public UpcomingBooking() {
    }

    public UpcomingBooking(int bookingId, String bookingTime, String staffName, String clientName) {
        this.bookingId = bookingId;
        this.bookingTime = bookingTime;
        this.staffName = staffName;
        this.clientName = clientName;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String staffClient) {
        this.clientName = staffClient;
    }
}
