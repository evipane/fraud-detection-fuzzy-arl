package org.processmining.framework.models.recommendation.net;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.processmining.framework.models.recommendation.net.client.RestartRequest;
import org.processmining.lib.xml.Document;
import org.processmining.lib.xml.Tag;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class RecommendationRestartMarshal {
	protected static final Charset UTF8 = Charset.forName("UTF-8");
	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS");
	protected SAXParserFactory parserFactory = SAXParserFactory.newInstance();

	public String marshal(String contributor, String scale) throws Exception {
		StringWriter writer = new StringWriter();
		// start document
		Document doc = new Document(writer, RecommendationQueryMarshal.UTF8);
		Tag root = doc.addNode("RestartRequest");
		root.addAttribute("contributor", contributor);
		root.addAttribute("scale", scale);
		doc.close();
		writer.flush();
		// done!
		return writer.getBuffer().toString();
	}

	public RestartRequest unmarshal(String queryXml) throws Exception {
		SAXParser parser = parserFactory.newSAXParser();
		RecommendationRestartHandler handler = new RecommendationRestartHandler();
		parser.parse(new InputSource(new StringReader(queryXml)), handler);
		return handler.getQuery();
	}

	protected class RecommendationRestartHandler extends DefaultHandler {

		protected StringBuilder buffer = null;
		protected RestartRequest query = null;

		public RecommendationRestartHandler() {
			buffer = new StringBuilder();
		}

		public RestartRequest getQuery() {
			return query;
		}

		public void startDocument() throws SAXException {
			query = null;
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// create tag name
			String tagName = localName;
			if (tagName.equalsIgnoreCase("")) {
				tagName = qName;
			}
			if (tagName.equalsIgnoreCase("RestartRequest")) {
				// set filter state
				String contributor = attributes.getValue("contributor");
				String scale = attributes.getValue("scale");
				query = new RestartRequest(scale, contributor);
			}
			// reset buffer
			buffer.delete(0, buffer.length());
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// create tag name
			// reset buffer
			buffer.delete(0, buffer.length());
		}

		public void characters(char[] arg0, int arg1, int arg2)
				throws SAXException {
			buffer.append(arg0, arg1, arg2);
		}

		public void error(SAXParseException arg0) throws SAXException {
			System.err.println("Error parsing restart query (unmarshalling):");
			arg0.printStackTrace();
		}

		public void fatalError(SAXParseException arg0) throws SAXException {
			System.err
					.println("Fatal error parsing restart query (unmarshalling):");
			arg0.printStackTrace();
			query = null;
		}

	}
}
