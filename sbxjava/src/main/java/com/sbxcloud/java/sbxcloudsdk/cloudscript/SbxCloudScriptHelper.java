package com.sbxcloud.java.sbxcloudsdk.cloudscript;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sbxcloud.java.sbxcloudsdk.auth.SbxAuth;
import com.sbxcloud.java.sbxcloudsdk.query.SbxQueryBuilder;
import com.sbxcloud.java.sbxcloudsdk.util.SbxUrlComposer;
import com.sbxcloud.java.sbxcloudsdk.util.UrlHelper;

import org.json.JSONObject;

/**
 * Created by lgguzman on 12/06/17.
 */

public class SbxCloudScriptHelper {

    /**
     *
     * @param key llave del cloudScript
     * @param params parametros del CloudScript
     * @return
     * @throws Exception
     */
    public static SbxUrlComposer getUrlRunCloudScript(String key, ObjectNode params)throws Exception {

       // int domain = SbxAuth.getDefaultSbxAuth().getDomain();
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer( UrlHelper.CLOUDSCRIPT_RUN, UrlHelper.POST);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObject  = mapper.createObjectNode();
        try{
            jsonObject.put("key",key);
            jsonObject.set("params",params);
        }catch (Exception ex){}
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
                //       .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(jsonObject);
    }

}
