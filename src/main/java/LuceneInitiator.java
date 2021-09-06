import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class LuceneInitiator {
    private final IndexWriter indexWriter;
    private final Analyzer analyzer;

    public LuceneInitiator() throws IOException {
        analyzer = new StandardAnalyzer();
        Path indexPath = new File("IndexDir").toPath();
        Directory directory = FSDirectory.open(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        indexWriter = new IndexWriter(directory, config);

    }


    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    public QueryParser getQueryParser(IndexingTier indexingTier) {
        return new QueryParser(indexingTier.toString(), analyzer);
    }

    public IndexSearcher getIndexSearcher() throws IOException {
        DirectoryReader ireader = DirectoryReader.open(indexWriter);
        return new IndexSearcher(ireader);
    }
}
