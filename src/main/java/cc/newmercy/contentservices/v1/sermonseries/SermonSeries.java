package cc.newmercy.contentservices.v1.sermonseries;

public class SermonSeries {

	private long id;

	private String title;

	private String subTitle;

	private String image;

	private String description;

	public long getId() {
		return id;
	}

	public void setId(long idArg) {
		id = idArg;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String titleArg) {
		title = titleArg;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitleArg) {
		subTitle = subTitleArg;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String imageArg) {
		image = imageArg;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descriptionArg) {
		description = descriptionArg;
	}

	@Override
	public String toString() {
		return "SermonSeries [id=" + id + ", title=" + title + ", subTitle=" + subTitle + ", image=" + image
				+ ", description=" + description + "]";
	}
}
