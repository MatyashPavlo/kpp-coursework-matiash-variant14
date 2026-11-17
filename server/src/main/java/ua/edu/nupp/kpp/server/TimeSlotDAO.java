package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {

    public void add(TimeSlot slot) throws SQLException {
        String sql = "INSERT INTO time_slot(facility_id, slot_date, start_time, end_time, duration_minutes, is_available) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, slot);
            p.executeUpdate();
        }
    }

    public List<TimeSlot> getAll() throws SQLException {
        String sql = "SELECT id, facility_id, slot_date, start_time, end_time, duration_minutes, is_available " +
                "FROM time_slot ORDER BY id";
        List<TimeSlot> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public TimeSlot getById(long id) throws SQLException {
        String sql = "SELECT id, facility_id, slot_date, start_time, end_time, duration_minutes, is_available " +
                "FROM time_slot WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void update(TimeSlot slot) throws SQLException {
        String sql = "UPDATE time_slot SET facility_id = ?, slot_date = ?, start_time = ?, end_time = ?, " +
                "duration_minutes = ?, is_available = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, slot);
            p.setLong(7, slot.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM time_slot WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, TimeSlot s) throws SQLException {
        p.setLong(1, s.getFacilityId());
        p.setString(2, s.getSlotDate());
        p.setString(3, s.getStartTime());
        p.setString(4, s.getEndTime());
        p.setDouble(5, s.getDurationMinutes());
        p.setBoolean(6, s.isAvailable());
    }

    private TimeSlot map(ResultSet rs) throws SQLException {
        return new TimeSlot(
                rs.getLong("id"),
                rs.getLong("facility_id"),
                rs.getString("slot_date"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                rs.getDouble("duration_minutes"),
                rs.getBoolean("is_available")
        );
    }
}