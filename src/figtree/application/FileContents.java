package figtree.application;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.RootedTreeUtils;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.io.*;
import java.util.*;

public class FileContents {

	private static File loadedFile = null;
	private static int maxKeyLength = 0;
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
    		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(null);
        File file = new File("");

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            file = selectedFile;
        }
        setLoadedFile(file);
    }

    public static Map<String, String> lookUp(ArrayList<String> taxons) throws Exception {
    	
    		Map<String, String> lookUpMap = new HashMap<String, String>();
    		
    		BufferedReader br = new BufferedReader( new FileReader(getLoadedFile()) );
    		
    		String[] lines = br.lines().toArray(String[]::new);

    		br.close();
    		
        maxKeyLength = 0;
        
        for(String taxon : taxons) {
        	
        		if( taxon.length() > maxKeyLength ) {
        			maxKeyLength = taxon.length();
        		}
        	    
        		lookUpMap.put(taxon , "");
        		
        		boolean addNode = false;
        		
        		for (String line : lines) {
        			
        			if( line.startsWith(">") && line.contains(taxon) ){
                    	
                		addNode = true;
                		
                }else if( addNode ) {
        				
        				if( line.startsWith(">") || line.isEmpty() ) {
        					break;
        				}
        				
        				lookUpMap.put( taxon , lookUpMap.get(taxon) + line.replace("\n", "") );
        				
        			}
            }
        }

        return lookUpMap;
    }
    
    public static JScrollPane generateSequenceView(Map<String, String> taxons) {
    	
    		StringBuilder text = new StringBuilder();

	    taxons.forEach( (taxon, value) -> {
	    		text.append(">" + taxon + "\n" + value + "\n");
	    });
	    
	    JTextArea textArea = new JTextArea(32, 64);
	    textArea.setEditable(false);
	    textArea.setLineWrap(true);
	    textArea.setText( text.toString() );

        JPanel sequencePane = new JPanel();
		sequencePane.add(textArea);

        return new JScrollPane(sequencePane , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

    public static JScrollPane generateAlignmentView(Map<String, String> taxons) throws Exception {
		
		StringBuilder html = new StringBuilder();

		int keyWidth = maxKeyLength + 2;
		
		html.append(
			"<style>" + 
				"#content { white-space:nowrap; font-size: 12px; font-family: monospace; }" +
				".sA {background-color: red; }" +
				".sT {background-color: blue;}" +
				".sC {background-color: yellow;}" +
				".sG {background-color: green;}" +
				".sU {background-color: cyan;}" +
			"</style>");
		
		html.append( "<div id='content'>" );

		taxons.forEach( (taxon, value) -> {
			
			String taxonPadding = "";
			
			for( int i = 0, j = keyWidth - taxon.length(); i < j; i++ ) {
				taxonPadding += "&nbsp;";
			}

			html.append( "<span class='taxon'>" + taxon + taxonPadding + "</span>" );
			
			String chars = "";
	    		
	    		for( char nucleotide : value.toCharArray() ){
	    			
	    			String n = String.valueOf(nucleotide);
	    			
	    			if( chars.contains(n) ) {
	    				chars += n;
	    			}else {
	    				html.append( "<span class='s"+n.substring(0,1)+"'>"+n+"</span>" );
	    				chars = "";
	    			}
	    		}
	    		
	    		html.append("<br>");
	    });
		
		html.append( "</div>" );

		JEditorPane ep = new JEditorPane();
		ep.setEditable(false);
		ep.setContentType("text/html");
		ep.setText(html.toString());
	    	
		return new JScrollPane(ep , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public static void displayResults(ArrayList<String> results) throws Exception {
		
		System.out.println("display");

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
		
		if( loadedFile == null ) {
			JOptionPane.showMessageDialog(new JFrame(), "No .fa file loaded.");
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
    	
			if( loadedFile == null ) {
				JOptionPane.showMessageDialog(new JFrame(), "No .fa file loaded.");
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

    public static void cleanUp() {
		setSelectedNode(null);
		setSelectedNodes(null);
		setSelectedTree(null);
	}
}
