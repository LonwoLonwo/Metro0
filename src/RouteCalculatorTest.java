import core.Line;
import core.Station;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class RouteCalculatorTest extends TestCase
{
    private StationIndex stationIndex = new StationIndex();

    private RouteCalculator rtCalculator = new RouteCalculator(stationIndex);

    private Line line;
    private Line line2;
    private Line line3;
    private Station oneLineOne;
    private Station threeLineOne;
    private Station fourLineOne;
    private Station fiveLineTwo;
    private Station sevenLineThree;

    private List<Station> route1;
    private List<Station> route2;
    private List<Station> route3;


    @Override
    public void setUp() throws Exception {
        route1 = new ArrayList<>();
        route2 = new ArrayList<>();
        route3 = new ArrayList<>();

        line = new Line(1, "First");
        line2 = new Line(2, "Second");
        line3 = new Line(3, "Third");
        stationIndex.addLine(line);
        stationIndex.addLine(line2);
        stationIndex.addLine(line3);

        //станции первой линии, route1 без пересадок
        oneLineOne = new Station("Первая", line);
        line.addStation(oneLineOne);
        route1.add(oneLineOne);
        Station twoLineOne = new Station("Вторая", line);
        line.addStation(twoLineOne);
        route1.add(twoLineOne);
        threeLineOne = new Station("Третья", line);
        route1.add(threeLineOne);
        line.addStation(threeLineOne);
        fourLineOne = new Station("Четвёртая", line);
        route1.add(fourLineOne);
        line.addStation(fourLineOne);
        for(Station st : route1){
            stationIndex.addStation(st);
        }

        //станции второй линии
        Station threeLineTwo = new Station("Dritte", line2);
        line2.addStation(threeLineTwo);
        Station fourLineTwo = new Station("Vierte", line2);
        line2.addStation(fourLineTwo);
        fiveLineTwo = new Station("Funftes", line2);
        line2.addStation(fiveLineTwo);
        stationIndex.addStation(threeLineTwo);
        stationIndex.addStation(fourLineTwo);
        stationIndex.addStation(fiveLineTwo);

        //станции третьей линии
        Station sixLineThree = new Station("Sextus", line3);
        line3.addStation(sixLineThree);
        stationIndex.addStation(sixLineThree);
        sevenLineThree = new Station("Septima", line3);
        line3.addStation(sevenLineThree);
        stationIndex.addStation(sevenLineThree);


        //route2 с пересадкой
        route2.add(oneLineOne);
        route2.add(twoLineOne);
        route2.add(threeLineOne);
        route2.add(fourLineOne);
        route2.add(threeLineTwo);
        route2.add(fourLineTwo);
        route2.add(fiveLineTwo);

        //route3 с двумя пересадками
        route3.add(threeLineOne);
        route3.add(fourLineOne);
        route3.add(threeLineTwo);
        route3.add(fourLineTwo);
        route3.add(fiveLineTwo);
        route3.add(sixLineThree);
        route3.add(sevenLineThree);

        /*JSONParser parser = new JSONParser();
        //мой путь
        String dataFile = "src\\resources\\mapTest.json";
        JSONObject jsonData = (JSONObject) parser.parse(Main.getJsonFile(dataFile));
        JSONArray connectionsArray = (JSONArray) jsonData.get("connections");
        Main.parseConnections(connectionsArray, stationIndex);*/

        List<Station> connection1 = new ArrayList<>();
        connection1.add(fourLineOne);
        connection1.add(threeLineTwo);
        stationIndex.addConnection(connection1);
        List<Station> connection2 = new ArrayList<>();
        connection2.add(fiveLineTwo);
        connection2.add(sixLineThree);
        stationIndex.addConnection(connection2);

    }

    public void testCalculateDurationOneLine(){
        double actual = RouteCalculator.calculateDuration(route1);
        double expected = 7.5;
        assertEquals(expected, actual);
    }

    public void testCalculateDurationTwoLines(){
        double actual = RouteCalculator.calculateDuration(route2);
        double expected = 16;
        assertEquals(expected, actual);
    }

    public void testGetShortestRouteOneLine(){
        List<Station> expectedRoute = rtCalculator.getShortestRoute(oneLineOne, fourLineOne);
        assertEquals(expectedRoute, route1);
    }

    public void testGetShortestRouteOneConnection(){
        List<Station> expectedRoute = rtCalculator.getShortestRoute(oneLineOne, fiveLineTwo);
        assertEquals(expectedRoute, route2);
    }

    public void testGetShortestRouteTwoConnections(){
        List<Station> expectedRoute = rtCalculator.getShortestRoute(threeLineOne, sevenLineThree);
        assertEquals(expectedRoute, route3);
    }

    @Override
    public void tearDown(){
        line = null;
        line2 = null;
        line3 = null;
        stationIndex = null;
        rtCalculator = null;
    }
}
