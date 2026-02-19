import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.Clock;
import java.util.Map;
import java.util.TreeMap;

public class DashboardController {

    private PetProfile pet = new PetProfile("Jojo", Clock.systemDefaultZone());
    private DatabaseManager db;

    @FXML private Label hrLabel;
    @FXML private Label rrLabel;
    @FXML private ProgressBar actBar;
    @FXML private Label actLabel;
    @FXML private Label scratchLabel;
    @FXML private Label alert;

    @FXML private TextField hrInput;
    @FXML private TextField rrInput;
    @FXML private TextField actInput;
    @FXML private TextField scratchInput;

    @FXML private TextField foodName;
    @FXML private TextField foodAmt;
    @FXML private Label foodTotal;
    @FXML private ListView<String> foodList;

    @FXML private TextField wtInput;
    @FXML private ListView<String> wtList;

    @FXML private Label teethLabel;
    @FXML private Label earsLabel;
    @FXML private Label groomLabel;
    @FXML private Label nexLabel;
    @FXML private Label showerLabel;
    @FXML private ListView<String> groomList;

    @FXML private TextField tgtActInput;
    @FXML private TextField optWtInput;
    @FXML private TextField tgtFoodInput;
    @FXML private TextField teethIntInput;
    @FXML private TextField earsIntInput;
    @FXML private TextField groomIntInput;
    @FXML private TextField nexIntInput;
    @FXML private TextField showerIntInput;

    @FXML private ListView<String> actHistList;

    @FXML
    public void initialize() {
        db = new DatabaseManager();
        db.loadProfile(pet);
        init();
        checkDue();
    }

    private void init() {
        updateStats();
        updateFood();
        updateRoutines();
        loadSettings();
        updateGroomList();

        foodList.getItems().clear();
        foodList.getItems().addAll(pet.getMealLogs());

        wtList.getItems().clear();
        wtList.getItems().addAll(pet.getWeightLogs());

        updateHist();
    }

    private void updateHist() {
        if (actHistList != null) {
            actHistList.getItems().clear();
            Map<LocalDate, Integer> acts = new TreeMap<>(pet.getDailyActivities()).descendingMap();
            acts.forEach((d, m) -> actHistList.getItems().add(d + ": " + m + " mins"));
        }
    }

    @FXML
    public void saveStats() {
        try {
            int hr = Integer.parseInt(hrInput.getText());
            int rr = Integer.parseInt(rrInput.getText());
            int act = Integer.parseInt(actInput.getText());
            int scratch = Integer.parseInt(scratchInput.getText());

            if (hr < 0 || rr < 0 || act < 0 || scratch < 0) {
                alert("Invalid Input", "Values cannot be negative.");
                return;
            }

            pet.updateStats(hr, rr, act, scratch);
            db.saveStats(pet);
            updateStats();
            updateHist();

            clear(hrInput, rrInput, actInput, scratchInput);
            alert.setText("");

        } catch (NumberFormatException e) {
            alert("Invalid Input", "Please enter valid whole numbers.");
        }
    }

    private void updateStats() {
        hrLabel.setText(pet.getHr() + " bpm");
        rrLabel.setText(pet.getRr() + " /min");

        double p = 0.0;
        if (pet.getTgtAct() > 0) p = (double) pet.getAct() / pet.getTgtAct();
        actBar.setProgress(Math.min(p, 1.0));
        actLabel.setText(pet.getAct() + " / " + pet.getTgtAct() + " mins");

        scratchLabel.setText(String.valueOf(pet.getScratch()));
    }

    @FXML
    public void saveFood() {
        try {
            String name = foodName.getText();
            if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Meal name required");

            double amt = Double.parseDouble(foodAmt.getText());
            if (amt <= 0) {
                alert("Invalid Input", "Amount must be positive.");
                return;
            }

            pet.addFood(name, amt);
            db.addMeal(LocalDate.now(), name, amt);
            foodList.getItems().add(LocalDate.now() + " | " + name + ": " + amt);
            updateFood();

            clear(foodName, foodAmt);

        } catch (NumberFormatException e) {
             alert("Invalid Input", "Enter a valid number.");
        } catch (IllegalArgumentException e) {
             alert("Invalid Input", e.getMessage());
        }
    }

    private void updateFood() {
        foodTotal.setText(pet.getDailyFoodIntake() + " / " + pet.getTgtFood());
    }

    @FXML
    public void saveWt() {
        try {
            double wt = Double.parseDouble(wtInput.getText());
            if (wt <= 0) {
                alert("Invalid Input", "Weight must be positive.");
                return;
            }
            pet.addWeight(wt);
            db.addWeight(LocalDate.now(), wt);
            wtList.getItems().add(LocalDate.now() + ": " + wt + " kg");
            wtInput.clear();
        } catch (NumberFormatException e) {
            alert("Invalid Input", "Enter a valid weight.");
        }
    }

    @FXML public void brushTeeth() { doRoutine(pet.getTeeth()); }
    @FXML public void cleanEars() { doRoutine(pet.getEars()); }
    @FXML public void brushCoat() { doRoutine(pet.getGroom()); }
    @FXML public void giveNexgard() { doRoutine(pet.getNexgard()); }
    @FXML public void giveShower() { doRoutine(pet.getShower()); }

    private void doRoutine(Routine r) {
        r.done();
        pet.logGrooming(r.getName() + " Done");
        db.updateRoutine(r);
        db.addGroomingLog(LocalDate.now(), r.getName() + " Done");
        updateGroomList();
        updateRoutines();
    }

    private void updateGroomList() {
        groomList.getItems().clear();
        groomList.getItems().addAll(pet.getGroomingLogs());
    }

    private void updateRoutines() {
        teethLabel.setText(pet.getTeeth().stat());
        earsLabel.setText(pet.getEars().stat());
        groomLabel.setText(pet.getGroom().stat());
        nexLabel.setText(pet.getNexgard().stat());
        if (showerLabel != null) showerLabel.setText(pet.getShower().stat());
    }

    @FXML
    public void saveSettings() {
        try {
            int tgtAct = Integer.parseInt(tgtActInput.getText());
            double optWt = Double.parseDouble(optWtInput.getText());
            double tgtFood = Double.parseDouble(tgtFoodInput.getText());

            if (tgtAct < 0 || optWt <= 0 || tgtFood <= 0) {
                alert("Invalid Settings", "Ensure all values are positive.");
                return;
            }

            pet.setTargets(tgtAct, optWt, tgtFood);

            updateFreq(pet.getTeeth(), teethIntInput);
            updateFreq(pet.getEars(), earsIntInput);
            updateFreq(pet.getGroom(), groomIntInput);
            updateFreq(pet.getNexgard(), nexIntInput);
            updateFreq(pet.getShower(), showerIntInput);

            db.saveSettings(pet);

            updateFood();
            updateRoutines();
            updateStats();

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Settings Saved");
            a.setHeaderText(null);
            a.setContentText("Preferences updated.");
            a.showAndWait();

        } catch (NumberFormatException e) {
            alert("Invalid Settings", "Please ensure all fields contain valid numbers.");
        } catch (IllegalArgumentException e) {
            alert("Invalid Settings", e.getMessage());
        }
    }

    private void updateFreq(Routine r, TextField tf) {
        if (tf != null && tf.getText() != null && !tf.getText().isEmpty()) {
            r.setFreq(Integer.parseInt(tf.getText()));
        }
    }

    private void loadSettings() {
        tgtActInput.setText(String.valueOf(pet.getTgtAct()));
        optWtInput.setText(String.valueOf(pet.getOptWt()));
        tgtFoodInput.setText(String.valueOf(pet.getTgtFood()));

        teethIntInput.setText(String.valueOf(pet.getTeeth().getFreq()));
        earsIntInput.setText(String.valueOf(pet.getEars().getFreq()));
        groomIntInput.setText(String.valueOf(pet.getGroom().getFreq()));
        nexIntInput.setText(String.valueOf(pet.getNexgard().getFreq()));
        if (showerIntInput != null) showerIntInput.setText(String.valueOf(pet.getShower().getFreq()));
    }

    @FXML
    public void reload() {
        updateRoutines();
    }

    private void checkDue() {
        StringBuilder sb = new StringBuilder();
        checkDue(pet.getTeeth(), sb);
        checkDue(pet.getEars(), sb);
        checkDue(pet.getGroom(), sb);
        checkDue(pet.getNexgard(), sb);
        checkDue(pet.getShower(), sb);

        if (sb.length() > 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Care Due");
            a.setHeaderText("Action Required:");
            a.setContentText(sb.toString());
            a.showAndWait();
        }
    }

    private void checkDue(Routine r, StringBuilder sb) {
        if (r.dueIn() <= 0) sb.append("- ").append(r.getName()).append("\n");
    }

    private void alert(String title, String content) {
        alert.setText("⚠️ " + title + ": " + content);
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    private void clear(TextInputControl... controls) {
        for (TextInputControl c : controls) c.clear();
    }
}
