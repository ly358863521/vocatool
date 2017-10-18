import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The type Dot path exception.
 */
// dot路径未找到
class DotPathException extends Exception {
}

/**
 * {@code LinkSource} represents an edge in graph.
 *
 * @author Ding Shi
 */
class LinkSource {
    /**
     * Thread sleep time.
     */
    private static final int THREAD_SLEEP_TIME = 400;
    /**
     * Edge start.
     */
    private String start;
    /**
     * Edge end.
     */
    private String end;
    /**
     * Weight of edge node.
     */
    private int weight;
    /**
     * If edge is marked.
     */
    private boolean color = false;

    /**
     * Constructor of {@code LinkSource}.
     *
     * @param newStart  Start word.
     * @param newEnd    End word.
     * @param newWeight Weight of edge.
     * @param newColor  If edge is marked or not.
     */
    LinkSource(final String newStart, final String newEnd,
               final int newWeight, final boolean newColor) {
        this.start = newStart;
        this.end = newEnd;
        this.weight = newWeight;
        this.color = newColor;
    }

    /**
     * Override toString method.
     *
     * @return DOT format string.
     */
    @Override
    public String toString() {
        final StringBuilder string = new StringBuilder();
        string.append(String.format("\t%s -> %s [label=\"%d\"",
                start, end, weight));
        if (color) {
            string.append(",color=red];");
        } else {
            string.append("];");
        }
        return string.toString();
    }

    /**
     * Get Color.
     *
     * @return Value of color.
     */
    public boolean getColor() {
        return color;
    }

    /**
     * Set color.
     *
     * @param newColor Value of color.
     */
    public void setColor(final boolean newColor) {
        this.color = newColor;
    }
}

/**
 * 节点类.
 *
 * @author Ding Shi
 */
class Node {
    /**
     * The Name.
     */
    private String name;
    /**
     * The Weight.
     */
    private int weight;
    /**
     * The Color.
     */
    private boolean color;

    /**
     * Instantiates a new Node.
     *
     * @param newName  the name
     * @param newColor the color
     */
    Node(final String newName, final boolean newColor) {
        this.name = newName;
        this.color = newColor;
    }

    /**
     * Instantiates a new Node.
     *
     * @param newName   the name
     * @param newWeight the weight
     * @param newColor  the color
     */
    Node(final String newName, final int newWeight, final boolean newColor) {
        this.name = newName;
        this.weight = newWeight;
        this.color = newColor;
    }

    /**
     * Instantiates a new Node.
     *
     * @param newName the name
     */
    Node(final String newName) {
        this.name = newName;
        this.color = false;
    }

    /**
     * To String.
     *
     * @return string result.
     */
    @Override
    public String toString() {
        if (color) {
            return String.format("\t%s [color=red,weight=%d];", name, weight);
        }
        return String.format("\t%s [weight=%d];", name, weight);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets this.
     *
     * @return the this
     */
    public Node getThis() {
        return this;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public boolean getColor() {
        return color;
    }

    /**
     * Sets color.
     *
     * @param newColor the color
     */
    public void setColor(final boolean newColor) {
        this.color = newColor;
    }
}

/**
 * The type Word graph.
 */
public class WordGraph {
    /**
     * The constant UNREACHABLE.
     */
    public static final int UNREACHABLE = Integer.MAX_VALUE;
    /**
     * Iteration to check the thread status.
     */
    public static final int ITERATION_TIME = 10;
    /**
     * Initial capacity of Map.
     */
    public static final int INITIAL_CAPACITY = 100;
    /**
     * Thread sleep time predefined.
     */
    private static final int THREAD_SLEEP_TIME = 400;
    /**
     * Dot Processor path.
     */
    private static String dotPath;
    /**
     * Dot Processor status.
     */
    private static boolean dotAvailable = false;
    /**
     * String for test purpose. Remove in further code.
     */
    private static String testString = null;
    /**
     * Source string.
     */
    private String sourceString;
    /**
     * String Array.
     */
    private String[] stringArray, wordArray;
    /**
     * nodeCount.
     */
    private int nodeCount;
    /**
     * Weight of every unique word.
     */
    private Map<String, Integer> wordWeight;
    /**
     * Array List of node.
     */
    private ArrayList<String> nodeList;
    /**
     * Linked List of edges.
     */
    private LinkedList<LinkSource> linkSourcesList;
    /**
     * Graph node List.
     */
    private Map<String, Node> graphNodeList;
    /**
     * Array of weight.
     */
    private int[][] weightArray;
    /**
     * If edge is marked.
     */
    private boolean[][] edgeIsMarked;
    /**
     * If node is marked.
     */
    private boolean[] nodeIsMarked;
    /**
     * Word-classified direct connections.
     */
    private Integer[][] nextWord, prevWord;

    /**
     * Instantiates a new Word graph.
     *
     * @param s the s
     */
    public WordGraph(final String s) {
        // Preprocessor
        sourceString = s.replaceAll("[^A-Za-z\\s]", "").toLowerCase();
        stringArray = sourceString.split("\\s+");
        final HashSet<String> nodeSet = new HashSet<>();
        Collections.addAll(nodeSet, stringArray);
        nodeCount = nodeSet.size();
        wordWeight = new HashMap<>(INITIAL_CAPACITY);
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
        wordArray = nodeList.toArray(new String[nodeList.size()]);

        // Node Creation
        for (int i = 0; i < stringArray.length - 1; i++) {
            // Possible Bug: Inconcurrent ID.
            final int fromIndex = getIndex(stringArray[i]);
            final int toIndex = getIndex(stringArray[i + 1]);
            wordWeight.replace(stringArray[i],
                    wordWeight.getOrDefault(stringArray[i], 0)
                            + stringArray.length - i);
            weightArray[fromIndex][toIndex] += 1;
            if (!hasObejct(nextWord[fromIndex],
                    1, nextWord[fromIndex][0], toIndex)) {
                nextWord[fromIndex][++nextWord[fromIndex][0]] = toIndex;
            }
            if (!hasObejct(prevWord[toIndex], 1,
                    prevWord[toIndex][0], fromIndex)) {
                prevWord[toIndex][++prevWord[toIndex][0]] = fromIndex;
            }
        }
        this.graphNodeList = this.nodeList.stream().collect(
                Collectors.toMap(
                        Function.identity(),
                        (name) -> new Node(name,
                                wordWeight.getOrDefault(
                                        name, 1), false)));
        linkSourcesList = new LinkedList<>();
        // Connecting
        for (int i = 0; i < wordArray.length; i++) {
            for (int j = 0; j < wordArray.length; j++) {
                if (i != j && weightArray[i][j] != 0) {
                    linkSourcesList.add(new LinkSource(wordArray[i],
                            wordArray[j], weightArray[i][j], false));
                }
            }
        }
    }

    /**
     * Test if have objects.
     *
     * @param objects objects array.
     * @param start   Start index.
     * @param end     end index.
     * @param o       Target
     * @return result(True or false.)
     */
    private static boolean hasObejct(
            final Object[] objects, final int start,
            final int end, final Object o) {
        for (int i = start; i < end; i++) {
            if (objects[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得当前全局dot路径.
     *
     * @return dot路径 dot path
     */
    public static String getDotPath() {
        return WordGraph.dotPath;
    }

    /**
     * 设置当前全局dot路径.
     *
     * @param dotPathConfig dot路径
     */
    public static void setDotPath(final String dotPathConfig) {
        WordGraph.dotPath = dotPathConfig;
    }

    /**
     * 测试dot程序是否可用.
     *
     * @return 测试命令返回值 string
     * @throws IOException      无法访问指定目录
     * @throws DotPathException dot程序无响应或者响应不符合预期
     */
    public static String testDotPath() throws IOException, DotPathException {
        final Thread testThread = new Thread(() -> {
            try {
                Process p = Runtime.getRuntime().exec(dotPath + " -V");
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(p.getErrorStream()));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                WordGraph.testString = sb.toString();
            } catch (InterruptedIOException e) {
                testString = "No Response.";
            } catch (IOException e) {
                WordGraph.testString = null;
            }
        });
        testThread.start();
        try {
            Thread.sleep(THREAD_SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testThread.interrupt();
        if (testString == null) {
            throw new IOException();
        }
        if (testString.contains("dot") && testString.contains("graphviz")) {
            WordGraph.dotAvailable = true;
        }
        if (testString.equals("No Response.")) {
            throw new DotPathException();
        }
        return testString;
    }

    /**
     * 获得某一个词在图中的编号.
     *
     * @param word 词
     * @return 编号
     * @apiNote 若编号为负数，说明这个词不在图中
     */
    private int getIndex(final String word) {
        return Arrays.binarySearch(wordArray,
                word, String::compareToIgnoreCase);
    }

    /**
     * 根据当前图的信息导出dot文件到一个临时文件中.
     *
     * @return 临时文件File对象 file
     */
    public File dotGenerate() {
        try {
            final File dotFile = File.createTempFile("graphviz_dot", ".dot");
            final BufferedWriter writer =
                    new BufferedWriter(new FileWriter(dotFile));
            writer.write("digraph G{");
            writer.newLine();
            // Color node
            for (final Node i : this.graphNodeList.values()) {
                writer.write(i.toString());
                writer.newLine();
            }
            // Add edges
            for (final LinkSource linkSource : this.linkSourcesList) {
                writer.write(linkSource.toString());
                writer.newLine();
            }
            writer.write("}");
            writer.newLine();
            writer.close();
            return dotFile;
        } catch (IOException i0) {
            return null;
        }
    }

    /**
     * 导出svg文件到临时目录.
     *
     * @return 临时目录中的File对象 file
     * @throws DotPathException dot程序无响应
     */
    public File exportSVGFile() throws DotPathException {
        try {
            if (!WordGraph.dotAvailable) {
                throw new DotPathException();
            }
            final File svgTemp = File.createTempFile("graphviz_svg", ".svg");
            final File dotTemp = this.dotGenerate();
            final Process p = Runtime.getRuntime().exec(
                    String.format("%s -Tsvg %s -o %s",
                            WordGraph.dotPath,
                            dotTemp.getAbsolutePath(),
                            svgTemp.getAbsolutePath()));
            while (p.isAlive()) {
                try {
                    Thread.sleep(ITERATION_TIME);
                    System.out.println("Wait for 10ms");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Fix incompatible svg line.
            // 0x01 212D2D20
            // 0x31 22202D2D
            /*
             * 此处由于是Dot的Bug, 必须使用这种硬编码方式解决。
             */
            final RandomAccessFile raf = new RandomAccessFile(svgTemp, "rw");
            raf.seek(0x01);
            raf.writeInt(0x212D2D20);
            raf.seek(0x31);
            raf.writeInt(0x22202D2D);
            raf.close();
//                    BufferedReader br = new BufferedReader(new
//                            InputStreamReader(p.getErrorStream()));
//                    String line = null;
//                    StringBuilder sb = new StringBuilder();
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//                    WordGraph.testString = sb.toString();
            return svgTemp;
        } catch (IOException i0) {
            return null;
        }
    }

    /**
     * 导出png对象.
     *
     * @param file 可以写入的File对象
     * @throws DotPathException      dot程序无响应
     * @throws FileNotFoundException 目标png无法写入
     * @throws TranscoderException   转码器错误
     */
    public void exportPNG(final File file) throws
            DotPathException, FileNotFoundException, TranscoderException {
        final PNGTranscoder pngTranscoder = new PNGTranscoder();
        final TranscoderInput input = new TranscoderInput(
                new FileInputStream(this.exportSVGFile()));
        final TranscoderOutput output = new TranscoderOutput(
                new FileOutputStream(file));
        pngTranscoder.transcode(input, output);
    }

    /**
     * 查找桥接词.
     *
     * @param a 起点
     * @param b 终点
     * @return 词组表 string [ ]
     * @apiNote 若没有桥接词 ，返回空数组
     * @apiNote 若a和b不在图中 ，返回null
     */
    public String[] bridgeWord(final String a, final String b) {
        try {
            this.linkSourcesList.stream().forEach((LinkSource l) -> {
                l.setColor(false);
            });
            this.graphNodeList.values().stream().forEach(node -> {
                node.setColor(false);
            });
            final HashSet<Integer> aConnection = new HashSet<>();
            final HashSet<Integer> bConnection = new HashSet<>();
            Collections.addAll(aConnection,
                    Arrays.copyOfRange(nextWord[getIndex(a)], 1,
                            nextWord[getIndex(a)][0] + 1));
            Collections.addAll(bConnection,
                    Arrays.copyOfRange(prevWord[getIndex(b)],
                            1, prevWord[getIndex(b)][0] + 1));
            aConnection.retainAll(bConnection);
            String[] result = new String[aConnection.size()];
            final Integer[] indexArray = aConnection.toArray(
                    new Integer[aConnection.size()]);
            for (int i = 0; i < aConnection.size(); i++) {
                result[i] = wordArray[indexArray[i]];
                graphNodeList.get(result[i]).setColor(true);
            }
            // Color
            Arrays.fill(nodeIsMarked, false);
            for (final Integer i : indexArray) {
                nodeIsMarked[i] = true;
            }
            // Redraw Graph
//        redraw();
            return result;
        } catch (ArrayIndexOutOfBoundsException a0) {
            return new String[0];
        }
    }

    /**
     * 随机游走.
     *
     * @param path 路线图
     * @return 染色后的File对象 file
     * @throws DotPathException dot程序配置错误
     */
    public File randomPath(final LinkedList<String> path)
            throws DotPathException {
        this.linkSourcesList.stream().forEach((LinkSource l) -> {
            l.setColor(false);
        });
        this.graphNodeList.values().stream().forEach(node -> {
            node.setColor(false);
        });
        randomList(path);
        redraw();
        return exportSVGFile();
    }

    /**
     * 单源最短路径函数.
     *
     * @param begin   起点
     * @param svgFile 最短路径图
     * @return 可达词列表 string [ ]
     * @throws DotPathException dot配置错误
     */
    public String[] allShortestPath(final String begin,
                                    final Map<String, File> svgFile)
            throws DotPathException {
        final int start = getIndex(begin);
        if (start >= 0) {
            final ConcurrentMap<Integer, List<Integer>> map =
                    new ConcurrentHashMap<>();
            int[] wordDistance = new int[nodeCount];
            // 保存从某一点出发时，两点最短距离
            Arrays.fill(wordDistance, WordGraph.UNREACHABLE);
            final LinkedList<Integer> stack = new LinkedList<>();
            stack.push(start);
            int nowDistance = 0;
            wordDistance[start] = nowDistance;
            while (true) { // 渐进遍历未遍历到终点
                boolean added = false;
                final Set<Integer> keySet = new HashSet<>(map.keySet());
                // 在现在这一层深度有的节点
                nowDistance++; // 遍历深度
                for (final Integer i : stack) {
                    // From i to j
                    for (int j = 1; j <= nextWord[i][0]; j++) {
                        if (!keySet.contains(nextWord[i][j])
                                && wordDistance[nextWord[i][j]]
                                >= nowDistance) {
                            // 判断是否是下一层的节点:是否出现在前几层
                            final List<Integer> linkedList =
                                    map.getOrDefault(nextWord[i][j],
                                            new LinkedList<>());
                            // 列表中保存的是所有的前一个元素
                            added = true;
                            linkedList.add(i);
                            wordDistance[nextWord[i][j]]
                                    = nowDistance; // 记录下距离
                            map.put(nextWord[i][j], linkedList);
                        }
                    }
                }
                stack.clear();
                final Set<Integer> newSet = new HashSet<>(map.keySet());
                newSet.removeAll(keySet);
                stack.addAll(newSet);
                // 遍历完毕
                if (!added) {
                    break; // 不可达
                }
            }
            for (final Integer end : map.keySet()) {
                final List<List<Integer>> results = new ArrayList<>();
                dfs(map, new LinkedList<>(), results, start, end);

                for (int routeNumber = 0;
                     routeNumber < results.size();
                     routeNumber++) {
                    final List<Integer> route = results.get(routeNumber);
                    this.cleanMark();
                    this.linkSourcesList.clear();
                    for (final Integer i : route) {
                        nodeIsMarked[i] = true;
                    }
                    for (int i = 0; i < route.size() - 1; i++) {
                        edgeIsMarked[route.get(i)][route.get(i + 1)] = true;
                    }
                    redraw();
                    svgFile.put(String.format("%s_%d",
                            wordArray[end], routeNumber + 1),
                            this.exportSVGFile());
                }
            }
            return svgFile.keySet().toArray(new String[map.size()]);
        }
        return null;
    }

    /**
     * 重新根据nodeIsMarked和edgeIsMarked信息生成nodeList和edgeList.
     */
    public void redraw() {
        this.linkSourcesList.clear();
        for (int i = 0; i < wordArray.length; i++) {
            for (int j = 0; j < wordArray.length; j++) {
                if (i != j && weightArray[i][j] != 0) {
                    if (edgeIsMarked[i][j]) {
                        linkSourcesList.add(
                                new LinkSource(wordArray[i],
                                        wordArray[j],
                                        weightArray[i][j],
                                        true));
                    } else {
                        linkSourcesList.add(
                                new LinkSource(wordArray[i],
                                        wordArray[j],
                                        weightArray[i][j],
                                        false));
                    }
                }
            }
        }
        for (int i = 0; i < nodeIsMarked.length; i++) {
            if (nodeIsMarked[i]) {
                graphNodeList.get(wordArray[i]).setColor(true);
            }
        }
    }

    /**
     * 子图先深搜索函数.
     *
     * @param co    子图邻接表
     * @param buf   缓冲列表，递归用
     * @param res   结果列表
     * @param start 起点
     * @param end   终点
     */
    private void dfs(
            final Map<Integer, List<Integer>> co,
            final List<Integer> buf,
            final List<List<Integer>> res,
            final Integer start,
            final Integer end) {
        if (co.get(end).contains(start)) { // 有问题，不能正常退出
            if (buf.isEmpty()) {
                buf.add(end);
            }
            buf.add(0, start);
            res.add(new LinkedList<>(buf));
            buf.remove(0); // Clean the stack
        } else {
            if (buf.isEmpty()) {
                buf.add(end);
            }
            for (final Integer prevPoint : co.get(end)) {
                buf.add(0, prevPoint);
                dfs(co, buf, res, start, prevPoint);
                buf.remove(0);
            }
        }
    }

    /**
     * 最短路径函数.
     *
     * @param a        起点
     * @param b        终点
     * @param fileList svg文件列表，图片显示用
     * @return 路径数量 integer
     * @throws DotPathException dot文件配置有误
     */
    public Integer shortestPath(
            final String a, final String b, final List<File> fileList)
            throws DotPathException {
        // 得到集合
        // Occurrence Table
        // 返回最小距离，开始位置
        this.linkSourcesList.stream().forEach((LinkSource l) -> {
            l.setColor(false);
        });
        this.linkSourcesList.clear();
        this.graphNodeList.values().stream().forEach(node -> {
            node.setColor(false);
        });
        final int start = getIndex(a);
        final int end = getIndex(b);
        // 注意 这里没有end > start，因为start和end可能重复多次
        if (start >= 0 && end >= 0) {
            final ConcurrentMap<Integer, List<Integer>> map =
                    new ConcurrentHashMap<>();
            int[] wordDistance =
                    new int[nodeCount]; // 保存从某一点出发时，两点最短距离
            Arrays.fill(wordDistance, WordGraph.UNREACHABLE);
            final LinkedList<Integer> stack = new LinkedList<>();
            stack.push(start);
            int nowDistance = 0;
            wordDistance[start] = nowDistance;
            while (map.get(end) == null) { // 渐进遍历未遍历到终点
                boolean added = false;
                nowDistance++; // 遍历深度
                final Set<Integer> keySet =
                        new HashSet<>(map.keySet()); // 在现在这一层深度有的节点
                for (final Integer i : stack) {
                    // From i to j
                    for (int j = 1; j <= nextWord[i][0]; j++) {
                        if (!keySet.contains(nextWord[i][j])
                                && wordDistance[nextWord[i][j]]
                                >= nowDistance) {
                            // 判断是否是下一层的节点:是否出现在前几层
                            final List<Integer> linkedList =
                                    map.getOrDefault(nextWord[i][j],
                                            new LinkedList<>());
                            // 列表中保存的是所有的前一个元素
                            linkedList.add(i);
                            added = true;
                            wordDistance[nextWord[i][j]] = nowDistance;
                            // 记录下距离
                            map.put(nextWord[i][j], linkedList);
                        }
                    }
                }
                stack.clear();
                final Set<Integer> newSet = new HashSet<>(map.keySet());
                newSet.removeAll(keySet);
                stack.addAll(newSet);
                // 遍历完毕
                if (!added) {
                    break; // 不可达
                }
            }
            final List<List<Integer>> results = new ArrayList<>();
            dfs(map, new LinkedList<Integer>(), results, start, end);

            for (final List<Integer> route : results) {
                this.cleanMark();
                for (final Integer i : route) {
                    nodeIsMarked[i] = true;
                }
                for (int i = 0; i < route.size() - 1; i++) {
                    edgeIsMarked[route.get(i)][route.get(i + 1)] = true;
                }
                redraw();
                fileList.add(this.exportSVGFile());
            }
            return wordDistance[end]; // 在libtest.java 不可用
        }
        return null;
    }

    /**
     * Absolue value.
     *
     * @param i value.
     * @return abs.
     */
    private Integer abs(final Integer i) {
        if (i < 0) {
            return -i;
        }
        return i;
    }

    /**
     * Generate new text string.
     *
     * @param inputText the input text
     * @return the string
     */
    public String generateNewText(final String inputText) {
        final Random random = new Random();
        final String[] sentenceArray = inputText.split("\\s");
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentenceArray.length - 1; i++) {
            final String[] bridge = bridgeWord(sentenceArray[i].
                            toLowerCase().replaceAll("[^A-Za-z\\s]", ""),
                    sentenceArray[i + 1].toLowerCase()
                            .replaceAll("[^A-Za-z\\s]", ""));
            sb.append(sentenceArray[i]);
            sb.append(" ");
            if (bridge.length > 0) {
                sb.append(bridge[random.nextInt(bridge.length)]);
                sb.append(" ");
            }
        }
        sb.append(sentenceArray[sentenceArray.length - 1]);
        return sb.toString();
    }


    /**
     * 随机路径.
     *
     * @param path 节点List
     */
    private void randomList(final List<String> path) {
        Random random = new Random();
        int start = random.nextInt(nodeCount);
        this.cleanMark();
        while (true) {
            if (this.nextWord[start][0] != 0) {
                int nextStart = this.nextWord[start]
                        [random.nextInt(this.nextWord[start][0]) + 1];
                if (this.edgeIsMarked[start][nextStart]) {
                    path.add(wordArray[start]);
                    break;
                }
                this.edgeIsMarked[start][nextStart] = true;
                this.nodeIsMarked[start] = true;
                this.nodeIsMarked[nextStart] = true;
                path.add(wordArray[start]);
                start = nextStart;
            } else {
                path.add(wordArray[start]);
                break;
            }
        }
    }

    /**
     * Random walk string.
     *
     * @return the string
     */
    public String randomWalk() {
        // Unfinished function
        this.linkSourcesList.stream().forEach((LinkSource l) -> {
            l.setColor(false);
        });
        this.graphNodeList.values().stream().forEach(node -> {
            node.setColor(false);
        });
        LinkedList<String> path = new LinkedList<>();
        return String.join(" ", path);
    }

    /**
     * Query bridge words string.
     *
     * @param word1 the word 1
     * @param word2 the word 2
     * @return the string
     */
    public String queryBridgeWords(final String word1, final String word2) {
        try {
            String[] res = this.bridgeWord(word1, word2);
            switch (res.length) {
                case 0:
                    return "No bridge words from word1 to word2!";
                case 1:
                    return "The bridge words from word1 to word2 are:" + res[0];
                case 2:
                    return String.format("The bridge words from word1 "
                                    +
                                    "to word2 are:%s and %s",
                            res[0], res[1]);
                default:
                    return String.format("The bridge words from word1"
                                    +
                                    " to word2 are:%s and %s:",
                            String.join(",",
                                    Arrays.copyOfRange(res, 0, res.length - 2)),
                            res[res.length - 1]);
            }
        } catch (ArrayIndexOutOfBoundsException a) {
            return "No word1 or word2 in the graph!";
        }
    }

    /**
     * Clean mark.
     */
    private void cleanMark() {
        this.linkSourcesList.stream().forEach((LinkSource l) -> {
            l.setColor(false);
        });
        this.graphNodeList.values().stream().forEach(node -> {
            node.setColor(false);
        });
        Arrays.fill(nodeIsMarked, false);
        for (int i = 0; i < edgeIsMarked.length; i++) {
            for (int j = 0; j < edgeIsMarked[0].length; j++) {
                edgeIsMarked[i][j] = false;
            }
        }
    }

}
