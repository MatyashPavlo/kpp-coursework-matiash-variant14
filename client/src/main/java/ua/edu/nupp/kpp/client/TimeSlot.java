package ua.edu.nupp.kpp.client;

public class TimeSlot {
    private Long id;
    private long facilityId;
    private String slotDate;
    private String startTime;
    private String endTime;
    private double durationMinutes;
    private boolean isAvailable;

    public TimeSlot() {
    }

    public TimeSlot(Long id, long facilityId, String slotDate, String startTime,
                    String endTime, double durationMinutes, boolean isAvailable) {
        this.id = id;
        this.facilityId = facilityId;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.isAvailable = isAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public String getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(String slotDate) {
        this.slotDate = slotDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}