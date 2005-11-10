// $Id: FiBuUserException.java,v 1.1 2005/11/10 12:22:27 phormanns Exp $
package de.jalin.fibu.gui;

public class FiBuUserException extends FiBuException {

	private static final long serialVersionUID = -8852327651063864679L;

	public FiBuUserException() {
		super();
	}

	public FiBuUserException(String message) {
		super(message);
	}

	public FiBuUserException(Throwable exception) {
		super(exception);
	}

	public FiBuUserException(String message, Throwable exception) {
		super(message, exception);
	}
}

/*
 *  $Log: FiBuUserException.java,v $
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
