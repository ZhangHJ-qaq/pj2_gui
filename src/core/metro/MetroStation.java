package core.metro;

public class MetroStation {
    private String name;
    private int lineNumber;
    private int stationNumber;

    public MetroStation(String name, int lineNumber, int stationNumber) {
        this.name = name;
        this.lineNumber = lineNumber;
        this.stationNumber = stationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(int stationNumber) {
        this.stationNumber = stationNumber;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
