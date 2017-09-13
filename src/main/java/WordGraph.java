import com.sun.deploy.xml.XMLParser;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.model.Label;
import jnr.ffi.annotations.In;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.to;

/**
 *
 */
public class WordGraph {
    String sourceString;
    String[] stringArray, wordArray;
    int nodeCount;
    int[] wordWeight;
    ArrayList<String> nodeList;
    BufferedImage fullPic = null;
    BufferedImage[] imageLib;
    BufferedImage[] newImage;
    Graph fullGraph;

    int weightArray[][];
    boolean[][] edgeIsMarked;
    boolean[] nodeIsMarked;
    Integer[][] nextWord,prevWord;
    public WordGraph(String s){
        // Preprocessor
        sourceString = s.replaceAll("[^A-Za-z\\s]","").toLowerCase();
        stringArray = sourceString.split("\\s");
        HashSet<String> nodeSet = new HashSet<>();
        Collections.addAll(nodeSet,stringArray);
        nodeCount = nodeSet.size();
        wordWeight = new int[nodeCount];
        weightArray = new int[nodeCount][nodeCount];
        edgeIsMarked = new boolean[nodeCount][nodeCount];
        nodeIsMarked = new boolean[nodeCount];
        nextWord = new Integer[nodeCount][stringArray.length];
        prevWord = new Integer[nodeCount][stringArray.length];
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < stringArray.length; j++) {
                nextWord[i][j] = 0;
                prevWord[i][j] = 0;
            }
        }
        nodeList = new ArrayList<>(nodeSet);
        nodeList.sort(String::compareTo);
        wordArray = (String[]) nodeList.toArray(new String[nodeList.size()]);

        // Node Creation
        for (int i = 0; i < stringArray.length - 1; i++) {
            // Possible Bug: Unconcurrent ID.
            int fromIndex = getIndex(stringArray[i]);
            int toIndex = getIndex(stringArray[i+1]);
            wordWeight[fromIndex]  += stringArray.length - i;
            weightArray[fromIndex][toIndex] += 1;
            nextWord[fromIndex][++nextWord[fromIndex][0]] = toIndex;
            prevWord[toIndex][++prevWord[toIndex][0]] = fromIndex;
        }
        List<Node> graphNodeList = this.nodeList.stream().map(param -> {return node(param).with("weight",wordWeight[getIndex(param)]);}).collect(Collectors.toList());
        LinkedList<LinkSource> linkSourcesList = new LinkedList<>();
        // Connecting
        for (int i = 0; i < wordArray.length; i++){
            for (int j = 0; j < wordArray.length;j++){
                if(i != j && weightArray[i][j] != 0){
                    linkSourcesList.push(graphNodeList.get(i).link(to(graphNodeList.get(j)).with("weight",wordWeight[i]).with(Label.of(String.valueOf(weightArray[i][j])))));
                }
            }
        }
//        graphNodeList.sort((Node a, Node b) -> {
//            if(wordWeight[graphNodeList.indexOf(a)] > wordWeight[graphNodeList.indexOf(b)]){
//                return 1;
//            }else if(wordWeight[graphNodeList.indexOf(a)] == wordWeight[graphNodeList.indexOf(b)]){
//                return 0;
//            }else return -1;
//        });
        fullGraph = graph("Word Diagram").directed().with( linkSourcesList.toArray(new LinkSource[linkSourcesList.size()]));
//        graphNodeList.get(0).with(Style.SOLID,guru.nidi.graphviz.attribute.Color.RED);
    }

    private int getIndex(String word){
        return Arrays.binarySearch(wordArray,word,String::compareToIgnoreCase);
    }

    public BufferedImage exportFullImage(){
        return Graphviz.fromGraph(fullGraph).render(Format.PNG).toImage();
    }

    public File exportSVGFile() {
        try{
            File svgTemp = File.createTempFile("graphviz_svg",".svg");
            Graphviz.fromGraph(fullGraph).render(Format.SVG).toFile(svgTemp);
            RandomAccessFile raf = new RandomAccessFile(svgTemp.getAbsolutePath(),"rw");
            byte[] bytes = new byte[0x50];
            raf.seek(0xF0);
            raf.read(bytes,0,0x50);
            String content = new String(bytes);
            char[] spacetoReplace = new char[20];
            Arrays.fill(spacetoReplace,' ');
            String newContent = content.replaceAll("(?i)FFFFFF","F0F0F0").replaceAll("stroke=\"transparent\"",new String(spacetoReplace));
            raf.seek(0xF0);
            for(int i:newContent.getBytes())
                raf.write(i);
            return svgTemp;
        }catch (IOException i0){
            return null;
        }
    }

    public String[] bridgeWord(String a, String b){
        HashSet<Integer> aConnection = new HashSet<>();
        HashSet<Integer> bConnection = new HashSet<>();
        Collections.addAll(aConnection, Arrays.copyOfRange(nextWord[getIndex(a)],1,nextWord[getIndex(a)][0]+1));
        Collections.addAll(bConnection, Arrays.copyOfRange(prevWord[getIndex(b)],1,prevWord[getIndex(b)][0]+1));
        aConnection.retainAll(bConnection);
        String[] result = new String[aConnection.size()];
        Integer[] indexArray = aConnection.toArray(new Integer[aConnection.size()]);
        for(int i = 0;i < aConnection.size();i++){
            result[i] = wordArray[indexArray[i]];
        }
        // Color
        Arrays.fill(nodeIsMarked,false);
        for(Integer i:indexArray){
            nodeIsMarked[i] = true;
        }
        // Redraw Graph
        redraw();
        return result;
    }


    // Redraw with colored edge and node.
    public void redraw(){
        // Node Creation
        List<Node> graphNodeList = new LinkedList<>();
        for(int i =0; i< nodeList.size();i++){
            if(nodeIsMarked[i])
                graphNodeList.add(node(nodeList.get(i)).with(Style.FILLED,Color.hsv(0,0.62,1)).with("weight",wordWeight[getIndex(nodeList.get(i))])); // Watermelon Red
            else
                graphNodeList.add(node(nodeList.get(i)).with("weight",wordWeight[getIndex(nodeList.get(i))]));
        }
//        graphNodeList.sort((Node a, Node b) -> {
//            if(wordWeight[graphNodeList.indexOf(a)] > wordWeight[graphNodeList.indexOf(b)]){
//                return 1;
//            }else if(wordWeight[graphNodeList.indexOf(a)] == wordWeight[graphNodeList.indexOf(b)]){
//                return 0;
//            }else return -1;
//        });
        LinkedList<LinkSource> linkSourcesList = new LinkedList<>();
//        // Connecting
//        for (int i = 0; i < stringArray.length - 1; i++) {
//            // Possible Bug: Unconcurrent ID.
//            int fromIndex = getIndex(stringArray[i]);
//            int toIndex = getIndex(stringArray[i+1]);
//            weightArray[fromIndex][toIndex] += 1;
//            nextWord[fromIndex][++nextWord[fromIndex][0]] = toIndex;
//        }
        for (int i = 0; i < wordArray.length; i++){
            for (int j = 0; j < wordArray.length;j++){
                if(i != j && weightArray[i][j] != 0){
                    if(edgeIsMarked[i][j])
                        linkSourcesList.push(graphNodeList.get(i).link(to(graphNodeList.get(j)).with("weight",wordWeight[i]).with(Label.of(String.valueOf(weightArray[i][j])),Color.hsv(0,0.62,1))));
                    else
                        linkSourcesList.push(graphNodeList.get(i).link(to(graphNodeList.get(j)).with("weight",wordWeight[i]).with(Label.of(String.valueOf(weightArray[i][j])))));
                }
            }
        }
        fullGraph = graph("Word Diagram").directed().with( linkSourcesList.toArray(new LinkSource[linkSourcesList.size()]));
    }

    public Integer[] shortestPath(String a, String b){
        // 得到集合
        // Occurrence Table
        // 返回最小距离，开始位置
        int start = getIndex(a);
        int end = getIndex(b);
        // 注意 这里没有end > start，因为start和end可能重复多次
        if(start > 0 && end > 0){
            LinkedList<Integer> startOccurrence = new LinkedList<>();
            LinkedList<Integer> endOccurrence = new LinkedList<>();
            for(Integer i = 0;i < stringArray.length;i++){
                if(stringArray[i].equals(a))
                    startOccurrence.add(i);
                if(stringArray[i].equals(b))
                    endOccurrence.add(i);
            }
            LinkedList<Integer> startIndex = new LinkedList<>();

            int distance = 0x7FFFFFFF; // Refactor to maxInteger.
            for(Integer i:startOccurrence)
                for(Integer j:endOccurrence)
                    // 起始超过了结束
                    if(i > j)
                        continue;
                    else if(j - i < distance){
                        startIndex.clear();
                        distance = j - i;
                        startIndex.add(i);
                    }else if(j - i == distance){
                        startIndex.add(i);
                    }
            Arrays.fill(nodeIsMarked,false);
            for(int i = 0;i < distance + 1;i++){
                nodeIsMarked[getIndex(stringArray[i+startIndex.getFirst()])] = true;
            }
            for (int i = 0; i < edgeIsMarked.length; i++) {
                for (int j = 0; j < edgeIsMarked[0].length; j++) {
                    edgeIsMarked[i][j] = false;
                }
            }
            for (int i = 0; i < distance; i++) {
                edgeIsMarked[getIndex(stringArray[startIndex.getFirst()+i])][getIndex(stringArray[startIndex.getFirst()+i+1])] = true;
            }
            startIndex.add(0,distance);
            redraw();
            return startIndex.toArray(new Integer[startIndex.size()]);
        }
        return null;
    }

    private Integer abs(Integer i){
        if(i < 0) return -i;
        return i;
    }

    public Graph toGraph(String name){
        LinkedList<LinkSource> linkedList = new LinkedList<>();
        // Performance issue: Parallelizable function.
        return graph(name).with();
    }
}
