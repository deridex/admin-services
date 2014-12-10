package cc.newmercy.contentservices.web.api.v1.sermon;

public interface SermonRepository {
    PersistentSermon get(String id);

    PersistentSermon save(String seriesId, TransientSermon transientSermon);
}
