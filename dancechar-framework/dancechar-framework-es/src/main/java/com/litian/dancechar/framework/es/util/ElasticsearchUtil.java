package com.litian.dancechar.framework.es.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.litian.dancechar.framework.common.base.PageWrapperDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * es?????????
 *
 * @author tojson
 * @date 2022/7/9 6:30
 */
@Slf4j
public class ElasticsearchUtil {
    @Resource
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    /**
     * ????????????????????????
     */
    public RestClient getLowLevelClient() {
        return restHighLevelClient.getLowLevelClient();
    }

    /**
     * ????????????
     * @param indexName ??????
     * @return ??????
     */
    public boolean createIndex(String indexName){
        try{
            if(isIndexExist(indexName)){
                log.error("Index is  exits!");
                return false;
            }
            //1.??????????????????
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            //2.?????????????????????
            org.elasticsearch.client.indices.CreateIndexResponse response = restHighLevelClient.indices()
                    .create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e){
            log.error(e.getMessage() ,e);
            return false;
        }
    }

    /**
     * ????????????????????????
     * @param indexName ??????
     * @return ??????????????????
     */
    public boolean isIndexExist(String indexName) {
        try{
            GetIndexRequest request = new GetIndexRequest(indexName);
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * ????????????
     * @param indexName ????????????
     * @return ??????????????????
     */
    public boolean deleteIndex(String indexName) {
        try{
            if(!isIndexExist(indexName)) {
                log.error("Index is not exits!");
                return false;
            }
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            return delete.isAcknowledged();
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * ????????????????????????id
     * @param object ??????????????????
     * @param index      ????????????????????????
     * @param id         ??????ID,???null???es????????????
     * @return ????????????????????????
     */
    public String addData(Object object, String index, String id) {
        try{
            //????????????
            IndexRequest request = new IndexRequest(index);
            //?????? put /test_index/_doc/1
            request.id(id);
            request.timeout(TimeValue.timeValueSeconds(1));
            //????????????????????? json
            IndexRequest source = request.source(JSON.toJSONString(object), XContentType.JSON);
            //?????????????????????
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            return response.getId();
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * ???????????? ??????id
     */
    public String addData(Object object, String index) {
        try{
            return addData(object, index, UUID.randomUUID().toString()
                    .replaceAll("-", "").toUpperCase());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * ??????ID????????????
     */
    public boolean deleteDataById(String index,String id) {
        try{
            DeleteRequest request = new DeleteRequest(index, id);
            DeleteResponse deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            return deleteResponse.status().getStatus() == 200;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * ??????ID ????????????
     */
    public boolean updateDataById(Object object, String index, String id) {
        try{
            UpdateRequest update = new UpdateRequest(index, id);
            update.timeout("1s");
            update.doc(JSON.toJSONString(object), XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(update, RequestOptions.DEFAULT);
            return updateResponse.status().getStatus() == 200;
        } catch (Exception e){
            log.error(e.getMessage() ,e);
            return false;
        }
    }

    /**
     * ??????ID????????????
     */
    public Map<String,Object> searchDataById(String index, String id) {
        try {
            GetRequest request = new GetRequest(index, id);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            return response.getSource();
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * ??????ID????????????
     */
    public Map<String,Object> searchDataById(String index, String id, String fields) {
        try {
            GetRequest request = new GetRequest(index, id);
            if (StringUtils.isNotEmpty(fields)){
                //???????????????????????????????????????????????????????????????????????????
                request.fetchSourceContext(new FetchSourceContext(true,fields.split(",")
                        , Strings.EMPTY_ARRAY));
            }
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            return response.getSource();
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * ??????ID????????????????????????
     */
    public  boolean existsById(String index,String id) {
        try{
            GetRequest request = new GetRequest(index, id);
            //??????????????????_source????????????
            request.fetchSourceContext(new FetchSourceContext(false));
            request.storedFields("_none_");
            return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * ????????????,true:?????? false: ??????
     */
    public boolean bulkPost(String index, List<?> objects, List<String> ids) {
        BulkRequest bulkRequest = new BulkRequest();
        BulkResponse response=null;
        //????????????????????????20???
        for(int i=0;i<objects.size();i++){
            IndexRequest request = new IndexRequest(index);
            request.id(ids.get(i));
            request.source(JSON.toJSONString(objects.get(i)), XContentType.JSON);
            bulkRequest.add(request);
        }
        try {
            response = restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return !response.hasFailures();
    }

    /**
     * ????????????,true:?????? false: ??????
     */
    public boolean bulkPost(String index, List<?> objects) {
        BulkRequest bulkRequest = new BulkRequest();
        BulkResponse response=null;
        //????????????????????????20???
        for (Object object: objects) {
            IndexRequest request = new IndexRequest(index);
            request.source(JSON.toJSONString(object), XContentType.JSON);
            bulkRequest.add(request);
        }
        try {
            response = restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return !response.hasFailures();
    }

    /**
     *  ??????es??????(?????????)
     * @param index      ??????
     * @param builder    ????????????
     *  SearchSourceBuilder sourceBuilder=new SearchSourceBuilder(); // ??????????????????
     *  sourceBuilder.query(QueryBuilders.matchAllQuery());
     * 	sourceBuilder.query(QueryBuilders.multiMatchQuery("java","name"));  // ????????????name???????????????Java???
     * 	sourceBuilder.query(QueryBuilders.termsQuery("10001","opContent")) // ????????????opContent???????????????10001???
     * @param fields     ???????????????
     * @param sortField  ????????????
     * @param convertResultClass  ???????????????????????????
     * @return  ?????????es??????
     */
    public <T> List<T> queryListDataByCondition(String index, SearchSourceBuilder builder,
                                                String fields, String sortField,
                                                Class<T> convertResultClass) {
        try {
            SearchRequest request = new SearchRequest(index);
            if (StringUtils.isNotEmpty(fields)){
                // ????????????????????????????????????????????????????????????????????????
                builder.fetchSource(
                        new FetchSourceContext(true,fields.split(","), Strings.EMPTY_ARRAY));
            }
            if (StringUtils.isNotEmpty(sortField)){
                // ???????????????????????????proposal_no???text?????????????????????keyword?????????????????????.keyword
                builder.sort(sortField+".keyword", SortOrder.ASC);
            }
            request.source(builder);
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == 200) {
                SearchHit[] hits = response.getHits().getHits();
                if(hits != null && hits.length > 0){
                    List<T> result = new ArrayList<>();
                    for(SearchHit hit : hits){
                        Map<String,Object> source = hit.getSourceAsMap();
                        result.add(BeanUtil.mapToBean(source, convertResultClass, true));
                    }
                    return result;
                }
            }
            return null;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * ??????es??????(??????????????????)
     * @param index          ????????????
     * @param query          ????????????
     * @param pageSize       ???????????????
     * @param pageNo         ?????????
     * @param fields         ???????????????????????????????????????????????????????????????
     * @param sortField      ????????????
     * @param convertResultClass  ???????????????????????????
     * @return es??????
     */
    public <T> PageWrapperDTO<T> queryListPageDataByCondition(String index, SearchSourceBuilder query, Integer pageSize,
        Integer pageNo, String fields, String sortField, SortOrder sortOrder, Class<T> convertResultClass) {
        try {
            SearchRequest request = new SearchRequest(index);
            SearchSourceBuilder builder = query;
            if (StringUtils.isNotEmpty(fields)){
                // ???????????????????????????????????????????????????????????????????????????
                builder.fetchSource(new FetchSourceContext(true,fields.split(","),
                        Strings.EMPTY_ARRAY));
            }
            int size = (pageSize == null ? 10 : pageSize);
            int from = (pageNo == null || pageNo == 0 ? 0 : (pageNo-1) * size);
            builder.from(from).size(size);
            if (StringUtils.isNotEmpty(sortField)){
                // ???????????????????????????proposal_no???text?????????????????????keyword?????????????????????.keyword
                builder.sort(sortField+".keyword", sortOrder);
            }
            request.source(builder);
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == 200) {
                SearchHits searchHits = response.getHits();
                SearchHit[] hits = searchHits.getHits();
                if(hits != null && hits.length > 0){
                    PageWrapperDTO<T> pageWrapperDTO = new PageWrapperDTO<>();
                    List<T> result = Lists.newArrayList();
                    for(SearchHit hit : hits){
                        Map<String,Object> source = hit.getSourceAsMap();
                        result.add(BeanUtil.mapToBean(source, convertResultClass, true));
                    }
                    pageWrapperDTO.setList(result);
                    pageWrapperDTO.setTotal(Convert.toInt(searchHits.getTotalHits().value));
                    return pageWrapperDTO;
                }
            }
            return null;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * ??????es??????(???????????????)
     * @param index          ????????????
     * @param query          ????????????
     * @param size           ??????????????????
     * @param fields         ???????????????????????????????????????????????????????????????
     * @param sortField      ????????????
     * @param highlightField ????????????
     * @param convertResultClass  ???????????????????????????
     * @return es??????
     */
    public <T> PageWrapperDTO<T> queryListPageDataByCondition(String index, SearchSourceBuilder query,
        Integer size, Integer from, String fields, String sortField, String highlightField, Class<T> convertResultClass) {
        try{
            SearchRequest request = new SearchRequest(index);
            SearchSourceBuilder builder = query;
            if (StringUtils.isNotEmpty(fields)){
                // ???????????????????????????????????????????????????????????????????????????
                builder.fetchSource(new FetchSourceContext(true,fields.split(","),Strings.EMPTY_ARRAY));
            }
            from = from <= 0 ? 0 : from*size;
            // ???????????????????????????????????????????????????from??????????????????0
            builder.from(from).size(size);
            if (StringUtils.isNotEmpty(sortField)){
                // ???????????????????????????proposal_no???text?????????????????????keyword?????????????????????.keyword
                builder.sort(sortField+".keyword", SortOrder.ASC);
            }
            // ??????
            HighlightBuilder highlight = new HighlightBuilder();
            highlight.field(highlightField);
            // ??????????????????
            highlight.requireFieldMatch(false);
            highlight.preTags("<span style='color:red'>");
            highlight.postTags("</span>");
            builder.highlighter(highlight);
            request.source(builder);
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            if (response.status().getStatus() == 200) {
                SearchHits searchHits = response.getHits();
                SearchHit[] hits = searchHits.getHits();
                if(hits != null && hits.length > 0){
                    PageWrapperDTO<T> pageWrapperDTO = new PageWrapperDTO<>();
                    List<T> result = Lists.newArrayList();
                    for(SearchHit hit : hits){
                        Map<String, HighlightField> high = hit.getHighlightFields();
                        HighlightField title = high.get(highlightField);
                        Map<String,Object> source = hit.getSourceAsMap();
                        // ??????????????????,????????????????????????????????????
                        if (title != null){
                            Text[] texts = title.fragments();
                            String nTitle = "";
                            for (Text text : texts) {
                                nTitle += text;
                            }
                            source.put(highlightField, nTitle);
                        }
                        result.add(BeanUtil.mapToBean(source, convertResultClass, true));
                    }
                    pageWrapperDTO.setList(result);
                    pageWrapperDTO.setTotal(Convert.toInt(searchHits.getTotalHits().value));
                    return pageWrapperDTO;
                }
            }
            return null;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
