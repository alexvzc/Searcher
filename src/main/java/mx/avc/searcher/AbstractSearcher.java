/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexv
 */
public abstract class AbstractSearcher<S, M> implements PathSearcher<S, M> {

    protected final StateController<S, M> stateController;

    public AbstractSearcher(StateController<S, M> my_state_controller) {
        stateController = my_state_controller;
    }

    @Override
    public StateController<S, M> getStateController() {
        return stateController;
    }

    protected List<? extends M> buildReversePath(StateData<S, M> state_data) {
        List<M> solution_path = new ArrayList<>();
        while(state_data.fromStateData != null) {
            solution_path.add(0, state_data.movement);
            state_data = state_data.fromStateData;
        }

        return solution_path;
    }

    protected static class StateData<S, M> {

        protected final S state;

        protected StateData<S,M> fromStateData;

        protected M movement;

        protected StateData(S my_state) {
            state = my_state;
        }

        @Override
        public boolean equals(Object that) {
            if(that == this) {
                return true;
            }
            if(that == null || !(that instanceof StateData<?, ?>)) {
                return false;
            }
            return state.equals(((StateData<?, ?>)that).state);
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }

    }

}
