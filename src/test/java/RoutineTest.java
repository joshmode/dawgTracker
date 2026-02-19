import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDate;

public class RoutineTest {

    @Test
    public void testDueInSameDay() {
        Clock clock = Clock.fixed(Instant.parse("2023-10-01T10:00:00Z"), ZoneId.of("UTC"));
        Routine routine = new Routine("Test Routine", 5, clock);

        assertEquals(5, routine.dueIn());
        assertEquals("✅ Test Routine (Due in 5 days)", routine.stat());
    }

    @Test
    public void testDueInAfterOneDay() {
        Clock clock = Clock.fixed(Instant.parse("2023-10-02T10:00:00Z"), ZoneId.of("UTC"));
        Routine routine = new Routine("Test Routine", 5, clock);

        routine.setLast(LocalDate.of(2023, 10, 1));

        assertEquals(4, routine.dueIn());
        assertEquals("✅ Test Routine (Due in 4 days)", routine.stat());
    }

    @Test
    public void testDueNow() {
        Clock clock = Clock.fixed(Instant.parse("2023-10-06T10:00:00Z"), ZoneId.of("UTC"));
        Routine routine = new Routine("Test Routine", 5, clock);

        routine.setLast(LocalDate.of(2023, 10, 1));

        assertEquals(0, routine.dueIn());
        assertEquals("🔴 DUE NOW: Test Routine", routine.stat());
    }

    @Test
    public void testOverdue() {
        Clock clock = Clock.fixed(Instant.parse("2023-10-07T10:00:00Z"), ZoneId.of("UTC"));
        Routine routine = new Routine("Test Routine", 5, clock);

        routine.setLast(LocalDate.of(2023, 10, 1));

        assertEquals(-1, routine.dueIn());
        assertEquals("🔴 DUE NOW: Test Routine", routine.stat());
    }

    @Test
    public void testDone() {
        Clock clock = Clock.fixed(Instant.parse("2023-10-01T10:00:00Z"), ZoneId.of("UTC"));
        Routine routine = new Routine("Test Routine", 5, clock);

        routine.setLast(LocalDate.of(2023, 1, 1));
        assertTrue(routine.dueIn() < 0);

        routine.done();

        assertEquals(LocalDate.of(2023, 10, 1), routine.getLast());
        assertEquals(5, routine.dueIn());
    }

    @Test
    public void testNegativeInterval() {
        Clock clock = Clock.fixed(Instant.parse("2023-10-01T10:00:00Z"), ZoneId.of("UTC"));
        assertThrows(IllegalArgumentException.class, () -> {
            new Routine("Negative Routine", -1, clock);
        });
    }
}
