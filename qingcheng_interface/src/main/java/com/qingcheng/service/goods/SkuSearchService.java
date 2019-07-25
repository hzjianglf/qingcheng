package com.qingcheng.service.goods;

import java.util.Map;

/**
 * @Auther: wanjunyi
 * @Date: 2019/7/13 15:04
 * @Description:
 */
public interface SkuSearchService {

    void addAllSkuToSearch();

    Map search(Map<String, String> searchMap);
}
