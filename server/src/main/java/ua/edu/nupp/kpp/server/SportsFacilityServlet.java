package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class SportsFacilityServlet extends HttpServlet {
    private final SportsFacilityService service = new SportsFacilityService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                SportsFacility f = new SportsFacility(
                        null,
                        req.getParameter("code"),
                        req.getParameter("name"),
                        req.getParameter("facilityType"),
                        Integer.parseInt(req.getParameter("maxCapacity"))
                );
                service.add(f);
            } else if ("update".equals(action)) {
                SportsFacility f = new SportsFacility(
                        Long.parseLong(req.getParameter("id")),
                        req.getParameter("code"),
                        req.getParameter("name"),
                        req.getParameter("facilityType"),
                        Integer.parseInt(req.getParameter("maxCapacity"))
                );
                service.update(f);
            } else if ("delete".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                service.delete(id);
            } else {
                throw new IllegalArgumentException("Невідома дія: " + action);
            }
            writeJson(resp, Map.of("status", "ok"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        if (e instanceof IllegalArgumentException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        String msg = e.getMessage() != null ? e.getMessage() : "Помилка обробки запиту";
        mapper.writeValue(resp.getWriter(), Map.of("error", msg));
    }
}