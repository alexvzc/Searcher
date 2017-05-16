/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.util;

import java.util.Queue;

/**
 *
 * @author alexv
 */
public interface Deque<T> extends Queue<T> {

    public void addFirst(T elem);

    public void addLast(T elem);

    public T getFirst();

    public T getLast();

    public T removeFirst();

    public T removeLast();

}
