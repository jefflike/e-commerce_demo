package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

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

    /**
     * 管理商品的详情，需要创建Vo,定义成manage开头是因为标明这是一个后端的管理代码
     * 方便后续的项目代码拆分，
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorMessage("传入商品参数有误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("商品不存在或已下架");
        }
        // 返回一个vo对象,此对象是承载我们当前对象的各个值的一个承载对象
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 此方法通过当前传入的product对象将Vo的对象组装上，总的来说vo就是对原对象
     * 的详细版，增加一些原对象中不含有的对象。
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        // imagehost需要从配置文件读取，为了配置与代码分离，这样就减少了硬编码的操作，后期可以将配置抽取到配置中心，
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        // parentCategoryId的查看
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);// 没有父种类id则默认是根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());// 否则存下商品的父类id
        }

        // 需要将数据库中取出的时间戳数据以固定格式展示出来
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * 三个步骤就实现了分页查询
     * @param pageno
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getProductList(int pageno, int pageSize){
        // 第一步，startPage记录一个开始
        PageHelper.startPage(pageno, pageSize);
        List<Product> productList = productMapper.selectAllProduct();
        // 此时查出来的list信息过于详细，我们的list是不需要这么多信息的，所以我们做一个list的vo
        // 第二步，填充自己的sql查询逻辑
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        // 第三步，pageHelper收尾
        // 根据sql返回的集合就根据他自动进行分页处理了
        PageInfo pageResult = new PageInfo(productList);
        // 此时pageResult中的参数都是vo类型的参数了
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());

        return productListVo;
    }
}
