package cc.newmercy.contentservices.neo4j.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Datum {

	private List<Object> row;

	public List<Object> getRow() {
		return row;
	}

	@Override
	public String toString() {
		return "Datum [row=" + row + "]";
	}
}
