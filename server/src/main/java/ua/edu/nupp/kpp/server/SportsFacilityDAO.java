package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SportsFacilityDAO {

    public void add(SportsFacility facility) throws SQLException {
        String sql = "INSERT INTO sports_facility(code, name, facility_type, max_capacity) VALUES(?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, facility.getCode());
            p.setString(2, facility.getName());
            p.setString(3, facility.getFacilityType());
            p.setInt(4, facility.getMaxCapacity());
            p.executeUpdate();
        }
    }

    public List<SportsFacility> getAll() throws SQLException {
        String sql = "SELECT id, code, name, facility_type, max_capacity FROM sports_facility ORDER BY id";
        List<SportsFacility> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public SportsFacility getById(long id) throws SQLException {
        String sql = "SELECT id, code, name, facility_type, max_capacity FROM sports_facility WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void update(SportsFacility facility) throws SQLException {
        String sql = "UPDATE sports_facility SET code = ?, name = ?, facility_type = ?, max_capacity = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, facility.getCode());
            p.setString(2, facility.getName());
            p.setString(3, facility.getFacilityType());
            p.setInt(4, facility.getMaxCapacity());
            p.setLong(5, facility.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM sports_facility WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private SportsFacility map(ResultSet rs) throws SQLException {
        return new SportsFacility(
                rs.getLong("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("facility_type"),
                rs.getInt("max_capacity")
        );
    }
}