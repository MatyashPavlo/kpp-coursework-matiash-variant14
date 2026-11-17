package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public void add(Booking booking) throws SQLException {
        String sql = "INSERT INTO booking(user_id, slot_id, participants_count, equipment_score, " +
                "state, f1, f2, f3, integral_index) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, booking);
            p.executeUpdate();
        }
    }

    public List<Booking> getAll() throws SQLException {
        String sql = "SELECT id, user_id, slot_id, participants_count, equipment_score, " +
                "state, f1, f2, f3, integral_index FROM booking ORDER BY id";
        List<Booking> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Booking getById(long id) throws SQLException {
        String sql = "SELECT id, user_id, slot_id, participants_count, equipment_score, " +
                "state, f1, f2, f3, integral_index FROM booking WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void update(Booking booking) throws SQLException {
        String sql = "UPDATE booking SET user_id = ?, slot_id = ?, participants_count = ?, equipment_score = ?, " +
                "state = ?, f1 = ?, f2 = ?, f3 = ?, integral_index = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, booking);
            p.setLong(10, booking.getId());
            p.executeUpdate();
        }
    }

    public void updateState(long id, BookingState state) throws SQLException {
        String sql = "UPDATE booking SET state = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, state.name());
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM booking WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Booking b) throws SQLException {
        p.setLong(1, b.getUserId());
        p.setLong(2, b.getSlotId());
        p.setInt(3, b.getParticipantsCount());
        p.setDouble(4, b.getEquipmentScore());
        p.setString(5, b.getState().name());
        p.setDouble(6, b.getF1());
        p.setDouble(7, b.getF2());
        p.setDouble(8, b.getF3());
        p.setDouble(9, b.getIntegralIndex());
    }

    private Booking map(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("slot_id"),
                rs.getInt("participants_count"),
                rs.getDouble("equipment_score"),
                BookingState.valueOf(rs.getString("state")),
                rs.getDouble("f1"),
                rs.getDouble("f2"),
                rs.getDouble("f3"),
                rs.getDouble("integral_index")
        );
    }
}