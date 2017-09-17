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

	private static Node selectedNode;

	public static Node getSelectedNode() {
		return selectedNode;
	}

	public static void setSelectedNode(Node node) {
		selectedNode = node;
	}

	private static Set<Node> selectedNodes;

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

    public static List<String> lookUp(String taxon) throws Exception {
        FileReader fr = new FileReader(getLoadedFile());
        BufferedReader br = new BufferedReader(fr);
        String node;
        String keyword = ">" + taxon;
        List<String> values = new ArrayList<String>();

        while ((node=br.readLine())!=null) {
          if(node.contains(keyword)) {
              String nextLine = br.readLine();
              while (nextLine!=null && nextLine.startsWith(">") != true) {
                values.add(nextLine);
                nextLine = br.readLine();
              }
          }
        }
        return values;
    }
    
    private static JTextArea formatTextArea(JTextArea textArea, List<String> results, String taxon, String delimiter) {
    String taxonName = ">" + taxon + delimiter;
    	
		textArea.append(taxonName);

	    for (String result : results) {
	    	    	if(delimiter == "\n") {
	    	    		textArea.append(result + "\n");
	    	    	} else {
	    	    		textArea.append(result);
	    	    	}
	    }
	    textArea.append("\n");
	    return textArea;
    }
    
    public static JScrollPane generateSequenceView(ArrayList<String> taxons) {

    		JTextArea textArea = new JTextArea(32, 62);
    		
	    textArea.setEditable(false);

        for(String taxon : taxons) {
            List<String> results = new ArrayList<String>();
            try {
                results = lookUp(taxon);
            } catch (Exception e) {
                System.out.println("Could not look up taxon in file");
                e.printStackTrace();
            }

            textArea = formatTextArea(textArea, results, taxon, "\n");
        }
        
        JPanel sequencePane = new JPanel();
		sequencePane.add(textArea);

        return new JScrollPane(sequencePane , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

    public static JScrollPane generateAlignmentView( ArrayList<String> taxons ) throws Exception {

		int maxKeyLength = 0;
		
		for(String taxon : taxons) {
			if( taxon.length() > maxKeyLength ) {
				maxKeyLength = taxon.length();
			}
		}
		
		StringBuilder html = new StringBuilder();
		
		html.append("<style>\n" + 
				"  div { white-space:nowrap; font-size: 16px; font-family: monospace; }" +
				" .taxon {width: "+ maxKeyLength +"ch; margin-right: 3ch; }" +
				"  span:not(.taxon) { background-color: white; width: 2ch; }" + 
				" .red { background-color: red; }" + 
				" .blue { background-color: blue; }" + 
				" .yellow { background-color: yellow; }" + 
				" .green { background-color: green; }" + 
				" .cyan { background-color: cyan; }" + 
				" .black { background-color: black; }" + 
				"	</style>");

	    	for(String taxon : taxons) {
	    		
	    		html.append("<div><span class='taxon'>" + taxon + "</span>");
	    		
	    		List<String> results = new ArrayList<String>();
            try {
                results = lookUp(taxon);
            } catch (Exception e) {
                System.out.println("Could not look up taxon in file");
                e.printStackTrace();
            }
            
            String value = "";
            
            for(String s : results) {
            		value += s.replace("\t","");
            }
	    		
	    		for( char nucleotide : value.toCharArray() ){
	    			html.append( generateNucleotideColorHTML(nucleotide) );
	    		}
	    		
	    		html.append( "<br>" );
	    	}

		html.append( "</div>" );
			
		JEditorPane tp = new JEditorPane();
		tp.setEditable(false);
		tp.setContentType("text/html");
	    	tp.setText(html.toString());
	    	
		return new JScrollPane(tp , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	} 
	
	private static String generateNucleotideColorHTML(char nucleotide) throws Exception {
			
		String html = "";
			
		switch(nucleotide) {
			case 'A':
				html = "<span class='red'>" + String.valueOf(nucleotide) + "</span>";
				break;
			case 'T':
				html = "<span class='blue'>" + String.valueOf(nucleotide) + "</span>";
				break;
			case 'C':
				html = "<span class='yellow'>" + String.valueOf(nucleotide) + "</span>";
				break;
			case 'G':
				html = "<span class='green'>" + String.valueOf(nucleotide) + "</span>";
				break;
			case 'U':
				html = "<span class='cyan'>" + String.valueOf(nucleotide) + "</span>";
				break;
			default:
				html = "<span>" + String.valueOf(nucleotide) + "</span>";
				break;
		}
		
		return html;
	}
    
    public static void displayResults(ArrayList<String> taxons) throws Exception {
        
    		if( loadedFile == null ) {
    			JOptionPane.showMessageDialog(new JFrame(), "No .fa file loaded.");
    			return;
    		}
    	
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Sequence View", generateSequenceView(taxons) );
        tab.addTab("Alignment View", generateAlignmentView(taxons) );
        tab.addTab("Highlight View", new JScrollPane());
        
        JFrame frame = new JFrame("Sequence Key:Value Results");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(tab);
        frame.setSize(600,600);
        frame.setVisible(true);
        
    }

    public static final void initiateLookup(RootedTree tree, Node node) {
	
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
		ArrayList<String> tipTaxons = new ArrayList<String>();
		Integer count = 0;
		Set<Node> tips = new HashSet<Node>();

		for(Node node : nodes) {
			if(tree == null) {
				tips.add(node);
			} else {
				tips = RootedTreeUtils.getDescendantTips(tree, node);
			}

            if(tips.isEmpty()) {

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
}
