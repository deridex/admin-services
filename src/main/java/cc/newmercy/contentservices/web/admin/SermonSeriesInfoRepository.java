package cc.newmercy.contentservices.web.admin;

import java.util.List;

public interface SermonSeriesInfoRepository {
    List<SermonSeriesInfo> list(int page, int pageSize);
}
