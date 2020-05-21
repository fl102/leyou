package com.leyou.controller;

import com.leyou.service.GoodHtmlService;
import com.leyou.service.GoodService;
import com.leyou.service.impl.GoodHtmlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("item")
public class GoodsController {
    @Autowired
    private GoodService goodService;
    @Autowired
    private GoodHtmlServiceImpl goodHtmlService;

    @GetMapping("{id}.html")
    public String toGoodsDetail(@PathVariable("id") long id, Model model){
        Map<String, Object> map = goodService.loadData(id);
        model.addAllAttributes(map);
        goodHtmlService.asyncExcute(id);
        return "item";
    }
}
