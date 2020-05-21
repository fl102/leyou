package com.leyou.Service.impl;

import com.leyou.Dao.SpuDetailDao;
import com.leyou.Service.SpuDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpuDetailServiceImpl implements SpuDetailService {

    @Autowired
    private SpuDetailDao spuDetailDao;
}
