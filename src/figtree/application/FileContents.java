package figtree.application;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import figtree.treeviewer.TabbedPane;

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
        List values = new ArrayList();

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

    public static void displayResults(String taxon) throws Exception {
        List<String> results = lookUp(taxon);
        
        JTextArea sequenceTextArea = new JTextArea(32, 62);
        sequenceTextArea.setEditable(false);
        
        // sequence view
        sequenceTextArea.append(">" + taxon + "\n");

        for (String result : results) {
        	sequenceTextArea.append(result + "\n");
        }
        
        sequenceTextArea.append("\n\n\n");
        
        // alignment view
        JTextArea alignmentTextArea = new JTextArea(32, 62);
        alignmentTextArea.setEditable(false);
        alignmentTextArea.append(">" + taxon + ":");

        for (String result : results) {
        	alignmentTextArea.append(result);
        }
        
        alignmentTextArea.append("\n");

//        JOptionPane.showMessageDialog(null, pane, taxon, JOptionPane.PLAIN_MESSAGE);
        TabbedPane tp = new TabbedPane(sequenceTextArea, alignmentTextArea);
		tp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tp.setSize(800, 600);
		tp.setVisible(true);
    }
}
