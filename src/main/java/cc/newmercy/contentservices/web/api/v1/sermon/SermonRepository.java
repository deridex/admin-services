package cc.newmercy.contentservices.web.api.v1.sermon;

import java.time.Instant;
import java.util.List;

public interface SermonRepository {
    List<PersistentSermon> list(String seriesId);

    PersistentSermon save(String seriesId, TransientSermon transientSermon, Instant now);
}
