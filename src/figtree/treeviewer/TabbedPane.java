package figtree.treeviewer;

import javax.swing.*;

public class TabbedPane extends JFrame {
	JPanel sequencePanel = new JPanel();
	JPanel alignmentPanel = new JPanel();
	JPanel highlighterPanel = new JPanel();
	JLabel alignmentLabel = new JLabel("Alignment View");
	JLabel highlighterLabel = new JLabel("Highlighter View");
	
	JTabbedPane tabbedPane = new JTabbedPane();
	private final JTextArea sequenceTextArea = new JTextArea(32, 62);
	private final JTextArea alignmentTextArea = new JTextArea(32, 62);
	
	public TabbedPane(JTextArea sequenceTextArea, JTextArea alignmentTextArea) {
		
		sequenceTextArea.setEditable(false);
		JScrollPane sequenceScrollPane = new JScrollPane(sequenceTextArea);
		sequenceScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sequenceScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
 
		sequencePanel.add(sequenceScrollPane );


		alignmentTextArea.setEditable(false);
		JScrollPane alignmentScrollPane = new JScrollPane(alignmentTextArea);
		alignmentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		alignmentPanel.add(alignmentScrollPane);
		
		
		highlighterPanel.add(highlighterLabel);
		
		tabbedPane.add("Sequence View", sequencePanel);
		tabbedPane.add("Alignment View", alignmentPanel);
		tabbedPane.add("Highlighter View", highlighterPanel);
		
		getContentPane().add(tabbedPane);
	}
	
	public static void main(String[] args) {
		JTextArea sequenceTextArea = new JTextArea(32, 62);
		JTextArea alignmentTextArea = new JTextArea(32, 62);
		TabbedPane tp = new TabbedPane(sequenceTextArea, alignmentTextArea);
		tp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tp.setSize(800, 600);
		tp.setVisible(true);
	}
}
