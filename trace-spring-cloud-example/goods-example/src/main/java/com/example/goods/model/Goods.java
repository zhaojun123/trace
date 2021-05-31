package com.example.goods.model;

public class Goods {

    private Long goodsId;

    private String goodsName;

    private Long goodsStock;

    private Double goodsPrice;


    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getGoodsStock() {
        return goodsStock;
    }

    public void setGoodsStock(Long goodsStock) {
        this.goodsStock = goodsStock;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
}
