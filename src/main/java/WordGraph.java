import com.sun.deploy.xml.XMLParser;
import jnr.ffi.annotations.In;
import sun.awt.image.ImageWatched;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
class dotPathException extends Exception{}
class LinkSource{
    private String start;
    private String end;
    private int weight;
    private boolean color = false;

    public LinkSource(String start, String end, int weight, boolean color) {
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.color = color;
    }

    @Override
    public String toString() {
        StringBuilder string= new StringBuilder();
        string.append(String.format("\t%s -> %s [label=\"%d\"", start, end,weight));
        if(color)
            string.append(",color=red];");
        else string.append("];");
        return string.toString();
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public boolean getColor() {
        return color;
    }
}

class node{
    String name;
    int weight;
    boolean color;

    public node(String name, boolean color) {
        this.name = name;
        this.color = color;
    }

    public node(String name, int weight, boolean color) {
        this.name = name;
        this.weight = weight;
        this.color = color;
    }

    public node(String name) {
        this.name = name;
        this.color = false;
    }

    @Override
    public String toString() {
        if(color)
            return String.format("\t%s [color=red,weight=%d];", name,weight);
        return String.format("\t%s [weight=%d];", name,weight);
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public node getThis(){
        return this;
    }

    public boolean getColor() {
        return color;
    }
}

public class WordGraph {
    private String sourceString;
    private String[] stringArray, wordArray;
    private int nodeCount;
    private Map<String, Integer> wordWeight;
    private ArrayList<String> nodeList;
    private BufferedImage fullPic = null;
    private LinkedList<LinkSource> linkSourcesList;
    private String dotPath;
    private Map<String, node> graphNodeList;
    private int weightArray[][];
    private boolean[][] edgeIsMarked;
    private boolean[] nodeIsMarked;
    private Integer[][] nextWord,prevWord;
    public WordGraph(String s){
        // Preprocessor
        sourceString = s.replaceAll("[^A-Za-z\\s]","").toLowerCase();
        stringArray = sourceString.split("\\s");
        HashSet<String> nodeSet = new HashSet<>();
        Collections.addAll(nodeSet,stringArray);
        nodeCount = nodeSet.size();
        wordWeight = new HashMap<>(100);
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
            wordWeight.replace(stringArray[i],wordWeight.getOrDefault(stringArray[i],0) + stringArray.length - i);
            weightArray[fromIndex][toIndex] += 1;
            nextWord[fromIndex][++nextWord[fromIndex][0]] = toIndex;
            prevWord[toIndex][++prevWord[toIndex][0]] = fromIndex;
        }
        this.graphNodeList = this.nodeList.stream().collect(Collectors.toMap(Function.identity(), (name) -> new node(name, wordWeight.getOrDefault(name,1) ,false)));
        linkSourcesList = new LinkedList<>();
        // Connecting
        for (int i = 0; i < wordArray.length; i++){
            for (int j = 0; j < wordArray.length;j++){
                if(i != j && weightArray[i][j] != 0){
                    //linkSourcesList.push(graphNodeList.get(i).link(to(graphNodeList.get(j)).with("weight",wordWeight[i]).with(Label.of(String.valueOf(weightArray[i][j])))));
                    linkSourcesList.add(new LinkSource(wordArray[i], wordArray[j], weightArray[i][j],false));
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
//        fullGraph = graph("Word Diagram").directed().with( linkSourcesList.toArray(new LinkSource[linkSourcesList.size()]));
//        graphNodeList.get(0).with(Style.SOLID,guru.nidi.graphviz.attribute.Color.RED);
    }

    public String getDotPath() {
        return dotPath;
    }

    public void setDotPath(String dotPath) {
        this.dotPath = dotPath;
    }

    private int getIndex(String word){
        return Arrays.binarySearch(wordArray,word,String::compareToIgnoreCase);
    }

    public BufferedImage exportFullImage(){
        return null;
    }

    public File dotGenerate(){
        try {
            File dotFile = File.createTempFile("graphviz_dot", ".dot");
            BufferedWriter writer = new BufferedWriter(new FileWriter(dotFile));
            writer.write("digraph G{");
            writer.newLine();
            // Color node
            for(node i:this.graphNodeList.values()){
                writer.write(i.toString());
                writer.newLine();
            }
            // Add edges
            for(LinkSource linkSource : this.linkSourcesList){
                writer.write(linkSource.toString());
                writer.newLine();
            }
            writer.write("}");
            writer.newLine();
            writer.close();
            return dotFile;
        }catch (IOException i0){
            return null;
        }
    }

    public File exportSVGFile() {
        try{
            File svgTemp = File.createTempFile("graphviz_svg",".svg");

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
//        List<node> graphNodeList = new LinkedList<>();
//        for(int i =0; i< nodeList.size();i++){
//            if(nodeIsMarked[i])
//                graphNodeList.add(node(nodeList.get(i)).with(Style.FILLED,Color.hsv(0,0.62,1)).with("weight",wordWeight[getIndex(nodeList.get(i))])); // Watermelon Red
//            else
//                graphNodeList.add(node(nodeList.get(i)).with("weight",wordWeight[getIndex(nodeList.get(i))]));
//        }
//        graphNodeList.sort((Node a, Node b) -> {
//            if(wordWeight[graphNodeList.indexOf(a)] > wordWeight[graphNodeList.indexOf(b)]){
//                return 1;
//            }else if(wordWeight[graphNodeList.indexOf(a)] == wordWeight[graphNodeList.indexOf(b)]){
//                return 0;
//            }else return -1;
//        });
        this.linkSourcesList.clear();
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
                        linkSourcesList.add(new LinkSource(wordArray[i],wordArray[j],weightArray[i][j],true));
                    else
                        linkSourcesList.add(new LinkSource(wordArray[i],wordArray[j],weightArray[i][j],false));
                }
            }
        }
        for(int i = 0;i < nodeIsMarked.length;i++){
            if(nodeIsMarked[i]){
                graphNodeList.get(wordArray[i]).setColor(true);
            }
        }
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
                    if(j - i < distance){
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

}
