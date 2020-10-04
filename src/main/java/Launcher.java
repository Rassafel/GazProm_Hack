import Data.BankData;
import Data.BankStatistics;
import Data.Building;
import Data.UserData;
import Generators.GenerateBanksStatistics;
import Generators.GenerateUserData;
import InputStreams.StreamData;
import LoadersToDB.*;
import ReadAndLoadToDB.CalculateAvgBankStats;
import ReadAndLoadToDB.CalculateRating;
import ReadAndLoadToDB.NarrateCashMachines;
import ReadFromDB.GetIntegers;
import Readers.ReadData;
import Readers.ReaderBanks;
import Readers.ReaderBuildings;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.Logger;

public class Launcher {

    private static final Logger LOG = Logger.getLogger(Launcher.class.getName());
    public static final String URL_DB = "jdbc:postgresql://localhost:5432/gazbank";
    public static final String PATH = "C:\\Users\\user\\Desktop\\Programs\\Projects\\GazProm_Hack\\src\\main\\resources\\Datas\\магазин.json";
    public static final String URL_BANKS = "https://www.gazprombank.ru/rest/hackathon/atm/?page=";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "rassafel";

    public static void main(String[] args) throws InterruptedException {

        LOG.info("Init class Instant\n");
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        Instant startTime = now.toInstant(ZoneOffset.UTC);
        Instant endTime = now.plusHours(4).toInstant(ZoneOffset.UTC);
        LOG.info("\nstartTime = " + startTime + "\nendTime = " + endTime + '\n');


        LOG.info("Init class Generators.GenerateUserData\n");
        StreamData<UserData> users = new GenerateUserData();

        LOG.info("Init class Generators.GenerateBanksStatistics\n");
        StreamData<BankStatistics> statistics = new GenerateBanksStatistics();

        LOG.info("Init class Readers.ReaderBanks\n");
        ReadData<BankData> banks = new ReaderBanks();

        LOG.info("Init class Readers.ReaderBanks\n");
        ReadData<Building> buildings = new ReaderBuildings();

        LOG.info("Init class LoadersToDB.PrepareBankData\n");
        PrepareProcess<BankData> bankData = new PrepareBankData(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class LoadersToDB.PrepareUserData\n");
        PrepareProcess<UserData> userData = new PrepareUserData(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class LoadersToDB.PrepareBuilding\n");
        PrepareProcess<Building> buildingData = new PrepareBuilding(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class LoadersToDB.PrepareBankStatistics\n");
        PrepareProcess<BankStatistics> bankStatistics = new PrepareBankStatistics(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class ReadAndLoadToDB.CalculateRating\n");
        CalculateRating rateData = new CalculateRating(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class ReadAndLoadToDB.CalculateAvgBankStats\n");
        CalculateAvgBankStats avgData = new CalculateAvgBankStats(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class ReadAndLoadToDB.NarrateCashMachines\n");
        NarrateCashMachines narrateCashMachines = new NarrateCashMachines(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class ReadFromDB.GetIntegerList\n");
        GetIntegers select = new GetIntegers(URL_DB, USERNAME, PASSWORD);

//        LOG.info("Start create and load user\n");
//        userData.addAllData(users.getStream(1, startTime, endTime));

//        LOG.info("Start load banks\n");
//        bankData.addAllData(banks.getStream(URL_BANKS));

//        LOG.info("Start load buildings\n");
//        buildingData.addAllData(buildings.getStream(PATH));

//        LOG.info("Start create and load bankstats\n");
//        bankStatistics.addAllData(statistics.getStream(select.getArray(), startTime, endTime));

//        LOG.info("Start ReadAndLoadToDB.CalculateRating\n");
//        rateData.updateData();

        LOG.info("Start ReadAndLoadToDB.CalculateAvgBankStats\n");
        avgData.calculateAvgBanksStats();

        LOG.info("Start ReadAndLoadToDB.NarrateCashMachines\n");
        narrateCashMachines.narrateCashMachines();






        LOG.info("Completed successfully");
    }
}
