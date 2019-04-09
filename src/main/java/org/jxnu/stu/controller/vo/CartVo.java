package org.jxnu.stu.controller.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {

    private List<CartProductVoList> cartProductVoList;
    private boolean allChecked;
    private BigDecimal cartTotalPrice;

    public List<CartProductVoList> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVoList> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }
}
