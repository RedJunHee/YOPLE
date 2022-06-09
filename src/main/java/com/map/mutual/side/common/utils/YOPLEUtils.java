package com.map.mutual.side.common.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.validation.Errors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
/**
 * fileName       : YOPLEUtils
 * author         : kimjaejung
 * createDate     : 2022/03/15
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/15        kimjaejung       최초 생성
 *
 */
@Log4j2
public class YOPLEUtils {

    /**
     * 랜덤한 숫자를 반환하는 메소드
     * @return
     */
    public static String getSMSAuth()
    {
        return Integer.toString(RandomUtils.nextInt(1000, 10000));
    }

    /**
     * 밸리데이션 에러를 가공하기 위한 메소드
     *
     * @param errors
     * @return
     */
    public static LinkedList<LinkedHashMap<String, String>> refineValidationError(Errors errors) {
        LinkedList errorList = new LinkedList<LinkedHashMap<String, String>>();
        errors.getFieldErrors().forEach(e-> {
            LinkedHashMap<String, String> error = new LinkedHashMap<>();
            error.put(e.getField(), e.getDefaultMessage());
            errorList.push(error);
        });
        return errorList;
    }

    /**
     * 해당 경로에 디렉토리를 생성
     * @param path
     */
    public static void createDirectories(String path) {
        Path dirPath = Paths.get(path);
        try {
            Files.createDirectories(dirPath);
            log.info("\nCreated directories : {}", path);
        } catch (IOException e) {
            log.error("\nERROR : {}", e.getMessage());
        }
    }

    public static String getWorldRandomCode()
    {
        String code = "";

        for(int i = 0 ; i< 6 ; i++)
        {
            int random = (int)(Math.random()*43)+48;

            if(random >= 58 && random <= 64) {
                i--;
                continue;
            }
            code+= (char)random;
        }

        return code;
    }

    public static LocalDateTime TimeStamp2LocalDateTime (Timestamp time)
    {
        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = time.toLocalDateTime();
            return dateTime;

        }catch(DateTimeParseException e)
        {
            return null;
        }

    }

    public static String ClearXSS(String value) {
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");

        return value;
    }
    public static String DeClearXSS(String value) {
        value = value.replaceAll("& lt;","<").replaceAll( "& gt;",">");
        value = value.replaceAll( "& #40;", "\\(").replaceAll( "& #41;", "\\)");
        value = value.replaceAll( "& #39;", "'");
//        value = value.replaceAll( "\"\"", "[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']");
//        value = value.replaceAll( "", "script");

        return value;
    }


}
