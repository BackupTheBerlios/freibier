// $Id: FiBuGUI.java,v 1.2 2005/11/09 23:01:35 phormanns Exp $

package de.jalin.fibu.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import de.jalin.fibu.gui.tree.DynamicFolder;
import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.gui.tree.LeafNode;
import de.jalin.fibu.gui.tree.StaticFolder;

public class FiBuGUI {

	private JFrame frame;
	private JPanel workArea;
	private JTree treeMenu;

	public FiBuGUI() {
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
			public void valueChanged(TreeSelectionEvent e) {
				// TODO Auto-generated method stub
				boolean canLeaveOldNode = false;
				TreePath oldPath = e.getOldLeadSelectionPath();
				if (oldPath != null) {
					Editable oldNode = (Editable) oldPath.getLastPathComponent();
					System.out.println("old:" + oldNode);
					canLeaveOldNode = oldNode.validateAndSave();
				} else {
					canLeaveOldNode = true;
				}
				TreePath newPath = e.getNewLeadSelectionPath();
				if (newPath != null && canLeaveOldNode) {
					Editable newNode = (Editable) newPath.getLastPathComponent();
					System.out.println("new:" + newNode);
					workArea.removeAll();
					workArea.add(newNode.getEditor());
					workArea.revalidate();
					workArea.repaint();
				} else {
					treeMenu.setSelectionPath(oldPath);
				}
			}
		});
		JScrollPane treePane = new JScrollPane(treeMenu);
		treePane.setPreferredSize(new Dimension(200, 600));
		workArea = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JScrollPane formPane = new JScrollPane(workArea);
		workArea.add(new JLabel("<html><h1>Freibier - FiBu</h1>" +
				"<p>Eine einfache Finanzbuchhaltung</p>" +
				"<p>&copy; &nbsp; Thomas Bayen</p>" +
				"<p>&copy; &nbsp; Peter Hormanns</p>" +
				"<p>&copy; &nbsp; Susanne Wenz</p>" +
				"</html>"));
		formPane.setPreferredSize(new Dimension(600, 600));
		frame.getContentPane().add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, formPane));
		frame.pack();
		frame.setVisible(true);
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

	private TreeNode initTreeMenu() {
		StaticFolder root = new StaticFolder("FiBu");
		StaticFolder journals = new StaticFolder("Journale");
		journals.addFolder(new DynamicFolder("Offene Journale") {
			public Vector readChildren() {
				Vector children = new Vector();
				children.addElement(new LeafNode("Kasse 11/2005"));
				return children;
			}
		});
		root.addFolder(journals);
		root.addFolder(new StaticFolder("Konten"));
		root.addFolder(new StaticFolder("Auswertungen"));
		return root;
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

}


//
// $Log: FiBuGUI.java,v $
// Revision 1.2  2005/11/09 23:01:35  phormanns
// Menubar hinzugefügt
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//