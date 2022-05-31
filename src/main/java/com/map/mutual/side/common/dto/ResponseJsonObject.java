package com.map.mutual.side.common.dto;

import com.map.mutual.side.common.enumerate.ApiStatusCode;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Class       : ResponseJsonObject
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-11] - 조 준희 - Class Create
 */
@NoArgsConstructor
public class ResponseJsonObject {
    // meta
    private Meta meta = null;

    // 생성자
    public ResponseJsonObject(Meta meta) {
        this.meta = meta;
    }

    public Meta getMeta() {return meta;}

    // data
    private Object data;
    public Object getData() {return data;}      // data = null일 때는 필드에 나타나지 않음
    public ResponseJsonObject setData(Object val) {this.data = val; return this;}

    public static ResponseJsonObject withStatusCode(ApiStatusCode statusCode) {
        Meta meta = new Meta(statusCode);
        return new ResponseJsonObject(meta);
    }

    //meta Class
    public static class Meta {
        // statusCode (not null)
        private ApiStatusCode apiStatus = ApiStatusCode.NONE;
        private String msg = null;
        public int getCode(){ return this.apiStatus.getCode(); }


        public void setMsg(String msg){
            this.msg = msg;
        }
        public String getErrorType() {
            if(apiStatus.getIsErrorType() == false)
                return null;
            return this.apiStatus.getType();
        }

        public String getErrorMsg() {
            if(apiStatus.getIsErrorType() == false)
                return null;
            if(msg != null)
                return this.apiStatus.getMessage() + "( "+ msg +" )";

            return this.apiStatus.getMessage();
        }

        @Override
        public String toString() {
            if(msg != null)
                return this.apiStatus.getMessage() + "( "+ msg +" )";

            return this.apiStatus.getMessage();
        }

        /**
         * meta 생성자
         * @param statusCode
         */
        public Meta(ApiStatusCode statusCode) {
            this.apiStatus = statusCode;
        }
    }
}
