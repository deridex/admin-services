package cc.newmercy.contentservices.web.api.v1.sermon;

import java.time.Instant;

public interface SermonRepository {
    PersistentSermon get(String id);

    PersistentSermon save(String seriesId, TransientSermon transientSermon, Instant now);
}
