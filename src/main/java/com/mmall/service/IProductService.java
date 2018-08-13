package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageno, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId, int pageno, int pageSize);
}
