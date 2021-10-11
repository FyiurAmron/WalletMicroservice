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

    public ExecutionTimer( Date start, Date stop ) {
        this.start = start;
        this.stop = stop;
    }

    /**
     * Idempotent.
     *
     * @return time passed from start to stop in ms
     */
    public long stop() {
        if ( stop == null ) {
            stop = new Date();
        }
        return stop.getTime() - start.getTime();
    }

    @Override
    public void close() {
        stop();
    }
}
