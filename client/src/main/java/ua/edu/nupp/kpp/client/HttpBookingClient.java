package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpBookingClient {

    private static final String BASE = "http://localhost:8080/sports-booking-server";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public HttpBookingClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public List<SportsFacility> getFacilities() throws IOException, InterruptedException {
        return Arrays.asList(mapper.readValue(get("/facilities"), SportsFacility[].class));
    }

    public void addFacility(SportsFacility f) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("code", f.getCode());
        params.put("name", f.getName());
        params.put("facilityType", f.getFacilityType());
        params.put("maxCapacity", String.valueOf(f.getMaxCapacity()));
        post("/facilities", params);
    }

    public void updateFacility(SportsFacility f) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", String.valueOf(f.getId()));
        params.put("code", f.getCode());
        params.put("name", f.getName());
        params.put("facilityType", f.getFacilityType());
        params.put("maxCapacity", String.valueOf(f.getMaxCapacity()));
        post("/facilities", params);
    }

    public void deleteFacility(long id) throws IOException, InterruptedException {
        post("/facilities", Map.of("action", "delete", "id", String.valueOf(id)));
    }

    public List<User> getUsers() throws IOException, InterruptedException {
        return Arrays.asList(mapper.readValue(get("/users"), User[].class));
    }

    public void addUser(User u) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("fullName", u.getFullName());
        params.put("email", u.getEmail());
        params.put("phone", u.getPhone());
        post("/users", params);
    }

    public void updateUser(User u) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", String.valueOf(u.getId()));
        params.put("fullName", u.getFullName());
        params.put("email", u.getEmail());
        params.put("phone", u.getPhone());
        post("/users", params);
    }

    public void deleteUser(long id) throws IOException, InterruptedException {
        post("/users", Map.of("action", "delete", "id", String.valueOf(id)));
    }

    public List<TimeSlot> getTimeSlots() throws IOException, InterruptedException {
        return Arrays.asList(mapper.readValue(get("/slots"), TimeSlot[].class));
    }

    public void addTimeSlot(TimeSlot s) throws IOException, InterruptedException {
        Map<String, String> params = slotParams("add", s);
        post("/slots", params);
    }

    public void updateTimeSlot(TimeSlot s) throws IOException, InterruptedException {
        Map<String, String> params = slotParams("update", s);
        params.put("id", String.valueOf(s.getId()));
        post("/slots", params);
    }

    public void deleteTimeSlot(long id) throws IOException, InterruptedException {
        post("/slots", Map.of("action", "delete", "id", String.valueOf(id)));
    }

    private Map<String, String> slotParams(String action, TimeSlot s) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", action);
        params.put("facilityId", String.valueOf(s.getFacilityId()));
        params.put("slotDate", s.getSlotDate());
        params.put("startTime", s.getStartTime());
        params.put("endTime", s.getEndTime());
        params.put("durationMinutes", String.valueOf(s.getDurationMinutes()));
        params.put("available", String.valueOf(s.isAvailable()));
        return params;
    }

    public List<Booking> getBookings() throws IOException, InterruptedException {
        return Arrays.asList(mapper.readValue(get("/bookings"), Booking[].class));
    }

    public void addBooking(long userId, long slotId, int participantsCount, double equipmentScore)
            throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "add");
        params.put("userId", String.valueOf(userId));
        params.put("slotId", String.valueOf(slotId));
        params.put("participantsCount", String.valueOf(participantsCount));
        params.put("equipmentScore", String.valueOf(equipmentScore));
        post("/bookings", params);
    }

    public void updateBooking(long id, long userId, long slotId, int participantsCount, double equipmentScore)
            throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", String.valueOf(id));
        params.put("userId", String.valueOf(userId));
        params.put("slotId", String.valueOf(slotId));
        params.put("participantsCount", String.valueOf(participantsCount));
        params.put("equipmentScore", String.valueOf(equipmentScore));
        post("/bookings", params);
    }

    public void changeBookingState(long id, BookingState state) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "transition");
        params.put("id", String.valueOf(id));
        params.put("state", state.name());
        post("/bookings", params);
    }

    public void deleteBooking(long id) throws IOException, InterruptedException {
        post("/bookings", Map.of("action", "delete", "id", String.valueOf(id)));
    }

    private String get(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .GET()
                .build();
        return send(request);
    }

    private String post(String endpoint, Map<String, String> params) throws IOException, InterruptedException {
        StringBuilder body = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                body.append("&");
            }
            body.append(enc(entry.getKey())).append("=").append(enc(entry.getValue()));
            first = false;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    private String send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 400) {
            String errorText = extractErrorMessage(response.body(), response.statusCode());
            throw new IOException(errorText);
        }
        return response.body();
    }

    private String extractErrorMessage(String body, int status) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (Exception ignored) {
        }
        return "Помилка запиту: HTTP " + status + "\n" + body;
    }

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}