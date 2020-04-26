package my.salonapp.salonbookingapp;

public class Staff implements Comparable<Staff>  {

    private int staffId;
    private String staffName;
    private String staffEmail;
    private String staffPhone;

    public Staff() {
    }

    public Staff(int staffId, String staffName) {
        this.staffId = staffId;
        this.staffName = staffName;
    }


    public Staff(int staffId, String staffName,
                 String staffEmail, String staffPhone) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.staffEmail = staffEmail;
        this.staffPhone = staffPhone;
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

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    @Override
    public String toString() {
        return staffName;
    }

    @Override
    public int compareTo(Staff o) {
        return this.getStaffName().compareTo(o.getStaffName());
    }
}
