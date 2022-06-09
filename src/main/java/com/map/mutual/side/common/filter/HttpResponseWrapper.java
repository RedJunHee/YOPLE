package com.map.mutual.side.common.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponseWrapper extends HttpServletResponseWrapper {
    ByteArrayOutputStream byteArrayOutputStream;
    ResponseBodyServletOutputStream responseBodyServletOutputStream;

    public HttpResponseWrapper(HttpServletResponse response) {
        super(response);
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (responseBodyServletOutputStream == null) {
            responseBodyServletOutputStream = new ResponseBodyServletOutputStream(byteArrayOutputStream);
        }
        return responseBodyServletOutputStream;
    }

    // Response Body Get
    public String getDataStreamToString() {
        return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    private class ResponseBodyServletOutputStream extends ServletOutputStream {

        private final DataOutputStream outputStream;

        public ResponseBodyServletOutputStream(OutputStream output) {
            this.outputStream = new DataOutputStream(output);
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
        }
    }

}
