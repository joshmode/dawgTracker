import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.List;

public class PetProfileTest {

    @Test
    public void testInit() {
        PetProfile p = new PetProfile("TestDog");
        assertEquals("TestDog", p.getName());
        assertNotNull(p.getTeeth());
        assertNotNull(p.getEars());
        assertNotNull(p.getGroom());
        assertNotNull(p.getNexgard());
        assertNotNull(p.getShower());
    }

    @Test
    public void testUpdStat() {
        PetProfile p = new PetProfile("TestDog");
        p.updateStats(80, 20, 120, 5);

        assertEquals(80, p.getHr());
        assertEquals(20, p.getRr());
        assertEquals(120, p.getAct());
        assertEquals(5, p.getScratch());
    }

    @Test
    public void testAddFood() {
        Clock c = Clock.fixed(Instant.parse("2023-10-01T12:00:00Z"), ZoneId.of("UTC"));
        PetProfile p = new PetProfile("TestDog", c);

        p.addFood("Kibble", 1.5);
        assertEquals(1.5, p.getDailyFoodIntake());

        List<String> h = p.getMealLogs();
        assertEquals(1, h.size());
        assertTrue(h.get(0).contains("Kibble: 1.5"));

        p.addFood("Treat", 0.5);
        assertEquals(2.0, p.getDailyFoodIntake());
    }

    @Test
    public void testDailyFoodReset() {
        MutableClock c = new MutableClock(Instant.parse("2023-10-01T12:00:00Z"), ZoneId.of("UTC"));
        PetProfile p = new PetProfile("TestDog", c);

        p.addFood("Day 1 Food", 100);
        assertEquals(100, p.getDailyFoodIntake());

        c.setInstant(Instant.parse("2023-10-02T12:00:00Z"));

        assertEquals(0, p.getDailyFoodIntake());

        p.addFood("Day 2 Food", 50);
        assertEquals(50, p.getDailyFoodIntake());

        List<String> h = p.getMealLogs();
        assertEquals(2, h.size());
    }

    @Test
    public void testAddWt() {
        Clock c = Clock.fixed(Instant.parse("2023-10-01T12:00:00Z"), ZoneId.of("UTC"));
        PetProfile p = new PetProfile("TestDog", c);

        p.addWeight(10.5);

        List<String> h = p.getWeightLogs();
        assertEquals(1, h.size());
        assertTrue(h.get(0).contains("10.5 kg"));
    }

    @Test
    public void testUpdTgt() {
        PetProfile p = new PetProfile("TestDog");
        p.setTargets(90, 7.0, 2.5);

        assertEquals(90, p.getTgtAct());
        assertEquals(7.0, p.getOptWt());
        assertEquals(2.5, p.getTgtFood());
    }

    @Test
    public void testLogGroom() {
        Clock c = Clock.fixed(Instant.parse("2023-10-01T12:00:00Z"), ZoneId.of("UTC"));
        PetProfile p = new PetProfile("TestDog", c);

        p.logGrooming("Test Task");
        List<String> h = p.getGroomingLogs();
        assertEquals(1, h.size());
        assertTrue(h.get(0).contains("Test Task"));
    }

    @Test
    public void testDailyActivity() {
         Clock c = Clock.fixed(Instant.parse("2023-10-01T12:00:00Z"), ZoneId.of("UTC"));
         PetProfile p = new PetProfile("TestDog", c);

         p.updateStats(0, 0, 45, 0);
         assertEquals(45, p.getAct());
         assertTrue(p.getDailyActivities().containsKey(LocalDate.now(c)));
         assertEquals(45, p.getDailyActivities().get(LocalDate.now(c)));
    }

    static class MutableClock extends Clock {
        private Instant instant;
        private final ZoneId zone;

        public MutableClock(Instant instant, ZoneId zone) {
            this.instant = instant;
            this.zone = zone;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneId getZone() { return zone; }

        @Override
        public Clock withZone(ZoneId zone) { return new MutableClock(instant, zone); }

        @Override
        public Instant instant() { return instant; }
    }
}
