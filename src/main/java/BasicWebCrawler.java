import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.Callable;

public class BasicWebCrawler implements Callable<HashSet<String>> {

    private final HashSet<String> links;
    private final String initialUrl;
    private final int maxDepth;
    private final LuceneInitiator luceneInitiator;
    private final AppExecutors appExecutors;

    public BasicWebCrawler(String initialUrl, int maxDepth, LuceneInitiator luceneInitiator) {
        links = new HashSet<>();
        this.initialUrl = initialUrl;
        this.maxDepth = maxDepth;
        this.luceneInitiator = luceneInitiator;
        this.appExecutors = AppExecutors.getInstance();
    }

    private void getPageLinks(String URL, int depth) {
        //4. Check if you have already crawled the URLs

        if (!links.contains(URL) && depth < maxDepth) {
            try {
                //4. (i) If not add it to the index
                if (links.add(URL)) {
                    System.out.println("Thread::" + Thread.currentThread().getName() + " Depth::" + depth + " Visit :: " + URL + " :: " + links.size());
                }

                //2. Fetch the HTML code
                Document document = Jsoup.connect(URL).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();

                appExecutors.indexThreadExecute(new TextExtractor(URL, document, luceneInitiator));

                //3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");
                depth++;
                //5. For each extracted URL... go back to Step 4.
                int finalDepth = depth;
                linksOnPage.stream().filter(element -> !element.attr("href").contains("#")).filter(element -> element.attr("href").contains(Main.BASEURL)).forEach(element -> getPageLinks(element.attr("abs:href"), finalDepth));

            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }

    @Override
    public HashSet<String> call() throws Exception {
        this.getPageLinks(this.initialUrl, 0);
        return links;
    }
}