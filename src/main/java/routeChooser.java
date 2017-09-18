import org.apache.batik.swing.JSVGCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ContainerAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;

public class routeChooser {
    public JPanel choosePanel;
    public JPanel showPanel;
    public JPanel listPanel;
    private JScrollPane listScroll;
    private JSVGCanvas routePanel;
    public static final int MULTI_ROUTE  =407;
    public static final int MULTI_END = 226;
    routeChooser(String[] strings,Map<String, File> svgPath,int stateCode){
//        radioButtonList = new ArrayList<>();
        this.choosePanel.setMinimumSize(new Dimension(600,400));
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
            listScroll.add(nRB);
        }
        listScroll.revalidate();
    }
}
