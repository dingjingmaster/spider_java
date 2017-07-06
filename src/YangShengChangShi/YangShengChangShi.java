package YangShengChangShi;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class YangShengChangShi implements PageProcessor {
	
	//	1、 配置
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100).setCharset("utf-8").setTimeOut(3000);

    @Override
    public void process(Page page) {
        //	2、 链接发现
    	page.addTargetRequests(page.getHtml().links().regex("http://www.cnys.com/\\w+/\\d+\\.html").all());
    	
    	//page.putField("title", page.getHtml().xpath("//div[@class='readbox']/h1"));
    	//page.putField("passage", page.getHtml().xpath("//div[@class='reads']/p"));
    	
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
    	//	创建、 从。。。开始抓取、 开启线程数、怕重启动
        Spider.create(new YangShengChangShi())
        .addUrl("http://www.cnys.com/")
        .addPipeline(new FilePipeline("E:/test/spider/"))
        .thread(5)
        .run();
    }
}
