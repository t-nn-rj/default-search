import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Result extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea descriptionTextArea;
    private JTextArea titleTextArea;
    private JLabel picLabel;
    private JCheckBox relevantCheckBox;
    private SearchResult result;

    public Result(SearchResult result) {
        setContentPane(contentPane);
        setSize(800,600);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.result = result;

        try {
            URL url = new URL(result.imUrl);
            BufferedImage image = ImageIO.read(url);
            picLabel.setIcon(new ImageIcon(image));
        }
        catch (Exception e) {
            picLabel.setText("No picture available");
        }
        titleTextArea.setText(result.title);
        titleTextArea.setLineWrap(true);
        titleTextArea.setEditable(false);
        descriptionTextArea.setText(result.description);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setEditable(false);

        if (Home.relevantDocs.contains(result.asin)) {
            relevantCheckBox.setSelected(true);
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        if (relevantCheckBox.isSelected()) {
            Home.relevantDocs.add(result.asin);
        }
        dispose();
    }
}
