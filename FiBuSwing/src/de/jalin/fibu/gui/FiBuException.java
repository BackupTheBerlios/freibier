// $Id: FiBuException.java,v 1.1 2005/11/10 12:22:27 phormanns Exp $
package de.jalin.fibu.gui;

public class FiBuException extends Exception {

	private static final long serialVersionUID = -8852327651063864679L;

	public FiBuException() {
		super();
	}

	public FiBuException(String message) {
		super(message);
	}

	public FiBuException(Throwable exception) {
		super(exception);
	}

	public FiBuException(String message, Throwable exception) {
		super(message, exception);
	}
}

/*
 *  $Log: FiBuException.java,v $
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
