package ua.edu.nupp.kpp.server;

public class BookingCalculator {

    private static final double STANDARD_MAX_DURATION = 120.0;

    public static double factor1(int participantsCount, int maxCapacity) {
        if (participantsCount <= 0 || maxCapacity <= 0) {
            throw new IllegalArgumentException("Некоректні дані для розрахунку F1");
        }
        double value = ((double) participantsCount / maxCapacity) * 100.0;
        double clamped = Math.max(0.0, Math.min(100.0, value));
        return Math.round(clamped * 100.0) / 100.0;
    }

    public static double factor2(double durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Некоректна тривалість для розрахунку F2");
        }
        double value = (durationMinutes / STANDARD_MAX_DURATION) * 100.0;
        double clamped = Math.max(0.0, Math.min(100.0, value));
        return Math.round(clamped * 100.0) / 100.0;
    }

    public static double calculate(double f1, double f2, double f3) {
        check(f1);
        check(f2);
        check(f3);

        return Math.round((0.40 * f1 + 0.35 * f2 + 0.25 * f3) * 100.0) / 100.0;
    }

    private static void check(double value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Фактор має бути в діапазоні 0..100");
        }
    }
}