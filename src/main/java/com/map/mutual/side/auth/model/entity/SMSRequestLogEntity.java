package com.map.mutual.side.auth.model.entity;


import com.map.mutual.side.common.repository.config.TimeEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


/** Class       : SMSRequestLog (Model)
 *  Author      : 조 준 희
 *  Description : SMS 요청시 기록하는 LOG
 *  History     : [2022-03-11] - TEMP
 */
/*
* - SEQ
- 핸드폰 번호 (암호화)
- 보낸 SMS Authentication Number
- 받은 SMS Authentication Number
- 단말 ID
- InsertDt ( 로그 생성 시간 = 보낸 시간 ) - UpdateDt ( 로그 수정 시간 = 받은 시간)
* */

@Table(name="SMS_REQUEST_LOG")
@Entity
@Getter
@Setter
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SMSRequestLogEntity extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ", columnDefinition = "BIGINT")
    private Long seq;

    @Column(name="PHONE", nullable = false, columnDefinition = "VARCHAR(15)")
    private String phone;

    @Column(name = "REQUEST_AUTH_NUM", nullable = false, columnDefinition = "VARCHAR(6)")
    private String requestAuthNum;

    @Column(name = "RESPONSE_AUTH_NUM", columnDefinition = "VARCHAR(6)")
    private String responseAuthNum;

    @Column(name = "DUID", nullable = false, columnDefinition = "VARCHAR(50)")
    private String duid;

}
