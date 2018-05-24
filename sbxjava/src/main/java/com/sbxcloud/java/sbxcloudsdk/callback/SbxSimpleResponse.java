package com.sbxcloud.java.sbxcloudsdk.callback;


/**
 * Created by lgguzman on 21/02/17.
 */

public interface SbxSimpleResponse<T> {

     void onError(Exception e);

     void  onSuccess(T tClass);
}
