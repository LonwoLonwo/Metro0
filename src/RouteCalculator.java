import core.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RouteCalculator
{
    private StationIndex stationIndex;

    private static double interStationDuration = 2.5;
    private static double interConnectionDuration = 3.5;

    public RouteCalculator(StationIndex stationIndex)
    {
        this.stationIndex = stationIndex;
    }

    public List<Station> getShortestRoute(Station from, Station to)//возвращает список станций с кратчайшим путём от станции "от" до станции "до"
    {
        List<Station> route = getRouteOnTheLine(from, to);
        if(route != null) {
            return route;
        }

        route = getRouteWithOneConnection(from, to);
        if(route != null) {
            return route;
        }

        route = getRouteWithTwoConnections(from, to);
        return route;
    }

    public static double calculateDuration(List<Station> route)
    {
        double duration = 0;
        Station previousStation = null;
        for(int i = 0; i < route.size(); i++)
        {
            Station station = route.get(i);
            if(i > 0)
            {
                duration += previousStation.getLine().equals(station.getLine()) ?
                        interStationDuration : interConnectionDuration;
            }
            previousStation = station;
        }
        return duration;
    }

    //=========================================================================

    private List<Station> getRouteOnTheLine(Station from, Station to)//возвращает список станции по линии от станции from до станции to
    {
        if(!from.getLine().equals(to.getLine())) {//возвращает null если станции не с одной линии
            return null;
        }
        ArrayList<Station> route = new ArrayList<>();
        List<Station> stations = from.getLine().getStations();//копируется список всех станции с линии станции from
        int direction = 0;
        for(Station station : stations)
        {
            if(direction == 0)
            {
                if(station.equals(from)) {//если станция из списка всех станции одной из линий равна станции from
                    direction = 1;
                } else if(station.equals(to)) {//если станция из списка всех станции одной из линий равна станции to
                    direction = -1;
                }
            }

            if(direction != 0) {
                route.add(station);//если станция не совпадает со станциями from и to, то добавляется в список route
            }

            if((direction == 1 && station.equals(to)) ||
                    (direction == -1 && station.equals(from))) {//это выход из цикла, когда от станции from(direction становится 1) доходим до станции To, например
                break;
            }
        }
        if(direction == -1) {
            Collections.reverse(route);//поменять порядок станций, если station.equals(to)
        }
        return route;
    }

    private List<Station> getRouteWithOneConnection(Station from, Station to)
    {
        if(from.getLine().equals(to.getLine()) || (isConnected(from, to))) {
            return null;
        }

        ArrayList<Station> route = new ArrayList<>();

        List<Station> fromLineStations = from.getLine().getStations();
        List<Station> toLineStations = to.getLine().getStations();

        int count = 0;

        for(Station srcStation : fromLineStations)
        {
            for(Station dstStation : toLineStations)
            {
                if(isConnected(srcStation, dstStation))
                {
                    count++;
                    ArrayList<Station> way = new ArrayList<>();
                    way.addAll(getRouteOnTheLine(from, srcStation));
                    way.addAll(getRouteOnTheLine(dstStation, to));
                    if(route.isEmpty() || route.size() > way.size())
                    {
                        route.clear();
                        route.addAll(way);
                    }
                }
            }
        }
        if(count == 0){
            route = null;
        }
        return route;
    }

    private boolean isConnected(Station station1, Station station2)
    {
        Set<Station> connected = stationIndex.getConnectedStations(station1);//тут сет всех пересадок с линии, на которой станция 1?
        return connected.contains(station2);//и если среди этого сета есть вторая станция, то есть и пересадка?
    }

    private List<Station> getRouteViaConnectedLine(Station from, Station to)//проложить путь между станциями-пересадками для маршрута с двумя пересадками
    {
        Set<Station> fromConnected = stationIndex.getConnectedStations(from);
        Set<Station> toConnected = stationIndex.getConnectedStations(to);
        for(Station srcStation : fromConnected)
        {
            for(Station dstStation : toConnected)
            {
                if(srcStation.getLine().equals(dstStation.getLine())) {
                    return getRouteOnTheLine(srcStation, dstStation);
                }
            }
        }
        return null;
    }

    private List<Station> getRouteWithTwoConnections(Station from, Station to)
    {
        if (from.getLine().equals(to.getLine())) {
            return null;
        }

        ArrayList<Station> route = new ArrayList<>();

        List<Station> fromLineStations = from.getLine().getStations();
        List<Station> toLineStations = to.getLine().getStations();
        for(Station srcStation : fromLineStations)
        {
            for (Station dstStation : toLineStations)
            {
                List<Station> connectedLineRoute =
                        getRouteViaConnectedLine(srcStation, dstStation);
                if(connectedLineRoute == null) {
                    continue;
                }
                ArrayList<Station> way = new ArrayList<>();
                way.addAll(getRouteOnTheLine(from, srcStation));
                way.addAll(connectedLineRoute);
                way.addAll(getRouteOnTheLine(dstStation, to));
                if(route.isEmpty() || route.size() > way.size())
                {
                    route.clear();
                    route.addAll(way);
                }
            }
        }

        return route;
    }
}