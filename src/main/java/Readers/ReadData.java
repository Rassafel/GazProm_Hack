package Readers;

import java.util.stream.Stream;

public abstract class ReadData<T> {
    public abstract Stream<T> getStream(String path);

    public Stream<T> getStream(String...paths){
        Stream<T> result = Stream.empty();
        for (String path: paths) {
            result = Stream.concat(result, getStream(path));
        }
        return result;
    }
}
