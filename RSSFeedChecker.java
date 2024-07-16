package ca.nl.cna.java2.concurrency.rssfeedexample;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//This class implements the Runnable
public class RSSFeedChecker implements Runnable {
    //private String feedUrl;
    //RSSFeedManager instance used to retrieve feed URLs.
    private final RSSFeedManager feedManager;
    private volatile boolean running = true;//controls the loop in the run()

    public RSSFeedChecker(RSSFeedManager feedManager) {
        //this.feedUrl = feedUrl;
        this.feedManager = feedManager;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            //checkFeed();
            try{
                String feedUrl = feedManager.getFeed();//Gets a feed URL from a feedManager
                checkFeed(feedUrl);//Checks the feed at the given URL.
                Thread.sleep(60000);//Pauses the thread for 60 seconds
            }catch(InterruptedException e) {
                Thread.currentThread().interrupt();//re-interrupts the thread to signal that it was interrupted
            }
        }
    }
    //gracefully terminate the thread.
    public void stop() {
        running = false;
    }

    public void checkFeed(String feedUrl) {
        try {
            URL url = new URL(feedUrl);
            System.out.println("URL created");

            //XML Document building
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            System.out.println("DocumentBuilder created");

            Document doc = builder.parse(url.openStream());
            System.out.println("Document parsed");

            //This is how you work with XML - you do not need to modify this!
            NodeList itemList = doc.getElementsByTagName("item");
            List<RSSItem> items = new ArrayList<>();

            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    String title = itemElement.getElementsByTagName("title").item(0).getTextContent();
                    String link = itemElement.getElementsByTagName("link").item(0).getTextContent();
                    String pubDate = itemElement.getElementsByTagName("pubDate").item(0).getTextContent();
                    items.add(new RSSItem(title, link, pubDate));
                }
            }

            System.out.println("Feed: " + feedUrl);
            for (int i = 0; i < Math.min(3, items.size()); i++) {
                RSSItem item = items.get(i);
                System.out.println("Title: " + item.getTitle());
                System.out.println("Link: " + item.getLink());
                System.out.println("Published Date: " + item.getPubDate());
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class RSSItem {
        private String title;
        private String link;
        private String pubDate;

        public RSSItem(String title, String link, String pubDate) {
            this.title = title;
            this.link = link;
            this.pubDate = pubDate;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getPubDate() {
            return pubDate;
        }
    }
}
