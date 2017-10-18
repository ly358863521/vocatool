import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Map;

import javax.swing.*;

import org.apache.batik.swing.JSVGCanvas;

public class routeChooser {
    public static final int MULTI_ROUTE = 407;
    public static final int MULTI_END   = 226;
    public JPanel           choosePanel;
    public JPanel           showPanel;
    public JPanel           listPanel;
    private JScrollPane     listScroll;
    private JSVGCanvas      routePanel;

    routeChooser(String[] strings, Map<String, File> svgPath, int stateCode) {

//      radioButtonList = new ArrayList<>();
        this.choosePanel.setMinimumSize(new Dimension(600, 400));

        for (String s : strings) {
            JRadioButton nRB     = new JRadioButton(s);
            File         svgFile = svgPath.get(s);

            nRB.addActionListener(new ActionListener() {
                                      @Override
                                      public void actionPerformed(ActionEvent e) {
                                          if ((svgFile != null) && svgFile.isFile()) {
                                              routePanel.setURI(svgFile.toURI().toString());
                                          }
                                      }
                                  });
            listScroll.add(nRB);
        }

        listScroll.revalidate();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
