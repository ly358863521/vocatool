package com.ses.vocatool;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The type Main window.
 */
public class MainWindow {
    /**
     * Log4j Logger
     */
    public static org.slf4j.Logger logger = LoggerFactory.getLogger(MainWindow.class);
    /**
     * Declare tabbedPane1.
     */
    private JTabbedPane tabbedPane1;

    /**
     * Declare radioButton1.
     */
    private JRadioButton radioButton1;

    /**
     * Declare textField1.
     */
    private JTextField textField1;

    /**
     * Declare radioButton2.
     */
    private JRadioButton radioButton2;

    /**
     * Declare textArea1.
     */
    private JTextArea textArea1;

    /**
     * Declare generateButton.
     */
    private JButton generateButton;

    /**
     * Declare randomBegin.
     */
    private JButton randomBegin;

    /**
     * Declare exportRoute.
     */
    private JButton exportRoute;

    /**
     * Declare textField3.
     */
    private JTextField textField3;

    /**
     * Declare singleSP.
     */
    private JButton singleSP;

    /**
     * Declare allSP.
     */
    private JButton allSP;

    /**
     * Declare textArea2.
     */
    private JTextArea textArea2;

    /**
     * Declare genNewText.
     */
    private JButton genNewText;

    /**
     * Declare exportButton.
     */
    private JButton exportButton;

    /**
     * Declare mainPanel.
     */
    private JPanel mainPanel;

    /**
     * Declare submainPanel.
     */
    private JPanel submainPanel;

    /**
     * Declare controlZone.
     */
    private JPanel controlZone;

    /**
     * Declare inputFile.
     */
    private JPanel inputFile;

    /**
     * Declare functionTab.
     */
    private JPanel functionTab;

    /**
     * Declare imagePanel.
     */
    private JPanel imagePanel;

    /**
     * Declare imageZone.
     */
    private JPanel imageZone;

    /**
     * Declare exportZone.
     */
    private JPanel exportZone;

    /**
     * Declare innerExportZone.
     */
    private JPanel innerExportZone;

    /**
     * Declare inputPanel.
     */
    private JPanel inputPanel;

    /**
     * Declare innerInputPanel.
     */
    private JPanel innerInputPanel;

    /**
     * Declare textPanel.
     */
    private JPanel textPanel;

    /**
     * Declare textGrid.
     */
    private JPanel textGrid;

    /**
     * Declare buttonPanel.
     */
    private JPanel buttonPanel;

    /**
     * Declare buttonGrid.
     */
    private JPanel buttonGrid;

    /**
     * Declare randomTab.
     */
    private JPanel randomTab;

    /**
     * Declare randomPanel.
     */
    private JPanel randomPanel;

    /**
     * Declare shortestTab.
     */
    private JPanel shortestTab;

    /**
     * Declare shortestBPanel.
     */
    private JPanel shortestBPanel;

    /**
     * Declare newSPanel.
     */
    private JPanel newSPanel;

    /**
     * Declare sArea.
     */
    private JPanel sArea;

    /**
     * Declare sPanelS.
     */
    private JScrollPane sPanelS;

    /**
     * Declare sCommit.
     */
    private JPanel sCommit;

    /**
     * Declare textS.
     */
    private JScrollPane textS;

    /**
     * Declare shortestPanelB.
     */
    private JPanel shortestPanelB;

    /**
     * Declare shortestPanelA.
     */
    private JPanel shortestPanelA;

    /**
     * Declare textField2.
     */
    private JTextField textField2;

    /**
     * Declare bridgeTab.
     */
    private JPanel bridgeTab;

    /**
     * Declare bButtonPanel.
     */
    private JPanel bButtonPanel;

    /**
     * Declare endPanelB.
     */
    private JPanel endPanelB;

    /**
     * Declare endPointA.
     */
    private JPanel endPointA;

    /**
     * Declare showBridgeButton.
     */
    private JButton showBridgeButton;

    /**
     * Declare endTextB.
     */
    private JTextField endTextB;

    /**
     * Declare endTextA.
     */
    private JTextField endTextA;

    /**
     * Declare importFileChooseButton.
     */
    private JButton importFileChooseButton;

    /**
     * Declare svgPanel.
     */
    private JSVGCanvas svgPanel;

    /**
     * Declare dotPathButton.
     */
    private JButton dotPathButton;

    /**
     * Declare wordGraph.
     */
    private WordGraph wordGraph;

    /**
     * Declare chosenFile.
     */
    private File chosenFile;

    /**
     * Declare list of random Route.
     */
    private List<String> randomRoute;

    public static final String ERROR_MESSAGE = "错误", NOTICE_MESSAGE = "提示", DOT_INVALID = "Dot程序无响应或未配置！";

    /**
     * Instantiates a new Main window.
     */
    MainWindow() {
        // 导入按钮
        importFileChooseButton.addActionListener((ActionEvent
                                                          e) -> {
            final JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.showDialog(new JLabel(), "导入");
            final File file = jFileChooser.getSelectedFile();
            if (file != null && file.isFile()) {
                textField1.setText(file.getAbsolutePath());
                radioButton1.setSelected(true);
                radioButton2.setSelected(false);
                System.out.println(file.getAbsoluteFile());
                chosenFile = file;
            } else {
                JOptionPane.showMessageDialog(mainPanel,
                        "未选择任何文件！", NOTICE_MESSAGE, JOptionPane.WARNING_MESSAGE);
                textField1.setText("");
            }
        });
        // 生成按钮
        generateButton.addActionListener((ActionEvent e) -> {
            if (radioButton1.isSelected()) {
                try {
                    if (chosenFile == null
                            || !chosenFile.getAbsolutePath()
                                    .equals(textField1.getText())) {
                        chosenFile = new File(textField1.getText());
                    }
                    BufferedReader bufferedReader =
                            new BufferedReader(new FileReader(chosenFile));
                    StringBuilder document = new StringBuilder();
                    while (true) {
                        final String read = bufferedReader.readLine();
                        if (read == null) {
                            break;
                        }
                        document.append(read);
                    }
                    wordGraph = new WordGraph(document.toString());
                } catch (IOException e0) {
                    // File is missing after selected.
                    JOptionPane.showMessageDialog(mainPanel,
                            "文件读取失败！",
                            ERROR_MESSAGE,
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (radioButton2.isSelected()) {
                wordGraph = new WordGraph(textArea1.getText());
//                    System.out.println(imageZone.getSize());
//                    JFrame jFrame = new JFrame();
//                    jFrame.add(new JPanel(){
//                        @Override
//                        public void paintComponent(Graphics G){
//                            super.paintComponent(G);
//                            G.drawImage(bufferedImage,0,0,
//                                    imageZone.getW
//                                      idth(),imageZone.getHeight(),imageZone);
//                        }
//                    });
//                    jFrame.setSize(bufferedImage.
//                      getWidth()*5/4,bufferedImage.getHeight()*5/4 );
////                    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                    jFrame.setVisible(true);
            }
//                wordGraph = new WordGraph();
            try {
                String svgPath = wordGraph.exportSVGFile().toURI().toString();
                System.out.println(svgPath);
                svgPanel.loadSVGDocument(svgPath);
//                        svgPanel.setEnableZoomInteractor(true);
            } catch (DotPathException d0) {
                JOptionPane.showMessageDialog(mainPanel,
                        DOT_INVALID,
                        ERROR_MESSAGE,
                        JOptionPane.ERROR_MESSAGE);
                d0.printStackTrace();
            }
            System.gc();
        });
        // 自动选择
        textArea1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                super.focusGained(e);
                radioButton2.setSelected(true);
                radioButton1.setSelected(false);
            }
        });
        textField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                super.focusGained(e);
                radioButton1.setSelected(true);
                radioButton2.setSelected(false);
            }
        });
        // 最短路径
        singleSP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ArrayList<File> fileList = new ArrayList<>();
                try {
                    Integer res = wordGraph.shortestPath(textField2.getText().toLowerCase(Locale.SIMPLIFIED_CHINESE),
                            textField3.getText().toLowerCase(Locale.SIMPLIFIED_CHINESE), fileList);
                    if (res == null || res == WordGraph.UNREACHABLE) {
                        JOptionPane.showMessageDialog(mainPanel,
                                "未找到最短路径。",
                                "警告",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        if (1 == fileList.size()) {
                            JOptionPane.showMessageDialog(mainPanel,
                                    String.format("最短路径长度为%d.", res),
                                    "查找到最短路径",
                                    JOptionPane.INFORMATION_MESSAGE);
                            svgPanel.setURI(fileList.get(0).toURI().toString());
                        } else {
                            int reply = JOptionPane.showConfirmDialog(
                                    mainPanel,
                                    String.format("查找到%d条最短路径，路径长度为%d，是否查看所有最短路径？",
                                            fileList.size(), res),
                                    "查找到多条最短路径",
                                    JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION) {
                                ArrayList<JRadioButton> radioButtonList = new ArrayList<>();
                                final JPanel boxPanel = new JPanel();
                                boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
                                JSVGCanvas svgCanvas = new JSVGCanvas();
                                // Add radio button
                                for (int i = 0; i < fileList.size(); i++) {
                                    File file = fileList.get(i);
                                    if (file != null && file.exists()) {
                                        JRadioButton jRadioButton =
                                                new JRadioButton(String.format("路线 %d", i + 1));
                                        jRadioButton.addActionListener((ActionEvent e0) -> {
                                            svgCanvas.setURI(file.toURI().toString());
                                            radioButtonList.forEach((JRadioButton j) -> {
                                                j.setSelected(false);
                                            });
                                            jRadioButton.setSelected(true);
                                        });
                                        boxPanel.add(jRadioButton);
                                        radioButtonList.add(jRadioButton);
                                    } else {
                                        System.out.println(
                                                String.format(
                                                        "File %d removed accidentally.", i));
                                    }
                                }
                                JFrame subFrame = new JFrame("所有最短路径");
                                subFrame.getContentPane().setLayout(new GridBagLayout());
                                subFrame.getContentPane().setMinimumSize(
                                        new Dimension(600, 400));
                                GridBagConstraints scrollConstraint =
                                        new GridBagConstraints();
                                scrollConstraint.weightx = 2;
                                scrollConstraint.weighty = 1;
                                GridBagConstraints svgConstraint =
                                        new GridBagConstraints();
                                svgConstraint.weightx = 3;
                                svgConstraint.weighty = 1;
                                svgConstraint.gridx =
                                        GridBagConstraints.RELATIVE;
                                svgConstraint.gridy = GridBagConstraints.NONE;
                                svgConstraint.fill = GridBagConstraints.BOTH;
                                subFrame.getContentPane().add(
                                        new JScrollPane(boxPanel),
                                        scrollConstraint);
                                subFrame.getContentPane().add(
                                        svgCanvas, svgConstraint);
                                subFrame.setDefaultCloseOperation(
                                        WindowConstants.DISPOSE_ON_CLOSE);
                                subFrame.pack();
                                subFrame.setSize(800, 600);
                                subFrame.setVisible(true);
                            } else {
                                svgPanel.setURI(
                                        fileList.get(0).toURI().toString());
                            }
                        }
                    }
                } catch (DotPathException d0) {
                    d0.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel,
                            DOT_INVALID,
                            ERROR_MESSAGE,
                            JOptionPane.ERROR_MESSAGE);
                }
                System.gc();
            }
        });
        // 桥接词
        showBridgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    try {
                        wordGraph.bridgeWord(
                                endTextA.getText().toLowerCase(Locale.SIMPLIFIED_CHINESE),
                                endTextB.getText().toLowerCase(Locale.SIMPLIFIED_CHINESE));
                    } catch (ArrayIndexOutOfBoundsException e2) {
                        JOptionPane.showMessageDialog(mainPanel,
                                "图中没有这两个词！",
                                ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                    }
                    String svgPath =
                            wordGraph.exportSVGFile().toURI().toString();
                    System.out.println(svgPath);
//                    BufferedImage bufferedImage = wordGraph.exportFullImage();
                    svgPanel.setURI(svgPath);
                } catch (DotPathException d0) {
                    JOptionPane.showMessageDialog(mainPanel,
                            DOT_INVALID,
                            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                    d0.printStackTrace();
                }
                System.gc();
            }
        });
        // dot.exe 程序选择
        dotPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.setFileFilter(new FileNameExtensionFilter(
                        "GraphViz dot(*.exe)", "exe"));
                jFileChooser.showDialog(new JLabel(), "选择dot程序");
                File file = jFileChooser.getSelectedFile();
                WordGraph.setDotPath(file.getAbsolutePath());
                try {
                    String response = WordGraph.testDotPath();
                    if (response.toLowerCase(Locale.SIMPLIFIED_CHINESE).contains("graphviz")) {
                        JOptionPane.showMessageDialog(mainPanel,
                                "成功调用！\n" + response,
                                NOTICE_MESSAGE,
                                JOptionPane.PLAIN_MESSAGE);

                    } else {
                        int i = JOptionPane.showConfirmDialog(
                                mainPanel,
                                "Dot程序可能有问题。是否继续使用？\n" + response,
                                "警告",
                                JOptionPane.WARNING_MESSAGE);
                        if (i == JOptionPane.NO_OPTION) {
                            dotPathButton.getAction().actionPerformed(e);
                        }
                    }
                } catch (IOException i0) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Dot程序无法调用！",
                            ERROR_MESSAGE,
                            JOptionPane.ERROR_MESSAGE);
                } catch (DotPathException i0) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Dot程序无响应！",
                            ERROR_MESSAGE,
                            JOptionPane.ERROR_MESSAGE);
                }
                System.gc();
            }
        });
        // 生成新文本
        genNewText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Random random = new Random();
                final String[] sentenceArray = textArea2.getText().split("\\s");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < sentenceArray.length - 1; i++) {
                    String[] bridge = wordGraph.bridgeWord(
                            sentenceArray[i].toLowerCase(Locale.SIMPLIFIED_CHINESE)
                                    .replaceAll("[^A-Za-z\\s]", ""),
                            sentenceArray[i + 1].toLowerCase(Locale.SIMPLIFIED_CHINESE)
                                    .replaceAll("[^A-Za-z\\s]", ""));
                    sb.append(sentenceArray[i] + " ");
                    if (bridge.length > 0) {
                        sb.append(bridge[random.nextInt(bridge.length)]);
                        sb.append(' ');
                    }
                }
                sb.append(sentenceArray[sentenceArray.length - 1]);
                textArea2.setText(sb.toString());
                System.gc();
            }
        });
        // 导出
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    jFileChooser.setFileFilter(new FileNameExtensionFilter(
                            "PNG(.png)", "png"));
                    jFileChooser.setFileFilter(new FileNameExtensionFilter(
                            "SVG(.svg)", "svg"));
                    int i = jFileChooser.showDialog(new JLabel(), "导出");
                    if (i == JFileChooser.CANCEL_OPTION) {
                        return;
                    }
                    File file = jFileChooser.getSelectedFile();
                    switch (jFileChooser.getFileFilter().getDescription()) {
                        case "PNG(.png)":
                            wordGraph.exportPNG(file);
                            break;
                        case "SVG(.svg)":
                            if (!wordGraph.exportSVGFile().renameTo(file)) {
                                throw new FileNotFoundException();
                            }
                            break;
                        default:
                            System.out.println("No such option");
                    }
                } catch (TranscoderException t) {
                    t.printStackTrace();
                } catch (DotPathException d) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "dot程序未配置。",
                            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                } catch (FileNotFoundException f) {
                    JOptionPane.showMessageDialog(
                            mainPanel,
                            "无法创建文件。",
                            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                }
                System.gc();
            }
        });
        // 单源最短路径
        allSP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ConcurrentMap<String, File> fileMap = new ConcurrentHashMap<>();
                try {
                    String[] endpoint =
                            wordGraph.allShortestPath(
                                    textField2.getText(), fileMap);
                    ArrayList<JRadioButton> radioButtonList = new ArrayList<>();
                    JPanel boxPanel = new JPanel();
                    boxPanel.setMinimumSize(new Dimension(300, 600));
                    boxPanel.setLayout(
                            new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
                    JSVGCanvas svgCanvas = new JSVGCanvas();
                    // Add radio button
                    for (final String s : endpoint) {
                        File file = fileMap.get(s);
                        if (file != null && file.exists()) {
                            JRadioButton jRadioButton = new JRadioButton(s);
                            jRadioButton.addActionListener((ActionEvent e0) -> {
                                svgCanvas.setURI(file.toURI().toString());
                                radioButtonList.forEach((JRadioButton j) -> {
                                    j.setSelected(false);
                                });
                                jRadioButton.setSelected(true);
                            });
                            boxPanel.add(jRadioButton);
                            radioButtonList.add(jRadioButton);
                        } else {
                            System.out.println(
                                    String.format(
                                        "File %s removed accidentally.", s));
                        }
                    }
                    JFrame subFrame = new JFrame("所有最短路径");
                    subFrame.getContentPane().setLayout(new GridBagLayout());
                    subFrame.getContentPane().setMinimumSize(
                            new Dimension(600, 400));
                    GridBagConstraints scrollConstraint
                            = new GridBagConstraints();
                    scrollConstraint.weightx = 2;
                    scrollConstraint.weighty = 1;
                    GridBagConstraints svgConstraint = new GridBagConstraints();
                    svgConstraint.weightx = 3;
                    svgConstraint.weighty = 1;
                    svgConstraint.gridx = GridBagConstraints.RELATIVE;
                    svgConstraint.gridy = GridBagConstraints.NONE;
                    svgConstraint.fill = GridBagConstraints.BOTH;
                    subFrame.getContentPane().add(
                            new JScrollPane(boxPanel), scrollConstraint);
                    subFrame.getContentPane().add(svgCanvas, svgConstraint);
                    subFrame.setDefaultCloseOperation(
                            WindowConstants.DISPOSE_ON_CLOSE);
                    subFrame.pack();
                    subFrame.setSize(800, 600);
                    subFrame.setVisible(true);
                } catch (DotPathException d0) {
                    JOptionPane.showMessageDialog(mainPanel, DOT_INVALID,
                            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // 随机游走
        randomBegin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LinkedList<String> path = new LinkedList<>();
                try {
                    File svgFile = wordGraph.randomPath(path);
                    svgPanel.setURI(svgFile.toURI().toString());
                    randomRoute = path;
                } catch (DotPathException d0) {
                    JOptionPane.showMessageDialog(
                            mainPanel, DOT_INVALID,
                            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                    d0.printStackTrace();
                }
                System.gc();
            }
        });
        // 导出随机游走
        exportRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.setFileFilter(
                        new FileNameExtensionFilter("(.txt)", ".txt"));
                jFileChooser.showDialog(new JLabel(), "导入");
                File file = jFileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }
                String delimiter = JOptionPane.showInputDialog(
                        mainPanel,
                        "输入分隔符",
                        "文件分隔符",
                        JOptionPane.PLAIN_MESSAGE);
                try {
                    BufferedWriter writer =
                            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
                    writer.write(String.join(delimiter, randomRoute));
                    writer.newLine();
                    writer.close();
                } catch (IOException i0) {
                    JOptionPane.showMessageDialog(
                            mainPanel, "文件无法写入!",
                            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                }
                System.gc();
            }
        });
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(final String[] args) {
        // GridLayoutManager from JetBrains
        // caused a NullPointException in ui loading.
        JFrame frame = new JFrame("实验1 单词图");
        try {
            javax.swing.UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException
                | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        MainWindow mainWindow = new MainWindow();
        frame.setContentPane(mainWindow.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Create UI.
     */
    private void createUIComponents() {
        svgPanel.setBackground(Color.LIGHT_GRAY);
    }
}
