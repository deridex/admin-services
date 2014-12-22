package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.time.Instant;

public interface SermonSeriesRepository {
	PersistentSermonSeries save(TransientSermonSeries transientSeries, Instant now);

	PersistentSermonSeries get(String id);

	PersistentSermonSeries update(String id, Integer version, EditedSermonSeries editedSeries);
}
