package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseworkTest {

    @Test
    void f1ForDemoDataIs75() {
        assertEquals(75.0, BookingCalculator.factor1(15, 20), 0.001);
    }

    @Test
    void f2ForDemoSlotIs75() {
        assertEquals(75.0, BookingCalculator.factor2(90.0), 0.001);
    }

    @Test
    void integralIndexForDemoDataIs7125() {
        assertEquals(71.25, BookingCalculator.calculate(75.0, 75.0, 60.0), 0.001);
    }

    @Test
    void calculatorRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class,
                () -> BookingCalculator.calculate(101.0, 50.0, 50.0));
    }

    @Test
    void createdToConfirmedAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.CREATED, BookingState.CONFIRMED));
    }

    @Test
    void confirmedToActiveAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.CONFIRMED, BookingState.ACTIVE));
    }

    @Test
    void activeToCompletedAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.ACTIVE, BookingState.COMPLETED));
    }

    @Test
    void createdToCompletedRejected() {
        assertFalse(BookingService.isAllowedTransition(BookingState.CREATED, BookingState.COMPLETED));
    }

    @Test
    void facilityValidationWorks() {
        SportsFacility invalid = new SportsFacility(null, "", "Зал №1", "Ігровий зал", 20);
        assertThrows(IllegalArgumentException.class,
                () -> SportsFacilityService.validate(invalid));
    }

    @Test
    void timeSlotValidationWorks() {
        TimeSlot invalid = new TimeSlot(null, 1L, "2026-10-27", "10:00", "11:30", -30.0, true);
        assertThrows(IllegalArgumentException.class,
                () -> TimeSlotService.validate(invalid));
    }
}