package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpBookingClient api = new HttpBookingClient();

    private final TableView<SportsFacility> facilityTable = new TableView<>();
    private final TableView<User> userTable = new TableView<>();
    private final TableView<TimeSlot> slotTable = new TableView<>();
    private final TableView<Booking> bookingTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Спортивні об'єкти", buildFacilitiesTab()),
                new Tab("Користувачі", buildUsersTab()),
                new Tab("Часові слоти", buildSlotsTab()),
                new Tab("Бронювання", buildBookingsTab())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));

        Scene scene = new Scene(tabs, 980, 620);
        stage.setTitle("Бронювання спортивних об'єктів (Клієнт)");
        stage.setScene(scene);
        stage.show();

        safe(this::refreshAll);
    }

    private BorderPane buildFacilitiesTab() {
        TableColumn<SportsFacility, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));

        TableColumn<SportsFacility, String> colCode = new TableColumn<>("Код");
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCode()));

        TableColumn<SportsFacility, String> colName = new TableColumn<>("Назва");
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colName.setPrefWidth(260);

        TableColumn<SportsFacility, String> colType = new TableColumn<>("Тип");
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFacilityType()));
        colType.setPrefWidth(160);

        TableColumn<SportsFacility, Number> colCap = new TableColumn<>("Місткість");
        colCap.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaxCapacity()));

        facilityTable.getColumns().setAll(colId, colCode, colName, colType, colCap);

        TextField tfCode = new TextField();
        TextField tfName = new TextField();
        TextField tfType = new TextField();
        TextField tfCap = new TextField();

        facilityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                tfCode.setText(sel.getCode());
                tfName.setText(sel.getName());
                tfType.setText(sel.getFacilityType());
                tfCap.setText(String.valueOf(sel.getMaxCapacity()));
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10, 0, 10, 0));
        form.addRow(0, new Label("Код:"), tfCode, new Label("Назва:"), tfName);
        form.addRow(1, new Label("Тип:"), tfType, new Label("Місткість:"), tfCap);

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addFacility(new SportsFacility(null, tfCode.getText(), tfName.getText(),
                    tfType.getText(), Integer.parseInt(tfCap.getText().trim())));
            refreshFacilities();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            SportsFacility sel = facilityTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.updateFacility(new SportsFacility(sel.getId(), tfCode.getText(), tfName.getText(),
                    tfType.getText(), Integer.parseInt(tfCap.getText().trim())));
            refreshFacilities();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            SportsFacility sel = facilityTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.deleteFacility(sel.getId());
            refreshFacilities();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> safe(this::refreshFacilities));

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh);
        VBox bottom = new VBox(8, form, buttons);
        bottom.setPadding(new Insets(10));

        BorderPane root = new BorderPane(facilityTable);
        root.setBottom(bottom);
        root.setPadding(new Insets(10));
        return root;
    }

    private BorderPane buildUsersTab() {
        TableColumn<User, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));

        TableColumn<User, String> colName = new TableColumn<>("ПІБ");
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        colName.setPrefWidth(260);

        TableColumn<User, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colEmail.setPrefWidth(200);

        TableColumn<User, String> colPhone = new TableColumn<>("Телефон");
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colPhone.setPrefWidth(160);

        userTable.getColumns().setAll(colId, colName, colEmail, colPhone);

        TextField tfName = new TextField();
        TextField tfEmail = new TextField();
        TextField tfPhone = new TextField();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                tfName.setText(sel.getFullName());
                tfEmail.setText(sel.getEmail());
                tfPhone.setText(sel.getPhone());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10, 0, 10, 0));
        form.addRow(0, new Label("ПІБ:"), tfName, new Label("Email:"), tfEmail, new Label("Телефон:"), tfPhone);

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addUser(new User(null, tfName.getText(), tfEmail.getText(), tfPhone.getText()));
            refreshUsers();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            User sel = userTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.updateUser(new User(sel.getId(), tfName.getText(), tfEmail.getText(), tfPhone.getText()));
            refreshUsers();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            User sel = userTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.deleteUser(sel.getId());
            refreshUsers();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> safe(this::refreshUsers));

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh);
        VBox bottom = new VBox(8, form, buttons);
        bottom.setPadding(new Insets(10));

        BorderPane root = new BorderPane(userTable);
        root.setBottom(bottom);
        root.setPadding(new Insets(10));
        return root;
    }

    private BorderPane buildSlotsTab() {
        TableColumn<TimeSlot, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));

        TableColumn<TimeSlot, Number> colFac = new TableColumn<>("Об'єкт (ID)");
        colFac.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getFacilityId()));

        TableColumn<TimeSlot, String> colDate = new TableColumn<>("Дата");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlotDate()));

        TableColumn<TimeSlot, String> colStart = new TableColumn<>("Початок");
        colStart.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartTime()));

        TableColumn<TimeSlot, String> colEnd = new TableColumn<>("Кінець");
        colEnd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndTime()));

        TableColumn<TimeSlot, Number> colDur = new TableColumn<>("Тривалість (хв)");
        colDur.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getDurationMinutes()));
        colDur.setPrefWidth(130);

        TableColumn<TimeSlot, Boolean> colAvail = new TableColumn<>("Доступний");
        colAvail.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isAvailable()));

        slotTable.getColumns().setAll(colId, colFac, colDate, colStart, colEnd, colDur, colAvail);

        TextField tfFacId = new TextField();
        TextField tfDate = new TextField();
        TextField tfStart = new TextField();
        TextField tfEnd = new TextField();
        TextField tfDur = new TextField();
        CheckBox cbAvail = new CheckBox("Доступний");
        cbAvail.setSelected(true);

        slotTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                tfFacId.setText(String.valueOf(sel.getFacilityId()));
                tfDate.setText(sel.getSlotDate());
                tfStart.setText(sel.getStartTime());
                tfEnd.setText(sel.getEndTime());
                tfDur.setText(String.valueOf(sel.getDurationMinutes()));
                cbAvail.setSelected(sel.isAvailable());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10, 0, 10, 0));
        form.addRow(0, new Label("ID об'єкта:"), tfFacId, new Label("Дата:"), tfDate, new Label("Тривалість (хв):"), tfDur);
        form.addRow(1, new Label("Початок:"), tfStart, new Label("Кінець:"), tfEnd, cbAvail);

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addTimeSlot(new TimeSlot(null,
                    Long.parseLong(tfFacId.getText().trim()),
                    tfDate.getText(), tfStart.getText(), tfEnd.getText(),
                    Double.parseDouble(tfDur.getText().trim()), cbAvail.isSelected()));
            refreshSlots();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            TimeSlot sel = slotTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.updateTimeSlot(new TimeSlot(sel.getId(),
                    Long.parseLong(tfFacId.getText().trim()),
                    tfDate.getText(), tfStart.getText(), tfEnd.getText(),
                    Double.parseDouble(tfDur.getText().trim()), cbAvail.isSelected()));
            refreshSlots();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            TimeSlot sel = slotTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.deleteTimeSlot(sel.getId());
            refreshSlots();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> safe(this::refreshSlots));

        Button btnTestCsv = new Button("Прочитати тестовий рядок CSV");
        btnTestCsv.setOnAction(e -> safe(() -> {
            TimeSlot csvSlot = readTestTimeSlot();
            api.addTimeSlot(csvSlot);
            refreshSlots();
        }));

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh, btnTestCsv);
        VBox bottom = new VBox(8, form, buttons);
        bottom.setPadding(new Insets(10));

        BorderPane root = new BorderPane(slotTable);
        root.setBottom(bottom);
        root.setPadding(new Insets(10));
        return root;
    }

    private BorderPane buildBookingsTab() {
        TableColumn<Booking, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));

        TableColumn<Booking, Number> colUser = new TableColumn<>("Користувач (ID)");
        colUser.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getUserId()));
        colUser.setPrefWidth(115);

        TableColumn<Booking, Number> colSlot = new TableColumn<>("Слот (ID)");
        colSlot.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getSlotId()));

        TableColumn<Booking, Number> colPart = new TableColumn<>("Учасники");
        colPart.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getParticipantsCount()));

        TableColumn<Booking, Number> colF1 = new TableColumn<>("F1");
        colF1.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getF1()));

        TableColumn<Booking, Number> colF2 = new TableColumn<>("F2");
        colF2.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getF2()));

        TableColumn<Booking, Number> colF3 = new TableColumn<>("F3");
        colF3.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getF3()));

        TableColumn<Booking, Number> colIndex = new TableColumn<>("Інтегральний показник");
        colIndex.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getIntegralIndex()));
        colIndex.setPrefWidth(155);

        TableColumn<Booking, String> colState = new TableColumn<>("Стан");
        colState.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getState().name()));
        colState.setPrefWidth(120);

        bookingTable.getColumns().setAll(colId, colUser, colSlot, colPart, colF1, colF2, colF3, colIndex, colState);

        TextField tfUserId = new TextField();
        TextField tfSlotId = new TextField();
        TextField tfPart = new TextField();
        TextField tfEqScore = new TextField();
        ComboBox<BookingState> cbState = new ComboBox<>(FXCollections.observableArrayList(BookingState.values()));
        cbState.setValue(BookingState.CONFIRMED);

        bookingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                tfUserId.setText(String.valueOf(sel.getUserId()));
                tfSlotId.setText(String.valueOf(sel.getSlotId()));
                tfPart.setText(String.valueOf(sel.getParticipantsCount()));
                tfEqScore.setText(String.valueOf(sel.getEquipmentScore()));
                cbState.setValue(sel.getState());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10, 0, 10, 0));
        form.addRow(0, new Label("Користувач (ID):"), tfUserId, new Label("Слот (ID):"), tfSlotId, new Label("Стан:"), cbState);
        form.addRow(1, new Label("Учасники:"), tfPart, new Label("Обладнання F3 (0..100):"), tfEqScore);

        Button btnAdd = new Button("Додати");
        btnAdd.setOnAction(e -> safe(() -> {
            api.addBooking(
                    Long.parseLong(tfUserId.getText().trim()),
                    Long.parseLong(tfSlotId.getText().trim()),
                    Integer.parseInt(tfPart.getText().trim()),
                    Double.parseDouble(tfEqScore.getText().trim())
            );
            refreshBookings();
        }));

        Button btnUpdate = new Button("Змінити");
        btnUpdate.setOnAction(e -> safe(() -> {
            Booking sel = bookingTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.updateBooking(
                    sel.getId(),
                    Long.parseLong(tfUserId.getText().trim()),
                    Long.parseLong(tfSlotId.getText().trim()),
                    Integer.parseInt(tfPart.getText().trim()),
                    Double.parseDouble(tfEqScore.getText().trim())
            );
            refreshBookings();
        }));

        Button btnState = new Button("Змінити стан");
        btnState.setOnAction(e -> safe(() -> {
            Booking sel = bookingTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.changeBookingState(sel.getId(), cbState.getValue());
            refreshBookings();
        }));

        Button btnDelete = new Button("Видалити");
        btnDelete.setOnAction(e -> safe(() -> {
            Booking sel = bookingTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Виберіть запис");
            api.deleteBooking(sel.getId());
            refreshBookings();
        }));

        Button btnRefresh = new Button("Оновити");
        btnRefresh.setOnAction(e -> safe(this::refreshBookings));

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnState, btnDelete, btnRefresh);
        VBox bottom = new VBox(8, form, buttons);
        bottom.setPadding(new Insets(10));

        BorderPane root = new BorderPane(bookingTable);
        root.setBottom(bottom);
        root.setPadding(new Insets(10));
        return root;
    }

    private TimeSlot readTestTimeSlot() throws IOException {
        Path file = Path.of("sample-data", "time_slots.csv");
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.size() < 2) {
            throw new IllegalArgumentException("CSV-файл не містить рядків даних");
        }
        String line = lines.get(1);
        String[] parts = line.split(";", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Некоректна кількість стовпців: " + parts.length);
        }
        long facilityId = Long.parseLong(parts[0].trim());
        String slotDate = parts[1].trim();
        String startTime = parts[2].trim();
        String endTime = parts[3].trim();
        double durationMinutes = Double.parseDouble(parts[4].trim());
        boolean available = Boolean.parseBoolean(parts[5].trim());

        return new TimeSlot(null, facilityId, slotDate, startTime, endTime, durationMinutes, available);
    }

    private void refreshAll() throws IOException, InterruptedException {
        refreshFacilities();
        refreshUsers();
        refreshSlots();
        refreshBookings();
    }

    private void refreshFacilities() throws IOException, InterruptedException {
        facilityTable.setItems(FXCollections.observableArrayList(api.getFacilities()));
    }

    private void refreshUsers() throws IOException, InterruptedException {
        userTable.setItems(FXCollections.observableArrayList(api.getUsers()));
    }

    private void refreshSlots() throws IOException, InterruptedException {
        slotTable.setItems(FXCollections.observableArrayList(api.getTimeSlots()));
    }

    private void refreshBookings() throws IOException, InterruptedException {
        bookingTable.setItems(FXCollections.observableArrayList(api.getBookings()));
    }

    @FunctionalInterface
    private interface CheckedAction {
        void run() throws Exception;
    }

    private void safe(CheckedAction action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage() != null ? e.getMessage() : e.toString());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}