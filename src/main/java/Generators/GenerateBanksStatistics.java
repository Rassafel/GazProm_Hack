package Generators;

import Data.BankStatistics;
import InputStreams.StreamData;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class GenerateBanksStatistics extends StreamData<BankStatistics> {

    public static int interval = 12;

    public static int MAX_USERS = 10;
    public static int MIN_USERS = 0;

    @Override
    public Stream<BankStatistics> getStream(int idBank, final Instant start, final Instant end) {
        return getList(idBank, start, end).stream();
    }

    public List<BankStatistics> getList(int idBank, final Instant start, final Instant end){
        List<BankStatistics> list = new LinkedList<>();

        Instant last = start;
        while (last.isBefore(end)) {
            Instant cur = last.plusSeconds(interval * 60);
            list.add(generateIntervalData(idBank, last, cur));
            last = cur;
        }

        return list;
    }

    private BankStatistics generateIntervalData(int id, final Instant start, final Instant end) {
        Map<Instant, Integer> timeMap = new HashMap<>();
        Instant cur = start;
        while (cur.isBefore(end)) {
            timeMap.put(cur, (int) (Math.random() * (MAX_USERS - MIN_USERS)) + MIN_USERS);
            cur = cur.plusSeconds(60);
        }
        int countUsers = timeMap.values().stream().mapToInt(t -> t).sum();
        int minUsers = timeMap.values().stream().min(Integer::compareTo).orElse(0),
                maxUsers = timeMap.values().stream().max(Integer::compareTo).orElse(0);
        return new BankStatistics(id, end, countUsers, minUsers, maxUsers);
    }
}
