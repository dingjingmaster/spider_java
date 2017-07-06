package Test;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;


/**	
 * 该接口 包含一个抓取网站的所有代码  分为三部分: 爬虫配置、页面元素抽取、链接发现
 *
 * @author DingJing-PC
 * 1、	爬虫配置
 * 		1. 流程化
 * 			1. Spider是爬虫启动的入口
 * 			2. 在启动爬虫之前，我们需要使用一个PageProcessor创建一个Spider对象
 * 			3. 使用run()进行启动
 * 			4. 同时Spider的其他组件（Downloader、Scheduler、Pipeline）都可以通过set方法来进行设置。
 * 		2. API
 * 			create(PageProcessor)		创建Spider									Spider.create(new GithubRepoProcessor())
 * 			addUrl(String…)				添加初始的URL									spider .addUrl("http://webmagic.io/docs/")
 * 			addRequest(Request...)		添加初始的Request								spider .addRequest("http://webmagic.io/docs/")
 * 			thread(n)					开启n个线程										spider.thread(5)
 * 			run()						启动，会阻塞当前线程执行								spider.run()
 * 			start()/runAsync()			异步启动，当前线程继续执行							spider.start()
 * 			stop()						停止爬虫										spider.stop()
 *			test(String)				抓取一个页面进行测试								spider .test("http://webmagic.io/docs/")	
 *			addPipeline(Pipeline)		添加一个Pipeline，一个Spider可以有多个Pipeline		spider .addPipeline(new ConsolePipeline())
 *			setScheduler(Scheduler)		设置Scheduler，一个Spider只能有个一个Scheduler		spider.setScheduler(new RedisScheduler())
 *			setDownloader(Downloader)	设置Downloader，一个Spider只能有个一个Downloader	spider .setDownloader(new SeleniumDownloader())
 *			get(String)					同步调用，并直接取得结果								ResultItems result = spider .get("http://webmagic.io/docs/")
 *			getAll(String…)				同步调用，并直接取得一堆结果							List<ResultItems> results = spider .getAll("http://webmagic.io/docs/", "http://webmagic.io/xxx")
 * 		
 * 		3. Site
 * 			对站点本身的一些配置信息，例如编码、HTTP头、超时时间、重试策略等、代理等，都可以通过设置Site对象来进行配置。
 * 			setCharset(String)				设置编码								site.setCharset("utf-8")
 * 			setUserAgent(String)			设置UserAgent							site.setUserAgent("Spider")
 * 			setTimeOut(int)					设置超时时间，单位是毫秒						site.setTimeOut(3000)
 * 			setRetryTimes(int)				设置重试次数								site.setRetryTimes(3)
 * 			setCycleRetryTimes(int)			设置循环重试次数							site.setCycleRetryTimes(3)
 * 			addCookie(String,String)		添加一条cookie							site.addCookie("dotcomt_user","code4craft")	
 * 			setDomain(String)				设置域名，需设置域名后，addCookie才可生效		site.setDomain("github.com")
 * 			addHeader(String,String)		添加一条addHeader						site.addHeader("Referer","https://github.com")
 * 			setHttpProxy(HttpHost)			设置Http代理							site.setHttpProxy(new HttpHost("127.0.0.1",8080))
 * 
 * 2、	页面元素抽取
 * 		1. XPath
 * 			xml 中获取元素的一种查询语言，但是用于Html也是比较方便的。
 * 			page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()")
 * 				查找所有class属性为'entry-title public'的h1元素，并找到他的strong子节点的a子节点，并提取a节点的文本信息
 *
 * 		2. 正则表达式
 * 			page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
 * 				这段代码就用到了正则表达式，它表示匹配所有"https://github.com/code4craft/webmagic"这样的链接。
 * 
 * 		3. CSS选择器
 * 		4. JSON格式可以用 JsonPath解析
 * 			JsonPath是于XPath很类似的一个语言，它用于从Json中快速定位一条内容。WebMagic中使用的JsonPath格式可以参考这里：https://code.google.com/p/json-path/
 * 
 * 			1. Jsoup 和 Xsoup
 * 				Jsoup 一个简单的HTML解析器，同时它支持使用CSS选择器的方式查找元素。
 * 			2. Xsoup 语法
 * 				nodename				nodename
 * 				immediate parent		/
 * 				parent					//
 * 				attribute				[@key=value]
 * 				nth child				tag[n]
 * 				attribute				/@key
 * 				wildcard in tagname		/*
 * 				wildcard in attribute	/[@*]
 * 				function				function()
 * 				or						a | b
 * 				text(n)					第n个直接文本子节点，为0表示所有
 * 				allText()				所有的直接和间接文本子节点
 * 				tidyText()				所有的直接和间接文本子节点，并将一些标签替换为换行，使纯文本显示更整洁
 * 				html()					内部html，不包括标签的html本身
 * 				outerHtml()				内部html，包括标签的html本身
 * 				regex(@attr,expr,group)	这里@attr和group均可选，默认是group0
 * 
 * 3、	链接发现
 * 		page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
 * 		page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all()用于获取所有满足"(https:/ /github\.com/\w+/\w+)"这个正则表达式的链接，
 * 		page.addTargetRequests()则将这些链接加入到待抓取的队列中去。
 * 
 * 4、 	使用Selectable抽取元素
 * 		Selectable相关的抽取元素链式API是WebMagic的一个核心功能。
 * 		使用Selectable接口，你可以直接完成页面元素的链式抽取，也无需去关心抽取的细节。
 * 		page.getHtml()返回的是一个Html对象，它实现了Selectable接口。这个接口包含一些重要的方法，我将它分为两类：抽取部分 和 获取结果部分。
 * 		1. 	抽取部分 API
 * 			xpath(String xpath)							使用XPath选择 				html.xpath("//div[@class='title']")
 * 			$(String selector)							使用Css选择器选择			html.$("div.title")
 * 			$(String selector,String attr)				使用Css选择器选择			html.$("div.title","text")
 * 			css(String selector)						功能同$()，使用Css选择器选择	html.css("div.title")
 * 			links()										选择所有链接					html.links()
 * 			regex(String regex)							使用正则表达式抽取				html.regex("\(.\*?)\")
 * 			regex(String regex,int group)				使用正则表达式抽取，并指定捕获组	html.regex("\(.\*?)\",1)
 * 			replace(String regex, String replacement)	替换内容					html.replace("\","")
 * 		这部分抽取API返回的都是一个Selectable接口，意思是说，抽取是支持链式调用的
 * 		
 * 		2.	获取 API
 * 			get()						返回一条String类型的结果					String link= html.links().get()
 *			toString()					功能同get()，返回一条String类型的结果			String link= html.links().toString()
 *			all()						返回所有抽取结果							List links= html.links().all()
 *			match()						是否有匹配结果							if (html.links().match()){ xxx; }
 * 5、	使用 Pipline 保存结果
 * 		1.	在输出到控制台这一过程也是通过 ConsolePipline 完成的
 * 		2. 	把结果用 json 保存下来	JsonFilePipeline
 * 				Spider.create(new GithubRepoPageProcessor())
 *	            //从"https://github.com/code4craft"开始抓
 *	            .addUrl("https://github.com/code4craft")
 *	            .addPipeline(new JsonFilePipeline("D:\\webmagic\\"))
 *	            //开启5个线程抓取
 *	            .thread(5)
 *	            //启动爬虫
 *	            .run();
 * 6、	爬虫的监控
 * 		利用这个功能，你可以查看爬虫的执行情况——已经下载了多少页面、还有多少页面、启动了多少线程等信息。该功能通过JMX实现，你可以使用Jconsole等JMX工具查看本地或者远程的爬虫信息。
 * 			
 *       Spider oschinaSpider = Spider.create(new OschinaBlogPageProcessor())
 *               .addUrl("http://my.oschina.net/flashsword/blog");
 *       Spider githubSpider = Spider.create(new GithubRepoPageProcessor())
 *               .addUrl("https://github.com/code4craft");
 *
 *       SpiderMonitor.instance().register(oschinaSpider);
 *       SpiderMonitor.instance().register(githubSpider);
 *       oschinaSpider.start();
 *       githubSpider.start();
 *       
 * 7、 	配置代理
 * 		WebMagic开始使用了新的代理APIProxyProvider。因为相对于Site的“配置”，ProxyProvider定位更多是一个“组件”，所以代理不再从Site设置，而是由HttpClientDownloader设置。
 * 		1。 API
 * 			HttpClientDownloader.setProxyProvider(ProxyProvider proxyProvider)			设置代理
 * 		2. ProxyProvider有一个默认实现：SimpleProxyProvider。它是一个基于简单Round-Robin的、没有失败检查的ProxyProvider。可以配置任意个候选代理，每次会按顺序挑选一个代理使用。它适合用在自己搭建的比较稳定的代理的场景。
 * 		3.
 * 			HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
 *   		httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("101.101.101.101",8888,"username","password")));
 *   		spider.setDownloader(httpClientDownloader);
 *   	4. 代理池
 *   		HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
 *   		httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(
 *   		new Proxy("101.101.101.101",8888)
 *   		,new Proxy("102.102.102.102",8888)));
 * 8、 	处理费 HTTP GET 请求
 * 		1. demo
 * 			Request request = new Request("http://xxx/path");
 *			request.setMethod(HttpConstant.Method.POST);
 *			request.setRequestBody(HttpRequestBody.json("{'id':1}","utf-8"));
 *		2. API
 *			HttpRequestBody.form(Map\ params, String encoding)								使用表单提交的方式			
 *			HttpRequestBody.json(String json, String encoding)								使用JSON的方式，json是序列化后的结果
 *			HttpRequestBody.xml(String xml, String encoding)								设置xml的方式，xml是序列化后的结果
 *			HttpRequestBody.custom(byte[] body, String contentType, String encoding)		设置自定义的requestBody
 * 		
 *
 */
public class Demo implements PageProcessor {

	//	1、 抓取网站相关相关配置， 编码、抓取间隔、重试次数
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    //	定制爬虫逻辑的和新街口， 编写抽取逻辑
    public void process(Page page) {
        //	2、 定义如何抽取网页信息，并保存下来
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
        
        //	3、 从页面发现后续的 url 地址来抓取
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	//	创建、 从。。。开始抓取、 开启线程数、怕重启动
        Spider.create(new Demo()).addUrl("https://github.com/code4craft").thread(5).run();
    }
}