package com.leyou.Service.impl;

import com.github.pagehelper.PageHelper;
import com.leyou.Dao.SkuDao;
import com.leyou.Dao.SpuDao;
import com.leyou.Dao.SpuDetailDao;
import com.leyou.Dao.StockDao;
import com.leyou.Service.GoodsService;
import com.leyou.domain.Sku;
import com.leyou.domain.SpuDetail;
import com.leyou.domain.Stock;
import com.leyou.domain.bo.SpuBo;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private SpuDao spuDao;

    @Autowired
    private SpuDetailDao spuDetailDao;

    @Autowired
    private SkuDao skuDao;

    @Autowired
    private StockDao stockDao;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //先插入spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuDao.insertSelective(spuBo);
        //插入spuDetail
        SpuDetail spuDetail= spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailDao.insertSelective(spuDetail);
        //保存sku
        saveSkuAndStock(spuBo);
        //保存stock
        sendMessage("insert", spuBo.getId());
    }

    private void sendMessage(String type, Long id) {
           try {
               amqpTemplate.convertAndSend("item." + type, id);
           } catch (Exception  e){
               e.printStackTrace();
           }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            sku.setSpuId(spuBo.getId());
            sku.setEnable(true);
            sku.setCreateTime(spuBo.getCreateTime());
            sku.setLastUpdateTime(spuBo.getCreateTime());
        skuDao.insertSelective(sku);
        Stock stock = new Stock();
        stock.setSkuId(sku.getId());
        stock.setStock(sku.getStock());
        stockDao.insertSelective(stock);
        });
    }

    /**
     * 通过spuid查询sku
     * @param spuId
     * @return
     */
    @Override
    public List<Sku> findSkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuDao.select(sku);
        skus.forEach(sku1 -> {
            Stock stock = this.stockDao.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());
        });
        return skus;
    }

    @Override
    @Transactional
    public void UpDateGoods(SpuBo spuBo) {
        //查询要删除的sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuDao.select(sku);
        skus.forEach(sku1 -> {
            //删除库存
            this.stockDao.deleteByPrimaryKey(sku1.getId());
        });
        //删除sku
        this.skuDao.delete(sku);


        //新增sku
        //新增库存
        saveSkuAndStock(spuBo);

        //更新spuDetail
        this.spuDetailDao.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //更新spu
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        this.spuDao.updateByPrimaryKeySelective(spuBo);

        sendMessage("update", spuBo.getId());
    }

    @Override
    public Sku findSkuBySkuId(Long skuid) {
        return this.skuDao.selectByPrimaryKey(skuid);
    }

}
