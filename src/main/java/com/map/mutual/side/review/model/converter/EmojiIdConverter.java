package com.map.mutual.side.review.model.converter;

import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.review.model.enumeration.EmojiType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;
/**
 * fileName       : EmojiIdConverter
 * author         : kimjaejung
 * createDate     : 2022/04/20
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/20        kimjaejung       최초 생성
 *
 */
@Converter(autoApply = true)
public class EmojiIdConverter implements AttributeConverter<EmojiType, Long> {

    @Override
    public Long convertToDatabaseColumn(EmojiType attribute) {
        System.out.println(attribute);
        if (Objects.isNull(attribute)) {
            try {
                throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
            } catch (YOPLEServiceException e) {
                e.printStackTrace();
            }
        }
        return attribute.getId();
    }


    @Override
    public EmojiType convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
            try {
                throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
            } catch (YOPLEServiceException e) {
                e.printStackTrace();
            }
        }
        return EmojiType.findId(dbData);
    }


}
