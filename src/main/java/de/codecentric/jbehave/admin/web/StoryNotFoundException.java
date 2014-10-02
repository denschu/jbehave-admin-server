package de.codecentric.jbehave.admin.web;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = org.springframework.http.HttpStatus.NOT_FOUND)
public class StoryNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	/** Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public StoryNotFoundException(String message) {
        super(message);
    }
}
