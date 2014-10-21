package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.Objects;

public class EditedSermonSeries extends SermonSeriesCommonFields {
	@Override
	public int hashCode() {
		return Objects.hash(getName(), getDescription(), getImageUrl());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EditedSermonSeries)) {
			return false;
		}

		EditedSermonSeries other = (EditedSermonSeries) o;

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
