import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager {
    private static final String url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/petmonitor");
    private static final String user = System.getenv().getOrDefault("DB_USER", "postgres");
    private static final String pass = System.getenv().getOrDefault("DB_PASS", "postgres");

    public DatabaseManager() {
        try {
            init();
        } catch (SQLException e) {
            System.err.println("DB Init Failed: " + e.getMessage());
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    private void init() throws SQLException {
        try (Connection conn = connect(); Statement s = conn.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS pet_stats (" +
                    "id INT PRIMARY KEY DEFAULT 1, hr INT, rr INT, act INT, scratch INT, " +
                    "tgt_act INT, opt_wt DOUBLE PRECISION, tgt_food DOUBLE PRECISION, " +
                    "last_updated DATE, CHECK (id = 1))");

            try {
                s.execute("ALTER TABLE pet_stats ADD COLUMN IF NOT EXISTS last_updated DATE");
            } catch (SQLException ignored) {}

            s.execute("INSERT INTO pet_stats (id, hr, rr, act, scratch, tgt_act, opt_wt, tgt_food, last_updated) " +
                    "VALUES (1, 0, 0, 0, 0, 60, 6.5, 2.0, CURRENT_DATE) ON CONFLICT (id) DO NOTHING");

            s.execute("CREATE TABLE IF NOT EXISTS meals (id SERIAL PRIMARY KEY, date DATE, name VARCHAR(255), amount DOUBLE PRECISION)");
            s.execute("CREATE TABLE IF NOT EXISTS weights (id SERIAL PRIMARY KEY, date DATE, weight DOUBLE PRECISION)");
            s.execute("CREATE TABLE IF NOT EXISTS grooming_logs (id SERIAL PRIMARY KEY, date DATE, description TEXT)");
            s.execute("CREATE TABLE IF NOT EXISTS routines (name VARCHAR(255) PRIMARY KEY, last_done DATE, interval_days INT)");
            s.execute("CREATE TABLE IF NOT EXISTS daily_activity (date DATE PRIMARY KEY, minutes INT)");
        }
    }

    public void loadProfile(PetProfile p) {
        try (Connection conn = connect()) {
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM pet_stats WHERE id = 1")) {
                if (rs.next()) {
                    int hr = rs.getInt("hr");
                    int rr = rs.getInt("rr");
                    int act = rs.getInt("act");
                    int scratch = rs.getInt("scratch");

                    Date lastDate = rs.getDate("last_updated");
                    LocalDate last = lastDate != null ? lastDate.toLocalDate() : LocalDate.MIN;

                    if (last.isBefore(LocalDate.now())) {
                        hr = 0; rr = 0; act = 0;
                        saveStatsInternal(conn, 0, 0, 0, scratch);
                    }

                    p.updateStats(hr, rr, act, scratch);
                    p.setTargets(rs.getInt("tgt_act"), rs.getDouble("opt_wt"), rs.getDouble("tgt_food"));
                }
            }

            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM meals ORDER BY date")) {
                while (rs.next()) p.addMeal(rs.getDate("date").toLocalDate(), rs.getString("name"), rs.getDouble("amount"));
            }

            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM weights ORDER BY date")) {
                while (rs.next()) p.addWeight(rs.getDate("date").toLocalDate(), rs.getDouble("weight"));
            }

            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM grooming_logs ORDER BY date")) {
                while (rs.next()) p.logGrooming(rs.getDate("date").toLocalDate(), rs.getString("description"));
            }

            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM routines")) {
                while (rs.next()) {
                    Routine r = getRoutine(p, rs.getString("name"));
                    if (r != null) {
                        r.setLast(rs.getDate("last_done").toLocalDate());
                        r.setFreq(rs.getInt("interval_days"));
                    }
                }
            }

             try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM daily_activity")) {
                while (rs.next()) p.addDailyActivity(rs.getDate("date").toLocalDate(), rs.getInt("minutes"));
            }

        } catch (SQLException e) {
            System.err.println("Load failed: " + e.getMessage());
        }
    }

    private Routine getRoutine(PetProfile p, String name) {
        if (p.getTeeth().getName().equals(name)) return p.getTeeth();
        if (p.getEars().getName().equals(name)) return p.getEars();
        if (p.getGroom().getName().equals(name)) return p.getGroom();
        if (p.getNexgard().getName().equals(name)) return p.getNexgard();
        if (p.getShower().getName().equals(name)) return p.getShower();
        return null;
    }

    public void saveStats(PetProfile p) {
        try (Connection conn = connect()) {
            saveStatsInternal(conn, p.getHr(), p.getRr(), p.getAct(), p.getScratch());
            saveActivity(conn, LocalDate.now(), p.getAct());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveStatsInternal(Connection conn, int hr, int rr, int act, int scratch) throws SQLException {
         try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE pet_stats SET hr=?, rr=?, act=?, scratch=?, last_updated=? WHERE id=1")) {
            ps.setInt(1, hr);
            ps.setInt(2, rr);
            ps.setInt(3, act);
            ps.setInt(4, scratch);
            ps.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
        }
    }

    private void saveActivity(Connection conn, LocalDate date, int minutes) throws SQLException {
         try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO daily_activity (date, minutes) VALUES (?, ?) " +
                "ON CONFLICT (date) DO UPDATE SET minutes=EXCLUDED.minutes")) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setInt(2, minutes);
            ps.executeUpdate();
        }
    }

    public void saveSettings(PetProfile p) {
        try (Connection conn = connect()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pet_stats SET tgt_act=?, opt_wt=?, tgt_food=? WHERE id=1")) {
                ps.setInt(1, p.getTgtAct());
                ps.setDouble(2, p.getOptWt());
                ps.setDouble(3, p.getTgtFood());
                ps.executeUpdate();
            }
            updateRoutine(p.getTeeth());
            updateRoutine(p.getEars());
            updateRoutine(p.getGroom());
            updateRoutine(p.getNexgard());
            updateRoutine(p.getShower());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMeal(LocalDate date, String name, double amount) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO meals (date, name, amount) VALUES (?, ?, ?)")) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setString(2, name);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addWeight(LocalDate date, double weight) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO weights (date, weight) VALUES (?, ?)")) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setDouble(2, weight);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addGroomingLog(LocalDate date, String description) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO grooming_logs (date, description) VALUES (?, ?)")) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setString(2, description);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRoutine(Routine r) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO routines (name, last_done, interval_days) VALUES (?, ?, ?) " +
                        "ON CONFLICT (name) DO UPDATE SET last_done=EXCLUDED.last_done, interval_days=EXCLUDED.interval_days")) {
            ps.setString(1, r.getName());
            ps.setDate(2, java.sql.Date.valueOf(r.getLast()));
            ps.setInt(3, r.getFreq());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
