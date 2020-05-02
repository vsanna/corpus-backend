package dev.ishikawa.corpus.service.crawler.urlfetch.bot;

import dev.ishikawa.corpus.domain.Medium;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging.TopicName;
import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/*
 * - 担当するmediaに対しアクセスし、
 * - urlを取得し、
 * - そのurlをpublishする
 * */
@Component
@Slf4j
public class TechcrunchUrlFetchBot implements UrlFetchBot {

    static final private Medium medium = Medium.TECH_CRUNCH;

    @Async
    public void run() {
        // TODO: ここがmain出ないことを確認
        log.info("getting urls for {}", medium.getName());

        getUrls().forEach(url -> {
            InMemoryMessaging.publish(
                TopicName.CRAWLER,
                CrawlerEvent.fetchedUrl(medium.getName(), url).toMessage());
        });
    }

    // TODO: separate
    private List<String> getUrls() {
        CrawlConfig config = new CrawlConfig();

        // Set the folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        config.setCrawlStorageFolder("/tmp/crawler4j/");

        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests).
        // Otherwise it may overload the target servers.
        config.setPolitenessDelay(1000);

        // You can set the maximum crawl depth here. The default value is -1 for unlimited depth.
        config.setMaxDepthOfCrawling(0);

        // You can set the maximum number of pages to crawl. The default value is -1 for unlimited number of pages.
        config.setMaxPagesToFetch(100);

        // Should binary data should also be crawled? example: the contents of pdf, or the metadata of images etc
        config.setIncludeBinaryContentInCrawling(false);

        // Do you need to set a proxy? If so, you can use:
        // config.setProxyHost("proxyserver.example.com");
        // config.setProxyPort(8080);

        // If your proxy also needs authentication:
        // config.setProxyUsername(username); config.getProxyPassword(password);

        // This config parameter can be used to set your crawl to be resumable
        // (meaning that you can resume the crawl from a previously
        // interrupted/crashed crawl). Note: if you enable resuming feature and
        // want to start a fresh crawl, you need to delete the contents of
        // rootFolder manually.
        config.setResumableCrawling(false);

        // Set this to true if you want crawling to stop whenever an unexpected error
        // occurs. You'll probably want this set to true when you first start testing
        // your crawler, and then set to false once you're ready to let the crawler run
        // for a long time.

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed(medium.getIndexPageUrl().toString());

        // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
        // speed depends on many other factors as well. You can experiment with this to figure out what number of
        // threads works best for you.
        int numberOfCrawlers = 3;

        // To demonstrate an example of how you can pass objects to crawlers, we use an AtomicInteger that crawlers
        // increment whenever they see a url which points to an image.
        AtomicInteger numSeenImages = new AtomicInteger();

        // The factory which creates instances of crawlers.
        List<String> urls = new CopyOnWriteArrayList<>();
        CrawlController.WebCrawlerFactory<Bot> factory = () -> new Bot(numSeenImages, urls);

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers);

        log.info("fetched url size: {}", urls.size());
        urls.forEach(url -> {
            log.info("fetched url: {}", url);
        });
        return urls;
    }


    static private class Bot extends WebCrawler {

        private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

        private final AtomicInteger numSeenImages;

        private List<String> urls;

        /**
         * Creates a new crawler instance.
         *
         * @param numSeenImages This is just an example to demonstrate how you can pass objects to
         *                      crawlers. In this example, we pass an AtomicInteger to all crawlers
         *                      and they increment it whenever they see a url which points to an
         *                      image.
         */
        public Bot(AtomicInteger numSeenImages, List<String> urls) {
            this.numSeenImages = numSeenImages;
            this.urls = urls;
        }

        /**
         * You should implement this function to specify whether the given url should be crawled or
         * not (based on your crawling logic).
         */
        @Override
        public boolean shouldVisit(Page referringPage, WebURL url) {
            String hrefWithoutQuery = url.getPath();
            String href = url.getURL().toLowerCase();
            // Ignore the url if it has an extension that matches our defined set of image extensions.
            if (IMAGE_EXTENSIONS.matcher(href).matches() || IMAGE_EXTENSIONS
                .matcher(hrefWithoutQuery).matches()) {
                numSeenImages.incrementAndGet();
                return false;
            }

            if (href.contains("/author/")) {
                numSeenImages.incrementAndGet();
                return false;
            }

            // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
            return href.startsWith(medium.getBaseUri().toString());
        }

        /**
         * This function is called when a page is fetched and ready to be processed by your
         * program.
         */
        @Override
        public void visit(Page page) {
            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String html = htmlParseData.getHtml();
                Document doc = Jsoup.parse(html);
                // 各記事のaタグを取得。jQueryのセレクターと同じ感じで記載
                Elements newsHeadlines = doc.select(".post-block__title__link");
                for (Element headline : newsHeadlines) {
                    urls.add(headline.absUrl("href"));
                }
            }
        }
    }

}
