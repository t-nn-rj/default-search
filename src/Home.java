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
    private JTextField searchTextBox;
    private JButton searchButton;
    private JPanel resultPanel;
    private JList resultList1;
    private JList resultList2;
    private JList resultList3;
    private DefaultListModel resultListModel1;
    private DefaultListModel resultListModel2;
    private DefaultListModel resultListModel3;


    private GalagoSearcher searcher1;
    private GalagoSearcher searcher2;
    private GalagoSearcher searcher3;
    private HashMap<String, SearchResult> objects;

    public Home() {
        super("Default Search Engine");
        this.setContentPane(this.mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 600);

        //set up the result lists
        resultListModel1 = new DefaultListModel();
        resultListModel2 = new DefaultListModel();
        resultListModel3 = new DefaultListModel();
        resultList1.setModel(resultListModel1);
        resultList2.setModel(resultListModel2);
        resultList3.setModel(resultListModel3);

        this.loadIndex();

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAndDisplay();
            }
        });
    }

    //Loads data from the index into memory
    private void loadIndex(){
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

    //performs the search and displays the results for the different models
    private void searchAndDisplay(){
        //clear the result page.
        clearLists();

        try {
            //for model 1
            for (String asin : searcher1.search(searchTextBox.getText())) {
                if (resultListModel1.getSize() < 100) {
                    resultListModel1.addElement(objects.get(asin).title);
                }
            }

            //for model 2
            for (String asin : searcher2.search(searchTextBox.getText())) {
                if (resultListModel2.getSize() < 100) {
                    resultListModel2.addElement(objects.get(asin).title);
                }
            }

            //for model 3
            for (String asin : searcher3.search(searchTextBox.getText())) {
                if (resultListModel3.getSize() < 100) {
                    resultListModel3.addElement(objects.get(asin).title);
                }
            }
        }
        catch (Exception ex) {
            System.err.println("Error during search: " + ex);
        }
    }

    private void clearLists(){
        resultListModel1.clear();
        resultListModel2.clear();
        resultListModel3.clear();
    }

    public static void main(String[] args) {
        Home home = new Home();
        home.setVisible(true);
        home.searcher1 = new GalagoSearcher("./data/index", "rdm");
        home.searcher2 = new GalagoSearcher("./data/index", "sdm");
        home.searcher3 = new GalagoSearcher("./data/index", "rm3");

    }
}
