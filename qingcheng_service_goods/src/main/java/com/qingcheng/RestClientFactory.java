package com.qingcheng;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/13 14:55
 * @Description:
 */
public class RestClientFactory {
    public static RestHighLevelClient getRestHighLevelClient(String hostname, int port) {
        HttpHost http = new HttpHost(hostname, port, "http");
        RestClientBuilder restClientBuilder = RestClient.builder(http); // rest构建器
        return new RestHighLevelClient(restClientBuilder);
    }
}
