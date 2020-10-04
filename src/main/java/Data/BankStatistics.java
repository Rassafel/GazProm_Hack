package Data;

import java.time.Instant;

public class BankStatistics {
    public int id;
    public Instant time;
    public int countUsers;
    public int minUsers;
    public int maxUsers;

    public BankStatistics(int id, Instant time, int countUsers, int minUsers, int maxUsers) {
        this.id = id;
        this.time = time;
        this.countUsers = countUsers;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
    }
}
