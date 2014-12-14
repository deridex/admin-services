package cc.newmercy.contentservices.web.time;

import java.time.Instant;

public interface ConsistentClock {
    Instant now();
}
