package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.Objects;

public class PersistentSermonSeries extends SermonSeriesCommonFields {

	private String id;

	public PersistentSermonSeries() {
	}

	public PersistentSermonSeries(TransientSermonSeries series) {
		super(series);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getDescription(), getImageUrl(), id);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PersistentSermonSeries)) {
			return false;
		}

		if (this == o) {
			return true;
		}

		PersistentSermonSeries other = (PersistentSermonSeries) o;

		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "PersistentSermonSeries [id=" + id + ", name=" + getName() + ", description=" + getDescription()
				+ ", imageUrl=" + getImageUrl() + "]";
	}
}
