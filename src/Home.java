import com.google.common.base.Stopwatch;
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Home extends JFrame{
    //GUI components
    private JPanel mainPanel;
    private JPanel searchPanel;
    private JTextField searchTextBox;
    private JButton searchButton;
    private JPanel resultPanel;
    private JList<String> resultList1;
    private JList<String> resultList2;
    private JList<String> resultList3;
    private JButton rdmButton;
    private JTextField rdmField;
    private JButton sdmButton;
    private JTextField sdmField;
    private JButton rm3Button;
    private JTextField rm3Field;
    private JButton clearFeedbackButton;
    private JTextField rdmTimer;
    private JTextField sdmTimer;
    private JTextField rm3Timer;
    private ListData resultListModel1;
    private ListData resultListModel2;
    private ListData resultListModel3;


    private GalagoSearcher searcher1;
    private GalagoSearcher searcher2;
    private GalagoSearcher searcher3;
    private HashMap<String, SearchResult> objects;

    public static HashSet<String> relevantDocs;

    public Home() {
        super("Default Search Engine");
        this.setContentPane(this.mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 700);

        //set up the result lists
        resultListModel1 = new ListData();
        resultListModel2 = new ListData();
        resultListModel3 = new ListData();
        resultList1.setModel(resultListModel1);
        resultList2.setModel(resultListModel2);
        resultList3.setModel(resultListModel3);

        relevantDocs = new HashSet<>();
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
                rdmField.setText(doNDCG(resultListModel1));
            }
        });

        sdmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sdmField.setText(doNDCG(resultListModel2));
            }
        });

        rm3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rm3Field.setText(doNDCG(resultListModel3));
            }
        });

        resultList1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Result rDialog = new Result(resultListModel1.getSearchResultAt(resultList1.getSelectedIndex()));
                rDialog.setVisible(true);
            }
        });

        resultList2.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Result rDialog = new Result(resultListModel2.getSearchResultAt(resultList2.getSelectedIndex()));
                rDialog.setVisible(true);
            }
        });

        resultList3.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Result rDialog = new Result(resultListModel3.getSearchResultAt(resultList3.getSelectedIndex()));
                rDialog.setVisible(true);
            }
        });

        clearFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLists();
                relevantDocs.clear();
            }
        });
    }

    //performs the ndcg@5 calculation
    private String doNDCG(ListData list){
        //make sure there are results to work with
        if (relevantDocs.isEmpty()){
            return "0";
        }

        //calculate DCG
        int count = 2;
        double DCG = 0;
        for (SearchResult result : list.sr){
            if (relevantDocs.contains(result.asin)){
                DCG += 1.0 / (Math.log(count) / Math.log(2));
            }
            count++;
            if (count == 7){
                break;
            }
        }

        //calculate iDCG
        int c = 2;
        double iDCG = 0;
        for (String asin : relevantDocs){
            iDCG += 1.0 / (Math.log(c) / Math.log(2));
            c++;
            if (c == 7){
                break;
            }
        }

        System.out.println(DCG + " / " + iDCG);
        return "" + (DCG / iDCG);
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
            //for model 1, rdm
            Instant start = Instant.now();
            ArrayList<String> list1 = searcher1.search(searchTextBox.getText());
            Instant end = Instant.now();
            rdmTimer.setText("" + Duration.between(start, end).getNano() / 1000000 + " milliseconds");
            for (String asin : list1) {
                if (resultListModel1.getSize() < 25) {
                    resultListModel1.addElement(objects.get(asin));
                }
            }

            //for model 2, sdm
            start = Instant.now();
            ArrayList<String> list2 = searcher2.search(searchTextBox.getText());
            end = Instant.now();
            sdmTimer.setText("" + Duration.between(start, end).getNano() / 1000000 + " milliseconds");
            for (String asin : list2) {
                if (resultListModel2.getSize() < 25) {
                    resultListModel2.addElement(objects.get(asin));
                }
            }

            //for model 3, rm3
            start = Instant.now();
            ArrayList<String> list3 = searcher3.search(searchTextBox.getText());
            end = Instant.now();
            rm3Timer.setText("" + Duration.between(start, end).getNano() / 1000000 + " milliseconds");
            for (String asin : list3) {
                if (resultListModel3.getSize() < 25) {
                    resultListModel3.addElement(objects.get(asin));
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

class ListData extends AbstractListModel {
    ArrayList<SearchResult> sr = new ArrayList<>();

    public ListData() {
    }

    public void addElement(SearchResult s) {
        sr.add(s);
        fireContentsChanged(this,0,getSize());
    }

    @Override
    public int getSize() {
        return sr.size();
    }

    @Override
    public String getElementAt(int index) {
        return sr.get(index).title;
    }

    public SearchResult getSearchResultAt(int index) {
        return sr.get(index);
    }

    public void clear() {
        sr = new ArrayList<>();
    }
}