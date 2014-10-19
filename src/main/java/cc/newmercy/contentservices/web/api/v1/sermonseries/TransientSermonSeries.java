package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.Objects;

public class TransientSermonSeries extends SermonSeriesCommonFields {
	@Override
	public int hashCode() {
		return Objects.hash(getName(), getDescription(), getImageUrl());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TransientSermonSeries)) {
			return false;
		}

		TransientSermonSeries other = (TransientSermonSeries) o;

		return Objects.equals(getName(), other.getName())
				&& Objects.equals(getDescription(), other.getDescription())
				&& Objects.equals(getImageUrl(), other.getImageUrl());
	}

	@Override
	public String toString() {
		return "TransientSermonSeries [name=" + getName() + ", description=" + getDescription() + ", imageUrl="
				+ getImageUrl() + "]";
	}
}
