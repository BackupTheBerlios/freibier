// $Id: FiBuGUI.java,v 1.4 2005/11/10 21:19:26 phormanns Exp $

package de.jalin.fibu.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import de.bayen.fibu.Journal;
import de.jalin.fibu.gui.forms.BuchungsForm;
import de.jalin.fibu.gui.forms.DummyForm;
import de.jalin.fibu.gui.forms.JournaleForm;
import de.jalin.fibu.gui.forms.KontenTreeForm;
import de.jalin.fibu.gui.forms.StammdatenForm;
import de.jalin.fibu.gui.tree.DynamicFolder;
import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.gui.tree.KontoNode;
import de.jalin.fibu.gui.tree.LeafNode;
import de.jalin.fibu.gui.tree.StaticFolder;

public class FiBuGUI {

	private JFrame frame;
	private JPanel workArea;
	private JTree treeMenu;
	private FiBuFacade fibu;

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
			treeMenu = new JTree(initTreeMenu());
			treeMenu.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			treeMenu.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent selectionEvent) {
					boolean canLeaveOldNode = false;
					TreePath oldPath = selectionEvent.getOldLeadSelectionPath();
					if (oldPath != null) {
						Editable oldNode = (Editable) oldPath.getLastPathComponent();
						System.out.println("old:" + oldNode);
						try {
							canLeaveOldNode = oldNode.validateAndSave();
						} catch (FiBuException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						canLeaveOldNode = true;
					}
					TreePath newPath = selectionEvent.getNewLeadSelectionPath();
					if (newPath != null && canLeaveOldNode) {
						Editable newNode = (Editable) newPath.getLastPathComponent();
						System.out.println("new:" + newNode);
						workArea.removeAll();
						try {
							workArea.add(newNode.getEditor());
						} catch (FiBuException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						workArea.revalidate();
						workArea.repaint();
					} else {
						treeMenu.setSelectionPath(oldPath);
					}
				}
			});
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

	private TreeNode initTreeMenu() throws FiBuException {
		StaticFolder root = new StaticFolder("FiBu", new StammdatenForm(fibu));
		StaticFolder journals = new StaticFolder("Journale", new DummyForm("Journale Form"));
		journals.addFolder(new DynamicFolder("Offene Journale", new JournaleForm(fibu, true)) {
			public Vector readChildren() {
				Vector offeneJournale = new Vector();
				try {
					offeneJournale = fibu.getOffeneJournale();
				} catch (FiBuException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Vector children = new Vector();
				Enumeration jourEnum = offeneJournale.elements();
				Journal jour = null;
				while (jourEnum.hasMoreElements()) {
					jour = (Journal) jourEnum.nextElement();
					children.addElement(
							new LeafNode(
									jour.getBuchungsperiode() 
										+ "/" + jour.getBuchungsjahr() 
										+ " ab: " + jour.getStartdatum(),
									new BuchungsForm(fibu, jour)
							));
				}
				return children;
			}
		});
		journals.addFolder(new DynamicFolder("Alle Journale", new JournaleForm(fibu, false)) {
			public Vector readChildren() {
				Vector offeneJournale = new Vector();
				try {
					offeneJournale = fibu.getAlleJournale();
				} catch (FiBuException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Vector children = new Vector();
				Enumeration jourEnum = offeneJournale.elements();
				Journal jour = null;
				while (jourEnum.hasMoreElements()) {
					jour = (Journal) jourEnum.nextElement();
					children.addElement(
							new LeafNode(
									jour.getBuchungsperiode() 
										+ "/" + jour.getBuchungsjahr() 
										+ " ab: " + jour.getStartdatum(),
									new DummyForm("Journal Form")
							));
				}
				return children;
			}
		});
		root.addFolder(journals);
		StaticFolder kontoRoot = new StaticFolder("Konten-Hierarchie", new KontenTreeForm(fibu));
		kontoRoot.addFolder(new KontoNode(null, fibu.getBilanzKonto()));
		root.addFolder(kontoRoot);
		root.addFolder(new StaticFolder("Auswertungen", new DummyForm("Auswertungen Form")));
		return root;
	}

	private void exitFiBu() {
		TreePath selectionPath = treeMenu.getSelectionPath();
		if (selectionPath != null) {
			Object leafNode = selectionPath.getLastPathComponent();
			if (leafNode != null) {
				try {
					if (((Editable) leafNode).validateAndSave()) {
						System.exit(0);
					}
				} catch (FiBuException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

}


//
// $Log: FiBuGUI.java,v $
// Revision 1.4  2005/11/10 21:19:26  phormanns
// Buchungsdialog begonnen
//
// Revision 1.3  2005/11/10 12:22:27  phormanns
// Erste Form tut was
//
// Revision 1.2  2005/11/09 23:01:35  phormanns
// Menubar hinzugef�gt
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//