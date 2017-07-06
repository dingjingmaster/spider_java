package YangShengChangShi;

import java.net.URL;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class YangShengChangShi implements PageProcessor {
	
	//	1�� ����
    private Site site = Site.me()
    		.setRetryTimes(3)
    		.setSleepTime(100)
    		.setCharset("utf-8")
    		.setTimeOut(3000);

	@Override
    public void process(Page page) {
    	
    	int 			pageNum = 0;
    	String 			nowUrl = null;
    	String 			nextPageStr = null;
		String 			nextPageNum = "0";
    	
        //	2�� ���ӷ���
    	List<String> allUrls = page.getHtml().links().regex("http://www.cnys.com/\\w+/\\d+\\.html").all();
    	
    	for(String url : allUrls) {
    		page.addTargetRequest(url);
    		
    		//	��ǰ����
    		nowUrl = page.getUrl().toString();
    		nowUrl = nowUrl.substring(0, nowUrl.lastIndexOf("."));
    		
    		//	��ȡʣ��ҳ����
	    	nextPageStr = page.getHtml().xpath("//div[@class='page']/span/tidyText()").toString();

	    	//	�ж��Ƿ�����һҳ 
	    	if (nextPageStr != null) {
	    		
	    		nextPageStr = nextPageStr.trim();

	    		//	��ȡ������
	    		nextPageStr = nextPageStr.trim();
	    		for (int i = 0; i < nextPageStr.length(); ++i) {
	    			
	    			if (nextPageStr.charAt(i) >= 48 && nextPageStr.charAt(i) <= 57)
	    			{
	    				nextPageNum += nextPageStr.charAt(i);
	    			}
	    		}
	    		
	    		//	���ҳ��
				pageNum = Integer.parseInt(nextPageNum);
				nextPageNum = "0";
	    	}

			//	�õ�ʣ�༸ҳ������
	    	for (int i = 2; i <= pageNum; ++i) {

	    		//	��һҳ
				nextPageStr = nowUrl + "_" + String.valueOf(i) + "." + "html";
				page.addTargetRequest(nextPageStr);
				System.out.println(nextPageStr);
			}
    	}
    	//page.addTargetRequests(page.getHtml().links().regex("http://www.cnys.com/\\w+/\\d+\\.html").all());
    	//page.addTargetRequests(page.getHtml().links().regex("http://www.cnys.com/\\w+/\\d+\\w+\\.html").all());
    	
    	page.putField("title", page.getHtml().xpath("//div[@class='readbox']/h1/tidyText()"));
    	page.putField("passage", page.getHtml().xpath("//div[@class='reads']/tidyText()"));
    }

    @Override
    public Site getSite() {
        return site;
    }
	
    //	ConsolePipeline()
    //	JsonFilePipeline("E:/test/spider/")
    public static void main(String[] args) {
    	//	������ �ӡ�������ʼץȡ�� �����߳�������������
        Spider.create(new YangShengChangShi())
        .addUrl("http://www.cnys.com/")
        .addPipeline(new ConsolePipeline())
        .thread(5)
        .run();
    }
}
