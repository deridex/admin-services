package cc.newmercy.contentservices.web.time;

import java.time.Clock;
import java.time.Instant;

public class DefaultClock implements ConsistentClock {

    private final Instant now = Clock.systemUTC().instant();

    @Override
    public Instant now() {
        return now;
    }
}
