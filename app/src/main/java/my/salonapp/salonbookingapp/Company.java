package my.salonapp.salonbookingapp;

public class Company {

    private int companyId;
    private String companyName;
    private String companyEmail;
    private String companyPassword;
    private String companyPhone;
    private String companyAddress;
    private Boolean mondayYN;
    private String mondayStartTime;
    private String mondayEndTime;
    private Boolean tuesdayYN;
    private String tuesdayStartTime;
    private String tuesdayEndTime;
    private Boolean wednesdayYN;
    private String wednesdayStartTime;
    private String wednesdayEndTime;
    private Boolean thursdayYN;
    private String thursdayStartTime;
    private String thursdayEndTime;
    private Boolean fridayYN;
    private String fridayStartTime;
    private String fridayEndTime;
    private Boolean saturdayYN;
    private String saturdayStartTime;
    private String saturdayEndTime;
    private Boolean sundayYN;
    private String sundayStartTime;
    private String sundayEndTime;

    public Company() {
    }

    public Company(int companyId, String companyName,
                   String companyEmail, String companyPassword,
                   String companyPhone, String companyAddress,
                   Boolean mondayYN, String mondayStartTime,
                   String mondayEndTime, Boolean tuesdayYN,
                   String tuesdayStartTime, String tuesdayEndTime,
                   Boolean wednesdayYN, String wednesdayStartTime,
                   String wednesdayEndTime, Boolean thursdayYN,
                   String thursdayStartTime, String thursdayEndTime,
                   Boolean fridayYN, String fridayStartTime,
                   String fridayEndTime, Boolean saturdayYN,
                   String saturdayStartTime, String saturdayEndTime,
                   Boolean sundayYN, String sundayStartTime,
                   String sundayEndTime) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.companyPassword = companyPassword;
        this.companyPhone = companyPhone;
        this.companyAddress = companyAddress;
        this.mondayYN = mondayYN;
        this.mondayStartTime = mondayStartTime;
        this.mondayEndTime = mondayEndTime;
        this.tuesdayYN = tuesdayYN;
        this.tuesdayStartTime = tuesdayStartTime;
        this.tuesdayEndTime = tuesdayEndTime;
        this.wednesdayYN = wednesdayYN;
        this.wednesdayStartTime = wednesdayStartTime;
        this.wednesdayEndTime = wednesdayEndTime;
        this.thursdayYN = thursdayYN;
        this.thursdayStartTime = thursdayStartTime;
        this.thursdayEndTime = thursdayEndTime;
        this.fridayYN = fridayYN;
        this.fridayStartTime = fridayStartTime;
        this.fridayEndTime = fridayEndTime;
        this.saturdayYN = saturdayYN;
        this.saturdayStartTime = saturdayStartTime;
        this.saturdayEndTime = saturdayEndTime;
        this.sundayYN = sundayYN;
        this.sundayStartTime = sundayStartTime;
        this.sundayEndTime = sundayEndTime;
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

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyPassword() {
        return companyPassword;
    }

    public void setCompanyPassword(String companyPassword) {
        this.companyPassword = companyPassword;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public Boolean getMondayYN() {
        return mondayYN;
    }

    public void setMondayYN(Boolean mondayYN) {
        this.mondayYN = mondayYN;
    }

    public String getMondayStartTime() {
        return mondayStartTime;
    }

    public void setMondayStartTime(String mondayStartTime) {
        this.mondayStartTime = mondayStartTime;
    }

    public String getMondayEndTime() {
        return mondayEndTime;
    }

    public void setMondayEndTime(String mondayEndTime) {
        this.mondayEndTime = mondayEndTime;
    }

    public Boolean getTuesdayYN() {
        return tuesdayYN;
    }

    public void setTuesdayYN(Boolean tuesdayYN) {
        this.tuesdayYN = tuesdayYN;
    }

    public String getTuesdayStartTime() {
        return tuesdayStartTime;
    }

    public void setTuesdayStartTime(String tuesdayStartTime) {
        this.tuesdayStartTime = tuesdayStartTime;
    }

    public String getTuesdayEndTime() {
        return tuesdayEndTime;
    }

    public void setTuesdayEndTime(String tuesdayEndTime) {
        this.tuesdayEndTime = tuesdayEndTime;
    }

    public Boolean getWednesdayYN() {
        return wednesdayYN;
    }

    public void setWednesdayYN(Boolean wednesdayYN) {
        this.wednesdayYN = wednesdayYN;
    }

    public String getWednesdayStartTime() {
        return wednesdayStartTime;
    }

    public void setWednesdayStartTime(String wednesdayStartTime) {
        this.wednesdayStartTime = wednesdayStartTime;
    }

    public String getWednesdayEndTime() {
        return wednesdayEndTime;
    }

    public void setWednesdayEndTime(String wednesdayEndTime) {
        this.wednesdayEndTime = wednesdayEndTime;
    }

    public Boolean getThursdayYN() {
        return thursdayYN;
    }

    public void setThursdayYN(Boolean thursdayYN) {
        this.thursdayYN = thursdayYN;
    }

    public String getThursdayStartTime() {
        return thursdayStartTime;
    }

    public void setThursdayStartTime(String thursdayStartTime) {
        this.thursdayStartTime = thursdayStartTime;
    }

    public String getThursdayEndTime() {
        return thursdayEndTime;
    }

    public void setThursdayEndTime(String thursdayEndTime) {
        this.thursdayEndTime = thursdayEndTime;
    }

    public Boolean getFridayYN() {
        return fridayYN;
    }

    public void setFridayYN(Boolean fridayYN) {
        this.fridayYN = fridayYN;
    }

    public String getFridayStartTime() {
        return fridayStartTime;
    }

    public void setFridayStartTime(String fridayStartTime) {
        this.fridayStartTime = fridayStartTime;
    }

    public String getFridayEndTime() {
        return fridayEndTime;
    }

    public void setFridayEndTime(String fridayEndTime) {
        this.fridayEndTime = fridayEndTime;
    }

    public Boolean getSaturdayYN() {
        return saturdayYN;
    }

    public void setSaturdayYN(Boolean saturdayYN) {
        this.saturdayYN = saturdayYN;
    }

    public String getSaturdayStartTime() {
        return saturdayStartTime;
    }

    public void setSaturdayStartTime(String saturdayStartTime) {
        this.saturdayStartTime = saturdayStartTime;
    }

    public String getSaturdayEndTime() {
        return saturdayEndTime;
    }

    public void setSaturdayEndTime(String saturdayEndTime) {
        this.saturdayEndTime = saturdayEndTime;
    }

    public Boolean getSundayYN() {
        return sundayYN;
    }

    public void setSundayYN(Boolean sundayYN) {
        this.sundayYN = sundayYN;
    }

    public String getSundayStartTime() {
        return sundayStartTime;
    }

    public void setSundayStartTime(String sundayStartTime) {
        this.sundayStartTime = sundayStartTime;
    }

    public String getSundayEndTime() {
        return sundayEndTime;
    }

    public void setSundayEndTime(String sundayEndTime) {
        this.sundayEndTime = sundayEndTime;
    }
}
