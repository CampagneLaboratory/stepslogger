package org.campagnelab.stepslogger;

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
public class StepsLogTool {


    public static void main(String[] args) throws Exception {
        JSAP jsap = new JSAP(StepsLogTool.class.getResource("StepsLogTool.jsap"));

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
                    + StepsLogTool.class.getName());
            System.err.println("                "
                    + jsap.getUsage());
            System.err.println();

            System.exit(1);
        }
        File logFile = config.getFile("log-file");
        StepsLogTool processor = new StepsLogTool();
        processor.process(config, logFile);
        System.exit(0);
    }

    private static boolean hasError(JSAPResult config) {
        boolean OK = config.userSpecified("log-file");
        final String action = config.getString("action");
        OK &= "view".equals(action)|| "verbose-view".equals(action);
        return !OK;
    }

    private void process(JSAPResult config, File logFile) throws IOException {
        StepsReportBuilder reporter = new StepsReportBuilder(logFile);
        reporter.setShowTime(true);
        final String action = config.getString("action");
        if ("view".equals(action)) {
            System.out.println(reporter.summarize());
        }else
        if ("verbose-view".equals(action)) {

                    System.out.println(reporter.vervoseOutput());
                }
    }
}