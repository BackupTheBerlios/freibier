// $Id: KontoNode.java,v 1.7 2006/02/24 22:24:22 phormanns Exp $
/* 
 * HSAdmin - hostsharing.net Paketadministration
 * Copyright (C) 2005, 2006 Peter Hormanns                               
 *                                                                
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License    
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.         
 *                                                                 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.                   
 *                                                                 
 * You should have received a copy of the GNU General Public      
 * License along with this program; if not, write to the Free      
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.                                                                                        
 */
package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.tree.TreeNode;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.forms.KontoTable;
import de.jalin.fibu.server.konto.KontoData;

public class KontoNode implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private KontoData kto;
	private Vector children;
	private KontoTable ktoTable;
	
	private KontoNode(FiBuGUI gui, TreeNode parent, KontoData root, Map ktoHash, Map childrenHash) {
		this.parent = parent;
		this.kto = root;
		this.children = new Vector();
		Enumeration childrenEnum = ((Vector) childrenHash.get(root.getKontoid())).elements();
		KontoData childKto = null;
		while (childrenEnum.hasMoreElements()) {
			childKto = (KontoData) ktoHash.get((Integer) childrenEnum.nextElement());
			this.children.addElement(new KontoNode(gui, this, childKto, ktoHash, childrenHash));
		}
		this.ktoTable = new KontoTable(gui, kto);
	}

	public int getChildCount() {
		return children.size();
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return (getChildCount() == 0);
	}

	public Enumeration children() {
		return children.elements();
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode getChildAt(int index) {
		return (TreeNode) children.get(index);
	}

	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}
	
	public String toString() {
		return kto.getKontonr() + " - " + kto.getBezeichnung();
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public boolean validateAndSave() {
		return ktoTable.validateAndSave();
	}

	public Component getEditor() {
		return ktoTable.getEditor();
	}
	
	public KontoData getKonto() {
		return kto;
	}

	public static KontoNode buildKontoTree(FiBuGUI gui, TreeNode parent) throws FiBuException {
		FiBuFacade fibu = gui.getFiBuFacade();
		Iterator kontenIterator = fibu.getKontenListe().iterator();
		KontoData kto = null;
		Map ktoHash = new TreeMap();
		Map childrenHash = new TreeMap();
		while (kontenIterator.hasNext()) {
			kto = (KontoData) kontenIterator.next();
			ktoHash.put(kto.getKontoid(), kto);
			childrenHash.put(kto.getKontoid(), new Vector());
		}
		kontenIterator = ktoHash.keySet().iterator();
		Integer oberkonto = null;
		while (kontenIterator.hasNext()) {
			kto = (KontoData) ktoHash.get((Integer) kontenIterator.next());
			oberkonto = kto.getOberkonto();
			if (oberkonto != null) {
				((Vector) childrenHash.get(oberkonto)).addElement(kto.getKontoid());
			}
		}
		return new KontoNode(gui, parent, fibu.getBilanzKonto(), ktoHash, childrenHash);
	}

	public void refresh() {
		// TODO Auto-generated method stub
		
	}
}

/*
 *  $Log: KontoNode.java,v $
 *  Revision 1.7  2006/02/24 22:24:22  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.6  2005/11/23 23:16:49  phormanns
 *  Lesen Konto-Hierarchie und Buchungsliste optimiert
 *
 *  Revision 1.5  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.4  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.3  2005/11/15 21:20:36  phormanns
 *  Refactorings in FiBuGUI
 *  Focus und Shortcuts in BuchungsForm und StammdatenForm
 *
 *  Revision 1.2  2005/11/12 11:44:46  phormanns
 *  KontoTabelle zeigt Buchungen zum Konto
 *
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
