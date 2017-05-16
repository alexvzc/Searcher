/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.util;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author alexv
 */
public class LinkedDeque<T> extends LinkedList<T> implements Deque<T> {

    public LinkedDeque() {
        super();
    }

    public LinkedDeque(Collection<? extends T> c) {
        super(c);
    }

}
