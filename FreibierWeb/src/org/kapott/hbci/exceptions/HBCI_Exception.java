
/*  $Id: HBCI_Exception.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2004  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.exceptions;


/** Diese Klasse ist die Super-Klasse aller Exceptions, die
    durch den HBCI-Kernel erzeugt werden. Beim Auftreten einer
    solchen Exception sollten die Messages der gesamten(!)
    Exception-Kette angezeigt werden, um die Fehlerursache
    bestm�glich bestimmen zu k�nnen.
    <pre>
try {
    // hier HBCI-Zeugs machen
} catch (HBCI_Exception e) {
    Throwable e2=e;
    String msg;
    
    System.out.println("HBCI-Exception:");
    while (e2!=null) {
        if ((msg=e2.getMessage())!=null) {
            System.out.println(msg);
        }
        e2=e2.getCause();
    }
}
    </pre> */
public class HBCI_Exception
     extends RuntimeException
{
    /** Erzeugen einer neuen HBCI_Exception ohne Message und
        ohne Cause */
    public HBCI_Exception()
    {
        super();
    }
    
    /** Erzeugen einer neuen HBCI_Exception mit bestimmter
        Message 
        @param s Message, die bei <code>getMessage()</code> zur�ckgegeben werden soll*/
    public HBCI_Exception(String s)
    {
        super(s);
    }
    
    /** Erzeugen einer neuen HBCI_Exception mit bestimmtem Cause.
        Die Message, die in dieser Exception gespeichert wird, ist
        auf jeden Fall leer 
        @param e "Ursache" dieser Exception, die in der Exception-Kette als
               <code>getCause()</code> zur�ckgegeben werden soll */
    public HBCI_Exception(Throwable e)
    {
        super(null,e);
    }
    
    /** Erzeugen einer neuen HBCI_Exception mit gegebener Message und Cause 
        @param st Message, die bei <code>getMessage()</code> zur�ckgegeben werden soll 
        @param e "Ursache" dieser Exception, die in der Exception-Kette als
               <code>getCause()</code> zur�ckgegeben werden soll */
    public HBCI_Exception(String st,Throwable e)
    {
        super(st,e);
    }
}
