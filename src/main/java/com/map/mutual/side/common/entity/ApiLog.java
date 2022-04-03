package com.map.mutual.side.common.entity;

import com.map.mutual.side.common.repository.config.CreateDtEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/** Class       : Api_Log (Model)
 *  Author      : 조 준 희
 *  Description : API_LOG 테이블에 매핑되어지는 JPA 객체모델
 *  History     : [2022-03-16] - Temp
 */
@Table(name="API_LOG")
@Getter
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 테스트할 경우 PUBLIC으로 설정
public class ApiLog extends CreateDtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SEQ", columnDefinition = "BIGINT")
    private Long seq;

    @Column(name="API_NAME", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private  String apiName;

    @Column(name="API_DESC", nullable = false, columnDefinition = "VARCHAR(8000)")
    private  String apiDesc;

    @Column(name="API_STATUS", nullable = false, columnDefinition = "VARCHAR(50)")
    private char apiStatus;

    @Column(name="PROCESS_TIME", nullable = false, columnDefinition = "FLOAT")
    private float processTime;
}
