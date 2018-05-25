package com.sbxcloud.java.sbxcloudsdk.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by lgguzman on 18/02/17.
 */

public class SbxUrlComposer {
    private String url;
    private String type;
    private HashMap<String,String> header;
    private ObjectNode body;
    private File file;
    private String fileName;
    private String fileModel;

    /**
     *
     * @return if the request is a PUT
     */
    public boolean isPOST(){
        return type.equals(UrlHelper.POST);
    }

    /**
     *
     * @return if the request is a GET
     */
    public boolean isGET(){
        return type.equals(UrlHelper.GET);
    }

    /**
     *
     * @return if the request is a PUT
     */
    public boolean isPUT(){
        return type.equals(UrlHelper.PUT);
    }

    /**
     *
     * @return the type of request of this object, PUT GET or POST
     */
    public String getType(){
        return type;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileModel() {
        return fileModel;
    }

    /**
     *
     * @param url
     * @param type
     */
    public SbxUrlComposer(String url, String type){
        this.url= UrlHelper.URL_BASE+ url;
        this.type=type;
        header= new HashMap<>();
        addHeader("User-Agent","AndroidALib");
        body= (new ObjectMapper()).createObjectNode();
    }



    /**
     *
     * @param key
     * @param value
     */
    public SbxUrlComposer setUrlParam(String key, String value){

        if(!this.url.contains("?"))
            this.url=this.url+"?"+key+"="+URLEncoder.encode(value);
        else
            this.url=this.url+"&"+key+"="+URLEncoder.encode(value);

        return this;
    }


    public SbxUrlComposer addMultipartData(File file, String name, String key){

        this.file=file;
        this.fileName=name;
        this.fileModel="{\"key\":\""+key+"\"}";
        return this;
    }

    /**
     *
     * @param body
     */
    public SbxUrlComposer addBody(ObjectNode body){
        this.body = body;
        return this;
    }


    /**
     *
     * @param key
     * @param value
     */
    public SbxUrlComposer addHeader(String key, String value){
       header.put(key,value);
        return this;
    }


    public HashMap<String, String> getHeader() {
        return header;
    }

    public String getHeaderString(){
        StringBuilder stringBuilder=new StringBuilder("");
        for (String key:header.keySet()){
            stringBuilder.append(key+": "+header.get(key)+":\n");
        }
        return stringBuilder.toString();
    }

    public ObjectNode getBody() {
        return body;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString(){
        return "\n"+getType()+"\n"+getUrl()+"\nHEADER\n"+getHeaderString()+"\nBODY\n"+((getBody().toString()==null)?"":getBody().toString())+"\n"
                +file==null?"":"\nMULTIPART\n"+"file:\n"+fileName+"\n"+"model:\n"+fileModel;
    }
}
