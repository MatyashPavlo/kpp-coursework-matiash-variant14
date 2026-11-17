package ua.edu.nupp.kpp.server;

public class SportsFacility {
    private Long id;
    private String code;
    private String name;
    private String facilityType;
    private int maxCapacity;

    public SportsFacility() {
    }

    public SportsFacility(Long id, String code, String name, String facilityType, int maxCapacity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.facilityType = facilityType;
        this.maxCapacity = maxCapacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}