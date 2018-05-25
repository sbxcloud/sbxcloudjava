import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxKey;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxModelName;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxParamField;

import java.util.Date;

import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
@SbxModelName("Product")
public class Product {

    @JsonProperty("_KEY")
    @SbxKey
    @Expose
    String key;

    @JsonProperty("description")
    @SbxParamField(name = "description")
    @Expose
    String description;

    @JsonProperty("price")
    @SbxParamField()
    @Expose
    double price;

    @Expose
    @JsonProperty("expireAt")
    @SbxParamField()
    Date expireAt;


    Category category;

    public Product(){}

    public Product(String name, double price, Category category) {
        this.category = category;
        this.price = price;
        this.description = name;
        this.expireAt=new Date();
    }


}