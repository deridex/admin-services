package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.List;
import java.util.Objects;

public class PersistentSermonSeriesList {

	private List<PersistentSermonSeries> data;

	public PersistentSermonSeriesList() {
	}

	public PersistentSermonSeriesList(List<PersistentSermonSeries> data) {
		this.data = Objects.requireNonNull(data, "sermon series list");
	}

	public List<PersistentSermonSeries> getData() {
		return data;
	}

	public void setData(List<PersistentSermonSeries> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SermonSeriesList [data=" + data + "]";
	}
}
