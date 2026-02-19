import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {
    @Test
    public void testInstantiation() {
        // This test merely checks if the class can be instantiated without throwing unchecked exceptions.
        // The constructor catches checked exceptions (ClassNotFoundException, SQLException), so this should pass
        // even without a running database, logging the error to stderr.
        DatabaseManager db = new DatabaseManager();
        assertNotNull(db);
    }
}
