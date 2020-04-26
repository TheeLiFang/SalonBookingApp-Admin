package my.salonapp.salonbookingapp;

public class Client implements Comparable<Client>  {

    private int clientId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String clientAllergicRemark;
    private String clientRemark;

    public Client() {
    }

    public Client(int clientId, String clientName) {
        this.clientId = clientId;
        this.clientName = clientName;
    }

    public Client(int clientId, String clientName,
                  String clientEmail, String clientPhone,
                  String clientAllergicRemark, String clientRemark) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.clientAllergicRemark = clientAllergicRemark;
        this.clientRemark = clientRemark;
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

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientAllergicRemark() {
        return clientAllergicRemark;
    }

    public void setClientAllergicRemark(String clientAllergicRemark) {
        this.clientAllergicRemark = clientAllergicRemark;
    }

    public String getClientRemark() {
        return clientRemark;
    }

    public void setClientRemark(String clientRemark) {
        this.clientRemark = clientRemark;
    }

    @Override
    public String toString() {
        return clientName;
    }

    @Override
    public int compareTo(Client o) {
        return this.getClientName().compareTo(o.getClientName());
    }
}
