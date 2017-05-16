/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import org.apache.commons.logging.Log;
import static java.text.MessageFormat.format;

/**
 *
 * @author alexv
 */
public abstract class SearchPerformanceReporter {

    protected int generatedStates;

    protected int visitedStates;

    protected int alreadyVisitedStates;

    protected int maximumSize;

    protected int maxRecordedStates;

    public void startSearching() {
        generatedStates = 0;
        visitedStates = 0;
        alreadyVisitedStates = 0;
        maxRecordedStates = 0;
        maximumSize = 0;
    }

    public void searchCompleted() {
        if(maxRecordedStates > 0) {
            getLogger().info(
                    format("Visited {0} states out of {1} generated states\n"
                    + "Maximum enqueued states to visit {2}\n"
                    + "Maximum recorded states {4}\n"
                    + "Generated {3} duplicated states",
                    visitedStates, generatedStates, maximumSize,
                    alreadyVisitedStates, maxRecordedStates));
        } else {
            getLogger().info(
                    format("Visited {0} states out of {1} generated states\n"
                    + "Maximum enqueued states to visit {2}\n"
                    + "Generated {3} duplicated states",
                    visitedStates, generatedStates, maximumSize,
                    alreadyVisitedStates));
        }
    }

    public abstract Log getLogger();

    /**
     * @return the generatedStates
     */
    public int getGeneratedStates() {
        return generatedStates;
    }

    /**
     * @return the visitedStates
     */
    public int getVisitedStates() {
        return visitedStates;
    }

    /**
     * @return the alreadyVisitedStates
     */
    public int getAlreadyVisitedStates() {
        return alreadyVisitedStates;
    }

    /**
     * @return the maximumSize
     */
    public int getMaximumSize() {
        return maximumSize;
    }

    /**
     * @return the maxRecordedStates
     */
    public int getMaxRecordedStates() {
        return maxRecordedStates;
    }

}
