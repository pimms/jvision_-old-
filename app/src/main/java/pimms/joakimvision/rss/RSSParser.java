package pimms.joakimvision.rss;


import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import pimms.joakimvision.RFC822Formatter;
import pimms.joakimvision.XMLHelper;


class RSSParser {
    public List<RSSItem> parseFeed(String xml, int maxItems) {
        List<RSSItem> rssItems = new ArrayList<>();

        try {
            Document document;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource source = new InputSource();
            source.setCharacterStream(new StringReader(xml));
            document = builder.parse(source);

            NodeList items = document.getElementsByTagName("item");
            for (int i=0; i < items.getLength() && rssItems.size() < maxItems; i++) {
                Element elem = (Element)items.item(i);
                RSSItem item = parseItem(elem);
                if (item != null) {
                    rssItems.add(item);
                }
            }
        } catch (Exception ex) {
            Log.e("RSSParser Error", ex.getMessage());
            ex.printStackTrace();
            return null;
        }

        return rssItems;
    }

    private RSSItem parseItem(Element element) throws Exception {
        Element eTitle = XMLHelper.findElement(element, "title");
        Element eDesc = XMLHelper.findElement(element, "description");
        Element ePub = XMLHelper.findElement(element, "pubDate");

        RSSItem item = new RSSItem();
        item.title = eTitle.getTextContent();
        item.description = eDesc.getTextContent();
        item.publicationTime = RFC822Formatter.stringToDate(ePub.getTextContent());

        return item;
    }
}
