package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if(org.apache.commons.lang3.StringUtils.isBlank(categoryName) || parentId == null){
            ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);// 表示默认这个字段刚创建是可用的
        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if(org.apache.commons.lang3.StringUtils.isBlank(categoryName) || categoryId == null){
            ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int updateCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("修改分类名称成功");
        }
        return ServerResponse.createByErrorMessage("修改分类名称失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        // 通过父类型的id找到所有的子类的集合
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            // 即使我们查找的这个类没有子类，我们也不需要给前端传一个什么错误，我们记录到日志即可（查找不到不是错误）
            logger.info("未找到当前分类的子分类");
        }
        // 这个方法接收的T方法，所以可以传递这个对象作为data
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 查询到此分类下的所有的分类id
     * 取到id查下面所有的类，并且判断子节点底下还有没有子节点
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
        // 需要对categorySet进行初始化，guava提供了它的初始化处理
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);

        // 我们要返回的是一个category的集合,也是用guava提供的初始化方法
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryIdList != null){
            for(Category category : categorySet){
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }
    /**
     * 写一个递归函数，选择Set结构直接可以排重，但是要做到去重，我们的种类需要重写hashCode代码和equals代码
     * 定义一个Set参数，将Set再返回给方法本身
     */
    private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        // 也就是说，当第一个id进来的时候，先加到set中，第二次进来的就开始是第二级的id
        if(category != null){
            categorySet.add(category);
        }
        // 第一级进来的时候，此时id和他所有的子类都进了set，查第二级的所有子类id，mybatis查不到不会返回null，所以不需要空判断，别处我们就需要对空做空判断
        // 以防止空指针
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        // 遍历第一级的所有分类，依次查看是否有子类，遍历知道子节点为空的时候，也就退出递归了
        for (Category cate:categoryList) {
            findChildCategory(categorySet, cate.getId());
        }
        return categorySet;
    }



}
