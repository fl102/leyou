package com.leyou.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.client.*;
import com.leyou.common.PageResult;
import com.leyou.domain.*;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;
import com.leyou.repository.GoodsRepository;
import com.leyou.service.SearchService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecClient specClient;
    @Autowired
    private SpuClient spuClient;

    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public Goods buildGoods(Spu spu) throws Exception {
        //构建goods对象
        Goods goods = new Goods();
        //获取品牌
        Brand brand = brandClient.findBrandById(spu.getBrandId());
        //获取商品分类
        List<String> names = categoryClient.findCategoriesByCids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询skus
        List<Sku> skus = goodsClient.findSkusBySpuId(spu.getId());
        //声明价格集合
        List<Long> prices = new ArrayList<>();
        //因为不需要所有的sku字段  所以我们采用map集合保存需要的字段
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            Map<String, Object> skuMap = new HashMap<>();
          prices.add(sku.getPrice());
          skuMap.put("id", sku.getId());
          skuMap.put("title", sku.getTitle());
          skuMap.put("price", sku.getPrice());
          skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0]: "");
          skuMapList.add(skuMap);
        });
        SpuDetail spuDetail = spuClient.findSpuDetailBySpuId(spu.getId());
        //获取规格参数
        //获取特殊规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {});
        //获取通用规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {});
        //查询所有的规格参数
        List<SpecParam> params = specClient.findParams(null, 76L, null, true);
        Map<String,Object> specMap = new HashMap<>();
        //查询spu详情
        params.forEach(specParam ->{
            //判断是否是通用参数
               if (specParam.getGeneric()){
                   //获取通用规格参数值
                   String value = genericSpecMap.get(specParam.getId()).toString();
                   //判断是否是数值类型
                   if (specParam.getNumeric()){
                       // 如果是数值的话，判断该数值落在那个区间
                       value = chooseSegment(value, specParam);
                   }
                   specMap.put(specParam.getName(), value);
               } else {
                   specMap.put(specParam.getName(), specialSpecMap.get(specParam.getId()));
               }
        });

        //设置参数
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        //查询条件 包含标题 品牌 商品分类
        goods.setAll(spu.getTitle() + " " + brand.getName() + " " + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(specMap);
        return goods;
    }

    @Override
    public SearchResult searchResult(SearchRequest searchRequest) {
        if (StringUtils.isBlank(searchRequest.getKey())){
            return null;
        }
        //构建查询条件
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //抽取基本查询条件
        //QueryBuilder queryBuilder = QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND);
        //添加过滤
        QueryBuilder queryBuilder = buildBoolBuilder(searchRequest);
        //全文检索
        builder.withQuery(queryBuilder);
        //设置返回结果过滤字段
        builder.withSourceFilter(new FetchSourceFilter(new String[]{"subTitle","id","skus"}, null));
        //设置分页
        builder.withPageable(PageRequest.of(searchRequest.getPage() - 1, searchRequest.getSize()));
        //根据商品分类聚合和品牌聚合
        String categoryAgg = "categories";
        String brandAgg = "brands";
        builder.addAggregation(AggregationBuilders.terms(categoryAgg).field("cid3"));
        builder.addAggregation(AggregationBuilders.terms(brandAgg).field("brandId"));
        //查询
        //解析查询结果
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) this.goodsRepository.search(builder.build());
        List<Map<String, Object>> categories = getCategories(search.getAggregation(categoryAgg));
        List<Brand> brands = getBrands(search.getAggregation(brandAgg));

        //规格参数的聚合
        List<Map<String, Object>> specs = null;
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            specs = getSpecs(queryBuilder, (Long)categories.get(0).get("id"));
        }
        //封装结果返回
        return new SearchResult(search.getTotalElements(), search.getTotalPages(), search.getContent(), categories, brands, specs);
    }

    @Override
    public void createIndex(Long id) throws Exception {
        Spu spu = this.spuClient.findSpuById(id);
        this.goodsRepository.save(buildGoods(spu));
    }

    @Override
    public void deleteById(Long id) {
        this.goodsRepository.deleteById(id);
    }

    private QueryBuilder buildBoolBuilder(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND));
        for (Map.Entry<String, Object> entry : searchRequest.getFilter().entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equals("分类", key)){
                key = "cid3";
            } else if (StringUtils.equals("品牌", key)){
                key = "brandId";
            } else {
                key = "specs."+ key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }
        return boolQueryBuilder;
    }

    private List<Map<String,Object>> getSpecs(QueryBuilder queryBuilder, Long id) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(queryBuilder);
        //根据cid查询规格参数
        List<SpecParam> params = this.specClient.findParams(null, id, null, true);
        params.forEach(param -> {
            builder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        builder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        //初始化结果集
        List<Map<String,Object>> specs = new ArrayList<>();
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) this.goodsRepository.search(builder.build());
        Map<String, Aggregation> aggregationMap = search.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
             Map<String, Object> map = new HashMap<>();
             map.put("k", entry.getKey());
            StringTerms terms = (StringTerms) entry.getValue();
            List<String> names = terms.getBuckets().stream().map(bucket -> {
                return bucket.getKeyAsString();
            }).collect(Collectors.toList());
            map.put("options", names);
            specs.add(map);
        }
        return specs;
    }


    private List<Map<String,Object>> getCategories(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        return terms.getBuckets().stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            List<String> names = this.categoryClient.findCategoriesByCids(Arrays.asList(bucket.getKeyAsNumber().longValue()));
            map.put("id", bucket.getKeyAsNumber().longValue());
            map.put("name",names.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    private List<Brand> getBrands(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.findBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }

    private String chooseSegment(String value, SpecParam specParam) {
        Double val = NumberUtils.toDouble(value);
        String result = "其他";
        String segments = specParam.getSegments();
        String[] segment = segments.split(",");
        for (String seg : segment){
            String[] segs = seg.split("-");
            //获取取值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            if (val > begin && val < end){
                if (segs.length == 1){
                    result = segs[0] + specParam.getUnit() + "以上";
                } else if (begin == 0){
                    result = segs[1] + specParam.getUnit() + "以下";
                } else {
                    result = seg + specParam.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
