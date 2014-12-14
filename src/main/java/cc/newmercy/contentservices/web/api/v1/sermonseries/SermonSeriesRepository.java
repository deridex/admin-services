package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.time.Instant;
import java.util.List;

public interface SermonSeriesRepository {
	List<PersistentSermonSeries> list(int page, int pageSize);

	PersistentSermonSeries save(TransientSermonSeries transientSeries, Instant now);

	PersistentSermonSeries get(String id);

	PersistentSermonSeries update(String id, EditedSermonSeries editedSeries);
}
