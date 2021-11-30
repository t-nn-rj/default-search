import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home {
    private JButton searchButton;
    private JPanel mainPanel;
    private JTextArea userQuery;

    public Home() {
        searchButton.addActionListener(new ActionListener() {

            //method called when search button is clicked
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, userQuery.getText());
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Home");
        frame.setContentPane(new Home().mainPanel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
