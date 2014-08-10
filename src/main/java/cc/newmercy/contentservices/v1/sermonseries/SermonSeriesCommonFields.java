package cc.newmercy.contentservices.v1.sermonseries;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public abstract class SermonSeriesCommonFields {
	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@URL
	private String imageUrl;

	public SermonSeriesCommonFields() {
	}

	public SermonSeriesCommonFields(SermonSeriesCommonFields commonFields) {
		name = commonFields.name;
		description = commonFields.description;
		imageUrl = commonFields.imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String nameArg) {
		name = nameArg;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descriptionArg) {
		description = descriptionArg;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrlArg) {
		imageUrl = imageUrlArg;
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object other);

	@Override
	public abstract String toString();
}
