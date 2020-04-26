package my.salonapp.salonbookingapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Booking implements Comparable<Booking> {

    private int bookingId;
    private int companyId;
    private String companyName;
    private int clientId;
    private String clientName;
    private int staffId;
    private String staffName;
    private long bookingDate;
    private int bookingDuration;
    private ArrayList<BookingService> bookingServices;

    public Booking() {
    }

    public Booking(int bookingId, int companyId,
                   String companyName, int clientId,
                   String clientName, int staffId,
                   String staffName, long bookingDate,
                   int bookingDuration, ArrayList<BookingService> bookingServices) {
        this.bookingId = bookingId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.staffId = staffId;
        this.staffName = staffName;
        this.bookingDate = bookingDate;
        this.bookingDuration = bookingDuration;
        this.bookingServices = bookingServices;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public long getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(long bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getBookingDuration() {
        return bookingDuration;
    }

    public void setBookingDuration(int bookingDuration) {
        this.bookingDuration = bookingDuration;
    }

    public ArrayList<BookingService> getBookingServices() {
        return bookingServices;
    }

    public void setBookingServices(ArrayList<BookingService> bookingServices) {
        this.bookingServices = bookingServices;
    }

    public Float getSubTotal(ArrayList<BookingService> bookingServices){
        Float subtotal = 0F;

        for(BookingService bookingService : bookingServices) {
            subtotal += bookingService.getServicePrice();
        }

        return subtotal;
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return simpleDateFormat.format(bookingDate);
    }

    @Override
    public int compareTo(Booking o) {
        return Long.toString(this.getBookingDate()).compareTo(Long.toString(o.getBookingDate()));
    }
}
