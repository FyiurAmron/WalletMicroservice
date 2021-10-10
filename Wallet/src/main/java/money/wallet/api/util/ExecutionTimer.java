package money.wallet.api.util;

import lombok.*;

import java.util.Date;

@Getter
public class ExecutionTimer implements AutoCloseable {
    private final Date start;
    private Date stop;

    public ExecutionTimer() {
        start = new Date();
    }

    public long stop() {
        stop = new Date();
        return stop.getTime() - start.getTime();
    }

    @Override
    public void close() {
        stop();
    }
}
