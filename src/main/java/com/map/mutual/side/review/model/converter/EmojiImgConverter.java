package com.map.mutual.side.review.model.converter;

import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter(autoApply = true)
public class EmojiImgConverter implements AttributeConverter<EmojiType, String> {

    @Override
    public String convertToDatabaseColumn(EmojiType attribute) {
        if (Objects.isNull(attribute)) {
            try {
                throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
            } catch (YOPLEServiceException e) {
                e.printStackTrace();
            }
        }
        return attribute.getImg_url();
    }

    @Override
    public EmojiType convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            try {
                throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
            } catch (YOPLEServiceException e) {
                e.printStackTrace();
            }
        }
        return EmojiType.findImg(dbData);
    }


}
