/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import static java.text.MessageFormat.format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alexv
 */

privileged public aspect SearchPerformanceReportAspect
        extends SearchPerformanceReporter percflow(searchExec()) {

    declare precedence: SearchPerformanceReportAspect, ExecTimeReportAspect;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(SearchPerformanceReportAspect.class);

    pointcut searchExec() :
        execution(List PathSearcher+.search(..));

    pointcut inBlindSearch() : within(BlindSearcher);

    pointcut inAStarSearch() : within(AStarSearcher);

    pointcut inIDAStarSearch() : within(IDAStarSearcher);

    pointcut inMySearch() :
            inBlindSearch() || inAStarSearch() || inIDAStarSearch();

    pointcut generateState() :
            call(*.StateData.new(..)) && inMySearch();

    pointcut visitState() :
            call(Set StateOperator+.nextMovements(*)) && inMySearch();

    pointcut checkStateQueueSize() :
            call(boolean Queue+.isEmpty()) && inMySearch();

    pointcut validateVisitedState() :
            call(boolean Set.contains(*))
            && (inBlindSearch() || inIDAStarSearch());

    pointcut validateVisitedStateAStar() :
            call(* Map.get(*)) && inAStarSearch();

    pointcut recordMaxStatesPerIteration() :
            call(* Set.clear()) && inIDAStarSearch();

    before() : searchExec() {
        startSearching();
    }

    after() : searchExec() {
        searchCompleted();
    }

    after() returning : generateState() {
        generatedStates++;
    }

    after() returning : visitState() {
        visitedStates++;
    }

    after() returning (boolean visited) : validateVisitedState() {
        if(visited) {
            alreadyVisitedStates++;
        }
    }

    after() returning (AStarSearcher.StateData that) :
            validateVisitedStateAStar() {
        if(that.visited) {
            alreadyVisitedStates++;
        }
    }

    after(Queue queue) returning (boolean empty) : checkStateQueueSize()
            && target(queue) {
        if(!empty) {
            int size = queue.size();
            if(size > maximumSize) {
               maximumSize = size;
            }
        }
    }

    before(Set that) : recordMaxStatesPerIteration() && target(that)
            && inIDAStarSearch() {
        int size = that.size();
        if(size > maxRecordedStates) {
            maxRecordedStates = size;
        }
    }

    public Logger getLogger() {
        return LOGGER;
    }
}
