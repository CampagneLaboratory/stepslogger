package stepslogger;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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

    @Test
    public void testProcess() throws Exception {
        FileStepsLogger slogger = new FileStepsLogger(testDirectory);
        RedirectStreams h = slogger.stepProcess("Running process command", "ls -lh");
        // run ls -lh somehow
        PrintWriter stdOutPrinter = new PrintWriter(h.getStandardOut());
        final String stdOutContent = "drwxr-x---   13 fac2003  staff   442B Mar 31 09:33 vcftools_0.1.10\n" +
                "drwxr-x---   13 fac2003  staff   442B Mar 31 09:40 vcftools_0.1.9\n" +
                "-rwxr-xr-x    1 fac2003  staff    46B Feb  9 10:04 vep\n" +
                "drwxr-xr-x@  30 fac2003  staff   1.0K Oct 15  2005 xsddoc-1.0";
        stdOutPrinter.println(stdOutContent);
        stdOutPrinter.close();
        PrintWriter stdErrPrinter = new PrintWriter(h.getStandardError());
        stdErrPrinter.print("(No error on stdout)");
        stdErrPrinter.close();
        slogger.processReturned(1);
        slogger.close();
        StepsReportBuilder reporter = new StepsReportBuilder(testDirectory.listFiles()[0]);
        // show only the error:
        reporter.setContextLength(0);
        assertEquals("Error encountered: \n" +
                "ERROR Running process command Status=1 Command=ls -lh\n" +
                "-------  StdOut> ---------\n" +
                stdOutContent + "\n" +
                "------- <StdOut ---------\n" +
                "\n" +
                "-------  StdErr> ---------\n" +
                "(No error on stdout)------- <StdErr ---------\n" +
                "\n", reporter.summarize());
    }


    @Test
    public void testProcessReturnedSmallBuffers() throws Exception {
        FileStepsLogger slogger = new FileStepsLogger(testDirectory);
        slogger.setRedirectBufferSize(100);
        RedirectStreams h = slogger.stepProcess("Running process command", "ls -lh");
        // run ls -lh somehow
        PrintWriter stdOutPrinter = new PrintWriter(h.getStandardOut());
        final String stdOutContent = "drwxr-x---   13 fac2003  staff   442B Mar 31 09:33 vcftools_0.1.10\n" +
                "drwxr-x---   13 fac2003  staff   442B Mar 31 09:40 vcftools_0.1.9\n" +
                "-rwxr-xr-x    1 fac2003  staff    46B Feb  9 10:04 vep\n" +
                "drwxr-xr-x@  30 fac2003  staff   1.0K Oct 15  2005 xsddoc-1.0";
        stdOutPrinter.println(stdOutContent);
        stdOutPrinter.close();
        PrintWriter stdErrPrinter = new PrintWriter(h.getStandardError());
        stdErrPrinter.print("(No error on stdout)");
        stdErrPrinter.close();
        slogger.processReturned(1);
        slogger.close();
        StepsReportBuilder reporter = new StepsReportBuilder(testDirectory.listFiles()[0]);
        // show only the error:
        reporter.setContextLength(0);
        assertEquals("Error encountered: \n" +
                "ERROR Running process command Status=1 Command=ls -lh\n" +
                "-------  StdOut> ---------\n" +
                "ac2003  staff    46B Feb  9 10:04 vep\n" +
                "drwxr-xr-x@  30 fac2003  staff   1.0K Oct 15  2005 xsddoc-1.0\n" +
                "------- <StdOut ---------\n" +
                "\n" +
                "-------  StdErr> ---------\n" +
                "(No error on stdout)------- <StdErr ---------\n\n", reporter.summarize());
    }

    @Test
    public void testProcessNoStdOut() throws Exception {
        FileStepsLogger slogger = new FileStepsLogger(testDirectory);
        RedirectStreams h = slogger.stepProcess("Running process command", "ls -lh");
        // run ls -lh somehow
        PrintWriter stdOutPrinter = new PrintWriter(h.getStandardOut());

        stdOutPrinter.close();
        PrintWriter stdErrPrinter = new PrintWriter(h.getStandardError());

        stdErrPrinter.close();
        slogger.processReturned(1);
        slogger.close();
        StepsReportBuilder reporter = new StepsReportBuilder(testDirectory.listFiles()[0]);
        // show only the error:
        reporter.setContextLength(0);
        assertEquals("Error encountered: \n" +
                "ERROR Running process command Status=1 Command=ls -lh (no StdOut) (no StdErr)\n"
                , reporter.summarize());
    }


    public void testObserved() throws Exception {

    }

    public void testStatus() throws Exception {

    }
}
