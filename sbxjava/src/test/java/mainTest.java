import com.sbxcloud.java.sbxcloudsdk.util.SbxMagicComposer;
import org.json.JSONObject;

public class mainTest {
    void test(){
        try {
            Category category = new Category("lacteos");

            //crea un objeto a partir de un Json y una clase personalizada con anotaciones
            JSONObject jsonObject = new JSONObject("{\"price\":13,\"description\":\"leche\",\"expireAt\":\"2017-02-20T20:45:36.756Z\",\"category\":\"laksdf-asdf-234-asdf\"}");
            Product p = SbxMagicComposer.getSbxModel(jsonObject, Product.class, 0);

            //crea objeto incluso si hace referencia a otro
            jsonObject = new JSONObject("{\"_KEY\": \"95979b68-cc4a-416d-46c550380031\",\"price\":13,\"description\":\"leche\",\"expireAt\":\"2017-02-20T20:45:36.756Z\",\"category\":{\"_KEY\": \"laksdf-asdf-234-asdf\",\"description\":\"lacteos\"}}");
            p = SbxMagicComposer.getSbxModel(jsonObject, Product.class, 0);
        }catch (Exception ex){

        }
    }
}
