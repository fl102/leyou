package com.leyou.service;

import com.leyou.domain.Cart;

import java.util.List;

public interface CartService {
    void addCart(Cart cart);

    void addCarts(List<Cart> cart);

    List<Cart> getCarts();

    void updateNum(Cart cart);

    void deleteCart(Long skuId);
}
