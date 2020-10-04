package Data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.StringJoiner;

public class Building {
    public static final transient Map<String, Short> PRIORITY_MAP = Map.of(
            "магазин", (short) 5,
            "метро", (short) 4,
            "образование", (short) 3);

    private Location geoData;
    @SerializedName("Name")
    private String name;
    private transient String type;
    @SerializedName("Address")
    private String address;
    private transient short priority;
    public static final transient JsonDeserializer<Location> geoDeser = (element, type1, context) -> {
        Location result = new Location();
        JsonArray geos = element.getAsJsonObject().getAsJsonArray("coordinates");
        result.setLat(geos.get(1).getAsDouble());
        result.setLon(geos.get(0).getAsDouble());
        return result;
    };


    public Building() {
    }

    public Building(Location geoData, String name, String type, String address) {
        this.geoData = geoData;
        this.name = name;
        this.type = type;
        this.address = address;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Building.class.getSimpleName() + "[", "]")
                .add("geoData=" + geoData)
                .add("name='" + name + "'")
                .add("type='" + type + "'")
                .add("address='" + address + "'")
                .toString();
    }

    public short getPriority() {
        return priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public Location getGeoData() {
        return geoData;
    }

    public void setGeoData(Location geoData) {
        this.geoData = geoData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
