package com.leyou.controller;

import com.leyou.client.GoodClient;
import com.leyou.domain.Cart;
import com.leyou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        this.cartService.addCart(cart);
                return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("carts")
    public ResponseEntity<Void> addCarts(@RequestBody List<Cart> cart){
        this.cartService.addCarts(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("carts")
    public ResponseEntity<List<Cart>> getCarts(){
        List<Cart> carts = this.cartService.getCarts();
        if (carts == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carts);
    }

    @PutMapping("update")
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
        this.cartService.updateNum(cart);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("delete/{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        this.cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
