package figtree.application;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import figtree.treeviewer.TabbedPane;
import jebl.evolution.graphs.Node;

import javax.swing.JScrollPane;
import java.io.*;
import java.util.*;

public class FileContents {

	private static File loadedFile;

	private static File getLoadedFile() {
		return loadedFile;
	}

	private static void setLoadedFile(File file) {
		loadedFile = file;
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

    private static JTextArea generateTextArea(String taxon, List<String> results, String delimiter) {
	    	JTextArea textArea = new JTextArea(32, 62);
	    	textArea.setEditable(false);
	    	
	    textArea = formatTextArea(textArea, results, taxon, delimiter);

	    	return textArea;
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
    
    public static JTextArea generateMultiTextArea(ArrayList<String> taxons, String delimiter) {
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

            textArea = formatTextArea(textArea, results, taxon, delimiter);
        }

	return textArea;
	}

    public static void displayMultipleResults(ArrayList<String> taxons) throws Exception {
        JTextArea sequenceTextArea = generateMultiTextArea(taxons, "\n");
        
        JTextArea alignmentTextArea = generateMultiTextArea(taxons, ":");

        TabbedPane tp = new TabbedPane(sequenceTextArea, alignmentTextArea);
		tp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tp.setSize(800, 600);
		tp.setVisible(true);
    }

    public static void displayResults(String taxon) throws Exception {
        List<String> results = lookUp(taxon);
        
        JTextArea sequenceTextArea = generateTextArea(taxon, results, "\n");
        
        JTextArea alignmentTextArea = generateTextArea(taxon, results, ":");

        TabbedPane tp = new TabbedPane(sequenceTextArea, alignmentTextArea);
		tp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tp.setSize(800, 600);
		tp.setVisible(true);
    }
}
