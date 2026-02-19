import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetProfile {
    record Meal(LocalDate date, String name, double amount) {}

    private String name;
    private final Clock clock;

    private int hr;
    private int rr;
    private int act;
    private int scratch;

    private int tgtAct = 60;
    private double optWt = 6.5;
    private double tgtFood = 2.0;

    private List<Meal> meals = new ArrayList<>();
    private List<String> weights = new ArrayList<>();
    private List<String> groomingLogs = new ArrayList<>();

    private Map<LocalDate, Integer> dailyActs = new HashMap<>();

    private Routine teeth;
    private Routine ears;
    private Routine groom;
    private Routine nexgard;
    private Routine shower;

    public PetProfile(String name) {
        this(name, Clock.systemDefaultZone());
    }

    public PetProfile(String name, Clock clock) {
        this.name = name;
        this.clock = clock;
        this.teeth = new Routine("Teeth Brushing", 1, clock);
        this.ears = new Routine("Ear Cleaning", 4, clock);
        this.groom = new Routine("Grooming", 14, clock);
        this.nexgard = new Routine("NexGard", 30, clock);
        this.shower = new Routine("Shower", 7, clock);
    }

    public void updateStats(int hr, int rr, int act, int scratch) {
        this.hr = hr;
        this.rr = rr;
        this.act = act;
        this.scratch = scratch;
        addDailyActivity(LocalDate.now(clock), act);
    }

    public void addFood(String name, double amount) {
        addMeal(LocalDate.now(clock), name, amount);
    }

    public void addMeal(LocalDate date, String name, double amount) {
        meals.add(new Meal(date, name, amount));
    }

    public void addWeight(double weight) {
        addWeight(LocalDate.now(clock), weight);
    }

    public void addWeight(LocalDate date, double weight) {
        weights.add(date + ": " + weight + " kg");
    }

    public void logGrooming(String description) {
        logGrooming(LocalDate.now(clock), description);
    }

    public void logGrooming(LocalDate date, String description) {
        groomingLogs.add(date + ": " + description);
    }

    public void setTargets(int tgtAct, double optWt, double tgtFood) {
        this.tgtAct = tgtAct;
        this.optWt = optWt;
        this.tgtFood = tgtFood;
    }

    public void addDailyActivity(LocalDate date, int minutes) {
        dailyActs.put(date, minutes);
    }

    public String getName() { return name; }

    public int getHr() { return hr; }
    public int getRr() { return rr; }
    public int getAct() { return act; }
    public int getScratch() { return scratch; }

    public int getTgtAct() { return tgtAct; }
    public double getOptWt() { return optWt; }
    public double getTgtFood() { return tgtFood; }

    public double getDailyFoodIntake() {
        LocalDate today = LocalDate.now(clock);
        double total = 0;
        for (Meal meal : meals) {
            if (meal.date.equals(today)) total += meal.amount;
        }
        return total;
    }

    public List<String> getMealLogs() {
        List<String> list = new ArrayList<>();
        for (Meal meal : meals) {
            list.add(meal.date + " | " + meal.name + ": " + meal.amount);
        }
        return list;
    }
    public List<String> getWeightLogs() { return new ArrayList<>(weights); }
    public List<String> getGroomingLogs() { return new ArrayList<>(groomingLogs); }
    public Map<LocalDate, Integer> getDailyActivities() { return new HashMap<>(dailyActs); }

    public Routine getTeeth() { return teeth; }
    public Routine getEars() { return ears; }
    public Routine getGroom() { return groom; }
    public Routine getNexgard() { return nexgard; }
    public Routine getShower() { return shower; }
}
