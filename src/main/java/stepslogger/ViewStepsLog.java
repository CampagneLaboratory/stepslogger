package stepslogger;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import java.io.File;
import java.io.IOException;

/**
 * Tool to view the content of a step log.
 *
 * @author Fabien Campagne
 *         Date: 5/26/13
 *         Time: 12:26 PM
 */
public class ViewStepsLog {


    public static void main(String[] args) throws Exception {
        JSAP jsap = new JSAP(ViewStepsLog.class.getResource("ViewStepsLog.jsap"));

        JSAPResult config = jsap.parse(args);

        if (!config.success() || config.getBoolean("help") || hasError(config)) {

            // print out the help, then specific error messages describing the problems
            // with the command line, THEN print usage.
            //  This is called "beating the user with a clue stick."

            System.err.println(jsap.getHelp());

            for (java.util.Iterator errs = config.getErrorMessageIterator();
                 errs.hasNext(); ) {
                System.err.println("Error: " + errs.next());
            }

            System.err.println();
            System.err.println("Usage: java "
                    + ViewStepsLog.class.getName());
            System.err.println("                "
                    + jsap.getUsage());
            System.err.println();

            System.exit(1);
        }
        File logFile = config.getFile("log-file");
        ViewStepsLog processor = new ViewStepsLog();
        processor.process(config, logFile);
        System.exit(0);
    }

    private static boolean hasError(JSAPResult config) {
        return !(config.userSpecified("log-file"));
    }

    private void process(JSAPResult config, File logFile) throws IOException {
        StepsReportBuilder reporter = new StepsReportBuilder(logFile);
        System.out.println(reporter.summarize());

    }
}