package InputStreams;

import java.time.Instant;
import java.util.stream.Stream;

public abstract class StreamData<T> {
    public abstract Stream<T> getStream(int id, final Instant start, final Instant end);

    public Stream<T> getStream(int[] id, final Instant start, final Instant end){
        Stream<T> result = Stream.empty();
        for (int i: id) {
            result = Stream.concat(result, getStream(i, start, end));
        }
        return result;
    }
}
