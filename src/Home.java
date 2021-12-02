import com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class Home extends JFrame{
    //GUI components
    private JPanel mainPanel;
    private JPanel searchPanel;
    private JPanel resultPanel;
    private JTextField searchTextBox;
    private JButton searchButton;

    private static GalagoSearcher searcher;
    private HashMap<String, SearchResult> objects;

    public Home() {
        super("Default Search Engine");
        this.setContentPane(this.mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print(searchTextBox.getText());
            }
        });

        this.objects = new HashMap<>();
//        Thread loadObjs = new Thread(() -> {
            try {
                File f = new File("./data/formattedCellData.json");
                Scanner s = new Scanner(f);
                Gson parser = new Gson();
                while (s.hasNextLine()) {
                    SearchResult sr = parser.fromJson(s.nextLine().trim(), SearchResult.class);
                    this.objects.put(sr.asin, sr);
                }
                s.close();
            }
            catch (Exception e) {
                System.err.println("Error during file reading: " + e);
                e.printStackTrace();
            }
//        });
//        loadObjs.start();
    }

    public static void main(String[] args) {
        Home home = new Home();
        home.setVisible(true);

        searcher = new GalagoSearcher("./data/index", "org.lemurproject.galago.core.retrieval.processing.RankedDocumentModel");
        try {
            for (String asin : searcher.search("iPhone 5s")) {
                System.out.println(home.objects.get(asin).title);
            }
        }
        catch (Exception e) {
            System.err.println("Error during search: " + e);
        }
    }
}
