package com.map.mutual.side.review.model.converter;

import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;
/**
 * fileName       : EmojiActiveTypeConverter
 * author         : kimjaejung
 * createDate     : 2022/04/20
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/20        kimjaejung       최초 생성
 *
 */
@Converter(autoApply = true)
public class EmojiActiveTypeConverter implements AttributeConverter<EmojiType, String> {

    @Override
    public String convertToDatabaseColumn(EmojiType attribute) {
        if (Objects.isNull(attribute)) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
        return attribute.getActiveType();
    }

    @Override
    public EmojiType convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
        return EmojiType.findActiveType(dbData);
    }


}
