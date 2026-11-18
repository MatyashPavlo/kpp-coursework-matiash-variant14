# Курсова робота: Інформаційна система бронювання спортивних об’єктів (Варіант №14)

Клієнт-серверна інформаційна система для обліку спортивних об’єктів, користувачів, розкладу доступних часових слотів та управління життєвим циклом бронювань із розрахунком інтегрального показника завантаженості.

## Технології
* **Java 21**, **Apache Maven**
* **Серверна частина:** Servlet API 4.0.1 (`javax.servlet`), Apache Tomcat 9.x, шари Service та DAO, JDBC (`mysql-connector-j 9.7.0`), MySQL, Jackson Databind `2.18.2`, `java.util.logging.Logger`
* **Клієнтська частина:** JavaFX `21.0.2`, `java.net.http.HttpClient`, фоновий імпорт через `JavaFX Task`
* **Тестування:** JUnit 5 (`junit-jupiter 5.11.4`, 10 модульних тестів)

## Структура репозиторію
```text
kpp-coursework-matiash-variant14/
├─ .gitignore                # виключення target/, .idea/, *.war, *.log
├─ README.md                 # документація та інструкція запуску
├─ database.sql              # SQL-скрипт створення БД sports_booking та початкових даних
├─ server/                   # серверний Maven-проєкт (packaging war)
│  ├─ pom.xml
│  ├─ src/main/java/ua/edu/nupp/kpp/server/
│  ├─ src/main/webapp/WEB-INF/web.xml
│  └─ src/test/java/ua/edu/nupp/kpp/server/CourseworkTest.java
└─ client/                   # клієнтський Maven-проєкт (JavaFX)
   ├─ pom.xml
   ├─ sample-data/time_slots.csv
   └─ src/main/java/ua/edu/nupp/kpp/client/
```

## Запуск системи

1. **Підготовка бази даних MySQL:**
   Запустіть MySQL (наприклад, через XAMPP) та імпортуйте файл `database.sql` у `phpMyAdmin`. Буде створено базу даних `sports_booking` з таблицями `sports_facility`, `users`, `time_slot`, `booking`.
2. **Тестування та збирання сервера:**
   ```powershell
   cd server
   mvn clean test
   mvn clean package
   ```
3. **Розгортання на Apache Tomcat 9.x:**
   Скопіюйте створений архів `server/target/sports-booking-server.war` у каталог `webapps` сервера Tomcat 9.x та запустіть Tomcat.
   Перевірте роботу API у браузері: `http://localhost:8080/sports-booking-server/facilities`
4. **Запуск JavaFX-клієнта:**
   ```powershell
   cd client
   mvn javafx:run
   ```

## Фоновий CSV-імпорт
* **Файл:** `client/sample-data/time_slots.csv` (кодування UTF-8, роздільник `;`).
* **Формат заголовка:** `facility_id;slot_date;start_time;end_time;duration_minutes;is_available`
* На вкладці «Часові слоти» натисніть кнопку **«Імпортувати CSV»** — читання файлу та передавання записів через HTTP виконуються у фоновому потоці (`ImportTimeSlotsTask`) із відображенням прогресу на `ProgressBar`.
* *Примітка:* повторний запуск імпорту тих самих рядків без попереднього відновлення БД може створити дублікати записів.