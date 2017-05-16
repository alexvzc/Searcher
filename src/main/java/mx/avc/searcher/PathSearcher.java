/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import java.util.List;
import java.util.Set;

/**
 *
 * @author alexv
 */
public interface PathSearcher<S, M> {

    public List<? extends M> search(S initial_state, Set<S> final_states);

    public StateController<S, M> getStateController();

}
