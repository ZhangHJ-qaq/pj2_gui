package core.metro;

import core.graph.GeneralPath;
import core.graph.UndirectedWeightedGraph;

import java.io.File;
import java.net.Inet4Address;
import java.security.Key;
import java.util.*;

public class MetroTransferProblem {

    //存储了从每个从起点到终点的路径（避免多次查找，空间换时间）
    GeneralPath[][] shortestPathSave;

    //记载了车站编号到车站的映射的哈希表
    HashMap<Integer, MetroStation> numberToStation;

    UndirectedWeightedGraph graph;

    public MetroTransferProblem(File stationFile, File gapFile) throws Exception {

        this.numberToStation = new HashMap<>();


        Scanner stationScanner = new Scanner(stationFile);
        Scanner gapScanner = new Scanner(gapFile);

        int howManyStations = 0;

        while (stationScanner.hasNext()) {
            String s = stationScanner.nextLine();
            String[] sss = s.split(",");

            String stationName = sss[0];//获得站名
            int stationNumber = Integer.parseInt(sss[1]);//获得车站编号
            int lineNumber;

            //先排除浦电路这两个奇葩
            if (stationName.startsWith("浦电路4")) {
                lineNumber = 4;
            } else if (stationName.startsWith("浦电路6")) {
                lineNumber = 6;
            } else {
                lineNumber = getLineNumber(stationName);
            }

            MetroStation station = new MetroStation(stationName, lineNumber, stationNumber);

            this.numberToStation.put(stationNumber, station);
        }


        //根据读到的站点数目建图
        this.graph = new UndirectedWeightedGraph(numberToStation.size());

        this.shortestPathSave = new GeneralPath[this.graph.numOfVertexes()][this.graph.numOfVertexes()];

        //读取边的信息并在图中加入边
        while (gapScanner.hasNext()) {
            String[] sss = gapScanner.nextLine().split(",");
            int start = Integer.parseInt(sss[0]);
            int end = Integer.parseInt(sss[1]);
            double weight = Double.parseDouble(sss[2]);

            this.graph.addEdge(start, end, weight);


        }


    }

    private MetroPath shortestPathBetweenTwoStations(String stationA, String stationB) throws StationNotFoundException, NoRouteException {

        //这是两个所有可能的起点站与终点站的列表。比如如果stationA为“人民广场”，srcStationNumList中就含有1号线的人民广场
        //2号线的人民广场和8号线的人民广场的编号
        List<Integer> srcStationNumList = getStationNumber(stationA);
        List<Integer> descStationNumList = getStationNumber(stationB);


        ArrayList<GeneralPath> targetGeneralPathList = new ArrayList<>();

        for (int stationNumberA : srcStationNumList
        ) {

            for (int stationNumberB : descStationNumList
            ) {
                //如果已经对起点A使用过迪杰特斯拉算法了
                if (shortestPathSave[stationNumberA][0] != null) {

                    targetGeneralPathList.add(shortestPathSave[stationNumberA][stationNumberB]);

                } else {//如果对起点A没有进行过迪杰特斯拉算法，则需要计算一遍

                    GeneralPath[] pathsFromA = graph.shortestPath(stationNumberA);

                    //得到目标的那条路
                    GeneralPath path = pathsFromA[stationNumberB];

                    if (path.route == null) {
                        continue;
                    }

                    targetGeneralPathList.add(path);

                    shortestPathSave[stationNumberA] = pathsFromA;

                }

            }

        }


        if (targetGeneralPathList.size() == 0) {
            throw new StationNotFoundException("");
        }

        ArrayList<MetroPath> metroPathArrayList = new ArrayList<>();

        for (GeneralPath generalPath : targetGeneralPathList
        ) {
            metroPathArrayList.add(new MetroPath(generalPath, this.numberToStation));

        }


        //找到所有路径中换乘最少的
        int currentMinNumberOfTransfer = metroPathArrayList.get(0).numberOfTransfer;
        int index = 0;

        for (int i = 0; i < metroPathArrayList.size(); i++) {
            if (metroPathArrayList.get(i).numberOfTransfer < currentMinNumberOfTransfer) {
                currentMinNumberOfTransfer = metroPathArrayList.get(i).numberOfTransfer;
                index = i;
            }

        }

        return metroPathArrayList.get(index);

    }

    /**
     * 输出站名后的两个数字作为路线编号，例如海伦路10 给出10
     *
     * @param stationName 例子：海伦路10
     * @return 输入：10
     */
    private static int getLineNumber(String stationName) {

        StringBuilder builder = new StringBuilder();

        while (true) {
            char c = stationName.charAt(stationName.length() - 1);

            if (c >= '0' && c <= '9') {
                builder.append(c);
                stationName = stationName.substring(0, stationName.length() - 1);
            } else {
                break;
            }

        }

        return Integer.parseInt(builder.reverse().toString());

    }

    /**
     * 根据站名，获得一个站的编号
     *
     * @param stationName 例：海伦路（不带线路名）
     * @return 四号线海伦路站和六号线海伦路站的车站编号的list
     */
    public List<Integer> getStationNumber(String stationName) throws StationNotFoundException {

        ArrayList<Integer> list = new ArrayList<>();

        for (Map.Entry<Integer, MetroStation> entry : this.numberToStation.entrySet()
        ) {
            MetroStation station = entry.getValue();

            if (MetroPath.getStationNameWithoutLineNumber(station).equals(stationName)) {
                list.add(station.getStationNumber());
            }

        }

        if (list.size() == 0) {
            throw new StationNotFoundException("找不到站点" + stationName);
        }

        return list;

    }

    /**
     * 给定车站的列表，输出最短路的描述
     *
     * @param stationList 车站列表
     * @return
     */
    public String shortestPath(String[] stationList) throws StationNotEnoughException, StationNotFoundException, NoRouteException {

        if (stationList.length <= 1) {
            throw new StationNotEnoughException("你输入的车站数目不够，需要至少输入两个车站");
        }

        if (stationList.length == 2) {//如果输入两个站
            MetroPath metroPath = this.shortestPathBetweenTwoStations(stationList[0], stationList[1]);
            StringBuilder pathDescriptionBuilder = new StringBuilder(metroPath.toString());

            pathDescriptionBuilder.append(",换乘数").append(metroPath.numberOfTransfer);
            pathDescriptionBuilder.append(",所用时间").append((int) metroPath.totalTime).append("分钟");

            return pathDescriptionBuilder.toString();
        } else {//如果输入多于两个站

            ArrayList<MetroPath> metroPathList = new ArrayList<>();

            int totalAmountOfTime = 0;
            int totalTransfers = 0;

            for (int i = 0, j = 1; j < stationList.length; j++, i++) {
                String stationNameA = stationList[i];
                String stationNameB = stationList[j];
                metroPathList.add(shortestPathBetweenTwoStations(stationNameA, stationNameB));


            }

            for (int i = 0, j = 1; j < metroPathList.size(); i++, j++) {
                if (metroPathList.get(i).metroStationList.getLast().getStationNumber() !=
                        metroPathList.get(j).metroStationList.getFirst().getStationNumber()) {
                    totalTransfers++;
                }
            }

            StringBuilder pathDescriptionBuilder = new StringBuilder();
            pathDescriptionBuilder.append(metroPathList.get(0).toString());


            for (int i = 0; i < metroPathList.size(); i++) {

                if (i != 0) {
                    String s = metroPathList.get(i).toString();
                    while (true) {
                        char c = s.charAt(0);
                        if (c != '-') {
                            s = s.substring(1);
                        } else {
                            break;
                        }

                    }
                    pathDescriptionBuilder.append(s);
                }
                totalAmountOfTime += metroPathList.get(i).totalTime;
                totalTransfers += metroPathList.get(i).numberOfTransfer;

            }

            pathDescriptionBuilder.append(",换乘数").append(totalTransfers);
            pathDescriptionBuilder.append(",所用时间").append((totalAmountOfTime)).append("分钟");
            return pathDescriptionBuilder.toString();

        }


    }


}
