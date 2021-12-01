import edu.stanford.nlp.ling.CoreAnnotations;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame{
    private JPanel mainPanel;
    private JPanel searchPanel;
    private JPanel resultPanel;
    private JTextField searchTextBox;
    private JButton searchButton;

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
    }

    public static void main(String[] args) {
        Home home = new Home();
        home.setVisible(true);
    }
}
