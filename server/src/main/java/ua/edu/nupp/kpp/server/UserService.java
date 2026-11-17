package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class.getName());
    private final UserDAO dao = new UserDAO();

    public List<User> getAll() throws SQLException {
        return dao.getAll();
    }

    public User getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(User user) throws SQLException {
        validate(user);
        dao.add(user);
        LOG.info("Додано користувача");
    }

    public void update(User user) throws SQLException {
        validate(user);
        dao.update(user);
        LOG.info("Оновлено користувача");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено користувача");
    }

    public static void validate(User u) {
        if (u == null || blank(u.getFullName()) || blank(u.getEmail()) || blank(u.getPhone())) {
            throw new IllegalArgumentException("ПІБ, email та телефон користувача є обов'язковими");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}