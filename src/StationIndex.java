import core.Line;
import core.Station;

import java.util.*;
import java.util.stream.Collectors;

public class StationIndex
{
    HashMap<Integer, Line> number2line;//зачем эта мапа?
    TreeSet<Station> stations;//просто сет станций
    TreeMap<Station, TreeSet<Station>> connections;

    public StationIndex()
    {
        number2line = new HashMap<>();
        stations = new TreeSet<>();
        connections = new TreeMap<>();
    }

    public void addStation(Station station)
    {
        stations.add(station);
    }

    public void addLine(Line line)//дублирование линий?
    {
        number2line.put(line.getNumber(), line);
    }

    public void addConnection(List<Station> stations)//добавляет пересадки, на входе список станций
    {
        for(Station station : stations)//перебор всех полученных станций
        {
            if(!connections.containsKey(station)) {//если в connections остуствтует станция из переданного списка
                connections.put(station, new TreeSet<>());//то станция вместе с новым TreeSet кладётся в connections
            }
            TreeSet<Station> connectedStations = connections.get(station);//тут не должно бть в начале else?
            connectedStations.addAll(stations
                    .stream()
                    .filter(s -> !s.equals(station))//фильтрация по признаку: станция из переданного списка не равна станции из списка connectedStations?
                    .collect(Collectors.toList()));
        }
    }

    public Line getLine(int number)//возвращает объект-линию из мапы по номеру линии
    {
        return number2line.get(number);
    }

    public Station getStation(String name)//метод возвращает объект-станцию по названию
    {
        for(Station station : stations)
        {
            if(station.getName().equalsIgnoreCase(name)) {//интересный метод есть у String
                return station;
            }
        }
        return null;
    }

    public Station getStation(String name, int lineNumber)//возвращает объект-станцию по названию и линии
    {
        Station query = new Station(name, getLine(lineNumber));
        Station station = stations.ceiling(query); //ceiling Возвращает наименьший ключ, больший или равный данному ключу
        return station.equals(query) ? station : null;
    }

    public Set<Station> getConnectedStations(Station station)//метод возвращает станции пересадок из connections по станции
    {
        if(connections.containsKey(station)) {
            return connections.get(station);//возвращает TreeSet
        }
        return new TreeSet<>();
    }
}
