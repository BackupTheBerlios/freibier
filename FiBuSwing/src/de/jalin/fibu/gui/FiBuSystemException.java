// $Id: FiBuSystemException.java,v 1.1 2005/11/10 12:22:27 phormanns Exp $
package de.jalin.fibu.gui;

public class FiBuSystemException extends FiBuException {

	private static final long serialVersionUID = 7880231524541861136L;

	public FiBuSystemException() {
		super();
	}

	public FiBuSystemException(String message) {
		super(message);
	}

	public FiBuSystemException(Throwable exception) {
		super(exception);
	}

	public FiBuSystemException(String message, Throwable exception) {
		super(message, exception);
	}
}

/*
 *  $Log: FiBuSystemException.java,v $
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
