/**
 * fileName       : HttpCustomUtil
 * author         : kimjaejung
 * createDate     : 2022/03/12
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/12        kimjaejung       최초 생성
 *
 */
package com.map.mutual.side.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class HttpSensClient {

    public String sensHttpGetResponseBody(String url, String token) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, InvalidKeyException {
        HttpClient httpClient = getHttpClientInsecure();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("x-ncp-apigw-timestamp", "");
        httpGet.addHeader("x-ncp-iam-access-key", "kNKPMYVwhTp3uIYbek9i");
        httpGet.addHeader("x-ncp-apigw-signature-v2", "");

        HttpResponse httpResponse = httpClient.execute(httpGet);

        String result = getHttpResponseBody(httpResponse);

        return result;
    }
    public static HttpClient getHttpClientInsecure() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return HttpClients.custom().setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
    }

    public static String getHttpResponseBody(HttpResponse httpResponse) throws IOException {
        HttpEntity httpEntity = httpResponse.getEntity();
        String result = EntityUtils.toString(httpEntity);
        return  result;
    }

    public static String sensHttpPost(String url, String jsonBody,  String token) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpClient httpClient = getHttpClientInsecure();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Authorization", "Bearer " + token);
        httpPost.addHeader("Content-Type", "application/json");

        StringEntity stringEntity = new StringEntity(jsonBody);

        httpPost.setEntity(stringEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        return getHttpResponseBody(httpResponse);
    }

    public static String sensHttpDelete(String url, String token) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpClient httpClient = getHttpClientInsecure();
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.addHeader("Connection", "keep-alive");
        httpDelete.addHeader("Authorization", "Bearer " + token);
        httpDelete.addHeader("Content-Type", "application/json");


        HttpResponse httpResponse = httpClient.execute(httpDelete);

        String result = getHttpResponseBody(httpResponse);

        return result;
    }

    public static int sensHttpGetStatusCode(String url, String token) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpClient httpClient = getHttpClientInsecure();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Authorization", "Bearer " + token);
        httpGet.addHeader("Content-Type", "application/json");

        HttpResponse httpResponse = httpClient.execute(httpGet);

        return httpResponse.getStatusLine().getStatusCode();
    }



    public static String convertYamlToJson(String yaml) throws JsonProcessingException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(yaml, Object.class);

        ObjectMapper jsonWriter = new ObjectMapper();
        return jsonWriter.writeValueAsString(obj);
    }






}
