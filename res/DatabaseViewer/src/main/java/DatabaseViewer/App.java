
package DatabaseViewer;

import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.internals.JacksonMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class App {

    Nitrite nitrite;

    public App() {
        nitrite = Nitrite.builder().compressed().filePath("./../data.db").openOrCreate("admin","2212");

        JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                nitrite.close();
                super.windowClosing(e);
            }
        });
        frame.setResizable(false);
        frame.setSize(1280,720);
        frame.setLayout(null);
        Container cp = frame.getContentPane();
        Set<String> collSet = nitrite.listCollectionNames();
        String[] collections = new String[collSet.size()];
        collections = collSet.toArray(collections);

        JTextArea contentArea = new JTextArea();

        contentArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBounds(270,10,980,650);
        cp.add(scrollPane);


        JComboBox<String> collectionSelction = new JComboBox<String>(collections);
        collectionSelction.setBounds(10,10,200,50);
        collectionSelction.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    String selected = (String) event.getItem();
                    StringBuilder content = new StringBuilder();
                    Cursor cursor = nitrite.getCollection(selected).find();
                    JacksonMapper mapper = new JacksonMapper();
                    for(Document document : cursor){
                        content.append(mapper.toJson(document)+"\n");
                    }
                    contentArea.setText(content.toString());
                }
            }
        });
        cp.add(collectionSelction);

        frame.setContentPane(cp);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);



    }

    public static void main(String[] args) {
       new App();
    }
}
