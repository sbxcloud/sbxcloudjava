package com.sbxcloud.java.sbxcloudsdk.auth;

public interface SbxAuthPersistenceStrategy {


    int getDomain();
    String getToken();
    String getAppKey();

    void setDomain(int domain);
    void setToken(String token);
    void setAppKey(String appkey);

}
