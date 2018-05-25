package com.sbxcloud.java.sbxcloudsdk.util;


import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by lgguzman on 24/03/17.
 */

public interface SbxJsonModeler {

    void wrapFromJson(ObjectNode jsonObject);

    ObjectNode toJson();

}
