/*
 * Copyright (c) [2017] [Weill Cornell Medical College]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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


    public static void parseJSap(String[] args) throws Exception {
        JSAP jsap = new JSAP(StepsLogTool.class.getResource("StepsLogTool.jsap"));

        JSAPResult config = jsap.parse(args);

    }

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
        File[] logFiles = config.getFileArray("log-file");
        StepsLogTool processor = new StepsLogTool();
        processor.process(config, logFiles);
        System.exit(0);
    }

    private static boolean hasError(JSAPResult config) {
        boolean OK = config.userSpecified("log-file");
        final String action = config.getString("action");
        OK &= "view".equals(action) || "verbose-view".equals(action);
        return !OK;
    }

    private void process(JSAPResult config, File[] logFiles) throws IOException {
        for (File logFile : logFiles) {
            System.out.println("Viewing "+logFile);
            StepsReportBuilder reporter = new StepsReportBuilder(logFile);
            reporter.setShowTime(true);
            final String action = config.getString("action");
            if ("view".equals(action)) {
                System.out.println(reporter.summarize());
            } else if ("verbose-view".equals(action)) {

                System.out.println(reporter.vervoseOutput());
            }
            System.out.println("------------------------------ (end of "+logFile+")");
        }
    }
}