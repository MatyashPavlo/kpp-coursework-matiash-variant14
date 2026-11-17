package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class BookingService {
    private static final Logger LOG = Logger.getLogger(BookingService.class.getName());
    private final BookingDAO dao = new BookingDAO();
    private final UserDAO userDAO = new UserDAO();
    private final TimeSlotDAO slotDAO = new TimeSlotDAO();
    private final SportsFacilityDAO facilityDAO = new SportsFacilityDAO();

    public List<Booking> getAll() throws SQLException {
        return dao.getAll();
    }

    public Booking getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(long userId, long slotId, int participantsCount, double equipmentScore) throws SQLException {
        Booking booking = buildBooking(null, userId, slotId, participantsCount, equipmentScore, BookingState.CREATED);
        dao.add(booking);
        LOG.info("Створено бронювання");
    }

    public void update(long id, long userId, long slotId, int participantsCount, double equipmentScore) throws SQLException {
        Booking old = dao.getById(id);
        if (old == null) {
            throw new IllegalArgumentException("Бронювання не знайдено");
        }
        Booking updated = buildBooking(id, userId, slotId, participantsCount, equipmentScore, old.getState());
        dao.update(updated);
        LOG.info("Оновлено бронювання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено бронювання");
    }

    public static boolean isAllowedTransition(BookingState from, BookingState to) {
        return (from == BookingState.CREATED && to == BookingState.CONFIRMED)
                || (from == BookingState.CONFIRMED && to == BookingState.ACTIVE)
                || (from == BookingState.ACTIVE && to == BookingState.COMPLETED);
    }

    public void changeState(long id, BookingState next) throws SQLException {
        Booking current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Бронювання не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан: " + current.getState() + " -> " + next);
    }

    private Booking buildBooking(Long id, long userId, long slotId,
                                 int participantsCount, double equipmentScore,
                                 BookingState state) throws SQLException {
        if (userId <= 0 || slotId <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор користувача або слоту");
        }
        if (participantsCount <= 0) {
            throw new IllegalArgumentException("Кількість учасників має бути додатною");
        }
        if (equipmentScore < 0 || equipmentScore > 100) {
            throw new IllegalArgumentException("Фактор F3 (обладнання) має бути в діапазоні 0..100");
        }

        User user = userDAO.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }

        TimeSlot slot = slotDAO.getById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Часовий слот не знайдено");
        }

        SportsFacility facility = facilityDAO.getById(slot.getFacilityId());
        if (facility == null) {
            throw new IllegalArgumentException("Спортивний об'єкт для слоту не знайдено");
        }

        double f1 = BookingCalculator.factor1(participantsCount, facility.getMaxCapacity());
        double f2 = BookingCalculator.factor2(slot.getDurationMinutes());
        double f3 = equipmentScore;
        double integralIndex = BookingCalculator.calculate(f1, f2, f3);

        return new Booking(id, userId, slotId, participantsCount, equipmentScore, state, f1, f2, f3, integralIndex);
    }
}