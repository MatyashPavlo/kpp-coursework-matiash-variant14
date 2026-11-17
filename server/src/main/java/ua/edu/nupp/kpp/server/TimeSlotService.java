package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TimeSlotService {
    private static final Logger LOG = Logger.getLogger(TimeSlotService.class.getName());
    private final TimeSlotDAO dao = new TimeSlotDAO();
    private final SportsFacilityDAO facilityDAO = new SportsFacilityDAO();

    public List<TimeSlot> getAll() throws SQLException {
        return dao.getAll();
    }

    public TimeSlot getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(TimeSlot slot) throws SQLException {
        validate(slot);
        requireFacility(slot.getFacilityId());
        dao.add(slot);
        LOG.info("Додано часовий слот");
    }

    public void update(TimeSlot slot) throws SQLException {
        validate(slot);
        requireFacility(slot.getFacilityId());
        dao.update(slot);
        LOG.info("Оновлено часовий слот");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено часовий слот");
    }

    public static void validate(TimeSlot s) {
        if (s == null) {
            throw new IllegalArgumentException("Часовий слот не може бути null");
        }
        if (s.getFacilityId() <= 0) {
            throw new IllegalArgumentException("Некоректний facilityId");
        }
        if (blank(s.getSlotDate()) || blank(s.getStartTime()) || blank(s.getEndTime())) {
            throw new IllegalArgumentException("Дата, час початку та час завершення слоту є обов'язковими");
        }
        if (s.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Тривалість слоту має бути більшою за 0");
        }
    }

    private void requireFacility(long facilityId) throws SQLException {
        SportsFacility facility = facilityDAO.getById(facilityId);
        if (facility == null) {
            throw new IllegalArgumentException("Спортивний об'єкт не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}