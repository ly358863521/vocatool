import org.apache.batik.swing.JSVGCanvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;

public class routeChooser {
    private JPanel choosePanel;
    private JPanel showPanel;
    private JPanel listPanel;
    private JScrollPane listScroll;
    private JSVGCanvas routePanel;
    private ArrayList<JRadioButton> radioButtonList;
    routeChooser(String[] strings,Map<String, File> svgPath){
        radioButtonList = new ArrayList<>();
        for(String s:strings){
            JRadioButton nRB = new JRadioButton(s);
            File svgFile = svgPath.get(s);
            nRB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(svgFile != null && svgFile.isFile())
                        routePanel.setURI(svgFile.toURI().toString());
                }
            });
        }
    }
}
