import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import guru.nidi.graphviz.model.Graph;
import javafx.util.Pair;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;

public class MainWindow {
    private JTabbedPane tabbedPane1;
    private JRadioButton radioButton1;
    private JTextField textField1;
    private JRadioButton radioButton2;
    private JTextArea textArea1;
    private JButton generateButton;
    private JButton randomBegin;
    private JButton nextStep;
    private JButton exportRoute;
    private JTextField textField3;
    private JButton singleSP;
    private JButton allSP;
    private JTextArea textArea2;
    private JButton genNewText;
    private JButton fullImage;
    private JButton exportButton;
    private JPanel mainPanel;
    private JPanel submainPanel;
    private JPanel controlZone;
    private JPanel inputFile;
    private JPanel functionTab;
    private JPanel imagePanel;
    private JPanel imageZone;
    private JPanel exportZone;
    private JPanel _exportZone;
    private JPanel inputPanel;
    private JPanel _inputPanel;
    private JPanel textPanel;
    private JPanel textGrid;
    private JPanel ButtonPanel;
    private JPanel ButtonGrid;
    private JPanel randomTab;
    private JPanel randomPanel;
    private JPanel shortestTab;
    private JPanel shortestBPanel;
    private JPanel newSPanel;
    private JPanel SArea;
    private JScrollPane SPanel_s;
    private JPanel SCommit;
    private JScrollPane text_s;
    private JPanel shortestPanelB;
    private JPanel shortestPanelA;
    private JTextField textField2;
    private JPanel bridgeTab;
    private JPanel bButtonPanel;
    private JPanel endPanelB;
    private JPanel endPointA;
    private JButton showBridgeButton;
    private JTextField endTextB;
    private JTextField endTextA;
    private JButton importFileChooseButton;
    private JSVGCanvas svgPanel;
    private WordGraph wordGraph;
    private File chosenFile;
    public MainWindow() {
        importFileChooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.showDialog(new JLabel(), "导入");
                File file = jFileChooser.getSelectedFile();
                if(file != null && file.isFile()) {
                    textField1.setText(file.getAbsolutePath());
                    radioButton1.setSelected(true);
                    radioButton2.setSelected(false);
                    System.out.println(file.getAbsoluteFile());
                    chosenFile = file;
                }else{
                    JOptionPane.showMessageDialog(mainPanel, "未选择任何文件！", "提示",JOptionPane.WARNING_MESSAGE);
                    textField1.setText("");
                }
            }
        });
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(radioButton1.isSelected()) {
                    try {
                        if(chosenFile == null || !chosenFile.getAbsolutePath().equals(textField1.getText())){
                            chosenFile = new File(textField1.getText());
                        }
                        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(chosenFile));
                    } catch (IOException e0) {
                        // File is removed after selected.
                        JOptionPane.showMessageDialog(mainPanel, "文件读取失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else if(radioButton2.isSelected()) {
                    wordGraph = new WordGraph(textArea1.getText());
                    String svgPath = wordGraph.exportSVGFile().toURI().toString();
                    System.out.println(svgPath);
                    BufferedImage bufferedImage = wordGraph.exportFullImage();
//                    imageZone.removeAll();
                    svgPanel.setURI(svgPath);
                    svgPanel.setEnableZoomInteractor(true);
//                    System.out.println(imageZone.getSize());
//                    JFrame jFrame = new JFrame();
//                    jFrame.add(new JPanel(){
//                        @Override
//                        public void paintComponent(Graphics G){
//                            super.paintComponent(G);
//                            G.drawImage(bufferedImage,0,0,imageZone.getWidth(),imageZone.getHeight(),imageZone);
//                        }
//                    });
//                    jFrame.setSize(bufferedImage.getWidth()*5/4,bufferedImage.getHeight()*5/4 );
////                    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                    jFrame.setVisible(true);
                }
//                wordGraph = new WordGraph();

            }
        });
        textArea1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                radioButton2.setSelected(true);
                radioButton1.setSelected(false);
            }
        });
        textField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                radioButton1.setSelected(true);
                radioButton2.setSelected(false);
            }
        });
        singleSP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer[] res = wordGraph.shortestPath(textField2.getText().toLowerCase(),textField3.getText().toLowerCase());
                if(res == null){
                    JOptionPane.showMessageDialog(mainPanel, "未找到最短路径。", "警告", JOptionPane.WARNING_MESSAGE);
                }else{
                    String svgPath = wordGraph.exportSVGFile().toURI().toString();
                    System.out.println(svgPath);
                    BufferedImage bufferedImage = wordGraph.exportFullImage();
                    svgPanel.setURI(svgPath);
                }
            }
        });
        showBridgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wordGraph.bridgeWord(endTextA.getText().toLowerCase(),endTextB.getText().toLowerCase());
                String svgPath = wordGraph.exportSVGFile().toURI().toString();
                System.out.println(svgPath);
                BufferedImage bufferedImage = wordGraph.exportFullImage();
                svgPanel.setURI(svgPath);
            }
        });
    }

    public static void main(String[] args) {
        // GridLayoutManager from JetBrains caused a NullPointException in ui loading.
        JFrame frame = new JFrame("实验1 单词图");
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e){
            e.printStackTrace();
        }
        MainWindow mainWindow = new MainWindow();
        frame.setContentPane(mainWindow.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        svgPanel.setBackground(Color.LIGHT_GRAY);
        // TODO: place custom component creation code here

    }
}
