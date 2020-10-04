package Data;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class BankData {
    private int id;

    private Location geolocation;
    private Address address;

    public BankData() {
        //does nothing
    }

    public BankData(int id, Location geolocation, Address address) {
        this.id = id;
        this.geolocation = geolocation;
        this.address = address;
    }

    public static BufferedReader getReader(String URL) {
        HttpURLConnection urlConnection;
        BufferedReader reader = null;
        try {
            urlConnection = (HttpURLConnection) new URL(URL).openConnection();
            urlConnection.setConnectTimeout(5000);
            InputStream stream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Location geolocation) {
        this.geolocation = geolocation;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "CashMachine{" +
                "id=" + id +
                ", geolocation=" + geolocation +
                ", address=" + address +
                '}';
    }

    public static class Address {
        private String region;
        private String regionType;
        private String settlementType;
        private String settlement;
        private String fullAddress;
        private String location;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getRegionType() {
            return regionType;
        }

        public void setRegionType(String regionType) {
            this.regionType = regionType;
        }

        public String getSettlementType() {
            return settlementType;
        }

        public void setSettlementType(String settlementType) {
            this.settlementType = settlementType;
        }

        public String getSettlement() {
            return settlement;
        }

        public void setSettlement(String settlement) {
            this.settlement = settlement;
        }

        public String getFullAddress() {
            return fullAddress;
        }

        public void setFullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "region='" + region + '\'' +
                    ", regionType=" + regionType +
                    ", settlementType=" + settlementType +
                    ", settlement='" + settlement + '\'' +
                    ", fullAddress='" + fullAddress + '\'' +
                    ", location='" + location + '\'' +
                    '}';
        }
    }
}
