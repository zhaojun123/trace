package com.example.order.request;

public class AddOrder {

    /**
     * 下单人
     */
    private Long userId;

    /**
     * 下单商品
     */
    private Long goodsId;

    /**
     * 购买数量
     */
    private Long count;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
