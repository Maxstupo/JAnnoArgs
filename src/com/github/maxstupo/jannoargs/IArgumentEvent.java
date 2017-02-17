package com.github.maxstupo.jannoargs;

/**
 * This interface allows events to be called when an argument is present in the command line arguments.
 * 
 * @author Maxstupo
 */
public interface IArgumentEvent {

    /**
     * Called when the argument registered to this event is present in the command line arguments.
     * 
     * @param name
     *            the argument name.
     */
    void onEvent(String name);
}
