package com.ge.util;
import com.ge.action.*;
import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.entity.Npc;
import com.ge.baseobject.gameobject.*;
import com.ge.baseobject.item.*;
import com.ge.baseobject.room.Room;
import com.ge.general.ApplicationWindow;
import com.ge.general.DialogResponse;
import com.ge.general.World;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DataIO {

    //Takes a xml file containing the game world definition
    //Returns World object if successfully loaded
    //otherwise returns null if an error occurred
    public static World loadWorldFromFile(String filePath) throws ParserConfigurationException, IOException, SAXException {
        World world = new World();

        if (!loadDefinitions(world))
            return null;    //error occurred during loading, no point to continue

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            world.setGreetingMessage(rootElement.getAttributes().getNamedItem("Greeting").getTextContent());
            NodeList list = rootElement.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    if (node.getNodeName().equalsIgnoreCase("Room")){

                        int roomId = Integer.parseInt(element.getAttribute("Id"));
                        Room room = world.getRoomById(roomId);


                        NodeList roomEntities = element.getElementsByTagName("Entity");
                        for (int j = 0; j < roomEntities.getLength(); j++){


                            int entityId = Integer.parseInt(roomEntities.item(j).getAttributes().getNamedItem("Id").getTextContent());
                            Entity entity = world.getEntityDefs().get(entityId);

                            if (entity != null)
                            {
                                entity.setRoom(room);
                                room.addEntity(entity);
                            } else {
                                ApplicationWindow.printError(String.format("Error, unable to load entity: %d.\n", entityId));
                                return null;    //an entity configuration is wrong in the xml
                            }
                        }

                        NodeList roomItems = element.getElementsByTagName("Item");
                        for (int j = 0; j < roomItems.getLength(); j++){
                            int itemId = Integer.parseInt(roomItems.item(j).getAttributes().getNamedItem("Id").getTextContent());
                            Item item = world.getItemDefs().get(itemId);

                            if (item != null)
                            {
                                item.setRoom(room);
                                room.addItem(item);
                            }
                        }
                    }
                }
            }

            //finally, load game objects
            for(GameObject go : world.getGameObjectDefs()){
                try {
                    Room roomDef = go.getRoom();
                    Room targetRoom = world.getRoomById(roomDef.getId());
                    targetRoom.addGameObject(go);
                } catch (Exception ex){
                }

            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            ApplicationWindow.printError("Error, unable to load world");
            return null;
        }

        return world;
    }

    //Takes a xml file containing room definition entries
    //Returns an ArrayList of Room if successfully loaded
    //otherwise returns null if an error occurred
    public static ArrayList<Room> loadRoomsFromFile(String filePath) throws NullPointerException, IllegalArgumentException{
        ArrayList<Room> roomDefinitions = new ArrayList<>();

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("Room");

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    // get entity attributes
                    int id = Integer.parseInt(element.getAttribute("Id"));
                    String name = element.getAttribute("Name");
                    String examine = element.getAttribute("Examine");
                    boolean isLocked = element.getAttribute("IsLocked").equalsIgnoreCase("Yes");
                    int neighNorth = Integer.parseInt(element.getAttribute("NeighbourNorth"));
                    int neighSouth = Integer.parseInt(element.getAttribute("NeighbourSouth"));
                    int neighEast = Integer.parseInt(element.getAttribute("NeighbourEast"));
                    int neighWest = Integer.parseInt(element.getAttribute("NeighbourWest"));

                    //construct a npc object with mandatory properties
                    Room room = new Room(id, name, examine, isLocked, neighNorth, neighSouth, neighEast, neighWest);
                    roomDefinitions.add(room);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            ApplicationWindow.printError("Unable to load rooms from file");
            return null;
        }
        return roomDefinitions;
    }

    //Takes a xml file containing entity definition entries
    //Note the file opening and closing is handled by the Document class
    //Returns an ArrayList of Entity if successfully loaded
    //otherwise returns null if an error occurred
    public static ArrayList<Entity> loadEntitiesFromFile(String filePath) {
        ArrayList<Entity> entityDefinitions = new ArrayList<>();

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(filePath));

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("Entity");

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    // get entity attributes
                    int id = Integer.parseInt(element.getAttribute("Id"));
                    String name = element.getAttribute("Name");
                    String examine = element.getAttribute("Examine");
                    boolean isAttackable = element.getAttribute("IsAttackable").equalsIgnoreCase("Yes");

                    //construct a generic npc object with mandatory properties
                    Npc npc = new Npc(id, name, examine, isAttackable);

                    entityDefinitions.add(npc);

                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            ApplicationWindow.printError("Unable to load entities from file");
            return null;
        }

        return entityDefinitions;
    }

    //Takes a xml file containing item definition entries
    //Returns an ArrayList of Item if successfully loaded
    //otherwise returns null if an error occurred
    public static ArrayList<Item> loadItemsFromFile(String filePath) throws NullPointerException, IllegalArgumentException{
        ArrayList<Item> itemDefinitions = new ArrayList<>();

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            NodeList list = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    // get entity attributes
                    int id = Integer.parseInt(element.getAttribute("Id"));
                    String name = element.getAttribute("Name");
                    String examine = element.getAttribute("Examine");

                    Item item = null;
                    if (element.getNodeName() == "Weapon")
                    {
                        item = new Weapon(id, name, examine);
                    } else if (element.getNodeName() == "Misc")
                    {
                        item = new Misc(id, name, examine);
                    } else if (element.getNodeName() == "Consumable")
                    {
                        item = new Consumable(id, name, examine);
                    }

                    if (item != null)
                        itemDefinitions.add(item);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            ApplicationWindow.printError("Unable to load items from file");
            return null;
        }

        return itemDefinitions;
    }

    public static ArrayList<DialogResponse> loadDialogResponses(String filePath){
        ArrayList<DialogResponse> dialogResponses = new ArrayList<>();

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(filePath));

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("DialogText");

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // get entity attributes
                    int id = Integer.parseInt(element.getAttribute("Id"));
                    String message = element.getAttribute("Message");

                    //construct a npc object with mandatory properties
                    DialogResponse dialogResponse = new DialogResponse(id, message);
                    dialogResponses.add(dialogResponse);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            ApplicationWindow.printError("Unable to load dialog responses from file");
            return null;
        }

        return dialogResponses;
    }

    public static ArrayList<DialogResponse> loadGameMessages(String filePath){
        ArrayList<DialogResponse> gameMessages = new ArrayList<>();

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(filePath));

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("Message");

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // get entity attributes
                    int id = Integer.parseInt(element.getAttribute("Id"));
                    String message = element.getAttribute("Message");

                    //construct a npc object with mandatory properties
                    DialogResponse dialogResponse = new DialogResponse(id, message);
                    gameMessages.add(dialogResponse);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            ApplicationWindow.printError("Unable to load dialog responses from file");
            return null;
        }

        return gameMessages;
    }

    public static ArrayList<ActionDef> loadActionDefs(String filePath){
        ArrayList<ActionDef> actionsDefs = new ArrayList<>();

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            NodeList list = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    String[] verbs = element.getAttribute("verbs").trim().split("\\s*,\\s*");
                    String description = element.getAttribute("description");
                    ActionDef actionDef = new ActionDef(node.getNodeName(), new HashSet<>(Arrays.asList(verbs)), description);
                    actionsDefs.add(actionDef);

                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            ApplicationWindow.printError("Unable to load actions from file");
            return null;
        }

        return actionsDefs;
    }


    //Loads and validates world object data
    //return true if success, otherwise false
    private static boolean loadDefinitions(World world) {
        world.setRoomDefs(loadRoomsFromFile("def_room.xml"));
        if (world.getRoomDefs() == null)
            return false;

        world.setEntityDefs(loadEntitiesFromFile("def_entity.xml"));
        if (world.getEntityDefs() == null)
            return false;

        world.setItemDefs(loadItemsFromFile("def_item.xml"));
        if (world.getItemDefs() == null)
            return false;

        world.setDialogResponseDefs(loadDialogResponses("def_dialog.xml"));
        if (world.getDialogResponseDefs() == null)
            return false;

        world.setGameMessageDefinitions(loadGameMessages("def_gamemessage.xml"));
        if (world.getGameMessageDefinitions() == null)
            return false;

        world.loadGameObjects(); //since game objects have unique and special behavior, they are defined by source code
        world.loadPrompts();

       return true;
    }

    //TODO : Save the current state of the game to a specified xml file
    //Returns true on success otherwise false if an error occurred
    public static boolean saveGameState(String name){
        return false;
    }

    //TODO : Load a state of the game from a specified xml file
    //Returns true on success otherwise false if an error occurred
    public static boolean loadGameState(String name){
        return false;
    }

}


