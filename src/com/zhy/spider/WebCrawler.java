package com.zhy.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {  
    ArrayList<String> allurlSet = new ArrayList<String>();//所有的网页url，需要更高效的去重可以考虑HashSet  
    ArrayList<String> notCrawlurlSet = new ArrayList<String>();//未爬过的网页url  
    HashMap<String, Integer> depth = new HashMap<String, Integer>();//所有网页的url深度  
    int crawDepth  = 2; //爬虫深度  
    int threadCount = 10; //线程数量  
    int count = 0; //表示有多少个线程处于wait状态  
    public static final Object signal = new Object();   //线程间通信变量  
      
    public static void main(String[] args) {  
        final WebCrawler wc = new WebCrawler();  
//      wc.addUrl("http://www.126.com", 1);  
        wc.addUrl("http://news.sina.com.cn/", 1);  
        long start= System.currentTimeMillis();  
        System.out.println("开始爬虫.........................................");  
        wc.begin();  
          
        while(true){  
            if(wc.notCrawlurlSet.isEmpty()&& Thread.activeCount() == 1||wc.count==wc.threadCount){  
                long end = System.currentTimeMillis();  
                System.out.println("总共爬了"+wc.allurlSet.size()+"个网页");  
                System.out.println("总共耗时"+(end-start)/1000+"秒");  
                System.exit(1);  
//              break;  
            }  
              
        }  
    }  
    private void begin() {  
        for(int i=0;i<threadCount;i++){  
            new Thread(new Runnable(){  
                public void run() {  
//                  System.out.println("当前进入"+Thread.currentThread().getName());  
//                  while(!notCrawlurlSet.isEmpty()){ ----------------------------------（1）  
//                      String tmp = getAUrl();  
//                      crawler(tmp);  
//                  }  
                    while (true) {   
//                      System.out.println("当前进入"+Thread.currentThread().getName());  
                        String tmp = getAUrl();  
                        if(tmp!=null){  
                            crawler(tmp);  
                        }else{  
                            synchronized(signal) {  //------------------（2）  
                                try {  
                                    count++;  
                                    System.out.println("当前有"+count+"个线程在等待");  
                                    signal.wait();  
                                } catch (InterruptedException e) {  
                                    // TODO Auto-generated catch block  
                                    e.printStackTrace();  
                                }  
                            }  
                              
                              
                        }  
                    }  
                }  
            },"thread-"+i).start();  
        }  
    }  
    public synchronized  String getAUrl() {  
        if(notCrawlurlSet.isEmpty())  
            return null;  
        String tmpAUrl;  
//      synchronized(notCrawlurlSet){  
            tmpAUrl= notCrawlurlSet.get(0);  
            notCrawlurlSet.remove(0);  
//      }  
        return tmpAUrl;  
    }  
//  public synchronized  boolean isEmpty() {  
//      boolean f = notCrawlurlSet.isEmpty();  
//      return f;  
//  }  
      
    public synchronized void  addUrl(String url,int d){  
            notCrawlurlSet.add(url);  
            allurlSet.add(url);  
            depth.put(url, d);  
    }  
      
    //爬网页sUrl  
    public  void crawler(String sUrl){  
        URL url;  
        try {  
                url = new URL(sUrl);  
//              HttpURLConnection urlconnection = (HttpURLConnection)url.openConnection();   
                URLConnection urlconnection = url.openConnection();  
                urlconnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");  
                InputStream is = url.openStream();  
                BufferedReader bReader = new BufferedReader(new InputStreamReader(is));  
                StringBuffer sb = new StringBuffer();//sb为爬到的网页内容  
                String rLine = null;  
                while((rLine=bReader.readLine())!=null){  
                    sb.append(rLine);  
                    sb.append("/r/n");  
                }  
                
                
                Document doc;
        	    try {
        			doc = Jsoup.connect(sUrl.trim()).get();
        			Elements elements = doc.select("title");//.getElementsByAttributeValue("id", "artibody");
        			for(Element a:elements){
        				System.out.println(Html2Text(a.toString()).trim());
        			}
        			Elements elements1 = doc.getElementsByAttributeValue("id", "artibody");
        			for(Element a:elements1){
        				System.out.println(Html2Text(a.toString()).trim());
        			}
        		} catch (IOException e) {
        			// TODO 自动生成的 catch 块
        			e.printStackTrace();
        		}
                
                int d = depth.get(sUrl); 
                System.out.println("爬网页"+sUrl+"成功，深度为"+d+" 是由线程"+Thread.currentThread().getName()+"来爬");  
                
                
                
                
                if(d<crawDepth){  
                    //解析网页内容，从中提取链接  
                    parseContext(sb.toString(),d+1);  
                }  
//              System.out.println(sb.toString());  
  
              
        } catch (IOException e) {  
//          crawlurlSet.add(sUrl);  
//          notCrawlurlSet.remove(sUrl);  
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


  
    //从context提取url地址  
    public  void parseContext(String context,int dep) {  
        String regex = "<a href.*?/a>";  
//      String regex = "<title>.*?</title>";  
        String s = "fdfd<title>我 是</title><a href=\"http://www.iteye.com/blogs/tag/Google\">Google</a>fdfd<>";  
        // String regex ="http://.*?>";  
        Pattern pt = Pattern.compile(regex);  
        Matcher mt = pt.matcher(context);  
        while (mt.find()) {  
//          System.out.println(mt.group());  
            Matcher myurl = Pattern.compile("href=\".*?\"").matcher(  
                    mt.group());  
            while(myurl.find()){  
                String str = myurl.group().replaceAll("href=\"|\"", "");  
//              System.out.println("网址是:"+ str);  
                if(str.contains("http:")){ //取出一些不是url的地址  
                    if(!allurlSet.contains(str)){  
                        addUrl(str, dep);//加入一个新的url  
                        if(count>0){ //如果有等待的线程，则唤醒  
                            synchronized(signal) {  //---------------------（2）  
                                count--;  
                                signal.notify();  
                            }  
                        }  
                          
                    }  
                }  
            }  
        }  
    }  
}     