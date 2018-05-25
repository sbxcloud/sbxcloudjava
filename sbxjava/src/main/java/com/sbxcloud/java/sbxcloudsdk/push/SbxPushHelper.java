package com.sbxcloud.java.sbxcloudsdk.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sbxcloud.java.sbxcloudsdk.auth.SbxAuth;
import com.sbxcloud.java.sbxcloudsdk.util.SbxJsonModeler;
import com.sbxcloud.java.sbxcloudsdk.util.SbxUrlComposer;
import com.sbxcloud.java.sbxcloudsdk.util.UrlHelper;

import org.json.JSONObject;

/**
 * Created by lgguzman on 24/03/17.
 */

public class SbxPushHelper {

    public static SbxUrlComposer getUrlSendPush(String title, String alias, String message, SbxJsonModeler sbxJsonModeler) throws  Exception{

        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        int domain= SbxAuth.getDefaultSbxAuth().getDomain();

        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.PUSH
                , UrlHelper.POST
        );

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObject  = mapper.createObjectNode();
        jsonObject.put("domain", domain);
        jsonObject.put("message", message);
        jsonObject.put("title", title);
        jsonObject.put("alias", alias);
        jsonObject.put("custom_fields", sbxJsonModeler.toJson());
        jsonObject.put("platform", 1);
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(jsonObject);
    }
}
