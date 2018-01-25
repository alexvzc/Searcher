/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import java.util.Comparator;

/**
 *
 * @author alexv
 */
public abstract class AbstractAStarSearcher<S, M>
        extends AbstractSearcher<S, M> {

    protected AbstractAStarSearcher(StateController<S, M> state_controller) {
        super(state_controller);
    }

    protected static class StateData<S, M>
            extends AbstractSearcher.StateData<S, M> {

        protected final float hScore;

        protected float gScore;

        protected float fScore;

        protected boolean visited;

        protected StateData(S my_state, float my_h_score) {
            super(my_state);
            hScore = my_h_score;
            visited = false;
        }

    }

    protected static class StateComparator<S, M>
            implements Comparator<StateData<S, M>> {

        @Override
        public int compare(StateData<S, M> a, StateData<S, M> b) {
            int ret = Float.valueOf(a.fScore).compareTo(b.fScore);
            if(ret == 0) {
                int a_hash = a.hashCode();
                int b_hash = b.hashCode();
                if(a_hash < b_hash) {
                    ret = -1;
                } else if(a_hash > b_hash) {
                    ret = 1;
                }
            }
            return ret;
        }

    }

}
