// $Id: FiBuGUI.java,v 1.1 2005/11/09 22:28:33 phormanns Exp $

package de.jalin.fibu.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
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

	public FiBuGUI() {
		frame = new JFrame("Freibier - Buchhaltung");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		JTree menuTree = new JTree(initMenuTree());
		menuTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		menuTree.addTreeSelectionListener(new TreeSelectionListener() {
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
					((JTree) e.getSource()).setSelectionPath(oldPath);
				}
			}
		});
		JScrollPane treePane = new JScrollPane(menuTree);
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

	private TreeNode initMenuTree() {
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
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//