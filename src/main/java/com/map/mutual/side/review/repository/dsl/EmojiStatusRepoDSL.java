package com.map.mutual.side.review.repository.dsl;

import com.map.mutual.side.auth.model.dto.notification.EmojiNotiDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Description : 이모지 상태
 * Name        : EmojiStatusRepoDSL
 * Author      : 조 준 희
 * History     : [2022-04-27] - 조 준 희 - Create
 */
public interface EmojiStatusRepoDSL {
    List<EmojiNotiDto> findEmojiNotis(String suid);
    boolean existsNewNoti (String suid, LocalDateTime searchLocalDateTime);
}
