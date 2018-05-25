import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxKey;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxModelName;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxParamField;

@SbxModelName("Category")
public class Category {

    @SbxKey
    String key;

    @SbxParamField(name = "description")
    String name;

    public Category(){}

    public Category(String name) {
        this.name = name;
    }
}