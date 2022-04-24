package com.map.mutual.side.common.utils;

import org.springframework.util.Assert;

public class YOPLEAssert extends Assert {

    public static void isSUID(String suid) throws Exception {
        try {
            CryptUtils.AES_Decode(suid);
        }catch(Exception e){
            throw e;
        }

    }
}
