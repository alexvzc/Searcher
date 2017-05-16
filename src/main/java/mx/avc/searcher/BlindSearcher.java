/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mx.avc.util.Deque;
import mx.avc.util.LinkedDeque;

/**
 *
 * @author alexv
 */
public class BlindSearcher<S, M> extends AbstractSearcher<S, M> {

    public enum SearchType {

        DEPTH_FIRST, BREATH_FIRST

    }

    private final SearchType searchType;

    public BlindSearcher(SearchType search_type,
            StateController<S, M> state_controller) {
        super(state_controller);
        searchType = search_type;
    }

    public List<? extends M> search(S initial_state, Set<S> final_states) {
        Deque<StateData<S, M>> states_to_inspect =
                new LinkedDeque<StateData<S, M>>();
        Set<S> visited_states = new HashSet<S>();

        StateData<S, M> state_data = new StateData<S, M>(initial_state);
        visited_states.add(initial_state);

        states_to_inspect.add(state_data);

        while(!states_to_inspect.isEmpty()) {
            state_data = states_to_inspect.removeFirst();

            S current_state = state_data.state;
            if(final_states.contains(current_state)) {
                return buildReversePath(state_data);
            }

            Set<? extends M> possible_moves = stateController.nextMovements(
                    current_state);

            for(M next_move : possible_moves) {
                S next_state = stateController.nextState(current_state, next_move);

                if(!visited_states.contains(next_state)) {
                    StateData<S, M> next_state_data =
                            new StateData<S, M>(next_state);
                    next_state_data.fromStateData = state_data;
                    next_state_data.movement = next_move;
                    visited_states.add(next_state);

                    switch(searchType) {
                        case DEPTH_FIRST:
                            states_to_inspect.addFirst(next_state_data);
                            break;
                        case BREATH_FIRST:
                            states_to_inspect.addLast(next_state_data);
                            break;
                    }
                }
            }
        }

        throw new UnreachableStateException();
    }

}
