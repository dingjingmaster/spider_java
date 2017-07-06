package YangShengChangShi;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class YangShengChangShi implements PageProcessor {
	
	//	1、 配置
    private Site site = Site.me()
    		.setRetryTimes(0)
    		.setSleepTime(100)
    		.setCharset("utf-8")
    		.setTimeOut(3000);

	@Override
    public void process(Page page) {
    	
    	int 				pageNum = 0;
    	String 				nowUrl = null;
    	String 				nextPageStr = null;
		String 				nextPageNum = "0";
		HashSet<String>		filterLink = null;
		List<String> 		allUrls = null;
		LinkedList<String> 	subAllUrls = null;
		Object 				titleInfo;
		Object 				pageInfo;
    	
		subAllUrls = new LinkedList<String>();
				
        //	2、 链接发现
    	allUrls = page.getHtml().links().regex("http://www.cnys.com/\\w+/\\d+\\.html").all();

    	//	链接去重
    	filterLink = new HashSet<String>(allUrls);
    	allUrls.clear();
    	allUrls.addAll(filterLink);
    	
    	for(Iterator<String> it = allUrls.iterator(); it.hasNext();) {
    		
    		String url = it.next();
    		page.addTargetRequest(url);
    		
    		//	当前链接
    		nowUrl = page.getUrl().toString();
    		nowUrl = nowUrl.substring(0, nowUrl.lastIndexOf("."));
    		
    		//	获取剩余页面数
	    	nextPageStr = page.getHtml().xpath("//div[@class='page']/span/tidyText()").toString();
	    	
	    	if(nextPageStr == null) {
	    		
	    		continue;
	    	}

	    	//	判断是否有下一页 
	    	if (nextPageStr != null) {
	    		
	    		nextPageStr = nextPageStr.trim();

	    		//	提取出数字
	    		nextPageStr = nextPageStr.trim();
	    		for (int i = 0; i < nextPageStr.length(); ++i) {
	    			
	    			if (nextPageStr.charAt(i) >= 48 && nextPageStr.charAt(i) <= 57)
	    			{
	    				nextPageNum += nextPageStr.charAt(i);
	    			}
	    		}
	    		
	    		//	获得页数
				pageNum = Integer.parseInt(nextPageNum);

				//	得到剩余几页的数据
		    	for (int i = 2; i <= pageNum; ++i) {
		    		
		    		//	下一页
					nextPageStr = nowUrl + "_" + String.valueOf(i) + "." + "html";
					
					subAllUrls.add(nextPageStr);
				}
	    	}
	    
	    	pageNum = 0;
	    	nextPageStr = null;
			nextPageNum = "0";
    	}
    	
    	//	链接去重
    	filterLink = new HashSet<String>(subAllUrls);
    	subAllUrls.clear();
    	subAllUrls.addAll(filterLink);

    	page.addTargetRequests(subAllUrls);
    	
    	titleInfo = page.getHtml().xpath("//div[@class='readbox']/h1/tidyText()");
    	pageInfo = page.getHtml().xpath("//div[@class='reads']/tidyText()");
    	
    	
    	if (titleInfo.toString() == null) {
    		
    		page.setSkip(true);
    	}
    	
    	page.putField("title", titleInfo);
    	page.putField("passage", pageInfo);
    }

    @Override
    public Site getSite() {
        return site;
    }
	
    //	ConsolePipeline()
    //	JsonFilePipeline("E:/test/spider/")
    public static void main(String[] args) {
    	//	创建、 从。。。开始抓取、 开启线程数、怕重启动
        Spider.create(new YangShengChangShi())
        .addUrl("http://www.cnys.com/")
        .addPipeline(new ConsolePipeline())
        .thread(1)
        .run();
    }
}
