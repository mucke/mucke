package at.tuwien.mucke.plugin.clef2011.index;

import at.tuwien.mucke.config.ConfigurationManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.Date;

/**
 * Indexes the Wikipedia documents of the ImageCLEF 2011 collection.
 *
 * @author Ralf Bierig
 */
public class CLEF2011WikiDocIndexer {

    private ConfigurationManager configManager = null;

    /**
     * Constructor
     */
    public CLEF2011WikiDocIndexer(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Indexes all text files stored in the given directory
     *
     * @param String File path to content directory
     * @param String File path to index directory to be produced
     */
    public void index(String contentDirectory, String indexDirectory) {

        // true creates a new index / false updates the existing index
        boolean create = true;

        // check if data directory exists
        final File docDir = new File(contentDirectory);
        if (!docDir.exists() || !docDir.canRead()) {
            System.out.println("Document directory '" + docDir.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        // to calculate indexing time as a performance measure
        Date start = new Date();

        try {
            System.out.println("Indexing to directory '" + indexDirectory + "'...");

            Directory dir = FSDirectory.open(new File(indexDirectory));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_42, analyzer);

            if (create) {
                // Create a new index in the directory, removing any
                // previously indexed documents:
                iwc.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to an existing index:
                iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            // index
            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocs(writer, docDir);
            writer.close();

            // time stamping
            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    private void indexDocs(IndexWriter writer, File file) throws IOException {
        // do not try to index files that cannot be read
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                // an IO error could occur
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        indexDocs(writer, new File(file, files[i]));
                    }
                }
            } else {

                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    // at least on windows, some temporary files raise this
                    // exception with an "access denied" message
                    // checking if the file can be read doesn't help
                    return;
                }

                try {

                    // make a new, empty document
                    Document doc = new Document();

                    // path of the indexed file
                    Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
                    doc.add(pathField);

                    // filename of the indexed file
                    Field filenameField = new StringField("filename", file.getName(), Field.Store.YES);
                    doc.add(filenameField);

                    // last modified date of the file. Uses a LongField that is indexed (i.e. efficiently
                    // filterable with NumericRangeFilter) which uses a milli-second resolution.
                    doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));

                    // the entire contents of the indexed file. FileReader expects the file to be in UTF-8
                    // encoding. If that's not the case searching for special characters will fail.
                    doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

                    if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                        // New index, so we just add the document (no old document can be there):
                        System.out.println("adding " + file);
                        writer.addDocument(doc);
                    } else {
                        // Existing index (an old copy of this document may have  been indexed) so we use
                        // updateDocument instead to replace the old one matching the exact path, if present:
                        System.out.println("updating " + file);
                        writer.updateDocument(new Term("path", file.getPath()), doc);
                    }

                } finally {
                    fis.close();
                }
            }
        }
    }
}