package com.leyou.test;

import com.leyou.LeyouSearchApplication;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.SpuClient;
import com.leyou.common.PageResult;
import com.leyou.domain.Brand;
import com.leyou.domain.bo.SpuBo;
import com.leyou.pojo.Goods;
import com.leyou.repository.GoodsRepository;
import com.leyou.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchApplication.class)
public class AllTest {


    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpuClient spuClient;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SearchService searchService;

   @Autowired
   private GoodsRepository goodsRepository;
    @Autowired
    @Test
    public void testQueryCategories() {
        List<String> names = this.categoryClient.findCategoriesByCids(Arrays.asList(1L, 2L, 3L));
        names.forEach(System.out::println);
    }

    @Test
    public void testQueryBrand() {
        Brand brand = this.brandClient.findBrandById(1528L);
        System.out.println(brand.getName());
    }


    @Test
    public void testInsertData(){
        //创建索引库
        this.elasticsearchTemplate.createIndex(Goods.class);
        //添加映射关系
        this.elasticsearchTemplate.putMapping(Goods.class);

        Integer page = 1;
        Integer pageSize = 100;
        do {
            PageResult<SpuBo> spuBos = spuClient.findSpuBoByPage(null, null, page, pageSize);
            if (!org.apache.commons.collections.CollectionUtils.isEmpty(spuBos.getItems())) {
                List<Goods> goodsList = spuBos.getItems().stream().map(spuBo -> {
                    try {
                        return searchService.buildGoods(spuBo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());

                this.goodsRepository.saveAll(goodsList);
                pageSize = spuBos.getItems().size();
                page++;

            }
        } while (pageSize == 100);
    }
}

