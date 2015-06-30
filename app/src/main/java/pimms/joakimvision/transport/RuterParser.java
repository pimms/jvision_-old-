package pimms.joakimvision.transport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class RuterParser {
    private int _lineRef;
    private String _destination;

    public RuterParser(int lineRef, String destinationStation) {
        _lineRef = lineRef;
        _destination = destinationStation;
    }

    public List<TransportDeparture> parse(String xml) throws Exception {
        List<TransportDeparture> deps = new ArrayList<>();

        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource source = new InputSource();
        source.setCharacterStream(new StringReader(xml));
        document = builder.parse(source);

        NodeList nodeList = document.getElementsByTagName("MonitoredStopVisit");
        for (int i=0; i<nodeList.getLength(); i++) {
            TransportDeparture departure = parseTransportDeparture((Element) nodeList.item(i));
            if (departure != null) {
                deps.add(departure);
            }
        }

        return deps;
    }


    private TransportDeparture parseTransportDeparture(Element root) throws Exception {
        TransportDeparture departure = new TransportDeparture();
        boolean valid = false;

        NodeList children = root.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)children.item(i);

                if (childElement.getTagName().equalsIgnoreCase("Extensions")) {
                    parseDeviations(childElement, departure);
                } else if (childElement.getTagName().equalsIgnoreCase("MonitoredVehicleJourney")) {
                    valid = parseJourneyData(childElement, departure);
                }
            }
        }

        return (valid ? departure : null);
    }

    private boolean parseJourneyData(Element root, TransportDeparture departure) throws Exception {
        Element eDestination = findElement(root, "DestinationName");
        Element eLineRef = findElement(root, "LineRef");
        Element eVehicleType = findElement(root, "VehicleMode");
        Element eAimedDeparture = findElement(root, "AimedDepartureTime");
        Element eExpectedDeparture = findElement(root, "ExpectedDepartureTime");
        Element ePlatformName = findElement(root, "DeparturePlatformName");
        Element eLineName = findElement(root, "PublishedLineName");

        if (eDestination == null || eLineRef == null || (eAimedDeparture == null || eExpectedDeparture == null)) {
            return false;
        }

        /* Ensure that the destination is correct, and assign it */
        String destination = eDestination.getTextContent();
        if (!destination.equalsIgnoreCase(_destination))
            return false;
        departure.setDestinationStation(destination);

        /* Ensure that the LineRef is correct */
        int lineRef = Integer.parseInt(eLineRef.getTextContent());
        if (lineRef != _lineRef)
            return false;

        /* Parse the date. Ensure that the departure is in the future! */
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        Date aimedDate = dateFormat.parse(eAimedDeparture.getTextContent());
        Date expectedDate = dateFormat.parse(eExpectedDeparture.getTextContent());

        Date now = new Date();
        if (now.after(aimedDate) && now.after(expectedDate))
            return false;

        long delay = expectedDate.getTime() - aimedDate.getTime();
        delay /= 1000;
        departure.setDeparture(expectedDate.after(aimedDate) ? expectedDate : aimedDate);
        departure.setDelayMinutes((int)delay / 60);

        /* Read the platform name */
        departure.setPlatformName(ePlatformName.getTextContent());

        /* Read the line name */
        departure.setLineName(eLineName.getTextContent());

        /* Read the VehicleMode, convert to VehicleType */
        String vehicleType = eVehicleType.getTextContent();
        switch (vehicleType) {
            case "train":
                departure.setVehicleType(TransportDeparture.VehicleType.TRAIN);
                 break;
            case "bus":
                departure.setVehicleType(TransportDeparture.VehicleType.BUS);
                break;
            case "metro":
                departure.setVehicleType(TransportDeparture.VehicleType.METRO);
                break;
            case "tram":
                departure.setVehicleType(TransportDeparture.VehicleType.TRAM);
                break;
        }

        return true;
    }

    private void parseDeviations(Element root, TransportDeparture departure) throws Exception {
        List<String> deviations = new ArrayList<>();

        NodeList deviationNodes = root.getElementsByTagName("Deviation");

        for (int i=0; i<deviationNodes.getLength(); i++) {
            Element element = (Element)deviationNodes.item(i);

            NodeList children = element.getChildNodes();
            for (int j=0; j<children.getLength(); j++) {
                Node childNode = children.item(j);
                if (childNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element childElem = (Element)childNode;
                if (childElem.getTagName().equalsIgnoreCase("Header")) {
                    String deviationText = childElem.getTextContent();
                    departure.addDeviation(deviationText);
                }
            }
        }
    }


    private Element findElement(Element root, String elementName) {
        return findElement(root, elementName, 0);
    }

    private Element findElement(Element root, String elementName, int index) {
        NodeList nodeList = root.getElementsByTagName(elementName);

        if (nodeList != null && nodeList.getLength() > index) {
            return (Element)nodeList.item(index);
        }

        return null;
    }
}
