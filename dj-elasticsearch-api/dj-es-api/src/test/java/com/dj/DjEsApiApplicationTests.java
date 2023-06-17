package com.dj;

import com.alibaba.fastjson.JSON;
import com.dj.pojo.User;
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
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DjEsApiApplicationTests {

    /*
    * 两种方法，如果名字不一样就会有红色的下划线RestHighLevelClient client
    * 这时候需要指定名字就可以消除下划线，而且Ctrl+左键可以跳过去
    * */
//    @Autowired
//    @Qualifier("restHighLevelClient")
//    private RestHighLevelClient client;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 测试索引的创建
    @Test
    void createIndexTest() throws IOException {
        // 1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("dj_index");
        // 2.客户端执行创建请求 IndicesClient，请求后获得响应
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    // 测试获取索引，只能判断其是否存在
    @Test
    void existIndexTest() throws IOException {
        GetIndexRequest request = new GetIndexRequest("dj_index");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 测试索引的删除
    @Test
    void deleteIndexTest() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("dj_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    //添加测试文档
    @Test
    void addDocumentTest() throws IOException {
        // 创建对象
        User user = new User("dj", 18);
        // 创建请求
        IndexRequest request = new IndexRequest("dj_index");

        // 规则 put /dj_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));  //设置从超时时间
        request.timeout("1s");

        // 将我们的数据放入请求 json
        request.source(JSON.toJSONString(user), XContentType.JSON);

        // 客户端发送请求
        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());
    }

    // 判断文档是否存在 get /dt_index/_doc/1
    @Test
    void existsDocumentTest() throws IOException {
        GetRequest getRequest = new GetRequest("dj_index", "1");
        // 不获取返回的 _source 的上下文了
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");  //不需要排序

        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 获取文档的信息
    @Test
    void getDocumentTest() throws IOException {
        GetRequest getRequest = new GetRequest("dj_index", "1");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        // 打印文档的内容
        System.out.println(getResponse.getSourceAsString());
        // 返回的全部内容和命令一样
        System.out.println(getResponse);
    }

    // 更新文档的信息
    @Test
    void updateDocumentTest() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("dj_index", "1");
        updateRequest.timeout("1s");

        User user = new User("狂神说Java", 18);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);

        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse.status());
    }

    // 删除文档的信息
    @Test
    void deleteDocumentTest() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("dj_index", "1");
        deleteRequest.timeout("1s");

        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }

    // 批量插入数据
    @Test
    void bulkRequestTest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("dj1", 18));
        userList.add(new User("dj2", 18));
        userList.add(new User("dj3", 18));
        userList.add(new User("KyDestroy1", 18));
        userList.add(new User("KyDestroy2", 18));
        userList.add(new User("KyDestroy3", 18));

        // 批处理请求
        for (int i = 0; i < userList.size(); i++) {
            // 批量更新和批量删除，就在这里修改对应的请求就可以了
            bulkRequest.add(
                    new IndexRequest("dj_index")
                            .id("" + i+1)
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON)
            );
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            // 是否失败,返回false代表成功
            System.out.println(bulkResponse.hasFailures());
        }
    }

    // 查询
// SearchRequest 搜索请求
// SearchSourceBuilder 条件构造
// HighLightBuilder 构建高亮
// TermQueryBuilder  精确查询
// MatchAllQueryBuilder 匹配所有
// xxx QueryBuilder 对应我们刚才看到的命令！
    @Test
    void searchRequestTest() throws IOException {
        SearchRequest searchRequest = new SearchRequest("dj_index");
        // 构建搜索的条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 查询条件，我们可以使用 QueryBuilders 工具来实现
        // QueryBuilders.termQuery  精确
        //QueryBuilders.matchAllQuery 匹配所有

        // 中文查询，查不出来这个样子QueryBuilders.termQuery("xxxx.keyword", "xxxx");
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "dj1");
        // MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        System.out.println("================");
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }

    }

}
