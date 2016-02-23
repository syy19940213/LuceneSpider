package com.zhy.spider.test;


import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zhy.spider.bean.LinkTypeData;
import com.zhy.spider.core.ExtractService;
import com.zhy.spider.rule.Rule;

public class Test
{
	
	public static void main(String args[]) throws InterruptedException {
		getDatasByClass();
		
	}
	@org.junit.Test
	public static void getDatasByClass()
	{
		Rule rule = new Rule("http://news.xinhuanet.com/politics/2016-02/20/c_1118106530.htm",  
	            new String[] { "class" }, new String[] { "body" },  
	            null, -1, Rule.GET);  
	    List<LinkTypeData> extracts = ExtractService.extract(rule);  
	    printf(extracts);
	    Document doc;
	    try {
			doc = Jsoup.connect("http://news.sina.com.cn/c/sz/2016-02-22/doc-ifxprucu3111608.shtml").get();
			Elements elements = doc.getElementsByAttributeValue("id", "artibody");
			for(Element a:elements){
				System.out.println(Html2Text(a.toString()).trim());
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	   
	}
	
	
	 public static String Html2Text(String inputString) {
	        String htmlStr = inputString; // 含html标签的字符串
	        String textStr = "";
	        java.util.regex.Pattern p_script;
	        java.util.regex.Matcher m_script;
	        java.util.regex.Pattern p_style;
	        java.util.regex.Matcher m_style;
	        java.util.regex.Pattern p_html;
	        java.util.regex.Matcher m_html;
	 
	        try {
	            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
	                                                                                                        // }
	            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
	                                                                                                    // }
	            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	 
	            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
	            m_script = p_script.matcher(htmlStr);
	            htmlStr = m_script.replaceAll(""); // 过滤script标签
	 
	            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
	            m_style = p_style.matcher(htmlStr);
	            htmlStr = m_style.replaceAll(""); // 过滤style标签
	 
	            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
	            m_html = p_html.matcher(htmlStr);
	            htmlStr = m_html.replaceAll(""); // 过滤html标签
	 
	            textStr = htmlStr;
	 
	        } catch (Exception e) {
	            System.err.println("Html2Text: " + e.getMessage());
	        }
	 
	        return textStr;// 返回文本字符串
	    
	 
	}
	

//	@org.junit.Test
//	public void getDatasByCssQuery()
//	{
//		Rule rule = new Rule("http://www.11315.com/search",
//				new String[] { "name" }, new String[] { "兴网" },
//				"div.g-mn div.con-model", Rule.SELECTION, Rule.GET);
//		List<LinkTypeData> extracts = ExtractService.extract(rule);
//		printf(extracts);
//	}

	public static void printf(List<LinkTypeData> datas)
	{
		for (LinkTypeData data : datas)
		{
			System.out.println(data.getLinkText());
			System.out.println(data.getLinkHref());
			System.out.println(data.getContent());
			System.out.println(data.getSummary());
			System.out.println("***********************************");
		}

	}
}
