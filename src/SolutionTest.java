import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import bridges.validation.RateLimitException;

@RunWith(Parameterized.class)
public class SolutionTest {
    
    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {{"basic"}, 
            {"minimal"}, {"rectangle_1"},
            {"rectangle_2"}, {"parallel"}, {"broken_room"},
            {"emerald"}, {"zig_zag"},
            {"spoke"}, {"web"}, {"eagle"}});
    }
    
    @Test
    public void test() throws IOException {
        List<String> expected = this.getExpectedOutput(dungeonName);
        Solution solution = new Solution();
        this.provideInput(dungeonName);
        solution.main(new String[0]);
        String actual = getOutput();
        String rooms = this.simpleTest(dungeonName, actual, expected, 1, "Rooms", false);
        this.simpleTest(dungeonName, actual, expected, 2, "Connections", false);
        this.simpleTest(dungeonName, actual, expected, 3, "Objectives", false);
        this.simpleTest(dungeonName, actual, expected, 4, "Density", true);
        this.simpleTest(dungeonName, actual, expected, 5, "Dead-ends", false);
        this.simpleTest(dungeonName, actual, expected, 6, "Hubs", false);
        this.simpleTest(dungeonName, actual, expected, 8, "Max Challenge", false);
        this.simpleTest(dungeonName, actual, expected, 9, "Median Challenge", true);
        String valid = this.simpleTest(dungeonName, actual, expected, 11, "Valid", false);
        if (!valid.toLowerCase().equals("true")) {
            return;
        }
        this.simpleTest(dungeonName, actual, expected, 14, "Balanced", false);
        String order = this.simpleTest(dungeonName, actual, expected, 16, "Order", false);
        for (String name: order.split(",")) {
            this.simpleTest(dungeonName, actual, expected, 18, "Route for "+name, false);
        }
        int roomNumber = Math.min(4, 1+Integer.parseInt(rooms));
        for (int i=0; i<roomNumber; i++) {
            this.simpleTest(dungeonName, actual, expected, 24, "Cluster "+i, false);
        }
    }

    private String dungeonName;
    
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;
    
    public SolutionTest(String dungeonName) {
        this.dungeonName = dungeonName;
    }
    
    private List<String> getExpectedOutput(String dungeonName) {
        String filename = String.format("dungeons/%s_out.txt", dungeonName);
        return this.readFile(filename);
    }

    
    
    private String findPrintedValue(List<String> printedText, String keyword) {
        Pattern pattern = Pattern.compile(String.format("^\\s*(%s)\\s*:\\s*(.*)$", keyword), Pattern.CASE_INSENSITIVE);
        for (String line: printedText) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(2);
            }
        }
        return null;
    }
        
    
    public String simpleTest(String dungeon, String actual, List<String> expected, int part, String keyword, boolean asFloat) {
        String[] actualLines = actual.split("\n");
        String actualResult = this.findPrintedValue(Arrays.asList(actualLines), keyword);
        if (actualResult == null) {
            fail(String.format("No output matches found for problem %d (%s) with dungeon %s", part, keyword, dungeon));
        }
        actualResult = actualResult.toLowerCase();
        String expectedResult = this.findPrintedValue(expected, keyword).toLowerCase();
        String message = String.format("Incorrect output for problem %d (%s) with dungeon {%s}", part, keyword, dungeon);
        if (asFloat) {
            assertEquals(message, Double.parseDouble(expectedResult), Double.parseDouble(actualResult), 2);
        } else {
            assertEquals(message, expectedResult, actualResult);
        }
        return expectedResult;
    }
    
    @Before
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }
    
    
    /**
     * Read the contents of a file into a List of Strings (an ArrayList).
     * 
     * @param filename The filename to open and read.
     * @return The contents of the file, each line an element of the list.
     */
    public static List<String> readFile(String filename) {
        List < String > records = new ArrayList < String > ();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
        }
        return records;
    }

}
