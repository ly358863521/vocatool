/*
 * Created by dings on 2017/9/6.
 * Minimum JRE : 1.8
 */


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class libtest {

    

//    public static void main(String[] args) {
//        Graph graph = graph("example").directed().with(node("a").link(node("b")));
//        JFrame mainFrame = new JFrame("Vocatool");
//        frameInitialize(mainFrame);
//        mainFrame.setVisible(true);
//    }
    public static void main(String[] args) throws dotPathException{
        WordGraph.setDotPath("D:\\graphviz-2.38\\release\\bin\\dot.exe");
        try{System.out.println(WordGraph.testDotPath());}
        catch(Exception e){
            System.out.println(false);
        }
        WordGraph wordGraph = new WordGraph("this is an example, this was an example, this could be an example");
        Map<String, File> fileMap = new HashMap<>();
        wordGraph.allShortestPath("this", fileMap);
//        System.out.println(wordGraph.allShortestPath("this"));
        for(String s : wordGraph.bridgeWord("this", "an"))
            System.out.println(s);
        try{
            System.out.println(wordGraph.exportSVGFile());
        }
        catch (dotPathException d){
            d.printStackTrace();
        };
        BufferedImage bufferedImage = null;
        System.out.println(wordGraph.dotGenerate().getAbsolutePath());
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

//    public static void showGraph(Graph graph){
//        final BufferedImage bufferedImage = Graphviz.fromGraph(graph).width(200).render(Format.PNG).toImage();
//        JFrame jFrame = new JFrame();
//        jFrame.add(new JPanel(){
//            @Override
//            public void paintComponent(Graphics G){
//                G.drawImage(bufferedImage,0,0,null);
//            }
//        });
//        jFrame.setSize(bufferedImage.getWidth()*5/4,bufferedImage.getHeight()*5/4 );
//        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jFrame.setVisible(true);
//    }

    /**
     * digest
     * @param s 预处理的字符串（已无标点）
     */
    public static void digestGraph(String s){
        String[] words = s.split(" ");
        Set<String> wordset = new HashSet<String>(Arrays.asList(words));

    }
}
