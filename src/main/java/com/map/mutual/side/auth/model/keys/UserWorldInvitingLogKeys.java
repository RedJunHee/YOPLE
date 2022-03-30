package com.map.mutual.side.auth.model.keys;


import lombok.EqualsAndHashCode;

import javax.persistence.Id;
import java.io.Serializable;

@EqualsAndHashCode
public class UserWorldInvitingLogKeys implements Serializable {
    private String worldinvitationCode;
    private String userSuid;
    private String targetSuid;
}
