package my.salonapp.salonbookingapp;

public class SelectedService {

    private int serviceId;
    private String serviceName;
    private Float servicePrice;
    private int serviceDuration;

    public SelectedService() {
    }

    public SelectedService(int serviceId, String serviceName, Float servicePrice, int serviceDuration) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceDuration = serviceDuration;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
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
}
