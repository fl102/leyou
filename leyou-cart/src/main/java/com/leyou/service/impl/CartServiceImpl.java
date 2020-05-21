package com.leyou.service.impl;

import com.leyou.client.GoodClient;
import com.leyou.domain.Cart;
import com.leyou.domain.Sku;
import com.leyou.domain.UserInfo;
import com.leyou.interceptor.LoginInterceptor;
import com.leyou.service.CartService;
import com.leyou.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String USER_CART = "user:cart:";
    @Autowired
    private GoodClient goodClient;
    @Override
    public void addCart(Cart cart) {
        //获取用户id
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询redis是否之前添加过
        BoundHashOperations<String, Object, Object> ops = this.redisTemplate.boundHashOps(USER_CART + userInfo.getId());
        //判断是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        if (ops.hasKey(skuId.toString())){
            //存在获取数据
            String cartJson = ops.get(skuId.toString()).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);
        } else {
            //如果不存在添加数据
            //首先查询通过skuId查询sku
            Sku sku = this.goodClient.findSkuBySkuId(skuId);
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setTitle(sku.getTitle());
            cart.setUserId(userInfo.getId());
            cart.setPrice(sku.getPrice());
            cart.setOwnSpec(sku.getOwnSpec());
        }
        ops.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    @Override
    public void addCarts(List<Cart> cart) {
        cart.forEach(c ->{
            addCart(c);
        });
    }

    @Override
    public List<Cart> getCarts() {
        //获取用户id
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //用户有没有购物车
        if(!this.redisTemplate.hasKey(USER_CART + userInfo.getId())){
            return null;
        }
        BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate.boundHashOps(USER_CART + userInfo.getId());
        //购物车是不是空
        List<Object> cartsJson = boundHashOps.values();
        if (CollectionUtils.isEmpty(cartsJson)){
            return null;
        }

        return cartsJson.stream().map(cartJson ->{
            return JsonUtils.parse(cartJson.toString(), Cart.class);
        }).collect(Collectors.toList());

    }

    @Override
    public void updateNum(Cart cart) {
        //获取用户id
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //用户有没有购物车
        if(!this.redisTemplate.hasKey(USER_CART + userInfo.getId())){
            return;
        }
        BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate.boundHashOps(USER_CART + userInfo.getId());
        //购物车是不是空
        Integer num = cart.getNum();
        String cartJson = boundHashOps.get(cart.getSkuId().toString()).toString();
        if (StringUtils.isBlank(cartJson)){
            return;
        }
        cart = JsonUtils.parse(cartJson, Cart.class);
        cart.setNum(num);
        boundHashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    @Override
    public void deleteCart(Long skuId) {
        //获取用户id
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //用户有没有购物车
        if(!this.redisTemplate.hasKey(USER_CART + userInfo.getId())){
            return;
        }
        BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate.boundHashOps(USER_CART + userInfo.getId());

        boundHashOps.delete(skuId.toString());
    }
}
