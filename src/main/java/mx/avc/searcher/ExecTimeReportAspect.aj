/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;


import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static java.text.MessageFormat.format;

/**
 *
 * @author alexv
 */

public aspect ExecTimeReportAspect pertarget(exec()) {

    private static final Log LOGGER =
            LogFactory.getLog(ExecTimeReportAspect.class);

    private long startTime;

    private long completionTime;

    pointcut exec() :
        execution(List PathSearcher+.search(..));

    before() : exec() {
        startTime = System.nanoTime();
    }

    after() returning (List l) : exec() {
        long end_time = System.nanoTime();
        completionTime = end_time - startTime;
        reportSuccess(l.size());
    }

    after() throwing : exec() {
        long end_time = System.nanoTime();
        completionTime = end_time - startTime;
        reportFailure();
    }

    protected void reportSuccess(int solution_steps) {
        float exec_time = completionTime/1000f;
        LOGGER.info(format(
                "Solution found in {0} steps after " +
                "{1,number,#,###.#} microseconds",
                solution_steps, exec_time));
    }

    protected void reportFailure() {
        float exec_time = completionTime/1000f;
        LOGGER.error(format(
                "Unreachable solution after {0,number,#,###.#} microseconds",
                exec_time));
    }

    public long getCompletionTime() {
        return completionTime;
    }

}
