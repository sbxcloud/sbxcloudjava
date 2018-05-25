import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxKey;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxModelName;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxParamField;

@JsonIgnoreProperties(ignoreUnknown = true)
@SbxModelName("Category")
public class Category {

    @SbxKey
    String key;

    @SbxParamField(name = "description")
    String description;

    public Category(){}

    public Category(String name) {
        this.description = name;
    }
}