package com.sbxcloud.java.sbxcloudsdk.auth;


import com.sbxcloud.java.sbxcloudsdk.auth.config.SbxAppKeyField;
import com.sbxcloud.java.sbxcloudsdk.auth.config.SbxDomainField;
import com.sbxcloud.java.sbxcloudsdk.auth.user.SbxAuthToken;
import com.sbxcloud.java.sbxcloudsdk.auth.user.SbxEmailField;
import com.sbxcloud.java.sbxcloudsdk.auth.user.SbxNameField;
import com.sbxcloud.java.sbxcloudsdk.auth.user.SbxPasswordField;
import com.sbxcloud.java.sbxcloudsdk.auth.user.SbxUsernameField;
import com.sbxcloud.java.sbxcloudsdk.exception.SbxAuthException;
import com.sbxcloud.java.sbxcloudsdk.exception.SbxConfigException;
import com.sbxcloud.java.sbxcloudsdk.util.SbxUrlComposer;
import com.sbxcloud.java.sbxcloudsdk.util.UrlHelper;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by lgguzman on 18/02/17.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SbxAuth {

    private static final String FILE_NAME=SbxAuth.class.getName();
    private static final String FILE_NAME_TOKEN=FILE_NAME+"_Token";
    private static final String FILE_NAME_DOMAIN=FILE_NAME+"_Domain";
    private static final String FILE_NAME_APPKEY=FILE_NAME+"_Appkey";
    private static SbxAuth defaultSbxAuth;
    private  int domain;
    private  String appKey;
    private String token;
    private SbxAuthPersistenceStrategy sbxAuthPersistenceStrategy;
    private boolean HttpLog;




    /**
     * initialize the data for comunicate on sbxcloud.com
     * @param domain the id of the domain on sbxcloud.com
     * @param appKey the app key on sbxcloud.com
     */
    public static void initializeIfIsNecessary( int domain, String appKey) {
        if(defaultSbxAuth ==null) {
            defaultSbxAuth = new SbxAuth();
            defaultSbxAuth.domain = domain;
            defaultSbxAuth.appKey = appKey;
        }
    }

    /**
     * initialize the data for comunicate on sbxcloud.com
     * @param domain the id of the domain on sbxcloud.com
     * @param appKey the app key on sbxcloud.com
     */
    public static void initializeIfIsNecessary( int domain, String appKey, SbxAuthPersistenceStrategy sbxAuthPersistenceStrategy) {
        if(defaultSbxAuth ==null) {
            defaultSbxAuth = new SbxAuth();
            defaultSbxAuth.domain = domain;
            defaultSbxAuth.appKey = appKey;
            defaultSbxAuth.setSbxAuthPersistenceStrategy(sbxAuthPersistenceStrategy);
            defaultSbxAuth.getSbxAuthPersistenceStrategy().setDomain(domain);
            defaultSbxAuth.getSbxAuthPersistenceStrategy().setAppKey(appKey);
        }
    }

    public boolean isHttpLog() {
        return HttpLog;
    }


    public void setHttpLog(boolean httpLog) {
        HttpLog = httpLog;
    }

    /**
     * set a persistence strategy
     * @param sbxAuthPersistenceStrategy
     */
    public void setSbxAuthPersistenceStrategy(SbxAuthPersistenceStrategy sbxAuthPersistenceStrategy){
        this.sbxAuthPersistenceStrategy =sbxAuthPersistenceStrategy;
    }

    public SbxAuthPersistenceStrategy getSbxAuthPersistenceStrategy(){
        return sbxAuthPersistenceStrategy;
    }

    /**
     *
     * @return
     * @throws SbxConfigException
     */
    public static SbxAuth getDefaultSbxAuth()throws SbxConfigException {
        if(defaultSbxAuth==null)
            throw  new SbxConfigException("SbxAuth not initialized");
        return defaultSbxAuth;
    }

    /**
     *
     * @return
     * @throws SbxConfigException
     */
    public static SbxAuth getDefaultSbxAuth(SbxAuthPersistenceStrategy sbxAuthPersistenceStrategy)throws SbxConfigException {
        if(defaultSbxAuth==null )
            if (sbxAuthPersistenceStrategy==null)
                throw  new SbxConfigException("SbxAuth not initialized");
            else
                SbxAuth.initializeIfIsNecessary(sbxAuthPersistenceStrategy.getDomain(),
                        sbxAuthPersistenceStrategy.getAppKey(), sbxAuthPersistenceStrategy);
        return defaultSbxAuth;
    }

    /**
     *
     * @return
     * @throws SbxConfigException
     */
    public int getDomain()throws SbxConfigException {
        if(domain==0){
            if(sbxAuthPersistenceStrategy==null)
                throw new SbxConfigException("SbxAuth not initialized");
            else
                return domain = sbxAuthPersistenceStrategy.getDomain();
        }
        return domain;
    }

    /**
     *
     * @return
     * @throws SbxConfigException
     */
    public String getToken()throws SbxConfigException {
        if(token==null){
            if (sbxAuthPersistenceStrategy==null || sbxAuthPersistenceStrategy.getToken().equals(""))
                throw new SbxConfigException("User is not login yet.");
            return token = sbxAuthPersistenceStrategy.getToken();
        }
        return token;
    }

    /**
     *
     * @return
     * @throws SbxConfigException
     */
    public String getAppKey()throws SbxConfigException{
        if(appKey==null){
            if(sbxAuthPersistenceStrategy==null)
                throw new SbxConfigException("SbxAuth not initialized");
            else
                return appKey = sbxAuthPersistenceStrategy.getAppKey();
        }
        return appKey;
    }


    public void resetToken(){
        if(sbxAuthPersistenceStrategy!=null){
            sbxAuthPersistenceStrategy.setToken("");
        }
        token=null;
    }

    /**
     *
     * @param obj
     * @return
     * @throws SbxConfigException
     */
    public  String refreshToken(final Object obj)throws SbxConfigException, SbxAuthException{
        Class<?> myClass=obj.getClass();
        final Field[] variables = myClass.getDeclaredFields();
        for (final Field variable : variables) {

            final Annotation annotation = variable.getAnnotation(SbxAuthToken.class);

            if (annotation != null && annotation instanceof SbxAuthToken) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    token= (String)variable.get(obj);
                    variable.setAccessible(isAccessible);
                    if(sbxAuthPersistenceStrategy!=null){
                        sbxAuthPersistenceStrategy.setToken(token);
                    }
                    return  token;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxConfigException(e);
                }

            }
        }
        throw new SbxAuthException();
    }

    /**
     *
     * @param app
     * @return
     * @throws SbxConfigException
     */
    private static String getAppKeyAnnotation(final Object app)throws SbxConfigException{
        Class<?> myClass=app.getClass();
        final Field[] variables = myClass.getDeclaredFields();
        for (final Field variable : variables) {

            final Annotation annotation = variable.getAnnotation(SbxAppKeyField.class);

            if (annotation != null && annotation instanceof SbxAppKeyField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    String s= (String)variable.get(app);
                    variable.setAccessible(isAccessible);
                    return s;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxConfigException(e);
                }

            }
        }
        throw new SbxConfigException();
    }

    /**
     *
     * @param app
     * @return
     * @throws SbxConfigException
     */
    private static int getDomainAnnotation(final Object app) throws SbxConfigException{
        Class<?> myClass=app.getClass();
        final Field[] variables = myClass.getDeclaredFields();
        for (final Field variable : variables) {

            final Annotation annotation = variable.getAnnotation(SbxDomainField.class);

            if (annotation != null && annotation instanceof SbxDomainField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    int s= variable.getInt(app);
                    variable.setAccessible(isAccessible);
                    return s;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxConfigException(e);
                }

            }
        }
        throw new SbxConfigException();
    }


    public SbxUrlComposer getUrllogin(Object o)throws SbxConfigException, SbxAuthException{
        int domain=getDomain();
        String appKey=getAppKey();
        String username=null;
        String password=null;
        String email=null;
        Class<?> myClass=o.getClass();
        final Field[] variables = myClass.getDeclaredFields();
        for (final Field variable : variables) {


            final Annotation annotation = variable.getAnnotation(SbxUsernameField.class);

            if (annotation != null && annotation instanceof SbxUsernameField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    username= (String)variable.get(o);
                    variable.setAccessible(isAccessible);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxAuthException(e);
                }

            }


            final Annotation annotationE = variable.getAnnotation(SbxEmailField.class);

            if (annotationE != null && annotationE instanceof SbxEmailField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    email= (String)variable.get(o);
                    variable.setAccessible(isAccessible);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxAuthException(e);
                }

            }

            final Annotation annotationP = variable.getAnnotation(SbxPasswordField.class);

            if (annotationP != null && annotationP instanceof SbxPasswordField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    password= (String)variable.get(o);
                    variable.setAccessible(isAccessible);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxAuthException(e);
                }

            }
        }
        SbxUrlComposer sbxUrlComposer;

        if(username==null){
            if(email==null){
                throw new SbxAuthException();
            }else{
                if(password==null){
                    throw new SbxAuthException();
                }else{
                    sbxUrlComposer = new SbxUrlComposer(
                            UrlHelper.URL_LOGIN
                            , UrlHelper.GET
                    ).setUrlParam("email",email)
                            .setUrlParam("password",password);
                }
            }
        }else{
            if(password==null){
                throw new SbxAuthException();
            }else{
                sbxUrlComposer = new SbxUrlComposer(
                        UrlHelper.URL_LOGIN
                        , UrlHelper.GET
                ).setUrlParam("login",username)
                        .setUrlParam("password",password);
            }
        }
       return  sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY,appKey);
            //    .addHeader(UrlHelper.HEADER_KEY_ENCODING,UrlHelper.HEADER_GZIP);
    }

    public SbxUrlComposer getUrlSigIn(Object o)throws SbxConfigException, SbxAuthException {
        int domain = getDomain();
        String appKey = getAppKey();
        String username = null;
        String password = null;
        String email = null;
        String name=null;
        Class<?> myClass = o.getClass();
        final Field[] variables = myClass.getDeclaredFields();
        for (final Field variable : variables) {

            final Annotation annotation = variable.getAnnotation(SbxUsernameField.class);

            if (annotation != null && annotation instanceof SbxUsernameField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    username = (String) variable.get(o);
                    variable.setAccessible(isAccessible);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxAuthException(e);
                }

            }


            final Annotation annotationE = variable.getAnnotation(SbxEmailField.class);

            if (annotationE != null && annotationE instanceof SbxEmailField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    email = (String) variable.get(o);
                    variable.setAccessible(isAccessible);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxAuthException(e);
                }

            }

            final Annotation annotationP = variable.getAnnotation(SbxPasswordField.class);

            if (annotationP != null && annotationP instanceof SbxPasswordField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    password = (String) variable.get(o);
                    variable.setAccessible(isAccessible);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxAuthException(e);
                }

            }

            final Annotation annotationN = variable.getAnnotation(SbxNameField.class);

            if (annotationN != null && annotationN instanceof SbxNameField) {
                try {
                    boolean isAccessible=variable.isAccessible();
                    variable.setAccessible(true);
                    name = (String) variable.get(o);
                    variable.setAccessible(isAccessible);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new SbxAuthException(e);
                }

            }
        }
        if(username == null){
            throw new SbxAuthException("Annnotation SbxUsernameField is required");
        }
        if(email == null){
            throw new SbxAuthException("Annnotation SbxEmailField is required");
        }
        if(password == null){
            throw new SbxAuthException("Annnotation SbxPasswordField is required");
        }
        if(name == null){
            throw new SbxAuthException("Annnotation SbxNameField is required");
        }

        return new SbxUrlComposer(
                UrlHelper.URL_SIGN_UP
                , UrlHelper.GET
        ).setUrlParam("email",email)
                .setUrlParam("password",password)
                .setUrlParam("login",username)
                .setUrlParam("name",name)
                .setUrlParam("domain",domain+"")
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey);
           //     .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP);
    }

    public static SbxUrlComposer getUrlRequestPasswordCode(String emailTemplate, String subject, String from) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
    //    String token = SbxAuth.getDefaultSbxAuth().getToken();
        int domain = SbxAuth.getDefaultSbxAuth().getDomain();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.PASSWORD_REQUEST
                , UrlHelper.POST
        );
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("domain", domain);
        jsonObject.put("email_template", emailTemplate);
        jsonObject.put("subject", subject);
        jsonObject.put("user_email", from);
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
 //               .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(jsonObject);
    }


    public static SbxUrlComposer getUrlChangePasswordCode(int userId, int code, String password) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
      //  String token = SbxAuth.getDefaultSbxAuth().getToken();
        int domain = SbxAuth.getDefaultSbxAuth().getDomain();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.PASSWORD_REQUEST
                , UrlHelper.PUT
        );
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("domain", domain);
        jsonObject.put("user_id", userId);
        jsonObject.put("code", code);
        jsonObject.put("password", password);
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
 //               .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token)
                .addBody(jsonObject);
    }


    public static SbxUrlComposer getUrlDomainList() throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        //       String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.DOMAIN
                , UrlHelper.GET
        );
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey);
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
//                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token);
    }

    public static SbxUrlComposer validateToken(String token) throws  Exception{
        String appKey = SbxAuth.getDefaultSbxAuth().getAppKey();
        //       String token = SbxAuth.getDefaultSbxAuth().getToken();
        SbxUrlComposer sbxUrlComposer = new SbxUrlComposer(
                UrlHelper.VALIDATE
                , UrlHelper.GET
        );
        return sbxUrlComposer
                .addHeader(UrlHelper.HEADER_KEY_APP_KEY, appKey)
                .setUrlParam("token",token);
//                .addHeader(UrlHelper.HEADER_KEY_ENCODING, UrlHelper.HEADER_GZIP)
//                .addHeader(UrlHelper.HEADER_KEY_CONTENT_TYPE, UrlHelper.HEADER_JSON)
//                .addHeader(UrlHelper.HEADER_KEY_AUTORIZATION, UrlHelper.HEADER_BEARER+token);
    }

}
