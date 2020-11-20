package core.metro;

import core.graph.GeneralPath;
import core.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MetroPath {
    public LinkedList<MetroStation> metroStationList;
    private HashMap<Integer, MetroStation> numberToStation;
    private ArrayList<Double> transferPointList = new ArrayList<>();
    public int numberOfTransfer;
    public double totalTime;


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        String startStationName = getStationNameWithoutLineNumber(metroStationList.get(0));

        builder.append(startStationName);

        for (int i = 0; i < transferPointList.size(); i++) {

            double currentTransferPoint = transferPointList.get(i);
            MetroStation lastStationBeforeTransfer = this.metroStationList.get((int) Math.floor(currentTransferPoint));

            builder.append("-").append(lastStationBeforeTransfer.getLineNumber()).append("-").append(
                    getStationNameWithoutLineNumber(lastStationBeforeTransfer)
            );

        }

        MetroStation lastStation = metroStationList.getLast();

        builder.append("-").append(lastStation.getLineNumber()).append("-").append(
                getStationNameWithoutLineNumber(lastStation)
        );

        return builder.toString();

    }


    public MetroPath(GeneralPath generalPath, HashMap<Integer, MetroStation> numberToStation) throws NoRouteException {
        LinkedList<MetroStation> stationLinkedList = new LinkedList<>();



        for (int stationNumber : generalPath.route
        ) {
            stationLinkedList.add(numberToStation.get(stationNumber));
        }


        this.metroStationList = stationLinkedList;
        this.totalTime = generalPath.length;
        this.numberToStation = numberToStation;


        //计算换乘数目
        ArrayList<Double> transferPointList = new ArrayList<>();

        //i,j两个指针。如果索引为i处线路为a，索引为b处线路为b，定义换乘点为(i+j)/2.0
        for (int i = 0, j = 1; j < metroStationList.size(); i++, j++) {
            MetroStation stationA = metroStationList.get(i);
            MetroStation stationB = metroStationList.get(j);

            if (stationA.getLineNumber() != stationB.getLineNumber()) {
                double transferPoint = (i + j) / 2.0;
                transferPointList.add(transferPoint);
            }

        }

        boolean deleteFromHead = false;

        try {
            //如果一上来就是换乘点，说明一上来就找错站了。比如应该找8号线的人民广场，结果找到了1号线的。这种情况我们不计换乘
            if (Utility.doubleEquals(0.5, transferPointList.get(0), 1e-4)) {
                transferPointList.remove(0);
                metroStationList.removeFirst();
                deleteFromHead = true;
            }

            //如果有从头部删除车站，则所有的换乘点都需要减一
            if (deleteFromHead) {
                for (int i = 0; i < transferPointList.size(); i++) {
                    transferPointList.set(i, transferPointList.get(i) - 1);
                }
            }

            //同理，如果末尾存在换乘点，说明找错终点站了，比如进行到9号线的肇嘉浜路就可以停止，但是程序输入的终点站是7号线的肇嘉浜路，也不计换乘
            if (Utility.doubleEquals(this.metroStationList.size() - 1.5, transferPointList.get(transferPointList.size() - 1), 1e-4)) {
                transferPointList.remove(transferPointList.size() - 1);
                metroStationList.removeLast();
            }
        } catch (Exception ignored) {
        }


        this.numberOfTransfer = transferPointList.size();

        this.transferPointList = transferPointList;

    }


    /**
     * 给出不带线路编号的车站名
     *
     * @param station 车站
     * @return 车站名
     */
    public static String getStationNameWithoutLineNumber(MetroStation station) {

        //先排除浦电路
        if (station.getName().startsWith("浦电路4")) {
            return "浦电路4";
        } else if (station.getName().startsWith("浦电路6")) {
            return "浦电路6";
        }

        //对于其他的站，删除后面的数字就是不带数字的站名

        String name = station.getName();

        while (true) {
            char c = name.charAt(name.length() - 1);
            if (c >= '0' && c <= '9') {
                name = name.substring(0, name.length() - 1);
            } else {
                break;
            }

        }

        return name;

    }


}
