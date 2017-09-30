package figtree.application;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.RootedTreeUtils;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.io.*;
import java.util.*;

import figtree.application.FigTreeApplication;

public class FileContents {

	private static File loadedFile = null;
	private static Node selectedNode;
	private static Set<Node> selectedNodes;

	private static File getLoadedFile() {
		return loadedFile;
	}

	private static void setLoadedFile(File file) {
		loadedFile = file;
	}

	private static RootedTree selectedTree;

	public static RootedTree getSelectedTree() {
		return selectedTree;
	}

	public static void setSelectedTree(RootedTree tree) {
		selectedTree = tree;
	}

	public static Node getSelectedNode() {
		return selectedNode;
	}

	public static void setSelectedNode(Node node) {
		selectedNode = node;
	}

	public static Set<Node> getSelectedNodes() {
		return selectedNodes;
	}

	public static void setSelectedNodes(Set<Node> nodes) {
		selectedNodes = nodes;
	}

    public static final void load() {
    	
    		FileDialog fd = new FileDialog( new JFrame() , "Open .fa file" , FileDialog.LOAD );
    		
        fd.setDirectory(FigTreeApplication.fileDir);
        
        fd.setAlwaysOnTop(true);
        fd.setMultipleMode(false);
        
        fd.setVisible(true);
        
        if( null != fd.getFile() ) {
        		setLoadedFile( new File ( fd.getDirectory() + fd.getFile () ) );
        }
    }

    public static Map<String, String> lookUp(ArrayList<String> taxons) throws Exception {
    	
    		Map<String, String> lookUpMap = new HashMap<String, String>();
    		
        for(String taxon : taxons) {
        		
        		boolean addNode = false;
        		
        		BufferedReader br = new BufferedReader( new FileReader(getLoadedFile()) );
        		
        		String line = "";
        		String value = "";
        		
        		while( (line = br.readLine()) != null) {
        			
        			if( line.startsWith(">") && line.contains(taxon) ){
                    	
                		addNode = true;
                		
                }else if( addNode ) {
        				
        				if( line.startsWith(">") || line.isEmpty() ) {
        					br.close();
        					lookUpMap.put( taxon , value );
        					break;
        				}
        				
        				value += line.replace("\n", "");
        			}
            }
        		//key has no value in file
        }

        return lookUpMap;
    }
    
    public static JScrollPane generateSequenceView(Map<String, String> taxons) {
    	
    		StringBuilder text = new StringBuilder();
    		
    		for(Map.Entry<String, String> entry : taxons.entrySet()) {
    			text.append(">" + entry.getKey() + "\n" + entry.getValue() + "\n");
    		}
	    
	    JTextArea textArea = new JTextArea(32, 64);
	    textArea.setEditable(false);
	    textArea.setLineWrap(true);
	    textArea.setText( text.toString() );

        JPanel sequencePane = new JPanel(new BorderLayout());
		sequencePane.add(textArea);

        return new JScrollPane(sequencePane , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

    public static JSplitPane generateAlignmentView(Map<String, String> taxons) throws Exception {
		
		StringBuilder taxonHTML = new StringBuilder();
		StringBuilder valueHTML = new StringBuilder();
		
		String css = "p{white-space:nowrap;font-size:12px;font-family:monospace;margin: 0;padding: 0;}";
		
		taxonHTML.append("<style>"+css+"</style>");
		
		css +=  ".sA{background-color:red;}" +
				".sT{background-color:blue;}" +
				".sC{background-color:yellow;}" +
				".sG{background-color:green;}" +
				".sU{background-color:cyan;}";
		
		valueHTML.append("<style>"+css+"</style>");

		for(Map.Entry<String, String> entry : taxons.entrySet()) {
			
			char[] values = entry.getValue().toCharArray();
			StringBuilder sb = new StringBuilder();
			String holdChars = "";
			int i = 0 , c = values.length - 1;
			
	    		for( char n : values ){
	    			
	    			String s = String.valueOf( n );
	    			
	    			if( i != c && s == String.valueOf( values[i+1] ) ) {
	    				holdChars += s;
	    			}else {
	    				String ch = holdChars.isEmpty() ? s : holdChars;
	    				sb.append( "<span class=s"+s+">"+ch+"</span>" );
		    			holdChars = "";
	    			}
	    			i++;
	    		}
	    		taxonHTML.append("<p>"+entry.getKey() + "</p>");
	    		valueHTML.append("<p>"+sb.toString() + "</p>");
		}
		
		valueHTML.append("<br>"); //set scroll to start at left

		JEditorPane taxonEP = new JEditorPane();
		taxonEP.setEditable(false);
		taxonEP.setContentType("text/html");
		taxonEP.setText(taxonHTML.toString());
		
		JEditorPane valueEP = new JEditorPane();
		valueEP.setEditable(false);
		valueEP.setContentType("text/html");
		valueEP.setText(valueHTML.toString());
		
		JScrollPane taxonPane = new JScrollPane(taxonEP , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane valuePane = new JScrollPane(valueEP , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		taxonPane.getVerticalScrollBar().setModel(valuePane.getVerticalScrollBar().getModel());
		valuePane.getVerticalScrollBar().setModel(taxonPane.getVerticalScrollBar().getModel());
		
		JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT , taxonPane, valuePane);
		
		panel.setResizeWeight(0.5f);
    
		return panel;
    }

	public static void displayResults(ArrayList<String> results) throws Exception {

    		Map<String, String> taxons = lookUp( results );

    		System.out.println("looked up");

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Sequence View", generateSequenceView(taxons) );
        tab.addTab("Alignment View", generateAlignmentView(taxons) );
        
        System.out.println("views generated");
        
        tab.addTab("Highlight View", new JScrollPane());
        
        JFrame frame = new JFrame("Sequence Key:Value Results");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(tab);
        frame.setSize(1000,1000);
        frame.setVisible(true);
        
        System.out.println("framed");
    }

	public static final void initiateLookup(RootedTree tree, Node node) {
		
		if( checkEmptyVars(node == null) ) {
			return;
		}
	
    		Set<Node> tips = RootedTreeUtils.getDescendantTips(tree, node);

		if (tips.isEmpty()) {
			System.out.println("Taxons");
				String taxon = new String();
			taxon = tree.getTaxon(node).toString();
			ArrayList<String> tipTaxons = new ArrayList<String>();
			tipTaxons.add(taxon);

			try {
				displayResults(tipTaxons);
			} catch (Exception e) {
				System.out.println("Could not display results for selected node");
				e.printStackTrace();
			}
		} else {
			System.out.println("tips implementation");
			ArrayList<String> tipTaxons = new ArrayList<String>();

			System.out.println(tips.size());

			for(Node tip : tips) {
				tipTaxons.add(tree.getTaxon(tip).toString());
			}

			try {
				displayResults(tipTaxons);
			} catch (Exception e) {
				System.out.println("Could not display results for selected nodes");
				e.printStackTrace();
			}
		}
    }

    public static final void initiateHighlightedLookup(RootedTree tree, Set<Node> nodes) {
    	
    		if( checkEmptyVars(nodes.isEmpty()) ) {
    			return;
    		}

		ArrayList<String> tipTaxons = new ArrayList<String>();
		Integer count = 0;
		Set<Node> tips = new HashSet<Node>();

		for(Node node : nodes) {
			
			tips = RootedTreeUtils.getDescendantTips(tree, node);
	
			if(tips.isEmpty()) {
				tips.add(node);
			}
			for(Node tip : tips) {
				System.out.println(tree.getTaxon(tip).toString());
				tipTaxons.add(tree.getTaxon(tip).toString());
				count++;
			}
		}

		try {
			displayResults(tipTaxons);
		} catch (Exception e) {
			System.out.println("Could not display results for selected multiple taxons");
			e.printStackTrace();
		}
    }

    private static boolean checkEmptyVars(boolean emptySet) {
		
		if( loadedFile == null ) {
			JOptionPane.showMessageDialog(new JFrame(), "No .fa file loaded.");
			return true;
		}
		
		if( emptySet ) {
    			JOptionPane.showMessageDialog(new JFrame(), "No nodes selected.");
    			return true;
		}
		
		return false;
	}
    
    public static void cleanUp() {
		setSelectedNode(null);
		setSelectedNodes(null);
		setSelectedTree(null);
	}
}
