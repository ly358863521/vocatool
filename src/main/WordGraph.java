import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
// dot路径未找到
class dotPathException extends Exception{}

// 边类
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

/**
 * 节点类
 */
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
    private LinkedList<LinkSource> linkSourcesList;
    private static String dotPath;
    private Map<String, node> graphNodeList;
    private int weightArray[][];
    private boolean[][] edgeIsMarked;
    private boolean[] nodeIsMarked;
    private Integer[][] nextWord,prevWord;
    private static boolean dotAvailable = false;
    public static int UNREACHABLE = Integer.MAX_VALUE;
    public WordGraph(String s){
        // Preprocessor
        sourceString = s.replaceAll("[^A-Za-z\\s]","").toLowerCase();
        stringArray = sourceString.split("\\s+");
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
            // Possible Bug: Inconcurrent ID.
            int fromIndex = getIndex(stringArray[i]);
            int toIndex = getIndex(stringArray[i+1]);
            wordWeight.replace(stringArray[i],wordWeight.getOrDefault(stringArray[i],0) + stringArray.length - i);
            weightArray[fromIndex][toIndex] += 1;
            if(!hasObejct(nextWord[fromIndex],1,nextWord[fromIndex][0],toIndex))
                nextWord[fromIndex][++nextWord[fromIndex][0]] = toIndex;
            if(!hasObejct(prevWord[toIndex],1,prevWord[toIndex][0],fromIndex))
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
    }

    private static boolean hasObejct(Object[] objects, int start, int end, Object o){
        for (int i = start; i < end; i++) {
            if (objects[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得当前全局dot路径
     * @return dot路径
     */
    public static String getDotPath() {
        return WordGraph.dotPath;
    }

    /**
     * 设置当前全局dot路径
     * @param dotPath dot路径
     */
    public static void setDotPath(String dotPath) {
        WordGraph.dotPath = dotPath;
    }

    /**
     * 获得某一个词在图中的编号
     * @param word 词
     * @return 编号
     * @apiNote 若编号为负数，说明这个词不在图中
     */
    private int getIndex(String word){
        return Arrays.binarySearch(wordArray,word,String::compareToIgnoreCase);
    }


    /**
     * 根据当前图的信息导出dot文件到一个临时文件中
     * @return 临时文件File对象
     */
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
    private static String testString = null;

    /**
     * 测试dot程序是否可用
     * @return 测试命令返回值
     * @throws IOException 无法访问指定目录
     * @throws dotPathException dot程序无响应或者响应不符合预期
     */
    public static String testDotPath() throws IOException,dotPathException{
        Thread testThread = new Thread(() -> {
            try {
                Process p = Runtime.getRuntime().exec(dotPath + " -V");
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                WordGraph.testString = sb.toString();
            }catch (InterruptedIOException e){
                testString = "No Response.";
            }catch (IOException e){
                WordGraph.testString = null;
            }
        } );
        testThread.start();
        try {
            Thread.sleep(400);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        testThread.interrupt();
        if(testString.contains("dot") && testString.contains("graphviz"))
            WordGraph.dotAvailable = true;
        if(testString == null)
            throw new IOException();
        if(testString.equals("No Response."))
            throw new dotPathException();
        return testString;
    }

    /**
     * 导出svg文件到临时目录
     * @return 临时目录中的File对象
     * @throws dotPathException dot程序无响应
     */
    public File exportSVGFile() throws dotPathException {
        try{
            if(!WordGraph.dotAvailable)
                throw new dotPathException();
            File svgTemp = File.createTempFile("graphviz_svg",".svg");
            File dotTemp = this.dotGenerate();
            Process p = Runtime.getRuntime().exec(String.format("%s -Tsvg %s -o %s", WordGraph.dotPath,dotTemp.getAbsolutePath(),svgTemp.getAbsolutePath()));
            while(p.isAlive())
                try{
                    Thread.sleep(10);
                    System.out.println("Wait for 10ms");
                }catch (InterruptedException e){
                }
            // Fix incompatible svg line.
            // 0x01 212D2D20
            // 0x31 22202D2D
            RandomAccessFile raf = new RandomAccessFile(svgTemp,"rw");
            raf.seek(0x01);
            raf.writeInt(0x212D2D20);
            raf.seek(0x31);
            raf.writeInt(0x22202D2D);
            raf.close();
//                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                    String line = null;
//                    StringBuilder sb = new StringBuilder();
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//                    WordGraph.testString = sb.toString();
            return svgTemp;
        }catch (IOException i0){
            return null;
        }
    }

    /**
     * 导出png对象
     * @param file 可以写入的File对象
     * @throws dotPathException dot程序无响应
     * @throws FileNotFoundException 目标png无法写入
     * @throws TranscoderException 转码器错误
     */
    public void exportPNG(File file) throws dotPathException,FileNotFoundException,TranscoderException{
        PNGTranscoder pngTranscoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new FileInputStream(this.exportSVGFile()));
        TranscoderOutput output = new TranscoderOutput(new FileOutputStream(file));
        pngTranscoder.transcode(input,output);
    }

    /**
     * 查找桥接词
     * @param a 起点
     * @param b 终点
     * @return 词组表
     * @apiNote 若没有桥接词，返回空数组
     * @apiNote 若a和b不在图中，返回null
     */
    public String[] bridgeWord(String a, String b){
        try {
            this.linkSourcesList.stream().forEach((LinkSource l) -> {
                l.setColor(false);
            });
            this.graphNodeList.values().stream().forEach(node -> {
                node.setColor(false);
            });
            HashSet<Integer> aConnection = new HashSet<>();
            HashSet<Integer> bConnection = new HashSet<>();
            Collections.addAll(aConnection, Arrays.copyOfRange(nextWord[getIndex(a)], 1, nextWord[getIndex(a)][0] + 1));
            Collections.addAll(bConnection, Arrays.copyOfRange(prevWord[getIndex(b)], 1, prevWord[getIndex(b)][0] + 1));
            aConnection.retainAll(bConnection);
            String[] result = new String[aConnection.size()];
            Integer[] indexArray = aConnection.toArray(new Integer[aConnection.size()]);
            for (int i = 0; i < aConnection.size(); i++) {
                graphNodeList.get(result[i] = wordArray[indexArray[i]]).setColor(true);
            }
            // Color
            Arrays.fill(nodeIsMarked, false);
            for (Integer i : indexArray) {
                nodeIsMarked[i] = true;
            }
            // Redraw Graph
//        redraw();
            return result;
        }catch (ArrayIndexOutOfBoundsException a0){
            return new String[0];
        }
    }

    /**
     * 随机游走
     * @param path 路线图
     * @return 染色后的File对象
     * @throws dotPathException dot程序配置错误
     */
    public File randomPath(LinkedList<String> path)throws dotPathException{
        // Unfinished function
        this.linkSourcesList.stream().forEach((LinkSource l) ->{l.setColor(false);});
        this.graphNodeList.values().stream().forEach(node -> {node.setColor(false);});
        randomList(path);
        redraw();
        return exportSVGFile();
    }

    /**
     * 单源最短路径函数
     * @param begin 起点
     * @param svgFile 最短路径图
     * @return 可达词列表
     * @throws dotPathException dot配置错误
     */
    public String[] allShortestPath(String begin, Map<String,File> svgFile) throws dotPathException{
        int start = getIndex(begin);
        if(start >= 0) {
            Map<Integer, List<Integer>> map = new HashMap<>();
            int[] wordDistance = new int[nodeCount]; // 保存从某一点出发时，两点最短距离
            Arrays.fill(wordDistance, WordGraph.UNREACHABLE);
            LinkedList<Integer> stack = new LinkedList<>();
            stack.push(start);
            int nowDistance = 0;
            wordDistance[start] = nowDistance;
            while (true) { // 渐进遍历未遍历到终点
                boolean added = false;
                Set<Integer> keySet = new HashSet<>(map.keySet()); // 在现在这一层深度有的节点
                nowDistance++; // 遍历深度
                for (Integer i : stack) {
                    // From i to j
                    for (int j = 1; j <= nextWord[i][0]; j++) {
                        if (!keySet.contains(nextWord[i][j]) && wordDistance[nextWord[i][j]] >= nowDistance) { // 判断是否是下一层的节点:是否出现在前几层
                            List<Integer> linkedList = map.getOrDefault(nextWord[i][j], new LinkedList<>());
                            // 列表中保存的是所有的前一个元素
                            added = true;
                            linkedList.add(i);
                            wordDistance[nextWord[i][j]] = nowDistance; // 记录下距离
                            map.put(nextWord[i][j], linkedList);
                        }
                    }
                }
                stack.clear();
                Set<Integer> newSet = new HashSet<>(map.keySet());
                newSet.removeAll(keySet);
                stack.addAll(newSet);
                // 遍历完毕
                if (!added)
                    break; // 不可达
            }
            for (Integer end : map.keySet()) {
                List<List<Integer>> results = new ArrayList<>();
                DFS(map, new LinkedList<>(), results, start, end);

                for (int routeNumber = 0; routeNumber < results.size(); routeNumber++) {
                    List<Integer> route = results.get(routeNumber);
                    this.cleanMark();
                    this.linkSourcesList.clear();
                    for (Integer i : route)
                        nodeIsMarked[i] = true;
                    for (int i = 0; i < route.size() - 1; i++) {
                        edgeIsMarked[route.get(i)][route.get(i + 1)] = true;
                    }
                    redraw();
                    svgFile.put(String.format("%s_%d", wordArray[end], routeNumber+1), this.exportSVGFile());
                }
            }
            return svgFile.keySet().toArray(new String[map.size()]);
        }
        return null;
    }

    /**
     * 重新根据nodeIsMarked和edgeIsMarked信息生成nodeList和edgeList
     */
    public void redraw(){
        this.linkSourcesList.clear();
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

    /**
     * 子图先深搜索函数
     * @param co 子图邻接表
     * @param buf 缓冲列表，递归用
     * @param res 结果列表
     * @param start 起点
     * @param end 终点
     */
    private void DFS(Map<Integer, List<Integer>> co,List<Integer> buf,List<List<Integer>> res,Integer start, Integer end){
        if(co.get(end).contains(start)) { // 有问题，不能正常退出
            if(buf.isEmpty())
                buf.add(end);
            buf.add(0,start);
            res.add(new LinkedList<>(buf));
            buf.remove(0); // Clean the stack
        }
        else {
            if(buf.isEmpty())
                buf.add(end);
            for(Integer prevPoint :co.get(end) ){
                buf.add(0,prevPoint);
                DFS(co,buf,res,start,prevPoint);
                buf.remove(0);
            }
        }
    }

    /**
     * 最短路径函数
     * @param a 起点
     * @param b 终点
     * @param fileList svg文件列表，图片显示用
     * @return 路径数量
     * @throws dotPathException dot文件配置有误
     */
    public Integer shortestPath(String a, String b, List<File> fileList)throws dotPathException{
        // 得到集合
        // Occurrence Table
        // 返回最小距离，开始位置
        this.linkSourcesList.stream().forEach((LinkSource l) ->{l.setColor(false);});
        this.linkSourcesList.clear();
        this.graphNodeList.values().stream().forEach(node -> {node.setColor(false);});
        int start = getIndex(a);
        int end = getIndex(b);
        // 注意 这里没有end > start，因为start和end可能重复多次
        if(start >= 0 && end >= 0){
            Map<Integer, List<Integer>> map = new HashMap<>();
            int[] wordDistance = new int[nodeCount]; // 保存从某一点出发时，两点最短距离
            Arrays.fill(wordDistance,WordGraph.UNREACHABLE);
            LinkedList<Integer> stack = new LinkedList<>();
            stack.push(start);
            int nowDistance = 0;
            wordDistance[start] = nowDistance;
            while(map.get(end) == null){ // 渐进遍历未遍历到终点
                boolean added = false;
                nowDistance++; // 遍历深度
                Set<Integer> keySet = new HashSet<>(map.keySet()); // 在现在这一层深度有的节点
                for(Integer i:stack){
                    // From i to j
                    for (int j = 1; j <= nextWord[i][0]; j++) {
                        if(!keySet.contains(nextWord[i][j]) && wordDistance[nextWord[i][j]] >= nowDistance){ // 判断是否是下一层的节点:是否出现在前几层
                            List<Integer> linkedList = map.getOrDefault(nextWord[i][j],new LinkedList<>());
                            // 列表中保存的是所有的前一个元素
                            linkedList.add(i);
                            added = true;
                            wordDistance[nextWord[i][j]]=nowDistance; // 记录下距离
                            map.put(nextWord[i][j],linkedList);
                        }
                    }
                }
                stack.clear();
                Set<Integer> newSet = new HashSet<>(map.keySet());
                newSet.removeAll(keySet);
                stack.addAll(newSet);
                // 遍历完毕
                if(!added)
                    break; // 不可达
            }
            List<List<Integer>> results = new ArrayList<>();
            DFS(map,new LinkedList<Integer>(),results,start,end);

            for(List<Integer> route : results){
                this.cleanMark();
                for(Integer i:route)
                    nodeIsMarked[i] = true;
                for(int i = 0; i < route.size()-1;i++){
                    edgeIsMarked[route.get(i)][route.get(i+1)] = true;
                }
                redraw();
                fileList.add(this.exportSVGFile());
            }
            return wordDistance[end]; // 在libtest.java 不可用
        }
        return null;
    }

    private Integer abs(Integer i){
        if(i < 0) return -i;
        return i;
    }

    public String generateNewText(String inputText){
        Random random = new Random();
        String[] sentenceArray = inputText.split("\\s");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentenceArray.length - 1; i++) {
            String[] bridge = bridgeWord(sentenceArray[i].toLowerCase().replaceAll("[^A-Za-z\\s]",""),
                    sentenceArray[i+1].toLowerCase().replaceAll("[^A-Za-z\\s]",""));
            sb.append(sentenceArray[i]+" ");
            if(bridge.length > 0){
                sb.append(bridge[random.nextInt(bridge.length)]+" ");
            }
        }
        sb.append(sentenceArray[sentenceArray.length-1]);
        return sb.toString();
    }


    private void randomList(List<String> path){
        Random random = new Random();
        int start = random.nextInt(nodeCount);
        this.cleanMark();
        while (true){
            if(this.nextWord[start][0] != 0) {
                int nextStart = this.nextWord[start][random.nextInt(this.nextWord[start][0])+1];
                if(this.edgeIsMarked[start][nextStart]) {
                    path.add(wordArray[start]);
                    break;
                }
                this.edgeIsMarked[start][nextStart] = true;
                this.nodeIsMarked[start] = true;
                this.nodeIsMarked[nextStart] = true;
                path.add(wordArray[start]);
                start = nextStart;
            }else {
                path.add(wordArray[start]);
                break;
            }
        }
    }
    public String randomWalk(){
        // Unfinished function
        this.linkSourcesList.stream().forEach((LinkSource l) ->{l.setColor(false);});
        this.graphNodeList.values().stream().forEach(node -> {node.setColor(false);});
        LinkedList<String> path = new LinkedList<>();
        return String.join(" ",path);
    }

    public String queryBridgeWords(String word1, String word2){
        try {
            String[] res = this.bridgeWord(word1, word2);
            switch (res.length) {
                case 0:return "No bridge words from word1 to word2!";
                case 1:return "The bridge words from word1 to word2 are:"+res[0];
                case 2:return String.format("The bridge words from word1 to word2 are:%s and %s", res[0],res[1]);
                default:return String.format("The bridge words from word1 to word2 are:%s and %s:", String.join(",",Arrays.copyOfRange(res,0,res.length-2)),res[res.length-1]);
            }
        }catch (ArrayIndexOutOfBoundsException a) {
            return "No word1 or word2 in the graph!";
        }
    }

    private void cleanMark(){
        this.linkSourcesList.stream().forEach((LinkSource l) ->{l.setColor(false);});
        this.graphNodeList.values().stream().forEach(node -> {node.setColor(false);});
        Arrays.fill(nodeIsMarked,false);
        for (int i = 0; i < edgeIsMarked.length; i++) {
            for (int j = 0; j < edgeIsMarked[0].length; j++) {
                edgeIsMarked[i][j] = false;
            }
        }
    }

}
