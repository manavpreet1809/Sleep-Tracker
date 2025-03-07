import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @Test
    public void testRecordArrayWithMockedUserInput() {
    }

    @Test
    public void oneDigitRound() {
        String expected = "2.000";
        String actual = Main.round(2);
        assertEquals(expected, actual);
    }

    @Test
    public void twoDigitRound() {
        String expected = "37.89";
        String actual = Main.round(37.89);
        assertEquals(expected, actual);
    }

    @Test
    public void threeDigitRound() {
        String expected = "101.0";
        String actual = Main.round(101);
        assertEquals(expected, actual);
    }

    @Test
    public void fourDigitRound() {
        String expected = " 2379";
        String actual = Main.round(2379.31412);
        assertEquals(expected, actual);
    }

    @Test
    public void fiveDigitRound() {
        String expected = "10000";
        String actual = Main.round(10000.00);
        assertEquals(expected, actual);
    }
}

