package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class SportsFacilityService {
    private static final Logger LOG = Logger.getLogger(SportsFacilityService.class.getName());
    private final SportsFacilityDAO dao = new SportsFacilityDAO();

    public List<SportsFacility> getAll() throws SQLException {
        return dao.getAll();
    }

    public SportsFacility getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(SportsFacility facility) throws SQLException {
        validate(facility);
        dao.add(facility);
        LOG.info("Додано спортивний об'єкт");
    }

    public void update(SportsFacility facility) throws SQLException {
        validate(facility);
        dao.update(facility);
        LOG.info("Оновлено спортивний об'єкт");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено спортивний об'єкт");
    }

    public static void validate(SportsFacility f) {
        if (f == null || blank(f.getCode()) || blank(f.getName()) || blank(f.getFacilityType())) {
            throw new IllegalArgumentException("Усі текстові поля спортивного об'єкта є обов'язковими");
        }
        if (f.getMaxCapacity() <= 0) {
            throw new IllegalArgumentException("Максимальна місткість має бути додатною");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}