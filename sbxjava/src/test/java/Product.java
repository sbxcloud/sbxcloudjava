import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxKey;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxModelName;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxParamField;

import java.util.Date;

@SbxModelName("Product")
public class Product {

    @SbxKey
    String key;

    @SbxParamField(name = "description")
    String name;

    @SbxParamField()
    double price;

    @SbxParamField()
    Date expireAt;

    @SbxParamField()
    Category category;

    public Product(){}

    public Product(String name, double price, Category category) {
        this.category = category;
        this.price = price;
        this.name = name;
        this.expireAt=new Date();
    }
}