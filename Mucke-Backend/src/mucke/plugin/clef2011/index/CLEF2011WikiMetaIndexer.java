package mucke.plugin.clef2011.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.w3c.dom.NodeList;

import com.memetix.mst.language.Language;

import mucke.config.ConfigurationManager;
import mucke.plugin.clef2011.ConfigConstants;

import mucke.plugin.clef2011.util.clir.BingLanguageTranslator;
import mucke.util.Logger;
import mucke.util.xml.XMLTools;

/**
 * Indexes the Wikipedia metadata of the ImageCLEF 2011 collection.
 * 
 * @author Ralf Bierig
 */
public class CLEF2011WikiMetaIndexer {

    	private ConfigurationManager configManager = null;
    	private static int countTranslate = 0;
    	
	
    	/** Constructor */
	public CLEF2011WikiMetaIndexer(ConfigurationManager configManager) {
	    this.configManager = configManager; 
	}

	
	/** 
	 * Indexes all files stored in the given directory 
	 * 
	 * @param String filepath to the directory to be indexed recursively
	 */
	public void index(String filepath){
	    index(filepath, null);
	}
	
	
	/** Indexes all files stored in the given directory 
	 * 
	 * @param String filepath to the directory to be indexed recursively
	 * @param Language language to which all fields of the index are translated, 
	 * null leaves the fields in their original language and behaves like 
	 * <code>index(String filepath)</code>.
	 */
	public void index(String filepath, Language translateTo) {

	    	// logger
	    	Logger logger = new Logger(new String[]{
	    		"docID", 
	    		"descriptionEn", "commentEn", "captionEn", 
	    		"descriptionDe", "commentDe", "captionDe",
	    		"descriptionFr", "commentFr", "captionFr",
	    		"comment"
	    		});
	    
	    
		// true creates a new index / false updates the existing index
		boolean create = true;

		// check if data directory exists
		final File docDir = new File(filepath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out
					.println("Document directory '"
							+ docDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		// to calculate indexing time as a performance measure
		Date start = new Date();

		try {
			System.out.println("Indexing to directory '"
					+ System.getProperty(ConfigConstants.WIKI_META_INDEXDIR) + "'...");

			Directory dir = FSDirectory.open(new File(
				System.getProperty(ConfigConstants.WIKI_META_INDEXDIR)));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_42,
					analyzer);

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
			indexDocs(writer, docDir, translateTo, logger);
			writer.close();

			// time stamping
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	/**
	 * Recursively indexes files and directories of ImageCLEF 2011 Wikipedia
	 * meta information. <strong>Does not index files that are not readable!</strong>
	 * 
	 * @param writer The index writer
	 * @param file The file to index. Causes recursive call if it is a directory.
	 * @param translateTo The language that all text fields of the file should be translated to, null 
	 * leaves the fields in their original language 
	 * @throws IOException
	 */
	private void indexDocs(IndexWriter writer, File file, Language translateTo, Logger logger) throws IOException {
		
	    	// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]), translateTo, logger);
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
				    	
				    	// reset
				    	logger.resetColumns();	

					// make a new, empty document
					Document doc = new Document();

					// metadata path as an indexed, non-tokenized and stored field 
					Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
					
					doc.add(pathField);
					
					// metadata filename as an indexed, non-tokenized and stored field 
					Field filenameField = new StringField("filename", file.getName(), Field.Store.YES);
					// log
					logger.setColumn("docID", filenameField.stringValue());
					doc.add(filenameField);
					
					// image Id as an indexed, non-tokenized and stored field 
					NodeList imageIDNodes = XMLTools.getNodes(new File(file.getPath()), "/image/@id");
					Field imageIDField = new StringField("imageID", imageIDNodes.item(0).getTextContent(), Field.Store.YES);
					doc.add(imageIDField);

					// image path as an indexed, non-tokenized and stored field 
					NodeList imagepathNodes = XMLTools.getNodes(new File(file.getPath()), "/image/@file");
					Field imagepathField = new StringField("imagepath", imagepathNodes.item(0).getTextContent(), Field.Store.YES);
					doc.add(imagepathField);
					
					// English document ID (if it exists) as an indexed, non-tokenized and stored field 
					String documentIdEn = "";
					NodeList documentIdEnNodes = XMLTools.getNodes(new File(file.getPath()), "/image/text[@lang='en']/caption/@article");
					if (documentIdEnNodes != null && documentIdEnNodes.getLength() > 0) {
						// select the last part of the path
						documentIdEn = documentIdEnNodes.item(0).getTextContent().substring(
								documentIdEnNodes.item(0).getTextContent().lastIndexOf("/") + 1);
					}
					
					// German document ID (if it exists) as an indexed, non-tokenized and stored field 
					String documentIdDe = "";
					NodeList documentIdDeNodes = XMLTools.getNodes(new File(file.getPath()), "/image/text[@lang='de']/caption/@article");
					if (documentIdDeNodes != null && documentIdDeNodes.getLength() > 0) {
						// select the last part of the path
						documentIdDe = documentIdDeNodes.item(0).getTextContent().substring(
								documentIdDeNodes.item(0).getTextContent().lastIndexOf("/") + 1);
					}
					
					// French document ID (if it exists) as an indexed, non-tokenized and stored field 
					String documentIdFr = "";
					NodeList documentIdFrNodes = XMLTools.getNodes(new File(file.getPath()), "/image/text[@lang='fr']/caption/@article");
					if (documentIdFrNodes != null && documentIdFrNodes.getLength() > 0) {
						// select the last part of the path
						documentIdFr = documentIdFrNodes.item(0).getTextContent().substring(
								documentIdFrNodes.item(0).getTextContent().lastIndexOf("/") + 1);
					}
					
					// English description, comment and caption as an indexed, non tokenized and stored field 
					String descriptionEn = extractTokenString(new File(file.getPath()), "/image/text[@lang='en']/description");
					String commentEn = extractTokenString(new File(file.getPath()), "/image/text[@lang='en']/comment");
					String captionEn = extractTokenString(new File(file.getPath()), "/image/text[@lang='en']/caption");
					
					// German description, comment and caption as an indexed, non tokenized and stored field 
					String descriptionDe =  extractTokenString(new File(file.getPath()), "/image/text[@lang='de']/description");
					String commentDe = extractTokenString(new File(file.getPath()), "/image/text[@lang='de']/comment");
					String captionDe = extractTokenString(new File(file.getPath()), "/image/text[@lang='de']/caption");
					
					// French description, comment and caption as an indexed, non tokenized and stored field 
					String descriptionFr = extractTokenString(new File(file.getPath()), "/image/text[@lang='fr']/description");
					String commentFr = extractTokenString(new File(file.getPath()), "/image/text[@lang='fr']/comment");
					String captionFr = extractTokenString(new File(file.getPath()), "/image/text[@lang='fr']/caption");
						
					
					// tranlation strategy
					if (translateTo == Language.ENGLISH){
					    // Prefer French over German for English translations
					    
					    String tdescriptionEn = translateString(translateTo, descriptionEn, new String[]{descriptionFr, descriptionDe});
					    // logging
					    if (tdescriptionEn.equals(descriptionEn)){
						logger.setColumn("descriptionEn", "0");
					    } else {
						logger.setColumn("descriptionEn", "1");
					    }
					    descriptionEn = tdescriptionEn;
					    
					    String tcommentEn = translateString(translateTo, commentEn, new String[]{commentFr, commentDe});
					    // logging
					    if (tcommentEn.equals(commentEn)){
						logger.setColumn("commentEn", "0");
					    } else {
						logger.setColumn("commentEn", "1");
					    }
					    commentEn = tcommentEn;
					    
					    
					    String tcaptionEn = translateString(translateTo, captionEn, new String[]{captionFr, captionDe});
					    // logging
					    if (tcaptionEn.equals(captionEn)){
						logger.setColumn("captionEn", "0");
					    } else {
						logger.setColumn("captionEn", "1");
					    }
					    captionEn = tcaptionEn;
					    
					} else if (translateTo == Language.FRENCH){
					    // Prefer French over German for English translations
					    descriptionFr = translateString(translateTo, descriptionFr, new String[]{descriptionEn, descriptionDe});
					    commentFr = translateString(translateTo, commentFr, new String[]{commentEn, commentDe});
					    captionFr = translateString(translateTo, captionFr, new String[]{captionEn, captionDe});
					} else if (translateTo == Language.GERMAN){
					    // Prefer French over German for English translations
					    descriptionDe = translateString(translateTo, descriptionDe, new String[]{descriptionEn, descriptionFr});
					    commentDe = translateString(translateTo, commentDe, new String[]{commentEn, commentFr});
					    captionDe = translateString(translateTo, captionDe, new String[]{captionEn, captionFr});
					} else {
					    System.err.println("Translation Service for this index only works for ENGLISH, FRENCH and GERMAN. Nothing tranlated.");
					}
					
					
					//
					// Write language specific index fields
					// 
					
					// English
					Field descriptionEnField = new TextField("descriptionEn", descriptionEn, Field.Store.YES);
					doc.add(descriptionEnField);
					Field commentEnField = new TextField("commentEn", commentEn, Field.Store.YES);
					doc.add(commentEnField);
					Field documentIdEnField = new StringField("documentIdEn", documentIdEn, Field.Store.YES);
					doc.add(documentIdEnField);
					Field captionEnField = new TextField("captionEn", captionEn, Field.Store.YES);
					doc.add(captionEnField);
					
					// German
					Field descriptionDeField = new TextField("descriptionDe", descriptionDe, Field.Store.YES);
					doc.add(descriptionDeField);
					Field commentDeField = new TextField("commentDe", commentDe, Field.Store.YES);
					doc.add(commentDeField);
					Field documentIdDeField = new StringField("documentIdDe", documentIdDe, Field.Store.YES);
					doc.add(documentIdDeField);
					Field captionDeField = new TextField("captionDe", captionDe, Field.Store.YES);
					doc.add(captionDeField);
					
					// French
					Field descriptionFrField = new TextField("descriptionFr", descriptionFr, Field.Store.YES);
					doc.add(descriptionFrField);
					Field commentFrField = new TextField("commentFr", commentFr, Field.Store.YES);
					doc.add(commentFrField);
					Field documentIdFrField = new StringField("documentIdFr", documentIdFr, Field.Store.YES);
					doc.add(documentIdFrField);
					Field captionFrField = new TextField("captionFr", captionFr, Field.Store.YES);
					doc.add(captionFrField);
										
					
					//
					// general comment field, indexed and stored
					//
					NodeList commentNodes = XMLTools.getNodes(new File(file.getPath()), "/image/comment");
					String comment ="";
					if (commentNodes != null && commentNodes.getLength() > 0) {
					    comment = commentNodes.item(0).getTextContent();
					    // logging -- never translate
					    logger.setColumn("comment", "0");
					}
					Field commentField = new TextField("comment", comment, Field.Store.YES);
					doc.add(commentField);
					
					
					// The last modified date as indexed (i.e. efficiently filterable with NumericRangeFilter) field. 
					// Indexes to milli-second resolution, which is often too fine. You could instead create a number 
					// based on year/month/day/hour/minutes/seconds, down the resolution you require. For example 
					// the long value 2011021714 would mean February 17, 2011, 2-3 PM.
					doc.add(new LongField("modified", file.lastModified(),
							Field.Store.NO));

					// contents of the entire file. Specifies a Reader, so that the text of the file is
					// tokenized and indexed, but not stored. Note that FileReader expects the 
					// file to be in UTF-8 encoding. If that's not the case searching for special 
					// characters will fail.
					doc.add(new TextField("contents", new BufferedReader(
							new InputStreamReader(fis, "UTF-8"))));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
					    // New index, so we just add the document
					    //System.out.println("adding " + file);
					    writer.addDocument(doc);
					} else {
					    // Existing index so we use updateDocument instead of replacing
					    //System.out.println("updating " + file);
					    writer.updateDocument(new Term("path", file.getPath()), doc);
					}
					
					// log entry writing
					logger.log();

				} finally {
					fis.close();
					
				}
			}
			
		} // if
		
		System.out.println("Total translations: " + countTranslate);
		
		logger.log();
		logger.flush();
		
	} // indexDoc
	
	
	
	/** Simple extraction of text token for the given xpath 
	 * @param file from which token needs to be extracted 
	 * @param xPath String xPath for required text token
	 * @return String text 
	 */
	public String extractTokenString(File file, String xPath){
	    
	    String result = "";
	    
	    NodeList captionFrNodes = XMLTools.getNodes(new File(file.getPath()), xPath);
	    if (captionFrNodes != null && captionFrNodes.getLength() > 0) {
		result = captionFrNodes.item(0).getTextContent().trim();
	    }
	    return result;
	}
	
	
	
	/** 
	 * Translates text conditionally.
	 * 
	 *  @param destinationLanguage The language to which should be translated (e.g. Language.ENGLISH).
	 *  @param toText Text to which should be translated. This might be empty but also may contain for text
	 *  already, in which case no translation will be performed and the text will be returned directly.
	 *  @param fromTexts An array of Strings with source texts. The array is prioritized in order of 
	 *  translation quality. This means that the first item in the String array is the most fit text 
	 *  for the translation. Only and only if this text is an empty string, the method tries to 
	 *  translate the second entry of the array. If this one is also empty, it continues with the third
	 *  entry, and so on until the end of the array is reached or a translation has been successful 
	 *  before. If all texts were empty Strings, no translation is performed and the method returns
	 *  and empty String. 
	 *  @return the translated String or an empty String if no translation was possible
	 * */
	public String translateString(Language destinationLanguage, String toText, String[] fromTexts){
	    
	    // check if translation already exists
	    if (toText.length() > 0){
		return toText;
	    }
	    
	    for (String fromText : fromTexts){
		if (fromText.length() > 0){
		    countTranslate++;
		    return BingLanguageTranslator.translate(fromText, null, destinationLanguage);
		}
	    }
	    
	    // no result is an empty String
	    return ""; 
	}
	
}