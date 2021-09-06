import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Locale;

public class TextExtractor implements Runnable {
    Document document;
    LuceneInitiator luceneInitiator;
    String url;

    public TextExtractor(String url, Document document, LuceneInitiator initiator) {
        this.document = document;
        this.luceneInitiator = initiator;
        this.url = url;
    }

    private static org.apache.lucene.document.Document generateDoc(String url, String title, String body) {
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
        doc.add(new TextField(IndexingTier.URL.toString(), url, Field.Store.YES));
        doc.add(new TextField(IndexingTier.TITLE.toString(), title.toLowerCase().trim(), Field.Store.YES));
        doc.add(new TextField(IndexingTier.BODY.toString(), body.toLowerCase().trim(), Field.Store.YES));
        return doc;
    }

    @Override
    public void run() {

        String title = document.select("h1").text() +
                document.select("h2").text() +
                document.select("h3").text() +
                document.select("h4").text() +
                document.select("h5").text() +
                document.select("h6").text();
        org.apache.lucene.document.Document luceneDoc = generateDoc(this.url, title, document.select("p").text());
        try {
            this.luceneInitiator.getIndexWriter().addDocument(luceneDoc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Thread::" + Thread.currentThread().getName() + " IndexingPage::" + luceneDoc.getField(IndexingTier.URL.toString()).stringValue() + " Indexed.");
    }
}
