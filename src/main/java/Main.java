import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Main {
    public final static String BASEURL = "https://machinelearningmastery.com";

    public static void main(String[] args) throws IOException, ParseException, InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);
        LuceneInitiator luceneInitiator = new LuceneInitiator();
        BasicWebCrawler crawler = new BasicWebCrawler(BASEURL, 3, luceneInitiator);
        AppExecutors appExecutors = AppExecutors.getInstance();
        appExecutors.crawlerThreadSubmit(crawler);
        appExecutors.shutdownThreads();
        while (!appExecutors.isIndexThreadTerminated() && !appExecutors.isCrawlerThreadTerminated()) {
        }
        System.out.println("Crawling and Indexing Ended.");
        System.out.println("Write Your Query.");
        String inputQuery = scanner.nextLine();
        search(luceneInitiator, inputQuery);
        System.out.println("Do you want try again? (y/n)");
        String s = scanner.nextLine();
        while (s.equals("y")) {
            System.out.println("Write Your Query.");
            inputQuery = scanner.nextLine();
            search(luceneInitiator, inputQuery);
            System.out.println("Do you want try again? (y/n)");
            s = scanner.nextLine();
        }

    }


    public static void search(LuceneInitiator luceneInitiator, String inputQuery) throws IOException, ParseException {
        Query qtitle = luceneInitiator.getQueryParser(IndexingTier.TITLE).parse(inputQuery);
        Query qbody = luceneInitiator.getQueryParser(IndexingTier.BODY).parse(inputQuery);
        LinkedHashSet<ScoreDoc> result = new LinkedHashSet<>(Arrays.asList(luceneInitiator.getIndexSearcher().search(qtitle, 10).scoreDocs));
        result.addAll(Arrays.asList(luceneInitiator.getIndexSearcher().search(qbody, 10).scoreDocs));
        System.out.println("Query is : " + qtitle + " " + qbody);
        result.stream().map(scoreDoc -> scoreDoc.doc).map(docID -> {
            try {
                return luceneInitiator.getIndexSearcher().doc(docID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).forEach(fields -> System.out.println(fields.getField(IndexingTier.URL.toString()).stringValue()));
    }


}