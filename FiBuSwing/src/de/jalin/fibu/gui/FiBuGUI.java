// $Id: FiBuGUI.java,v 1.9 2005/11/20 21:29:10 phormanns Exp $

package de.jalin.fibu.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.gui.tree.MenuTreeModel;

public class FiBuGUI {

	private JFrame frame;
	private JPanel workArea;
	private JTree treeMenu;
	private FiBuFacade fibu;
	private MenuTreeModel treeMenuModel;

	public FiBuGUI() {
		try {
			fibu = new FiBuFacade();
			frame = new JFrame("Freibier - Buchhaltung");
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					exitFiBu();
				}
			});
			frame.setJMenuBar(initMenuBar());
			treeMenuModel = new MenuTreeModel(this);
			treeMenu = new JTree(treeMenuModel);
			treeMenu.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			treeMenu.addTreeSelectionListener(new MenuTreeSelectionListener());
			JScrollPane treePane = new JScrollPane(treeMenu);
			treePane.setPreferredSize(new Dimension(200, 600));
			workArea = new JPanel(new BorderLayout());
			workArea.add(new JLabel("<html><body><h1>Freibier - FiBu</h1>" +
					"<p>Eine einfache Finanzbuchhaltung</p>" +
					"<p>&copy; &nbsp; Thomas Bayen</p>" +
					"<p>&copy; &nbsp; Peter Hormanns</p>" +
					"<p>&copy; &nbsp; Susanne Wenz</p>" +
					"</body></html>"), BorderLayout.NORTH);
			workArea.setPreferredSize(new Dimension(600, 600));
			frame.getContentPane().add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, workArea));
			frame.pack();
			frame.setVisible(true);
		} catch (FiBuException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	public FiBuFacade getFiBuFacade() {
		return fibu;
	}

	public void handleException(FiBuException e) {
		JOptionPane.showMessageDialog(frame, e.getMessage(), "Fehlermeldung", JOptionPane.ERROR_MESSAGE);
		System.out.println(e.getMessage());
	}

	public void refreshJournale() {
		treeMenuModel.refreshJournale();
	}

	public JFrame getFrame() {
		return frame;
	}

	private JMenuBar initMenuBar() {
		JMenuBar mainMenu = new JMenuBar();
		JMenu fileMenu = new JMenu("Datei");
		JMenuItem prefsItem = new JMenuItem("Einstellungen");
		fileMenu.add(prefsItem);
		fileMenu.addSeparator();
		JMenuItem exitItem = new JMenuItem("beenden");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent exitEvent) {
				exitFiBu();
			}
		});
		fileMenu.add(exitItem);
		mainMenu.add(fileMenu);
		return mainMenu;
	}

	private void exitFiBu() {
		TreePath selectionPath = treeMenu.getSelectionPath();
		if (selectionPath != null) {
			Object leafNode = selectionPath.getLastPathComponent();
			if (leafNode != null) {
				if (((Editable) leafNode).validateAndSave()) {
					System.exit(0);
				}
			}
		} else {
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		try {
			// PlasticLookAndFeel.setMyCurrentTheme(new SkyBluer());
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
			UIManager.put("ClassLoader", LookUtils.class.getClassLoader());
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("Konnte Look-And-Feel nicht installieren.");
		}
		new FiBuGUI();
	}
	
	private class MenuTreeSelectionListener implements TreeSelectionListener {
		
		public void valueChanged(TreeSelectionEvent selectionEvent) {
			boolean canLeaveOldNode = false;
			TreePath oldPath = selectionEvent.getOldLeadSelectionPath();
			if (oldPath != null) {
				Editable oldNode = (Editable) oldPath.getLastPathComponent();
				canLeaveOldNode = oldNode.validateAndSave();
			} else {
				canLeaveOldNode = true;
			}
			TreePath newPath = selectionEvent.getNewLeadSelectionPath();
			if (newPath != null && canLeaveOldNode) {
				Editable newNode = (Editable) newPath.getLastPathComponent();
				workArea.removeAll();
				workArea.add(newNode.getEditor());
				workArea.revalidate();
				workArea.repaint();
			} else {
				treeMenu.setSelectionPath(oldPath);
			}
		}
	}

}


//
// $Log: FiBuGUI.java,v $
// Revision 1.9  2005/11/20 21:29:10  phormanns
// Umstellung auf XMLRPC Server
//
// Revision 1.8  2005/11/16 18:24:11  phormanns
// Exception Handling in GUI
// Refactorings, Focus-Steuerung
//
// Revision 1.7  2005/11/15 21:20:36  phormanns
// Refactorings in FiBuGUI
// Focus und Shortcuts in BuchungsForm und StammdatenForm
//
// Revision 1.6  2005/11/12 11:52:23  phormanns
// Vector in alleJournale unbenannt
//
// Revision 1.5  2005/11/11 19:46:26  phormanns
// MWSt-Berechnung im Buchungsdialog
//
// Revision 1.4  2005/11/10 21:19:26  phormanns
// Buchungsdialog begonnen
//
// Revision 1.3  2005/11/10 12:22:27  phormanns
// Erste Form tut was
//
// Revision 1.2  2005/11/09 23:01:35  phormanns
// Menubar hinzugefügt
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//