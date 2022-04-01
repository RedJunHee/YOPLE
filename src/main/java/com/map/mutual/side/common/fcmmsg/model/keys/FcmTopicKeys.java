package com.map.mutual.side.common.fcmmsg.model.keys;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class FcmTopicKeys implements Serializable {
    private String fcmToken;
    private Long worldId;

}
