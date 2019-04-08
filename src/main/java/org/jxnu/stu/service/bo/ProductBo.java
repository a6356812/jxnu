package org.jxnu.stu.service.bo;

import org.jxnu.stu.dao.pojo.Product;

public class ProductBo extends Product {
    private String imageHost;
    private Integer parentCategoryId;

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
