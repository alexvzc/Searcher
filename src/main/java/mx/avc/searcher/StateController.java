/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import java.util.Set;

/**
 *
 * @author alexv
 */
public interface StateController<S, M> {

    public Set<? extends M> nextMovements(S current_state);

    public S nextState(S current_state, M movement);

    public float getDistance(S current_state, Set<S> final_states);

    public float getCost(S current_state, M movement);

}
