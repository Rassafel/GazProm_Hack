package Readers;

import Data.Building;
import Data.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ReaderBuildings extends ReadData<Building> {
    @Override
    public Stream<Building> getStream(String path) {
        return getList(path).stream();
    }

    public List<Building> getList(String path){
        Path filePath = Path.of(path);
        Gson gson = new GsonBuilder().
                registerTypeAdapter(Location.class, Building.geoDeser).
                create();
        List<Building> list = new LinkedList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            for (JsonElement j : jsonArray) {
                Building building = gson.fromJson(j, Building.class);
                building.setType(filePath.getName(filePath.getNameCount() - 1).toString().replaceAll("(?iU)(.+?)\\.(.+)", "$1"));
                building.setPriority(Building.PRIORITY_MAP.getOrDefault(building.getType(), (short) -1));
                list.add(building);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
