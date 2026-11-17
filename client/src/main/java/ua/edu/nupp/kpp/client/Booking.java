package ua.edu.nupp.kpp.client;

public class Booking {
    private Long id;
    private long userId;
    private long slotId;
    private int participantsCount;
    private double equipmentScore;
    private BookingState state;
    private double f1;
    private double f2;
    private double f3;
    private double integralIndex;

    public Booking() {
    }

    public Booking(Long id, long userId, long slotId, int participantsCount,
                   double equipmentScore, BookingState state,
                   double f1, double f2, double f3, double integralIndex) {
        this.id = id;
        this.userId = userId;
        this.slotId = slotId;
        this.participantsCount = participantsCount;
        this.equipmentScore = equipmentScore;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.integralIndex = integralIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSlotId() {
        return slotId;
    }

    public void setSlotId(long slotId) {
        this.slotId = slotId;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public double getEquipmentScore() {
        return equipmentScore;
    }

    public void setEquipmentScore(double equipmentScore) {
        this.equipmentScore = equipmentScore;
    }

    public BookingState getState() {
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    public double getF1() {
        return f1;
    }

    public void setF1(double f1) {
        this.f1 = f1;
    }

    public double getF2() {
        return f2;
    }

    public void setF2(double f2) {
        this.f2 = f2;
    }

    public double getF3() {
        return f3;
    }

    public void setF3(double f3) {
        this.f3 = f3;
    }

    public double getIntegralIndex() {
        return integralIndex;
    }

    public void setIntegralIndex(double integralIndex) {
        this.integralIndex = integralIndex;
    }
}