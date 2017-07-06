package Test;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;


/**	
 * �ýӿ� ����һ��ץȡ��վ�����д���  ��Ϊ������: �������á�ҳ��Ԫ�س�ȡ�����ӷ���
 *
 * @author DingJing-PC
 * 1��	��������
 * 		1. ���̻�
 * 			1. Spider���������������
 * 			2. ����������֮ǰ��������Ҫʹ��һ��PageProcessor����һ��Spider����
 * 			3. ʹ��run()��������
 * 			4. ͬʱSpider�����������Downloader��Scheduler��Pipeline��������ͨ��set�������������á�
 * 		2. API
 * 			create(PageProcessor)		����Spider									Spider.create(new GithubRepoProcessor())
 * 			addUrl(String��)				��ӳ�ʼ��URL									spider .addUrl("http://webmagic.io/docs/")
 * 			addRequest(Request...)		��ӳ�ʼ��Request								spider .addRequest("http://webmagic.io/docs/")
 * 			thread(n)					����n���߳�										spider.thread(5)
 * 			run()						��������������ǰ�߳�ִ��								spider.run()
 * 			start()/runAsync()			�첽��������ǰ�̼߳���ִ��							spider.start()
 * 			stop()						ֹͣ����										spider.stop()
 *			test(String)				ץȡһ��ҳ����в���								spider .test("http://webmagic.io/docs/")	
 *			addPipeline(Pipeline)		���һ��Pipeline��һ��Spider�����ж��Pipeline		spider .addPipeline(new ConsolePipeline())
 *			setScheduler(Scheduler)		����Scheduler��һ��Spiderֻ���и�һ��Scheduler		spider.setScheduler(new RedisScheduler())
 *			setDownloader(Downloader)	����Downloader��һ��Spiderֻ���и�һ��Downloader	spider .setDownloader(new SeleniumDownloader())
 *			get(String)					ͬ�����ã���ֱ��ȡ�ý��								ResultItems result = spider .get("http://webmagic.io/docs/")
 *			getAll(String��)				ͬ�����ã���ֱ��ȡ��һ�ѽ��							List<ResultItems> results = spider .getAll("http://webmagic.io/docs/", "http://webmagic.io/xxx")
 * 		
 * 		3. Site
 * 			��վ�㱾���һЩ������Ϣ��������롢HTTPͷ����ʱʱ�䡢���Բ��Եȡ�����ȣ�������ͨ������Site�������������á�
 * 			setCharset(String)				���ñ���								site.setCharset("utf-8")
 * 			setUserAgent(String)			����UserAgent							site.setUserAgent("Spider")
 * 			setTimeOut(int)					���ó�ʱʱ�䣬��λ�Ǻ���						site.setTimeOut(3000)
 * 			setRetryTimes(int)				�������Դ���								site.setRetryTimes(3)
 * 			setCycleRetryTimes(int)			����ѭ�����Դ���							site.setCycleRetryTimes(3)
 * 			addCookie(String,String)		���һ��cookie							site.addCookie("dotcomt_user","code4craft")	
 * 			setDomain(String)				����������������������addCookie�ſ���Ч		site.setDomain("github.com")
 * 			addHeader(String,String)		���һ��addHeader						site.addHeader("Referer","https://github.com")
 * 			setHttpProxy(HttpHost)			����Http����							site.setHttpProxy(new HttpHost("127.0.0.1",8080))
 * 
 * 2��	ҳ��Ԫ�س�ȡ
 * 		1. XPath
 * 			xml �л�ȡԪ�ص�һ�ֲ�ѯ���ԣ���������HtmlҲ�ǱȽϷ���ġ�
 * 			page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()")
 * 				��������class����Ϊ'entry-title public'��h1Ԫ�أ����ҵ�����strong�ӽڵ��a�ӽڵ㣬����ȡa�ڵ���ı���Ϣ
 *
 * 		2. ������ʽ
 * 			page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
 * 				��δ�����õ���������ʽ������ʾƥ������"https://github.com/code4craft/webmagic"���������ӡ�
 * 
 * 		3. CSSѡ����
 * 		4. JSON��ʽ������ JsonPath����
 * 			JsonPath����XPath�����Ƶ�һ�����ԣ������ڴ�Json�п��ٶ�λһ�����ݡ�WebMagic��ʹ�õ�JsonPath��ʽ���Բο����https://code.google.com/p/json-path/
 * 
 * 			1. Jsoup �� Xsoup
 * 				Jsoup һ���򵥵�HTML��������ͬʱ��֧��ʹ��CSSѡ�����ķ�ʽ����Ԫ�ء�
 * 			2. Xsoup �﷨
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
 * 				text(n)					��n��ֱ���ı��ӽڵ㣬Ϊ0��ʾ����
 * 				allText()				���е�ֱ�Ӻͼ���ı��ӽڵ�
 * 				tidyText()				���е�ֱ�Ӻͼ���ı��ӽڵ㣬����һЩ��ǩ�滻Ϊ���У�ʹ���ı���ʾ������
 * 				html()					�ڲ�html����������ǩ��html����
 * 				outerHtml()				�ڲ�html��������ǩ��html����
 * 				regex(@attr,expr,group)	����@attr��group����ѡ��Ĭ����group0
 * 
 * 3��	���ӷ���
 * 		page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
 * 		page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all()���ڻ�ȡ��������"(https:/ /github\.com/\w+/\w+)"���������ʽ�����ӣ�
 * 		page.addTargetRequests()����Щ���Ӽ��뵽��ץȡ�Ķ�����ȥ��
 * 
 * 4�� 	ʹ��Selectable��ȡԪ��
 * 		Selectable��صĳ�ȡԪ����ʽAPI��WebMagic��һ�����Ĺ��ܡ�
 * 		ʹ��Selectable�ӿڣ������ֱ�����ҳ��Ԫ�ص���ʽ��ȡ��Ҳ����ȥ���ĳ�ȡ��ϸ�ڡ�
 * 		page.getHtml()���ص���һ��Html������ʵ����Selectable�ӿڡ�����ӿڰ���һЩ��Ҫ�ķ������ҽ�����Ϊ���ࣺ��ȡ���� �� ��ȡ������֡�
 * 		1. 	��ȡ���� API
 * 			xpath(String xpath)							ʹ��XPathѡ�� 				html.xpath("//div[@class='title']")
 * 			$(String selector)							ʹ��Cssѡ����ѡ��			html.$("div.title")
 * 			$(String selector,String attr)				ʹ��Cssѡ����ѡ��			html.$("div.title","text")
 * 			css(String selector)						����ͬ$()��ʹ��Cssѡ����ѡ��	html.css("div.title")
 * 			links()										ѡ����������					html.links()
 * 			regex(String regex)							ʹ��������ʽ��ȡ				html.regex("\(.\*?)\")
 * 			regex(String regex,int group)				ʹ��������ʽ��ȡ����ָ��������	html.regex("\(.\*?)\",1)
 * 			replace(String regex, String replacement)	�滻����					html.replace("\","")
 * 		�ⲿ�ֳ�ȡAPI���صĶ���һ��Selectable�ӿڣ���˼��˵����ȡ��֧����ʽ���õ�
 * 		
 * 		2.	��ȡ API
 * 			get()						����һ��String���͵Ľ��					String link= html.links().get()
 *			toString()					����ͬget()������һ��String���͵Ľ��			String link= html.links().toString()
 *			all()						�������г�ȡ���							List links= html.links().all()
 *			match()						�Ƿ���ƥ����							if (html.links().match()){ xxx; }
 * 5��	ʹ�� Pipline ������
 * 		1.	�����������̨��һ����Ҳ��ͨ�� ConsolePipline ��ɵ�
 * 		2. 	�ѽ���� json ��������	JsonFilePipeline
 * 				Spider.create(new GithubRepoPageProcessor())
 *	            //��"https://github.com/code4craft"��ʼץ
 *	            .addUrl("https://github.com/code4craft")
 *	            .addPipeline(new JsonFilePipeline("D:\\webmagic\\"))
 *	            //����5���߳�ץȡ
 *	            .thread(5)
 *	            //��������
 *	            .run();
 * 6��	����ļ��
 * 		����������ܣ�����Բ鿴�����ִ����������Ѿ������˶���ҳ�桢���ж���ҳ�桢�����˶����̵߳���Ϣ���ù���ͨ��JMXʵ�֣������ʹ��Jconsole��JMX���߲鿴���ػ���Զ�̵�������Ϣ��
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
 * 7�� 	���ô���
 * 		WebMagic��ʼʹ�����µĴ���APIProxyProvider����Ϊ�����Site�ġ����á���ProxyProvider��λ������һ��������������Դ����ٴ�Site���ã�������HttpClientDownloader���á�
 * 		1�� API
 * 			HttpClientDownloader.setProxyProvider(ProxyProvider proxyProvider)			���ô���
 * 		2. ProxyProvider��һ��Ĭ��ʵ�֣�SimpleProxyProvider������һ�����ڼ�Round-Robin�ġ�û��ʧ�ܼ���ProxyProvider�����������������ѡ����ÿ�λᰴ˳����ѡһ������ʹ�á����ʺ������Լ���ıȽ��ȶ��Ĵ���ĳ�����
 * 		3.
 * 			HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
 *   		httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("101.101.101.101",8888,"username","password")));
 *   		spider.setDownloader(httpClientDownloader);
 *   	4. �����
 *   		HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
 *   		httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(
 *   		new Proxy("101.101.101.101",8888)
 *   		,new Proxy("102.102.102.102",8888)));
 * 8�� 	����� HTTP GET ����
 * 		1. demo
 * 			Request request = new Request("http://xxx/path");
 *			request.setMethod(HttpConstant.Method.POST);
 *			request.setRequestBody(HttpRequestBody.json("{'id':1}","utf-8"));
 *		2. API
 *			HttpRequestBody.form(Map\ params, String encoding)								ʹ�ñ��ύ�ķ�ʽ			
 *			HttpRequestBody.json(String json, String encoding)								ʹ��JSON�ķ�ʽ��json�����л���Ľ��
 *			HttpRequestBody.xml(String xml, String encoding)								����xml�ķ�ʽ��xml�����л���Ľ��
 *			HttpRequestBody.custom(byte[] body, String contentType, String encoding)		�����Զ����requestBody
 * 		
 *
 */
public class Demo implements PageProcessor {

	//	1�� ץȡ��վ���������ã� ���롢ץȡ��������Դ���
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    //	���������߼��ĺ��½ֿڣ� ��д��ȡ�߼�
    public void process(Page page) {
        //	2�� ������γ�ȡ��ҳ��Ϣ������������
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
        
        //	3�� ��ҳ�淢�ֺ����� url ��ַ��ץȡ
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	//	������ �ӡ�������ʼץȡ�� �����߳�������������
        Spider.create(new Demo()).addUrl("https://github.com/code4craft").thread(5).run();
    }
}