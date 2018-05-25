import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sbxcloud.java.sbxcloudsdk.util.SbxMagicComposer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class mainTest {
    @Test
    public void test() throws Exception{
        Category category = new Category("lacteos");

        //crea un objeto a partir de un Json y una clase personalizada con anotaciones
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObject = mapper.readTree("{\"price\":13,\"description\":\"leche\",\"expireAt\":\"2017-02-20T20:45:36.756Z\",\"category\":\"laksdf-asdf-234-asdf\"}")
        Product p = SbxMagicComposer.getSbxModel(jsonObject, Product.class, 0);
        assertEquals(13, p.price,0.0000001);

        //crea objeto incluso si hace referencia a otro
        jsonObject = new JSONObject("{\"_KEY\": \"95979b68-cc4a-416d-46c550380031\",\"price\":13,\"description\":\"leche\",\"expireAt\":\"2017-02-20T20:45:36.756Z\",\"category\":{\"_KEY\": \"laksdf-asdf-234-asdf\",\"description\":\"lacteos\"}}");
        p = SbxMagicComposer.getSbxModel(jsonObject, Product.class, 0);
        assertEquals("lacteos", p.category.description);
    }

    @Test
    public void benchmark() throws Exception{
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject("{\"_KEY\": \"95979b68-cc4a-416d-46c550380031\",\"price\":13,\"description\":\"leche\",\"expireAt\":\"2017-02-20T20:45:36.756Z\",\"category\":{\"_KEY\": \"laksdf-asdf-234-asdf\",\"description\":\"lacteos\"}}");

        for (int i=0;i<=500000;i=i+1) {
            jsonArray.put(jsonObject);
        }

        long start = System.nanoTime();
        List<Product> list = SbxMagicComposer.getListSbxModel(jsonArray,Product.class);
        long end = System.nanoTime() - start;
        System.out.println("SbxLibrary: "+ end/1000000000);


        start = System.nanoTime();
        ObjectMapper mapper = new ObjectMapper();
        List<Product> list3 =   mapper.readValue(jsonA, new TypeReference<List<Product>>(){});
        long  end3 = System.nanoTime() - start;
        System.out.println("JACKSON Library: "+ end3/1000000000);


        assertEquals(list.get(0).description,list2.get(0).description);
        assertEquals(list.get(0).expireAt,list3.get(0).expireAt);
        assertTrue(end < end2);
        assertTrue(end > end3);

 }
}
