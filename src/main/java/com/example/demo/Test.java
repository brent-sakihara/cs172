package com.example.demo;
import java.io.*;
import java.nio.file.Paths;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
//import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import java.lang.Object;
import org.apache.lucene.util.Version;

class Doc {
    public String date;
    public String author;
    public String handle;
    public String text;
    public String lang;
    public String loc;
    public String url;
    public String urltitle;
    public String numlikes;
    public String numretweets;

    Doc(String date_param, String author_param, String handle_param, String text_param,
        String lang_param, String loc_param, String url_param, String urltitle_param, String numlikes_param,
        String numretweets_param) {
        this.date = date_param;
        this.author = author_param;
        this.handle = handle_param;
        this.text = text_param;
        this.lang = lang_param;
        this.loc = loc_param;
        this.url = url_param;
        this.urltitle = urltitle_param;
        this.numlikes = numlikes_param;
        this.numretweets = numretweets_param;
    }
}

public class Test {

    public static final String INDEX_DIR = "test";

    public static void main(String[] args) throws CorruptIndexException, IOException {
        if (args.length == 0) {

            // READ FROM FILES
            // reader = null;
            int count = 0;
            int file_num = 1;

            try {
                File file = new File("file" + file_num + ".csv");
                BufferedReader reader = new BufferedReader(new FileReader(file));;
                while (file.exists()) {

                    System.out.println("Reading from file '" + file + "'...");
                    reader = new BufferedReader(new FileReader(file));

                    // Read every line in the file, and parse each tweet.
                    for (String line; (line = reader.readLine()) != null;) {
                        count++; // Count number of tweets
                        System.out.println("Tweets = " + count);
                        Scanner s = new Scanner(line).useDelimiter(",");
                        String date = s.next();
                        String author = s.next();
                        String handle = s.next();
                        String text = s.next();
                        String lang = s.next();
                        String loc = s.next();
                        String url = s.next();
                        String urltitle = s.next();
                        String numlikes = s.next();
                        String numretweets = s.next();
                        // String title = "";
                        // if (s.hasNext()) {
                        // title = s.next();
                        // }

                        // Declare tweet, and index it in Lucene
                        Doc tweet1 = new Doc(date, author, handle, text, lang, loc, url, urltitle, numlikes,
                                numretweets);
                        index(tweet1);
                    }

                    reader.close();
                    System.out.println("Current number of tweets = " + count);
                    file_num++;
                    file = new File("file" + file_num + ".csv");

                }
                reader.close();
                System.out.println("Total number of tweets = " + count);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void index(Doc tweet) {
        File index = new File("test");
        IndexWriter writer = null;

        try {
//            IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_34,
//                    new StandardAnalyzer(Version.LUCENE_35));
//            writer = new IndexWriter(FSDirectory.open(index), indexConfig);
            IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer());
            writer = new IndexWriter(FSDirectory.open(Paths.get(String.valueOf(index))), indexConfig); //questionable
            Document luceneDoc = new Document();
            IndexableFieldType noindex = new IndexableFieldType() {
                @Override
                public boolean stored() {
                    return true;
                }

                @Override
                public boolean tokenized() {
                    return false;
                }

                @Override
                public boolean storeTermVectors() {
                    return false;
                }

                @Override
                public boolean storeTermVectorOffsets() {
                    return false;
                }

                @Override
                public boolean storeTermVectorPositions() {
                    return false;
                }

                @Override
                public boolean storeTermVectorPayloads() {
                    return false;
                }

                @Override
                public boolean omitNorms() {
                    return false;
                }

                @Override
                public IndexOptions indexOptions() {
                    return IndexOptions.NONE;
//                    return null;
                }

                @Override
                public DocValuesType docValuesType() {
                    return null;
                }

                @Override
                public int pointDimensionCount() {
                    return 0;
                }

                @Override
                public int pointIndexDimensionCount() {
                    return 0;
                }

                @Override
                public int pointNumBytes() {
                    return 0;
                }

                @Override
                public int vectorDimension() {
                    return 0;
                }

                @Override
                public VectorSimilarityFunction vectorSimilarityFunction() {
                    return null;
                }

                @Override
                public Map<String, String> getAttributes() {
                    return null;
                }
            };

            IndexableFieldType analyze = new IndexableFieldType() {
                @Override
                public boolean stored() {
                    return true;
                }

                @Override
                public boolean tokenized() {
                    return true;
                }

                @Override
                public boolean storeTermVectors() {
                    return false;
                }

                @Override
                public boolean storeTermVectorOffsets() {
                    return false;
                }

                @Override
                public boolean storeTermVectorPositions() {
                    return false;
                }

                @Override
                public boolean storeTermVectorPayloads() {
                    return false;
                }

                @Override
                public boolean omitNorms() {
                    return false;
                }

                @Override
                public IndexOptions indexOptions() {
                    return IndexOptions.DOCS_AND_FREQS;
//                    return null;
                }

                @Override
                public DocValuesType docValuesType() {
                    return null;
                }

                @Override
                public int pointDimensionCount() {
                    return 0;
                }

                @Override
                public int pointIndexDimensionCount() {
                    return 0;
                }

                @Override
                public int pointNumBytes() {
                    return 0;
                }

                @Override
                public int vectorDimension() {
                    return 0;
                }

                @Override
                public VectorSimilarityFunction vectorSimilarityFunction() {
                    return null;
                }

                @Override
                public Map<String, String> getAttributes() {
                    return null;
                }
            };
//            luceneDoc.add(new Field("date", tweet.date, Field.Store.YES, Field.Index.NO));
            luceneDoc.add(new Field("date", tweet.date, noindex));
            luceneDoc.add(new Field("author", tweet.author, analyze));
            luceneDoc.add(new Field("handle", tweet.handle, noindex));
            luceneDoc.add(new Field("text", tweet.text, analyze));
            luceneDoc.add(new Field("lang", tweet.lang, analyze));
            luceneDoc.add(new Field("loc", tweet.loc, analyze));
            luceneDoc.add(new Field("url", tweet.url, analyze));
            luceneDoc.add(new Field("urltitle", tweet.urltitle, analyze));
            luceneDoc.add(new Field("numlikes", tweet.numlikes, noindex));
            luceneDoc.add(new Field("numretweets", tweet.numretweets, noindex));
            // luceneDoc.setBoost((float)1.0);
            writer.addDocument(luceneDoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (CorruptIndexException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public void close() throws IOException {
//        writer.close();
//    }

    public static String[] search(String q) throws IOException, ParseException { //String indexDir, param
//        IndexReader indexReader = IndexReader.open(FSDirectory.open(new File("test")));
//        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File("test")));
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File("test").toPath())); //?
        IndexSearcher is = new IndexSearcher(indexReader);

        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(q);
        try {
            StringTokenizer strtok = new StringTokenizer(q,
                    " ~`!@#$%^&*()_-+={[}]|:;'<>,./?\"\'\\/\n\t\b\f\r");
            String x = "";

            while (strtok.hasMoreElements()) {
                String token = strtok.nextToken();
                // querytoparse += "text:" + token + "^1" + "hashtags:" + token+ "^1.5" +
                // "ptitle:" + token+"^2.0";
                x += "text: " + token;
            }
            Query querys = parser.parse(x);
            TopDocs hits = is.search(querys, 10);
            int num_results = hits.scoreDocs.length;
            System.out.println(num_results);
            String[] returnTweets = new String[num_results];
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = is.doc(scoreDoc.doc);
                System.out.print(doc.get("text")); // idk if this shiz fuccin works
                // System.out.println(doc.get("fullpath"));
            }
            indexReader.close();
            return returnTweets;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    IndexWriter getIndexWriter(String dir) {
//        Directory indexDir = FSDirectory.open(new File(dir));
//        IndexWriterConfig luceneConfig = new IndexWriterConfig(
//                luceneVersion, new StandardAnalyzer());
//        return (new IndexWriter(indexDir, luceneConfig));
//    }
//
//    protected Document getDocument(File f) throws Exception {
//        Document doc = new Document();
//        doc.add(new TextField("contents", new FileReader(f)));
//        doc.add(new StringField("filename", f.getName()));
//        doc.add(new StringField("fullpath", f.getCanonicalPath()));
//        return doc;
//    }
//
//    private IndexWriter writer;
//
//    private void indexFile(File f) throws Exception {
//        Document doc = getDocument(f);
//        writer.addDocument(doc);
//    }
}
