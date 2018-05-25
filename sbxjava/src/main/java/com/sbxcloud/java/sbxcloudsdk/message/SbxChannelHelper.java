package com.sbxcloud.java.sbxcloudsdk.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sbxcloud.java.sbxcloudsdk.auth.SbxAuth;
import com.sbxcloud.java.sbxcloudsdk.query.SbxQueryBuilder;
import com.sbxcloud.java.sbxcloudsdk.util.SbxJsonModeler;
import com.sbxcloud.java.sbxcloudsdk.util.SbxUrlComposer;
import com.sbxcloud.java.sbxcloudsdk.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by lgguzman on 24/03/17.
 */

public class SbxChannelHelper {

    public static SbxUrlComposer getUrlCreateChannel(String channelName) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.MESSAGE_CHANNEL
                , UrlHelper.POST
        );
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObject  = mapper.createObjectNode();
        jsonObject.put("name", channelName);
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(jsonObject);
    }

    public static SbxUrlComposer getUrlAddMember(int channelId, int []idUsers) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.MESSAGE_CHANNEL_MEMBER
                , UrlHelper.POST
        );
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObject  = mapper.createObjectNode();
        ArrayNode jsonArray = mapper.createArrayNode();
        for (int i=0;i<idUsers.length;i++){
            jsonArray.add(idUsers[i]);
        }
        jsonObject.put("channel_id", channelId);
        jsonObject.set("members", jsonArray);
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(jsonObject);
    }

    public static SbxUrlComposer getUrlListMessage(int channelId) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.MESSAGE_LIST
                , UrlHelper.GET
        );
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .setUrlParam("channel_id",channelId+"");

    }

    public static SbxUrlComposer getUrlSendMessage(int channelId, SbxJsonModeler body) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.MESSAGE_SEND
                , UrlHelper.POST
        );

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObject  = mapper.createObjectNode();
        jsonObject.put("channel_id", channelId);
        jsonObject.set("body", body.toJson());
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(jsonObject);
              //  .setUrlParam("channel_id",channelId+"");
    }
}
