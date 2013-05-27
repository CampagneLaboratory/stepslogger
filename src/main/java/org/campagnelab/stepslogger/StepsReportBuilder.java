package org.campagnelab.stepslogger;


import campagnelab.stepslogger.LogFormat;

import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Builds a report of execution steps, as recorded in logged files. The primary goal of this tool is to
 * offer views of the logged information adapted to different circumstances. In default mode, when no step
 * failed, the report prints simple statistics about the number of steps logged. If any one of the steps failed,
 * the report provides details of the steps immediately preceding the failure.
 *
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 11:59 AM
 */
public class StepsReportBuilder {

    private int REPORT_K_BEFORE_ERROR = 5;
    private final LogFormat.Log log;
    private boolean errorEncountered;
    private List<Integer> stepsInErrorIndices = new ArrayList<Integer>();
    private int contextLength;

    public StepsReportBuilder(File logFile) throws IOException {
        assert logFile.isFile() : "directories are not supported at this time.";
        log = LogFormat.Log.parseDelimitedFrom(new FileInputStream(logFile));
        int stepIndex = 0;
        for (LogFormat.Step step : log.getStepsList()) {

            errorEncountered |= step.getError();
            if (step.getError()) {
                stepsInErrorIndices.add(stepIndex);
            }
            stepIndex++;
        }
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    private boolean showTime;

    public String summarize() {
        if (errorEncountered) {
            StringWriter stringBuffer = new StringWriter();
            int lastIndex = 0;
            for (int stepInErrorIndex : stepsInErrorIndices) {

                for (int i = REPORT_K_BEFORE_ERROR; i >= 0; i--) {

                    final int offset = stepInErrorIndex - i;
                    if (offset >= 0) {
                        int omittedSteps = offset - lastIndex;
                        if (omittedSteps > 0) {
                            stringBuffer.append(String.format("<omitting %d step%s>\n",
                                    omittedSteps,
                                    omittedSteps > 1 ? "s" : ""));
                        }
                        describe(log.getSteps(offset), stringBuffer);
                    }
                    lastIndex = stepInErrorIndex;
                }
                lastIndex += 1;
            }

            return "Error encountered: \n" + stringBuffer.toString();
        } else {
            return String.format("Success in %d steps", log.getStepsCount());
        }
    }

    private void describe(LogFormat.Step step, StringWriter writer) {

        writer.append(step.getError() ? "ERROR " : "OK    ");
        if (showTime) {
            final DateFormat instance = DateFormat.getInstance();
            final Date date = new Date();
            date.setTime(step.getTimeStamp());
            writer.append(instance.format(date));
            writer.append(" ");
        }
        writer.append(step.getTheMessage());
        if (step.hasStatusCode()) {
            writer.append(" Status=" + step.getStatusCode());
        }
        if (step.hasCommand()) {
            writer.append(" Command=" + step.getCommand());
            if (step.getStdout().length() > 0) {
                writer.append("\n" +
                        "-------  StdOut> ---------\n" + step.getStdout());
                writer.append("------- <StdOut ---------\n");
            } else {
                writer.append(" (no StdOut)");
            }
            if (step.getStdout().length() > 0) {
                writer.append("\n" +
                        "-------  StdErr> ---------\n" +
                        step.getStderr());
                writer.append("------- <StdErr ---------\n");
            } else {
                writer.append(" (no StdErr)");
            }

        }
        writer.append("\n");


    }

    /**
     * Set the maximum number of steps that will be shown just before an error.
     *
     * @param contextLength
     */
    public void setContextLength(int contextLength) {
        this.REPORT_K_BEFORE_ERROR = contextLength;
    }

    public String vervoseOutput() {
        StringWriter stringBuffer = new StringWriter();

        for (LogFormat.Step step : log.getStepsList()) {

            describe(step, stringBuffer);
        }
        return stringBuffer.toString();
    }

}

