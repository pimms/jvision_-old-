package pimms.joakimvision;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by pimms on 04.07.15.
 */
public class XMLHelper {
    public static Element findElement(Element root, String elementName) {
        return findElement(root, elementName, 0);
    }

    public static Element findElement(Element root, String elementName, int index) {
        NodeList nodeList = root.getElementsByTagName(elementName);

        if (nodeList != null && nodeList.getLength() > index) {
            return (Element)nodeList.item(index);
        }

        return null;
    }
}
