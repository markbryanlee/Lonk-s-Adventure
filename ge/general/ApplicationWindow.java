package com.ge.general; /**
 * <h1>Lonk's Adventure</h1>
 * <p>
 * Add some
 * useful summary
 * here
 *
 * <b>CCPS 406 Introduction to Software Engineering, section 2J0, Winter 2022. Ryerson University.</b>
 *
 * @author  Karel Tutsu, Mark Bryan Lee, Abrar Ahmad
 * @version 1.0
 * @since   2022-02-08
 */

import com.ge.action.*;
import com.ge.action.Action;
import com.ge.baseobject.item.Item;
import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.gameobject.GameObject;
import com.ge.baseobject.room.Room;
import com.ge.util.CommandParser;
import com.ge.util.DataIO;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;

public class ApplicationWindow {

    //game stuff
    private static boolean isVerbose = false;
    private static boolean isViewVisible = false;
    private static World world;
    private static String COLOR_SEPARATOR = ";";

    //GUI objects
    private static JFrame frame;
    private static JSplitPane splitMain;
    private static JSplitPane splitChild;
    private static PanelInfo panelInfo;
    private static PanelMap panelMap;
    private static JPanel panelGame;
    private static JTextPane console;
    public static JTextField input;
    private static StyledDocument document;
    private static JLabel labelCurRoom;
    private static JLabel labelCurMoves;

    public static World getWorld(){
        return world;
    }

    public void setWorld(World w){
        world = w;
    }

    public static void setCurRoom(Room room)
    {
        labelCurRoom.setText(room.getName());
    }

    public static void setCurMoves(int moves)
    {
        labelCurMoves.setText("Moves: " + moves);
    }

    private final ArrayList<String> commandHistory = new ArrayList<>();
    private int commandHistoryIndex = 0;
    private int getCommandHistoryIndex(){
        return commandHistoryIndex;
    }
    private void setCommandHistoryIndex(int index){
        if (index <= commandHistorySize && index > 0){
            commandHistoryIndex = index;
        }
        try {
            input.setText(commandHistory.get(commandHistory.size() - commandHistoryIndex));
        } catch (Exception ex){

        }

    }
    private final int commandHistorySize = 10;

    private static final int gameScreenWidth = 660; //arbitrary
    private static final int gameScreenHeight = 660; //arbitrary
    private static final String gameTitle = "Lonk's Adventure";
    private static final Font gameFont = new Font("Courier New", Font.PLAIN, 16);
    private static final Color gameBackColor = new Color(50, 50, 50);
    private static final Color gameTextColor = new Color(255, 255, 255);
    private static final Color gameTextColorError = new Color(255, 0, 0);
    private static Style style;


    /**
     * The application's main method which instantiates a static Console object.
     * @param args Unused.
     */
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        new ApplicationWindow();
    }

    /**
     * The special constructor method that initializes the Console object by
     * calling loadUIControls and loadGame methods.
     */
    public ApplicationWindow() throws ParserConfigurationException, IOException, SAXException {
        initializeUI();
        loadGame();
    }

    /**
     * A method that prepares and loads the UI controls for the game.
     */
    private void initializeUI() {
        frame = new JFrame();
        frame.setTitle(gameTitle);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());

        panelMap = new PanelMap();
        JScrollPane scrollPanMap = new JScrollPane(panelMap);
        scrollPanMap.setMinimumSize(new Dimension(0,0));

        panelGame = new JPanel(new BorderLayout());
        panelGame.setBackground(Color.DARK_GRAY);

        JPanel panelPlayerStats = new JPanel();
        panelPlayerStats.setLayout(new GridLayout(1, 4));
        panelPlayerStats.setBackground(Color.GRAY);
        panelPlayerStats.setPreferredSize(new Dimension(gameScreenWidth, 20));

        labelCurRoom = new JLabel();
        labelCurRoom.setFont(new Font(gameFont.getName(), Font.BOLD, gameFont.getSize()));
        labelCurMoves = new JLabel();
        labelCurMoves.setFont(gameFont);

        JButton btnSave = new JButton();
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //your actions
                ApplicationWindow.print("Saving game state...");
                String filename = "file.ser";

                // Serialization
                try
                {
                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream(filename);
                    ObjectOutputStream out = new ObjectOutputStream(file);

                    // Method for serialization of object
                    out.writeObject(getWorld());

                    out.close();
                    file.close();

                    System.out.println("Object has been serialized");
                    ApplicationWindow.print("Successfully saved");

                }

                catch(IOException ex)
                {
                    System.out.println("IOException is caught");
                    ApplicationWindow.printError("Error occured");
                }

            }
        });
        btnSave.setText("Save Game");
        JButton btnLoad = new JButton();
        btnLoad.setText("Load Game");
        btnLoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //your actions
                ApplicationWindow.print("Loading saved game...");

                // Deserialization
                try
                {
                    // Reading the object from a file
                    FileInputStream file = new FileInputStream("file.ser");
                    ObjectInputStream in = new ObjectInputStream(file);

                    // Method for deserialization of object
                    World loadedWorld = (World)in.readObject();

                    in.close();
                    file.close();
                    setWorld(loadedWorld);

                    world.getPlayer().changes.firePropertyChange("GAME LOADED" , "old", "new");
                    panelMap.setWorld(world);
                    panelInfo.setWorld(world);
                    panelMap.repaint();

                    ApplicationWindow.print("Successfully loaded");

                }

                catch(IOException ex)
                {
                    System.out.println("IOException is caught");
                    ex.printStackTrace();
                    ApplicationWindow.printError("Error occured");
                }

                catch(ClassNotFoundException ex)
                {
                    System.out.println("ClassNotFoundException is caught");
                }

            }
        });

        panelPlayerStats.add(labelCurRoom);
        panelPlayerStats.add(labelCurMoves);
        panelPlayerStats.add(btnSave);
        panelPlayerStats.add(btnLoad);

        console = new JTextPane();
        console.setEditable(false);
        console.setFont(gameFont);
        console.setOpaque(false);
        console.setMargin( new Insets(3,3,3,3) );
        JScrollPane scrollbar = new JScrollPane(console);
        scrollbar.setBorder(null);
        scrollbar.setOpaque(false);
        scrollbar.getViewport().setOpaque(false);
        document = console.getStyledDocument();

        input = new JTextField();
        input.setFont(gameFont);
        input.setEditable(true);
        input.setCaretColor(Color.WHITE);
        input.setOpaque(false);
        input.setBackground(Color.WHITE);
        input.setForeground(Color.BLACK);
        input.requestFocus();

        panelGame.add(panelPlayerStats, BorderLayout.NORTH);
        panelGame.add(scrollbar, BorderLayout.CENTER);
        panelGame.add(input, BorderLayout.SOUTH);

        splitChild = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPanMap, panelGame);
        splitChild.setDividerSize(5);
        splitChild.setResizeWeight(0.01); // equal weights to top and bottom

        panelInfo = new PanelInfo();

        splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitChild, panelInfo);
        splitMain.setDividerSize(5);
        splitMain.setResizeWeight(0.8);

        frame.getContentPane().add(splitMain);
        frame.pack();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setResizable(false);

        //hideView();
        showView();

        frame.setVisible(true);
        style = console.addStyle("MyStyle", null);
    }

    public static void hideView(){
        //hide view
        splitMain.setDividerLocation(splitMain.getLocation().x+splitMain.getSize().width);
        splitMain.setDividerSize(0);
        splitMain.setEnabled(false);
        splitChild.setDividerLocation(0);
        splitChild.setDividerSize(0);
        splitChild.setEnabled(false);
    }

    private static void showView(){
        //show view
        int newWidth = (int)(splitMain.getSize().width * 0.8);
        splitMain.setDividerLocation(newWidth);
        splitMain.setDividerSize(5);
        splitMain.setEnabled(true);
        splitChild.setDividerLocation(420);
        splitChild.setDividerSize(5);
        splitChild.setEnabled(true);
    }

    public static void toggleView(){
        if (isViewVisible){
            hideView();
        } else {
            showView();
        }
        //flip
        isViewVisible = !isViewVisible;
    }

    /**
     * A method that calls for game initialization to load a game state (defaults to start of the game)
     * If the game is successfully initialized, the method adds an ActionListener to user (input) text box
     * to parse input/output between the player and the game.
     * Otherwise, if the game initialization fails the program displays an error message to the user.
     */
    private void loadGame() throws ParserConfigurationException, IOException, SAXException {
        if (initializeWorld())
        {
            if (isVerbose)
            {
                print("LOADED @red;" + world.getEntityDefs().size() + "@ entity definitions");
                print("@yellow;LOADED@ " + world.getItemDefs().size() + " item definitions");
                print("LOADED " + world.getRoomDefs().size() + " @blue;room definitions@");
                print("@green;LOADED " + world.getGameObjectDefs().size() + " game object definitions@");
            }

            //dummy();    //debug

            panelMap.setWorld(world);
            panelInfo.setWorld(world);
            printItalic(world.getGreetingMessage());
            print(world.getPlayer().getRoom().getExamine());


            // A local class defined and used only inside this method.
            class MyActionListener implements ActionListener {
                // The action listener nested class must have this exact method.
                public void actionPerformed(ActionEvent ae) {
                    gameLoop();
                }
            }

            // A local class defined and used only inside this method.
            class MyKeyListener implements KeyListener {
                // The KeyListener listener nested class must have this exact method.
                public void keyPressed(KeyEvent e){
                    if (e.getKeyCode() == KeyEvent.VK_UP){
                        setCommandHistoryIndex(getCommandHistoryIndex() + 1);
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN){
                        setCommandHistoryIndex(getCommandHistoryIndex() - 1);
                    } else if (e.getKeyCode() == KeyEvent.VK_TAB){
                        String[] words = input.getText().split(" ");
                        String verb = words[0];

                        if (words.length == 1)
                        {
                            input.setText(autoCompleteInputVerb(verb));
                        } else {
                            StringBuilder sb = new StringBuilder();
                            for(int i=1; i < words.length; i++){
                                sb.append(words[i]);
                            }
                            input.setText(autoCompleteInputSubject(verb, sb.toString()));
                        }

                    }
                }
                // The KeyListener listener nested class must have this exact method.
                public void keyReleased(KeyEvent e){

                }
                // The KeyListener listener nested class must have this exact method.
                public void keyTyped(KeyEvent e){

                }
            }

            input.addActionListener(new MyActionListener());
            input.addKeyListener(new MyKeyListener());
            input.setFocusTraversalKeysEnabled(false);

            setCurMoves(world.getPlayer().getMoves());
            setCurRoom(world.getPlayer().getRoom());



        }
    }

    private String autoCompleteInputVerb(String verb){
        for (ActionDef action : CommandParser.getActionDefs()){
                for (String v: action.getVerbs()){
                    if (v.toLowerCase().startsWith(verb.toLowerCase())){
                        return String.format("%s ", v);
                    }
                }
        }

        //no match, return original
        return String.format("%s ", verb);
    }

    private String autoCompleteInputSubject(String verb, String subject){
        ActionDef action = null;
        //first find out what type of verb is it
        for (ActionDef def : CommandParser.getActionDefs()){
            for (String v: def.getVerbs()){
                if (v.equalsIgnoreCase(verb)){
                    action = def;
                    break;
                }
            }
        }

        if (action == null){
            //no match, return original
            return String.format("%s %s", verb, subject);
        }

        String matchedSubject = "";

        if (action.getType().equalsIgnoreCase("Navigate")){
            matchedSubject = matchSubjectNav(subject);
        } else if (action.getType().equalsIgnoreCase("ItemTake") || action.getType().equalsIgnoreCase("ItemDrop") || action.getType().equalsIgnoreCase("ItemInteract") || action.getType().equalsIgnoreCase("ItemRead") ){
            matchedSubject = matchSubjectItem(subject);
        } else if (action.getType().equalsIgnoreCase("GameObjectInteract")){
            matchedSubject =  matchSubjectGameObject(subject);
        } else if (action.getType().equalsIgnoreCase("Dialog") || action.getType().equalsIgnoreCase("Attack")){
            matchedSubject = matchSubjectEntity(subject);
        } else if (action.getType().equalsIgnoreCase("Examine")){
            //examine an item?
            matchedSubject = matchSubjectItem(subject);

            //examine an entity?
            if (matchedSubject.length() == 0){
                matchedSubject = matchSubjectEntity(subject);
            }
            //examine a game object?
            if (matchedSubject.length() == 0){
                matchedSubject = matchSubjectGameObject(subject);
            }

        }

        if (matchedSubject.length() == 0){
            //no match, return original
            return String.format("%s %s", verb, subject);
        } else {
            return String.format("%s %s", verb, matchedSubject);
        }

    }

    private String matchSubjectNav(String subject){
        String[] directions = new String[]{"North", "South", "East", "West"}; //TODO : EXTRACT TO CONF XML
        for(String dir : directions){
            if (dir.toLowerCase().startsWith(subject.toLowerCase())){
                return dir;
            }
        }
        return "";  //no match
    }

    private String matchSubjectItem(String subject){
        //note the player can only match items around them
        for (Item item : world.getPlayer().getRoom().getItems()){
            if (item.getName().toLowerCase().startsWith(subject.toLowerCase())){
                return item.getName();
            }
        }

        //or inventory
        for (Item item : world.getPlayer().getInventory()){
            if (item.getName().toLowerCase().startsWith(subject.toLowerCase())){
                return item.getName();
            }
        }
        return "";  //no match
    }

    private String matchSubjectGameObject(String subject){
        for (GameObject go : world.getPlayer().getRoom().getGameObjects()){
            if (go.getName().toLowerCase().startsWith(subject.toLowerCase())){
                return go.getName();
            }
        }
        return "";  //no match
    }

    private String matchSubjectEntity(String subject){
        for (Entity entity : world.getPlayer().getRoom().getEntities()){
            if (entity.getName().toLowerCase().startsWith(subject.toLowerCase())){
                return entity.getName();
            }
        }
        return "";  //no match
    }

    /**
     * Called when the user hits 'Enter' key in the Text box. This method effectively serves as the game update loop.
     * If the user typed any text in text box then the method will:
     * 1. Add that text to the commandHistory list
     * 2. Displays the user typed text on the screen with a '>' prefix (e.g., >go north)
     * 3. Passes the raw user input to CommandParser that will return a String of the consequence of this command
     * 4. Displays the result of CommandParser on the screen (e.g., 'Did not recognize the command' or 'You take Wooden Shield')
     * 5. Clears the input text box and scrolls the text display control to the bottom by default.
     *
     */
    private void gameLoop() {
        if (!world.getIsGameOver())
        {
            String text = input.getText();

            if (text.length() > 0){
                commandHistoryAdd(text);

                String rawInput = input.getText();

                if (rawInput.equalsIgnoreCase("::view")){
                    toggleView();
                } else {
                    printPlayer(">" + rawInput);  //display user types messages with > prefix to help distinguish player messages from game messages

                    Action action = CommandParser.parse(rawInput);

                    //execute single action
                    executeAction(action);

                    //update UI
                    updateUI();
                }

                input.setText("");
            }
        }
    }

    public static void executeAction(Action action) {
        String feedback = action.execute();
        print(feedback);  //give feedback to user
    }

    public static void updateUI() {
        setCurMoves(world.getPlayer().getMoves());
        setCurRoom(world.getPlayer().getRoom());
        scrollBottom();
    }

    /**
     * Called whenever user types in any text in text box.
     * Adds the latest input to commandHistory
     * If the number of commands in commandHistory is greater than commandHistorySize
     * Then pop (remove at 0) the first element in commandHistory to maintain intended (limited) size.
     */
    private void commandHistoryAdd(String text) {
        commandHistory.add(text);
        if (commandHistory.size() > commandHistorySize)
        {
            commandHistory.remove(0);
        }
        commandHistoryIndex = 0;
    }

    /**
     * Calls DataIO.loadWorldFromFile to load a world object from file
     * @return true if the world object was successfully created, otherwise false
     */
    private boolean initializeWorld() throws ParserConfigurationException, IOException, SAXException {
        if (isVerbose)
            print("Initializing game...");

        world = DataIO.loadWorldFromFile("world.xml");

        return world != null;
    }
    /**
     * Utility method to add standard messages to screen
     * @param message   Text to be printed on screen
     */
    public static void print(String message){
        echo(message, false, false, gameTextColor);
    }

    public static void printPlayer(String message){
        echo(message, false, false, Color.PINK);
    }

    public static void echo(String message, boolean isBold, boolean isItalic, Color color) {
        StyleConstants.setBold(style, isBold);
        StyleConstants.setItalic(style, isItalic);
        String[] lines = message.split(System.lineSeparator());

        for(String line : lines){
            if (line.contains("@")){
                //parse colored message

                String[] split = line.split("@");
                String colorName = split[1].split(COLOR_SEPARATOR)[0];
                Color highlightColor = getColorByName(colorName);

                if (highlightColor == null)
                    highlightColor = color;

                if (line.charAt(0) == '@'){
                    //colored string at the start
                    if (split.length == 3) {
                        try {
                            StyleConstants.setForeground(style, highlightColor);
                            document.insertString(document.getLength(), split[1].split(COLOR_SEPARATOR)[1], style);
                            StyleConstants.setForeground(style, gameTextColor);
                            document.insertString(document.getLength(), split[2]+ "\n", style);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        try {
                            StyleConstants.setForeground(style, highlightColor);
                            document.insertString(document.getLength(), split[1].split(COLOR_SEPARATOR)[1]+ "\n", style);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                } else if (line.charAt(line.length()-1) == '@') {
                    //colored string at the end
                    try {
                        StyleConstants.setForeground(style, gameTextColor);
                        document.insertString(document.getLength(), split[0], style);
                        StyleConstants.setForeground(style, highlightColor);
                        document.insertString(document.getLength(), split[1].split(COLOR_SEPARATOR)[1]+ "\n", style);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    //colored string in the middle
                    try {
                        document.insertString(document.getLength(), split[0], style);
                        StyleConstants.setForeground(style, highlightColor);
                        document.insertString(document.getLength(), split[1].split(COLOR_SEPARATOR)[1], style);
                        StyleConstants.setForeground(style, gameTextColor);
                        document.insertString(document.getLength(), split[2]+ "\n", style);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else {
                //parse regular message
                try {
                    StyleConstants.setForeground(style, color);
                    document.insertString(document.getLength(), line + "\n", style);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        try {
            StyleConstants.setForeground(style, color);
            document.insertString(document.getLength(), "\n", style);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Utility method to add error text to screen
     * @param message   Text to be printed on screen
     */
    public static void printError(String message){
        StyleConstants.setForeground(style, gameTextColorError);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        try {
            document.insertString(document.getLength(), message + "\n", style);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Utility method to add bold text to screen
     * @param message   Text to be printed on screen
     */
    public static void printBold(String message){
        echo(message, true, false, gameTextColor);
    }

    /**
     * Utility method to add italic text to screen
     * @param message   Text to be printed on screen
     */
    public static void printItalic(String message){
        echo(message, false, true, gameTextColor);
    }

    /**
     * Utility method to scroll text to bottom
     */
    public static void scrollBottom(){
        console.setCaretPosition(console.getDocument().getLength());
    }

    private static Color getColorByName(String name){
        Color color;
        try {
            Field field = Class.forName("java.awt.Color").getField(name);
            color = (Color)field.get(null);
        } catch (Exception e) {
            color = null; // Not defined
        }
        return color;
    }

    private void dummy(){


        //for(BaseItem item: world.getItemDefs())
        //{
        //    world.getPlayer().addInventoryItem(item);
        //}

        //world.getPlayer().addInventoryItem(world.getItemByName("Health Potion"));
        //world.getPlayer().addInventoryItem(world.getItemByName("Fairy"));
        //world.getPlayer().addInventoryItem(world.getItemByName("Wooden Shield"));
        //world.getPlayer().addInventoryItem(world.getItemByName("Dinged Sword"));
        //world.getPlayer().addInventoryItem(world.getItemByName("Red Vial"));
        //world.getPlayer().addInventoryItem(world.getItemByName("Yellow Vial"));
        //world.getPlayer().addInventoryItem(world.getItemByName("Blue Vial"));

        //GameObject ss = world.getGameObjectByName("Soul Swap");
        //GameObject cm = world.getGameObjectByName("Color Mixer");
        //GameObject sh = world.getGameObjectByName("Village Shop");
        //world.getPlayer().getRoom().addGameObject(ss);
        //world.getPlayer().getRoom().addGameObject(cm);
        //world.getPlayer().getRoom().addGameObject(sh);
    }
}

class PanelMap extends JPanel implements PropertyChangeListener {

    private ArrayList<Shape> shapes;
    private World world;
    private final int RECT_WIDTH = 160;
    private final int RECT_HEIGHT = 30;
    private final int PREF_WIDTH = 1600;
    private final int PREF_HEIGHT = 800;
    private Entity player;
    private boolean showId = true;

    public void setWorld(World world){
        this.world = world;
        this.player = world.getPlayer();
        player.addRoomChangeChangeListener(this);
        shapes = new ArrayList<>();

        int centerX = this.getPreferredSize().width / 2;
        int centerY = this.getPreferredSize().height / 2;

        ArrayList<Room> processed = new ArrayList<>();
        processRoom(world.getRoomDefs().get(0), centerX, centerY, processed);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                for (Shape s : shapes) {

                    if (s.contains(me.getPoint())) {//check if mouse is clicked within shape

                        if (me.getButton() == MouseEvent.BUTTON1)
                        {
                            MyRectangle rect = (MyRectangle) s;

                            if (!rect.getWorld().getPlayer().getRoom().equals(rect.getRoom()))
                            {
                                Room oldRoom = player.getRoom();
                                oldRoom.removeEntity(player);

                                Room newRoom = rect.getRoom();

                                ActionCheat actionCheat = new ActionCheat();
                                String desc = String.format("Teleport to: %s", newRoom.getName());
                                actionCheat.setDescription(desc);
                                newRoom.addEntity(player);
                                player.changes.firePropertyChange(String.format("Activated Cheat - %s", desc), "old","new");
                            }
                        } else if (me.getButton() == MouseEvent.BUTTON3) {
                            MyRectangle rect = (MyRectangle) s;
                            Room room = rect.getRoom();
                            JOptionPane.showMessageDialog(null,
                                    room,
                                    room.getName(),
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    public PanelMap(){
    }

    public void propertyChange(PropertyChangeEvent evt) {
        validate();
        repaint();
    }

    private void processRoom(Room room, int centerX, int centerY, ArrayList<Room> processed){
        MyRectangle origin = new MyRectangle(centerX, centerY, RECT_WIDTH, RECT_HEIGHT, room);
        origin.setWorld(world);
        shapes.add(origin);
        processed.add(room);

        if (room.getNeighbourNorth() != room.NEIGHBOUR_NONE){
            int northY = centerY - RECT_HEIGHT * 3;
            Room northRoom = world.getRoomById(room.getNeighbourNorth());
            MyRectangle rectNorth = new MyRectangle(centerX, northY, RECT_WIDTH, RECT_HEIGHT, northRoom);

            //draw connecting line
            MyRectangle link = new MyRectangle((int) origin.getCenterX(), (int) (rectNorth.getY() + rectNorth.getHeight()),1,
                    (int) origin.getY()-(int) (rectNorth.getY() + rectNorth.getHeight()), null);

            if (!shapes.contains(link))
                shapes.add(link);

            if (!processed.contains(northRoom))
            {
                processRoom(northRoom, centerX, northY, processed);
            }
        }


        if (room.getNeighbourSouth() != room.NEIGHBOUR_NONE){
            int southY = centerY + RECT_HEIGHT * 3;
            Room southRoom = world.getRoomById(room.getNeighbourSouth());
            MyRectangle rectSouth = new MyRectangle(centerX, southY, RECT_WIDTH, RECT_HEIGHT, southRoom);



            //draw connecting line
            MyRectangle link = new MyRectangle((int) rectSouth.getCenterX(), (int) (origin.getY() + origin.getHeight()),1,
                    (int) rectSouth.getY()-(int) (origin.getY() + origin.getHeight()), null);

            if (!shapes.contains(link))
                shapes.add(link);

            if (!processed.contains(southRoom))
            {
                processRoom(southRoom, centerX, southY, processed);
            }
        }

        if (room.getNeighbourEast() != room.NEIGHBOUR_NONE){
            int eastX = centerX + RECT_WIDTH + RECT_HEIGHT * 3;
            Room eastRoom = world.getRoomById(room.getNeighbourEast());
            MyRectangle rectEast = new MyRectangle(eastX, centerY, RECT_WIDTH, RECT_HEIGHT, eastRoom);

            //draw connecting line
            MyRectangle link = new MyRectangle((int) (origin.getX() + origin.getWidth()), (int) origin.getCenterY(),
                    (int) (rectEast.getX() - (int) (origin.getX() + origin.getWidth())), 1, null);

            if (!shapes.contains(link))
                shapes.add(link);

            if (!processed.contains(eastRoom))
            {
                processRoom(eastRoom, eastX, centerY, processed);
            }

        }

        if (room.getNeighbourWest() != room.NEIGHBOUR_NONE){
            int westX = centerX - RECT_WIDTH - RECT_HEIGHT * 3;
            Room westRoom = world.getRoomById(room.getNeighbourWest());
            MyRectangle rectWest = new MyRectangle(westX, centerY, RECT_WIDTH, RECT_HEIGHT, westRoom);

            //draw connecting line
            MyRectangle link = new MyRectangle((int) (rectWest.getX() + rectWest.getWidth()), (int) rectWest.getCenterY(),
                    (int) (origin.getX() - (int) (rectWest.getX() + rectWest.getWidth())), 1, null);

            if (!shapes.contains(link))
                shapes.add(link);

            if (!processed.contains(westRoom))
            {
                processRoom(westRoom, westX, centerY, processed);
            }

        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_WIDTH, PREF_HEIGHT);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (shapes != null){
            for (Shape s : shapes) {

                MyRectangle rect = (MyRectangle) s;

                if (rect.getRoom() != null)
                {
                    if (world.getPlayer().getRoom() == rect.getRoom()){
                        g2d.setPaint(Color.RED);
                    } else {
                        g2d.setPaint(Color.BLACK);
                    }

                    String text = rect.getRoom().getName();

                    Font font = new Font("Arial", Font.BOLD, 12);
                    g2d.setFont(font);

                    // Get the FontMetrics
                    FontMetrics metrics = g.getFontMetrics(font);
                    // Determine the X coordinate for the text
                    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
                    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
                    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
                    // Set the font
                    g.setFont(font);

                    // Draw the String
                    if (showId) {
                        text = String.format("%s(%d)", rect.getRoom().getName(), rect.getRoom().getId());
                    }

                    g.drawString(text, x, y);

                    //Draw the Rect
                    g2d.draw(rect);
                } else {
                    g2d.setPaint(Color.BLACK);

                    //Draw the Rect
                    g2d.draw(rect);
                }

            }
        }


    }
}

class PanelInfo extends JPanel implements PropertyChangeListener {

    private World world;
    private Entity player;
    private DefaultListModel listActions;
    private JTextArea roomDetails;
    private DefaultListModel listPlayerItems;

    public void setWorld(World world){

        this.removeAll();
        this.revalidate();
        this.repaint();

        this.world = world;
        this.player = world.getPlayer();
        this.setLayout(new GridLayout(3,1));
        this.world.addPropertyChangeListener(this);
        this.player.addPropertyChangeListener(this);

        Border blackline = BorderFactory.createLineBorder(Color.black);
        JPanel panActionLog = new JPanel(new BorderLayout());
        panActionLog.setBorder(blackline);
        JLabel lblActions = new JLabel("Activity Log:");
        Font boldFont = new Font("Verdana", Font.BOLD | Font.ITALIC, 14);
        lblActions.setFont(boldFont);
        this.add(lblActions);
        listActions = new DefaultListModel();
        JList<String> lActions = new JList<>( listActions );
        JScrollPane listScrollerActions = new JScrollPane(lActions);
        panActionLog.add(lblActions, BorderLayout.NORTH);
        panActionLog.add(listScrollerActions, BorderLayout.CENTER);
        this.add(panActionLog);

        JPanel panRoomDetails = new JPanel(new BorderLayout());
        panRoomDetails.setBorder(blackline);
        JLabel lblRoomDetails = new JLabel("Room Details:");
        lblRoomDetails.setFont(boldFont);
        this.add(lblRoomDetails);
        roomDetails = new JTextArea(world.getPlayer().getRoom().toString());
        roomDetails.setMargin( new Insets(3,3,3,3) );
        roomDetails.setEditable(false);
        panRoomDetails.add(lblRoomDetails, BorderLayout.NORTH);
        panRoomDetails.add(roomDetails, BorderLayout.CENTER);
        this.add(panRoomDetails);


        JPanel panPlayerInventory = new JPanel(new BorderLayout());
        panPlayerInventory.setBorder(blackline);
        JLabel lblPlayerInventory = new JLabel("Player Inventory:");
        lblPlayerInventory.setFont(boldFont);
        listPlayerItems = new DefaultListModel();
        JList<String> lPlayerItems = new JList<>( listPlayerItems );
        JScrollPane listScrollerPlayerItems = new JScrollPane(lPlayerItems);
        listPlayerItems.addAll(player.getInventory());
        panPlayerInventory.add(lblPlayerInventory, BorderLayout.NORTH);
        panPlayerInventory.add(listScrollerPlayerItems, BorderLayout.CENTER);
        this.add(panPlayerInventory);

    }


    public PanelInfo(){
        this.setMinimumSize(new Dimension(0,0));
    }

    public void propertyChange(PropertyChangeEvent evt) {
        listActions.addElement(evt.getPropertyName());
        roomDetails.setText(world.getPlayer().getRoom().toString());
        listPlayerItems.clear();
        listPlayerItems.addAll(player.getInventory());

    }
}

class MyRectangle extends Rectangle {

    private World world;
    public World getWorld(){
        return this.world;
    }
    public void setWorld(World world){
        this.world = world;
    }
    private Room room;
    public Room getRoom(){
        return this.room;
    }
    public void setRoom(Room room){
        this.room = room;
    }

    public MyRectangle(int x, int y, int width, int height, Room room){
        super(x, y, width, height);
        this.setRoom(room);
    }
}