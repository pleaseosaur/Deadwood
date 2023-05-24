// imports

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameData {

    private static GameData data;

    // constructor
    private GameData(InputStream boardFile, InputStream cardFile) throws ParserConfigurationException{
        Document boardDoc = getDocFromFile(boardFile);
        Document cardDoc = getDocFromFile(cardFile);
        createDeck(cardDoc);
        createBoard(boardDoc);
    }

    // initializeGameData: initializes game data
    public static void initializeGameData(InputStream boardFile, InputStream cardFile) throws ParserConfigurationException {
        if(data != null) {
            throw new IllegalStateException("Game data already initialized");
        }
        data = new GameData(boardFile, cardFile);
    }

    // getDocFromFile: gets doc for parsing
    private Document getDocFromFile(InputStream filename) throws ParserConfigurationException {
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); // create document builder factory
            DocumentBuilder db = dbf.newDocumentBuilder(); // create document builder
            Document doc = null; // create document

            try {
                doc = db.parse(filename); // parse file into document
            } catch (Exception ex) {
                System.out.println("XML parse failure");
                ex.printStackTrace();
            } // exception handling

            return doc; // return document
        } // exception handling
    }

    // createBoard: creates board from XML doc
    private void createBoard(Document d) {
        d.getDocumentElement().normalize(); // normalize document

        String boardName = d.getDocumentElement().getAttribute("name"); // get board name
        NodeList setNodes = d.getElementsByTagName("set"); // get location nodes
        List<Location> tempLocations = new ArrayList<>(); // create list of locations

        for(int i = 0; i < setNodes.getLength(); i++) {
            Node setNode = setNodes.item(i); // get location node

            if (setNode.getNodeType() == Node.ELEMENT_NODE) {
                Element setElement = (Element) setNode; // cast node to element
                Location set = createSet(setElement); // create location
                tempLocations.add(set); // add location to list
            }
        }

        Trailer trailer = createTrailer((Element) d.getElementsByTagName("trailer").item(0)); // get trailer location
        CastingOffice office = createOffice((Element) d.getElementsByTagName("office").item(0)); // get office location

        tempLocations.add(trailer); // add trailer to list
        tempLocations.add(office); // add office to list

        Map<String, Location> locations = constructGraph(tempLocations); // create map of locations

        Board.initializeBoard(boardName, locations, 10); // create board
        // printBoard(); // calls printBoard to print list of board's locations and their neighbors for debugging purposes
    }

    // constructGraph: turns list of locations into a graph for traversals
    private Map<String, Location> constructGraph(List<Location> tempLocations) {

        Map<String, Location> locations = new HashMap<>(); // create map of locations

        for(Location location : tempLocations) {
            locations.put(location.getName(), location); // add location to map
        }

        for(Location location : tempLocations) {
            List<String> neighbors = location.getTemp(); // get neighbors of location
            List<Location> neighborLocations = new ArrayList<>(); // create list of neighbor locations

            for(String neighbor : neighbors) {
                neighborLocations.add(locations.get(neighbor)); // add neighbor location to list
            }

            location.setNeighbors(neighborLocations); // set neighbors of location
        }
        return locations; // return map of locations
    }

    // createDeck: creates deck from XML doc
    private void createDeck(Document d) {
        d.getDocumentElement().normalize(); // normalize document

        NodeList cardNodes = d.getElementsByTagName("card"); // get card nodes

        List<Card> cards = new ArrayList<>(); // create list of cards

        for(int i = 0; i < cardNodes.getLength(); i++) {
            Node cardNode = cardNodes.item(i); // get card node

            if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                Element cardElement = (Element) cardNode; // cast node to element
                Card card = createCard(cardElement); // create card

                cards.add(card); // add card to list
            }
        }

        Deck.initializeDeck(cards); // create deck
    }

    // createCard: creates model.Scene card
    private Card createCard(Element cardElement) {

        String cardName = cardElement.getAttribute("name"); // get card name
        Element sceneNode = (Element) cardElement.getElementsByTagName("scene").item(0); // get scene node
        int sceneNumber = Integer.parseInt(sceneNode.getAttribute("number")); //get scene number
        String image = cardElement.getAttribute("img"); // get card image
        int budget = Integer.parseInt(cardElement.getAttribute("budget")); // get card budget
        String sceneDescription = cardElement.getElementsByTagName("scene").item(0).getTextContent(); // get scene description
        List<Role> roles = createRoles(cardElement.getElementsByTagName("part"), true); // get part/role nodes

        return new Card(cardName, sceneNumber, sceneDescription, budget, roles, image, false); // create card;
    }

    // createSet: creates Set location
    private Location createSet(Element setElement) {

        String locationName = setElement.getAttribute("name"); // get location name
        List<String> neighbors = createNeighbors(setElement.getElementsByTagName("neighbor")); // get neighbor nodes
        Area locationArea = getArea((Element) setElement.getElementsByTagName("area").item(0)); // get location area
        List<Take> takes = createTakes(setElement.getElementsByTagName("take")); // get take nodes
        List<Role> roles = createRoles(setElement.getElementsByTagName("part"), false); // get part/role nodes

        return new Set(locationName, neighbors, locationArea, null, takes, roles); // create set location
    }

    // createNeighbors: creates List of neighbors
    private List<String> createNeighbors(NodeList neighborNodes) {

        List<String> neighbors = new ArrayList<>(); // create list of neighbors

        // iterate through neighbor nodes
        for (int j = 0; j < neighborNodes.getLength(); j++) {
            Node neighborNode = neighborNodes.item(j); // get neighbor node

            if (neighborNode.getNodeType() == Node.ELEMENT_NODE) {
                Element neighborElement = (Element) neighborNode; // cast node to element
                if (neighborElement.getAttribute("name").equals("office")) {
                    neighbors.add("Casting Office"); // add office to list
                }
                else if (neighborElement.getAttribute("name").equals("trailer")) {
                    neighbors.add("Trailer"); // add trailer to list
                }
                else {
                    neighbors.add(neighborElement.getAttribute("name")); // add neighbor name to list
                }
            }
        }
        return neighbors;
    }

    // createTakes: creates List of takes
    private List<Take> createTakes(NodeList takeNodes) {

        List<Take> takes = new ArrayList<>(); // create list of takes

        // iterate through take nodes
        for (int j = 0; j < takeNodes.getLength(); j++) {
            Node takeNode = takeNodes.item(j); // get take node

            if (takeNode.getNodeType() == Node.ELEMENT_NODE) {
                Element takeElement = (Element) takeNode; // cast node to element
                Take take = createTake(takeElement); // create take
                takes.add(take); // add take to list
            }
        }
        return takes;
    }

    // createTake: creates one Take from element
    private Take createTake(Element takeElement) {

        int takeNumber = Integer.parseInt(takeElement.getAttribute("number")); // get take number
        Area takeArea = getArea((Element) takeElement.getElementsByTagName("area").item(0)); // get area

        return new Take(takeNumber, takeArea); // create take
    }

    // createRoles: creates roles from list of parts
    private List<Role> createRoles(NodeList partNodes, boolean onCard) {

        List<Role> roles = new ArrayList<>(); // create list of parts/roles

        // iterate through part/role nodes
        for(int j = 0; j < partNodes.getLength(); j++) {
            Node partNode = partNodes.item(j); // get part/role node

            if(partNode.getNodeType() == Node.ELEMENT_NODE) {
                Element partElement = (Element) partNode; // cast node to element
                Role role = createRole(partElement, onCard); // create role/part
                roles.add(role); // add part/role to list
            }
        }
        return roles;
    }

    // createRole: creates one Role from element
    private Role createRole(Element partElement, boolean onCard) {

        String partName = partElement.getAttribute("name"); // get part/role name
        int partLevel = Integer.parseInt(partElement.getAttribute("level")); // get part/role level
        Area partArea = getArea((Element) partElement.getElementsByTagName("area").item(0)); // get area
        String partLine = partElement.getAttribute("line"); // get part/role line

        return new Role(partName, partLevel, partArea, partLine, onCard, false); // create role/part
    }

    // createTrailer: creates trailer location
    private Trailer createTrailer(Element trailerElement) {

        List<String> trailerNeighbors = createNeighbors(trailerElement.getElementsByTagName("neighbor")); // get trailer neighbor nodes
        Area trailerArea = getArea((Element) trailerElement.getElementsByTagName("area").item(0)); // get trailer area

        return new Trailer("Trailer", trailerNeighbors, trailerArea); // create trailer
    }

    // createOffice: creates office location
    private CastingOffice createOffice(Element officeElement) {

        List<String> officeNeighbors = createNeighbors(officeElement.getElementsByTagName("neighbor")); // get office neighbor nodes
        Area officeArea = getArea((Element) officeElement.getElementsByTagName("area").item(0)); // get office area

        NodeList upgradeNodes = officeElement.getElementsByTagName("upgrade"); // get upgrade nodes
        List<Upgrade> upgrades = new ArrayList<>(); // create list of upgrades

        // iterate through upgrade nodes
        for(int i = 0; i < upgradeNodes.getLength(); i++) {

            Node upgradeNode = upgradeNodes.item(i); // get upgrade node

            if (upgradeNode.getNodeType() == Node.ELEMENT_NODE) {

                Element upgradeElement = (Element) upgradeNode; // cast node to element
                int level = Integer.parseInt(upgradeElement.getAttribute("level")); // get upgrade level
                String currency = upgradeElement.getAttribute("currency"); // get upgrade currency
                int amount = Integer.parseInt(upgradeElement.getAttribute("amt")); // get upgrade amount
                Area upgradeArea = getArea((Element) upgradeElement.getElementsByTagName("area").item(0)); // get upgrade area

                Upgrade upgrade = new Upgrade(level, currency + "s", amount, upgradeArea); // create upgrade
                upgrades.add(upgrade); // add upgrade to list
            }
        }

        return new CastingOffice("Casting Office", officeNeighbors, officeArea, upgrades); // create office
    }

    // getArea: gets area object
    private static Area getArea(Element area) {

        int x = Integer.parseInt(area.getAttribute("x")); // get x coordinate
        int y = Integer.parseInt(area.getAttribute("y")); // get y coordinate
        int h = Integer.parseInt(area.getAttribute("h")); // get height
        int w = Integer.parseInt(area.getAttribute("w")); // get width

        return new Area(x, y, h, w); // create area
    }
}