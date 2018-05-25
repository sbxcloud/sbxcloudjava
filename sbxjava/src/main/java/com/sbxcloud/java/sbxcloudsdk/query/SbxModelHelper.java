package com.sbxcloud.java.sbxcloudsdk.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbxcloud.java.sbxcloudsdk.auth.SbxAuth;
import com.sbxcloud.java.sbxcloudsdk.exception.SbxAuthException;
import com.sbxcloud.java.sbxcloudsdk.exception.SbxModelException;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxKey;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxModelName;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxParamField;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxReference;
import com.sbxcloud.java.sbxcloudsdk.util.SbxDataValidator;
import com.sbxcloud.java.sbxcloudsdk.util.SbxUrlComposer;
import com.sbxcloud.java.sbxcloudsdk.util.UrlHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by lgguzman on 19/02/17.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SbxModelHelper {
// @JsonInclude(JsonInclude.Include.NON_NULL)
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")


    public static <T>SbxUrlComposer getUrlInsertOrUpdateRows(List<T> objects)throws Exception {

        if(objects.isEmpty())
            throw new SbxModelException("Array can not be empty");
        int domain = SbxAuth.getDefaultSbxAuth().getDomain();
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();

        Object of=objects.get(0);

        Class<?> myClass = of.getClass();
        if(!SbxDataValidator.hasKeyAnnotation(myClass)){
            throw new SbxModelException("SbxKey is required");
        }
        Annotation annotationClass = myClass.getAnnotation(SbxModelName.class);
        String modelName="";
        if(annotationClass instanceof SbxModelName){
            SbxModelName myAnnotation = (SbxModelName) annotationClass;
            modelName=myAnnotation.value();
        }else{
            throw  new SbxModelException("SbxModelName is required");
        }
        SbxQueryBuilder queryBuilder = new SbxQueryBuilder(domain, modelName, SbxQueryBuilder.TYPE.INSERT);

        queryBuilder.insert(of);
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                queryBuilder.isInsert()? UrlHelper.URL_INSERT:UrlHelper.URL_UPDATE
                , UrlHelper.POST
        );

        for (int i=1; i<objects.size(); i=i+1){
            queryBuilder.insert(objects.get(i));
        }


        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
                //       .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(queryBuilder.compile());
    }

    public static SbxUrlComposer getUrlInsertOrUpdateRow(Object o)throws Exception {

        int domain = SbxAuth.getDefaultSbxAuth().getDomain();
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        String key=null;
        Class<?> myClass = o.getClass();
        if(!SbxDataValidator.hasKeyAnnotation(myClass)){
            throw new SbxModelException("SbxKey is required");
        }
        Annotation annotationClass = myClass.getAnnotation(SbxModelName.class);
        String modelName="";
        if(annotationClass instanceof SbxModelName){
            SbxModelName myAnnotation = (SbxModelName) annotationClass;
            modelName=myAnnotation.value();
        }else{
            throw  new SbxModelException("SbxModelName is required");
        }

        SbxQueryBuilder queryBuilder = new SbxQueryBuilder(domain, modelName, SbxQueryBuilder.TYPE.INSERT);
        queryBuilder.insert(o);
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                queryBuilder.isInsert()? UrlHelper.URL_INSERT:UrlHelper.URL_UPDATE
                , UrlHelper.POST
        );
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
                //       .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(queryBuilder.compile());
    }


    public static  String getKeyFromAnnotation(Object o) throws Exception{
        Class<?> myClass = o.getClass();
        final Field[] variables = myClass.getDeclaredFields();

        for (final Field variable : variables) {

            final Annotation annotation = variable.getAnnotation(SbxKey.class);

            if (annotation != null && annotation instanceof SbxKey) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    Object object=variable.get(o);
                    if(object==null){
                        return null;
                    }
                    String s= (String)object;
                    variable.setAccessible(isAccessible);
                    return s;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxAuthException(e);
                }
            }
        }
        throw  new SbxModelException("no Key present on object");
    }



    public static SbxQueryBuilder prepareQuery(Class<?> myClass) throws  Exception{
        int domain = SbxAuth.getDefaultSbxAuth().getDomain();
        Annotation annotationClass = myClass.getAnnotation(SbxModelName.class);
        String modelName="";
        if(annotationClass instanceof SbxModelName){
            SbxModelName myAnnotation = (SbxModelName) annotationClass;
            modelName=myAnnotation.value();
        }else{
            throw  new SbxModelException("SbxModelName is required");
        }
        return new SbxQueryBuilder(domain,modelName);

    }

    public static SbxQueryBuilder prepareQuery(Class<?> myClass,  int page, int limit) throws  Exception{
        int domain = SbxAuth.getDefaultSbxAuth().getDomain();
        Annotation annotationClass = myClass.getAnnotation(SbxModelName.class);
        String modelName="";
        if(annotationClass instanceof SbxModelName){
            SbxModelName myAnnotation = (SbxModelName) annotationClass;
            modelName=myAnnotation.value();
        }else{
            throw  new SbxModelException("SbxModelName is required");
        }
        return new SbxQueryBuilder(domain,modelName,page,limit);

    }

    public static SbxQueryBuilder prepareQueryToDelete(Class<?> myClass) throws  Exception{
        int domain = SbxAuth.getDefaultSbxAuth().getDomain();
        Annotation annotationClass = myClass.getAnnotation(SbxModelName.class);
        String modelName="";
        if(annotationClass instanceof SbxModelName){
            SbxModelName myAnnotation = (SbxModelName) annotationClass;
            modelName=myAnnotation.value();
        }else{
            throw  new SbxModelException("SbxModelName is required");
        }
        return new SbxQueryBuilder(domain,modelName, SbxQueryBuilder.TYPE.DELETE);

    }

    public static SbxUrlComposer getUrlDelete(SbxQueryBuilder sbxQueryBuilder)throws Exception{
        if(sbxQueryBuilder.getKeysD()==null)
            throw new SbxModelException("SbxQueryBuilder is not DELETE Type");
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.URL_DELETE
                , UrlHelper.POST
        );
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(sbxQueryBuilder.compile());
    }

    public static SbxUrlComposer getUrlQuery(SbxQueryBuilder sbxQueryBuilder) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.URL_FIND
                , UrlHelper.POST
        );
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(sbxQueryBuilder.compile());
    }

    public static SbxUrlComposer getUrlQueryKeys(SbxQueryBuilder sbxQueryBuilder, String [] keys) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.URL_FIND
                , UrlHelper.POST
        );
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(sbxQueryBuilder.compileWithKeys(keys));
    }


}