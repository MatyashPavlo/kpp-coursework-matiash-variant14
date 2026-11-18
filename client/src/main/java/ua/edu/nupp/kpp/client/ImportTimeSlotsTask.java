package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportTimeSlotsTask extends Task<Integer> {

    private static final Logger LOG = Logger.getLogger(ImportTimeSlotsTask.class.getName());
    private final Path file;
    private final HttpBookingClient api;

    public ImportTimeSlotsTask(Path file, HttpBookingClient api) {
        this.file = file;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        LOG.info("Початок CSV-імпорту: " + file.getFileName());
        updateMessage("Читання CSV-файлу...");

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        String header = "facility_id;slot_date;start_time;end_time;duration_minutes;is_available";

        if (lines.isEmpty() || !header.equals(lines.get(0).replace("\uFEFF", "").strip())) {
            throw new IllegalArgumentException("Некоректний заголовок CSV");
        }

        int total = (int) lines.stream().skip(1).filter(s -> !s.isBlank()).count();
        int processed = 0;
        int imported = 0;
        updateProgress(0, Math.max(1, total));

        for (int i = 1; i < lines.size(); i++) {
            if (isCancelled()) {
                break;
            }
            if (lines.get(i).isBlank()) {
                continue;
            }

            String[] p = lines.get(i).split(";", -1);
            if (p.length != 6) {
                LOG.warning("Пропущено рядок " + (i + 1) + ": некоректна кількість стовпців");
            } else {
                try {
                    TimeSlot slot = new TimeSlot(
                            null,
                            Long.parseLong(p[0].strip()),
                            p[1].strip(),
                            p[2].strip(),
                            p[3].strip(),
                            Double.parseDouble(p[4].strip()),
                            Boolean.parseBoolean(p[5].strip())
                    );

                    if (!Double.isFinite(slot.getDurationMinutes()) || slot.getDurationMinutes() <= 0) {
                        LOG.warning("Некоректна тривалість у рядку " + (i + 1));
                        throw new IllegalArgumentException("Некоректне числове значення у рядку " + (i + 1));
                    }

                    try {
                        api.addTimeSlot(slot);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Помилка HTTP під час імпорту рядка " + (i + 1), e);
                        throw e;
                    }
                    imported++;
                } catch (NumberFormatException e) {
                    LOG.warning("Некоректне число у рядку " + (i + 1));
                    throw new IllegalArgumentException("Некоректне число у рядку " + (i + 1), e);
                }
            }

            updateProgress(++processed, Math.max(1, total));
            updateMessage("Опрацьовано " + processed + " із " + total + "; імпортовано " + imported);
        }

        if (total == 0) {
            updateProgress(1, 1);
            updateMessage("Немає рядків для імпорту");
        }

        LOG.info("Імпортовано: " + imported);
        return imported;
    }
}