import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Routine {
    private String name;
    private LocalDate last;
    private int freq;
    private Clock clock;

    public Routine(String name, int freq) {
        this(name, freq, Clock.systemDefaultZone());
    }

    public Routine(String name, int freq, Clock clock) {
        if (freq <= 0) throw new IllegalArgumentException("Interval must be positive");
        this.name = name;
        this.freq = freq;
        this.clock = clock;
        this.last = LocalDate.now(clock);
    }

    public String getName() { return name; }

    public LocalDate getLast() { return last; }

    public void setLast(LocalDate last) { this.last = last; }

    public int getFreq() { return freq; }

    public void setFreq(int freq) {
        if (freq <= 0) throw new IllegalArgumentException("Interval must be positive");
        this.freq = freq;
    }

    public void done() { this.last = LocalDate.now(clock); }

    public long dueIn() {
        return freq - ChronoUnit.DAYS.between(last, LocalDate.now(clock));
    }

    public String stat() {
        long days = dueIn();
        if (days <= 0) return "🔴 DUE NOW: " + name;
        return "✅ " + name + " (Due in " + days + " days)";
    }
}
