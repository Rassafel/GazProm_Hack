package LoadersToDB;

import ReadAndLoadToDB.ConnectionToDB;

import java.util.stream.Stream;

public abstract class PrepareProcess<T> extends ConnectionToDB {
    protected PrepareProcess(String url, String username, String password) {
        super(url, username, password);
    }

    public abstract boolean addData(T data);

    public boolean addAllData(Stream<T> stream){
        try{
            stream.forEach(this::addData);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
