package Readers;

import Data.BankData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ReaderBanks extends ReadData<BankData> {

    public static int startPage = 1;
    public static int endPage = 57;

    @Override
    public Stream<BankData> getStream(String path) {
        return getList(path).stream();
    }

    public List<BankData> getList(String path) {
        Type bankType = new TypeToken<List<BankData>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<BankData> cashMachines = new ArrayList<>();

        for (int i = startPage; i <= endPage; i++) {
            cashMachines.addAll(gson.fromJson(BankData.getReader(path + i), bankType));
        }
        return cashMachines;
    }
}
