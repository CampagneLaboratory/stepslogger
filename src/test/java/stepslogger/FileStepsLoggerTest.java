package stepslogger;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 11:41 AM
 */
public class FileStepsLoggerTest {
    File testDirectory = new File("test-results/file-steps-logger");

    @Before
    public void createDirectory() throws IOException {
        FileUtils.deleteDirectory(testDirectory);
        FileUtils.forceMkdir(testDirectory);

    }

    @Test
    public void testStepsToSuccess() throws Exception {
        FileStepsLogger slogger = new FileStepsLogger(testDirectory);
        slogger.step("Step 1");
        slogger.step("Step 2");
        slogger.step("Step 3");
        slogger.close();
        StepsReportBuilder reporter = new StepsReportBuilder(testDirectory.listFiles()[0]);
        assertEquals("Success in 3 steps", reporter.summarize());
    }

    @Test
    public void testStepsToError() throws Exception {
        FileStepsLogger slogger = new FileStepsLogger(testDirectory);
        slogger.step("Step 1");
        slogger.step("Step 2");
        slogger.step("Step 3", 1);
        slogger.close();
        StepsReportBuilder reporter = new StepsReportBuilder(testDirectory.listFiles()[0]);
        assertEquals("Error encountered: \n" +
                "OK    Step 1\n" +
                "OK    Step 2\n" +
                "ERROR Step 3 Status=1\n", reporter.summarize());
    }

    @Test
    public void testOmittedStepsToError() throws Exception {
        FileStepsLogger slogger = new FileStepsLogger(testDirectory);
        slogger.step("Ommit 1");
        slogger.step("Ommit 2");
        slogger.step("Ommit 3");
        slogger.step("Step 1");
        slogger.step("Step 2");
        slogger.step("Step 3");
        slogger.step("Step 4");
        slogger.step("Step 5");
        slogger.step("Step 6", 1);
        slogger.close();
        StepsReportBuilder reporter = new StepsReportBuilder(testDirectory.listFiles()[0]);
        assertEquals("Error encountered: \n" +
                "<omitting 3 steps>\n" +
                "OK    Step 1\n" +
                "OK    Step 2\n" +
                "OK    Step 3\n" +
                "OK    Step 4\n" +
                "OK    Step 5\n" +
                "ERROR Step 6 Status=1\n", reporter.summarize());
    }

    @Test
    public void testOmittedStepsToErrorK2() throws Exception {
        FileStepsLogger slogger = new FileStepsLogger(testDirectory);

        slogger.step("Ommit 1");
        slogger.step("Ommit 2");
        slogger.step("Ommit 3");
        slogger.step("Step 1");
        slogger.step("Step 2");
        slogger.step("Step 3");
        slogger.step("Step 4");
        slogger.step("Step 5");
        slogger.step("Step 6", 1);
        slogger.step("Step 7");
        slogger.step("Step 8");
        slogger.step("Step 9");
        slogger.step("Step 10", 1);
        slogger.close();
        StepsReportBuilder reporter = new StepsReportBuilder(testDirectory.listFiles()[0]);
        reporter.setContextLength(2);
        assertEquals("Error encountered: \n" +
                "<omitting 6 steps>\n" +
                "OK    Step 4\n" +
                "OK    Step 5\n" +
                "ERROR Step 6 Status=1\n" +
                "<omitting 1 step>\n" +
                "OK    Step 8\n" +
                "OK    Step 9\n" +
                "ERROR Step 10 Status=1\n"
                , reporter.summarize());
    }

    public void testProcessReturned() throws Exception {

    }

    public void testObserved() throws Exception {

    }

    public void testStatus() throws Exception {

    }
}
