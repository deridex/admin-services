package cc.newmercy.contentservices.neo4j.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

	private List<String> columns;

	private List<Datum> data;

	public List<String> getColumns() {
		return columns;
	}

	public List<Datum> getData() {
		return data;
	}

	@Override
	public String toString() {
		return "Results [columns=" + columns + ", data.size()=" + data.size() + "]";
	}
}
