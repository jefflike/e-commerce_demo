package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    // 因为保存和更新接口非常相似，更新多一个产品id的值，我们只需要判断一下就可以讲连个接口合在一起使用
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product == null){
            return ServerResponse.createByErrorMessage("更新或增加的产品参数不正确");
        }
        if(StringUtils.isNotBlank(product.getSubImages())){
            // 如果子图不是空的，我们就取第一个子图作为我们的主图
            String[] subImgArray = product.getSubImages().split(",");
            if(subImgArray.length > 0) {
                product.setMainImage(subImgArray[0]);
            }
        }
        // 有id则为更新操作
        if(product.getId() != null){
            int rowCount = productMapper.updateByPrimaryKey(product);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("更新产品成功");
            }
            return ServerResponse.createBySuccessMessage("更新产品失败");
        }else {
            int rowCount = productMapper.insert(product);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("新增商品成功");
            }
            return ServerResponse.createBySuccessMessage("新增商品失败");
        }
    }

    /**
     * 修改商品状态
     */
    public ServerResponse<String > setSaleStatus(Integer productId, Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("修改商品状态成功");
        }
        return ServerResponse.createBySuccessMessage("修改商品状态失败");
    }
}
