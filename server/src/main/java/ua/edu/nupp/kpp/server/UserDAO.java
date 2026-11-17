package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void add(User user) throws SQLException {
        String sql = "INSERT INTO users(full_name, email, phone) VALUES(?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, user.getFullName());
            p.setString(2, user.getEmail());
            p.setString(3, user.getPhone());
            p.executeUpdate();
        }
    }

    public List<User> getAll() throws SQLException {
        String sql = "SELECT id, full_name, email, phone FROM users ORDER BY id";
        List<User> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public User getById(long id) throws SQLException {
        String sql = "SELECT id, full_name, email, phone FROM users WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, user.getFullName());
            p.setString(2, user.getEmail());
            p.setString(3, user.getPhone());
            p.setLong(4, user.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone")
        );
    }
}