/*
 * Created by dings on 2017/9/6.
 * Minimum JRE : 1.8
 */

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static guru.nidi.graphviz.model.Factory.*;
public class libtest {

    

//    public static void main(String[] args) {
//        Graph graph = graph("example").directed().with(node("a").link(node("b")));
//        JFrame mainFrame = new JFrame("Vocatool");
//        frameInitialize(mainFrame);
//        mainFrame.setVisible(true);
//    }
    public static void main(String[] args) {
        WordGraph wordGraph = new WordGraph("this is an example, this was an example, this could be an example");
        for(String s : wordGraph.bridgeWord("this", "an"))
            System.out.println(s);
        wordGraph.shortestPath("he","tactician");
        BufferedImage bufferedImage = wordGraph.exportFullImage();
        wordGraph.exportSVGFile();
        JFrame jFrame = new JFrame();
        jFrame.add(new JPanel(){
            @Override
            public void paintComponent(Graphics G){
                G.drawImage(bufferedImage,0,0,null);
            }
        });
        jFrame.setSize(bufferedImage.getWidth()*5/4,bufferedImage.getHeight()*5/4 );
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);

    }
    public static void frameInitialize(JFrame mainFrame){
        JRadioButton[] textInputWay = new JRadioButton[2];
        JFileChooser fileChooser = new JFileChooser();
        JTextArea inputText = new JTextArea("Input text here.");
        JTextArea replaceText = new JTextArea("Input text to replace.");
        JButton generatePic = new JButton("生成图片");
        JButton queryIsConnected = new JButton("查询");
        JButton queryIsBridge = new JButton("查询");
        JTextField wordA_Bridge = new JTextField(), wordB_Bridge = new JTextField(),
                wordA_Con = new JTextField(), wordB_Con = new JTextField();
        JButton export = new JButton("导出");
        JButton clean = new JButton("清除");
        JScrollPane jScrollPaneForInput = new JScrollPane(inputText), jScrollPaneForReplace = new JScrollPane(replaceText);
        JPanel section1 = new JPanel(), section2 = new JPanel(), section3 = new JPanel();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800,600);
        Container container = mainFrame.getContentPane();
        section1.setBackground(Color.BLUE);
        container.add(section1, new GridBagConstraints(0,0,3,4,1,1,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(10,10,10,10),0,0));
        section2.setBackground(Color.RED);
        container.add(section2, new GridBagConstraints(0,GridBagConstraints.RELATIVE,3,5,1,1,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(10,10,10,10),0,0));
        section3.setBackground(Color.GREEN);
        container.add(section3, new GridBagConstraints(GridBagConstraints.RELATIVE,0,2,9,1,1,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(10,10,10,10),10,10));
    }

    public static void showGraph(Graph graph){
        final BufferedImage bufferedImage = Graphviz.fromGraph(graph).width(200).render(Format.PNG).toImage();
        JFrame jFrame = new JFrame();
        jFrame.add(new JPanel(){
            @Override
            public void paintComponent(Graphics G){
                G.drawImage(bufferedImage,0,0,null);
            }
        });
        jFrame.setSize(bufferedImage.getWidth()*5/4,bufferedImage.getHeight()*5/4 );
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }

    /**
     * digest
     * @param s 预处理的字符串（已无标点）
     */
    public static void digestGraph(String s){
        String[] words = s.split(" ");
        Set<String> wordset = new HashSet<String>(Arrays.asList(words));

    }
}
