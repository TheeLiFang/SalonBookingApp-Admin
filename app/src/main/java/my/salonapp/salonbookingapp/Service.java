package my.salonapp.salonbookingapp;

public class Service implements Comparable<Service> {

    private int serviceId;
    private String serviceName;
    private int categoryId;
    private String categoryName;
    private Float servicePrice;
    private int serviceDuration;

    public Service() {
    }

    public Service(int serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public Service(int serviceId, String serviceName, int categoryId, String categoryName, Float servicePrice, int serviceDuration) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    @Override
    public String toString() {
        return serviceName;
    }

    @Override
    public int compareTo(Service o) {
        return this.getServiceName().compareTo(o.getServiceName());
    }
}
