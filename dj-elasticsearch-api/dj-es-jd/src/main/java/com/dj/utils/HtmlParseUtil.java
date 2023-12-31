package com.dj.utils;

import com.dj.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Component
public class HtmlParseUtil {

    /*
    public static void main(String[] args) throws IOException {
        // 获得请求 https://search.jd.com/Search?keyword=java
        // 前提：需要联网，不能获取ajax数据
        String url = "https://search.jd.com/Search?keyword=java";
        //解析网页。（Jsoup返回Document就是浏览器Document对象）
        Document document = Jsoup.parse(new URL(url),30000);
        //所有你在js中可以使用的方法，这里都能用！
        Element element = document.getElementById("J_goodsList");
        //获取所有的Li元素
        Elements elements = element.getElementsByTag("li");
        //获取元素中内容
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            System.out.println("===================================================");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);
        }
    }
     */
    public List<Content> parseJD(String keywords) throws Exception {
        List<Content> goodList = new ArrayList<>();
        // 获得请求 https://search.jd.com/Search?keyword=java
        // 前提：需要联网，不能获取ajax数据
        String url = "https://search.jd.com/Search?keyword=" + keywords;

        // 解析网页 (jsoup返回Document就是浏览器的Document对象）
        Document document = Jsoup.parse(new URL(url), 30000);
        // 所有在js中可以使用的方法，这里都能用
        Element element = document.getElementById("J_goodsList");
        // 获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        // 获取元素中的内容，这里el，就是每一个li标签
        for (Element el : elements) {
            // 关于图片特别多的网站，所有的图片都是延迟加载的 source-data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setTitle(title)
                    .setImg(img)
                    .setPrice(price);
            goodList.add(content);
        }
        return goodList;
    }
}