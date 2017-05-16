/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

/**
 *
 * @author alexv
 */
public class UnreachableStateException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnreachableStateException</code> without detail message.
     */
    public UnreachableStateException() {
    }


    /**
     * Constructs an instance of <code>UnreachableStateException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnreachableStateException(String msg) {
        super(msg);
    }

    public UnreachableStateException(Throwable cause) {
        super(cause);
    }


    public UnreachableStateException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
