package figtree.application;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
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
        JTextArea textArea = new JTextArea(15, 50);
        JScrollPane pane = new JScrollPane(textArea);

        textArea.setEditable(false);

        for (String result : results) {
        textArea.append(result + "\n");
        }

        JOptionPane.showMessageDialog(null, pane, taxon, JOptionPane.PLAIN_MESSAGE);
    }
}
