import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private JButton rdmButton;
    private JTextField rdmField;
    private JButton sdmButton;
    private JTextField sdmField;
    private JButton rm3Button;
    private JTextField rm3Field;
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
        this.setSize(1200, 700);

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

        rdmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rdmField.setText(doNDCG());
            }
        });

        sdmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        rm3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        resultList1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println(resultList1.getSelectedIndex());
                JOptionPane.showInputDialog(null, "This is where we would show an image " +
                        "of the item and an input field to " +
                        "set its relevance for the nDCG calculation");
            }
        });

        resultList2.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

            }
        });

        resultList3.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

            }
        });
    }

    //performs the ndcg calculation
    private String doNDCG(){
        //make sure there are results to work with

        //calculate DCG

        //calculate iDCG

        //return DCG / iDCG

        return "0.1223";
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
                if (resultListModel1.getSize() < 10) {
                    resultListModel1.addElement(objects.get(asin).title + " - " + objects.get(asin).relevance);
                }
            }

            //for model 2
            for (String asin : searcher2.search(searchTextBox.getText())) {
                if (resultListModel2.getSize() < 10) {
                    resultListModel2.addElement(objects.get(asin).title);
                }
            }

            //for model 3
            for (String asin : searcher3.search(searchTextBox.getText())) {
                if (resultListModel3.getSize() < 10) {
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
