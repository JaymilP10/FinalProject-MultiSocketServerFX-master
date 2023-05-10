package com.jtconnors.multisocketserverfx;

import com.jtconnors.socket.SocketListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;

import com.jtconnors.socketfx.FxMultipleSocketServer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 *
 * @author jtconnor
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField sendTextField;
    @FXML
    private TextField selectedTextField;
    @FXML
    private Button sendButton;
    @FXML
    private Label connectionsLabel, lblPickLoadout;
    @FXML
    private Button connectButton, btnEnterName, btnFindMatch, btnReady;
    @FXML
    private Button disconnectButton;
    @FXML
    private Label lblPort;
    @FXML
    private TextField portTextField, txtName;
    @FXML
    private ListView<String> rcvdMsgsListView;
    private ObservableList<String> sentMsgsData;
    @FXML
    private ListView<String> sentMsgsListView;
    private ObservableList<String> rcvdMsgsData;

    private FxMultipleSocketServer socketServer;
    private ListView<String> lastSelectedListView;
    private Tooltip portTooltip;

    @FXML
    private ListView lstPrimaryWeapon, lstSecondaryWeapon, lstItems;

    @FXML
    private ScrollPane scrollPane;

    Button[][] buttons = new Button[50][100];

    Button[][] displayButtons = new Button[26][26];
//    Button[][] displayButtons = new Button[27][27];

    Map[][] map = new Map[50][100];

    public enum ConnectionDisplayState {

        DISCONNECTED, WAITING, CONNECTED
    }

    private void displayState(ConnectionDisplayState state) {
        switch (state) {
            case DISCONNECTED:
                btnFindMatch.setDisable(false);
                disconnectButton.setDisable(true);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
                connectionsLabel.setText("Not connected");
                break;
            case WAITING:
                btnFindMatch.setDisable(true);
                disconnectButton.setDisable(false);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
                connectionsLabel.setText("Waiting for connections (1/8)");
                break;
            case CONNECTED:
                btnFindMatch.setDisable(true);
                disconnectButton.setDisable(false);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
                int numConnections = 1;
                numConnections += socketServer.getListenerCount();
                StringBuilder connectionsSB = new StringBuilder("Waiting for players (" + numConnections + "/8)");
                connectionsLabel.setText("Waiting for players (" + numConnections + "/8)");
                socketServer.postUpdate("NumConnections" + numConnections);
                if (numConnections == 2) {
                    connectionsLabel.setVisible(false);
                    txtName.setVisible(true);
                    btnFindMatch.setDisable(true);
                    btnEnterName.setDisable(true);
                    btnEnterName.setVisible(false);
                    btnFindMatch.setVisible(false);
                    btnReady.setVisible(true);
                    lblPickLoadout.setVisible(true);
                    lstPrimaryWeapon.setVisible(true);
                    lstSecondaryWeapon.setVisible(true);
                    lstItems.setVisible(true);
//                    scrollPane.setVisible(false);
                    Weapon LMG = new Weapon("LMG", "MachineGun", 25, 10, 100, .1);
                    Weapon RPG = new Weapon("RPG", "Rocket Launcher", 100, 400, 5, .25);
                    Weapon AR = new Weapon("AR", "Assault Rifle", 40, 50, 30, .25);
                    Weapon Sniper = new Weapon("Sniper", "Sniper", 100, 200, 10, .25);
                    weapons.add(LMG);
                    weapons.add(RPG);
                    weapons.add(AR);
                    weapons.add(Sniper);
                    lstPrimaryWeapon.getItems().add(LMG.weaponName);
                    lstPrimaryWeapon.getItems().add(AR.weaponName);
                    lstSecondaryWeapon.getItems().add(RPG.weaponName);
                    lstSecondaryWeapon.getItems().add(Sniper.weaponName);
//                    connectionsSB.append("s");
                }
//                connectionsLabel.setText(new String(connectionsSB));
                break;
        }
    }

    ArrayList<Weapon> weapons = new ArrayList<>();
    Weapon primaryWeapon = new Weapon();
    Weapon secondaryWeapon = new Weapon();
    Weapon currentlyUsingWeapon = new Weapon();
    int numPlayersReady = 0;

    ArrayList<Player> players = new ArrayList<>();

    Monsters dragon;

    int frame = 0;

    private String playerName;
    Player player;

    @FXML
    private void enterName(){
        playerName = txtName.getText();
        btnFindMatch.setVisible(true);
        btnFindMatch.setDisable(false);

    }

    @FXML
    private void pickLoadout(ActionEvent event){
        String primaryWeaponName = lstPrimaryWeapon.getSelectionModel().getSelectedItem().toString();
        String secondaryWeaponName = lstSecondaryWeapon.getSelectionModel().getSelectedItem().toString();
        for (Weapon weapon : weapons) {
            if (weapon.weaponName.equals(primaryWeaponName))
                primaryWeapon = weapon;
            else if (weapon.weaponName.equals(secondaryWeaponName))
                secondaryWeapon = weapon;
        }
        currentlyUsingWeapon = primaryWeapon;
        btnReady.setDisable(true);
        btnReady.setVisible(false);
        numPlayersReady++;
        player = new Player(playerName, 1, 250, 25, .5, 5, 23, map);
        player.primary = primaryWeapon;
        player.secondary = secondaryWeapon;
        updateScreen();
        players.add(player);
        socketServer.postUpdate("Create Player:" + playerName);
        socketServer.postUpdate("Ready" + numPlayersReady);
        if (numPlayersReady == 2){
            lstPrimaryWeapon.setVisible(false);
            lstSecondaryWeapon.setVisible(false);
            lstItems.setVisible(false);
//            scrollPane.setVisible(true);
            txtName.setVisible(false);
            btnEnterName.setVisible(false);
            lblPickLoadout.setVisible(false);
            MAP.setVisible(true);
            start();
        }
    }

    @FXML
    private void move(KeyEvent keyEvent){
        System.out.println("works");
        KeyCode key = keyEvent.getCode();
        System.out.println("Key Pressed: " + key);
//        if (keyEvent.getCode().equals(KeyCode.D) || keyEvent.getCode().equals(KeyCode.A) || keyEvent.getCode().equals(KeyCode.W) || key  == KeyCode.S || key == KeyCode.Q)
        if (keyEvent.getCode().equals(KeyCode.D) && player.xLoc < 99) {  // left arrow key
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.xLoc++;
            socketServer.postUpdate("Move Player Right:" + playerName);
        }
        else if (keyEvent.getCode().equals(KeyCode.A) && player.xLoc > 0 && player.xLoc - 1 > 1) {
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            System.out.println("rk");
            player.xLoc--;
            socketServer.postUpdate("Move Player Left:" + playerName);
        }
        else if (keyEvent.getCode().equals(KeyCode.W) && player.yLoc > 0 && player.yLoc - 1 > 1) {
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            System.out.println("w");
            player.yLoc--;
            socketServer.postUpdate("Move Player Upp:" + playerName);
        }
        else if (key == KeyCode.S && player.yLoc < 49) {
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc++;
            socketServer.postUpdate("Move Player Downn:" + playerName);
        } else if (key == KeyCode.Q && player.yLoc > 0 && player.xLoc > 0 && player.yLoc - 1 > 1 && player.xLoc - 1 > 1){
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc--;
            player.xLoc--;
            socketServer.postUpdate("Move Player Up-left:" + playerName);
        } else if (key == KeyCode.E && player.yLoc > 0 && player.xLoc < 99 && player.yLoc - 1 > 1){
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc--;
            player.xLoc++;
            socketServer.postUpdate("Move Player Up-right:" + playerName);
        } else if (key == KeyCode.Z && player.yLoc < 99 && player.xLoc > 0 && player.xLoc - 1 > 1){
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc++;
            player.xLoc--;
            socketServer.postUpdate("Move Player Down-left:" + playerName);
        } else if (key == KeyCode.C && player.yLoc < 49 && player.xLoc < 99){
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc++;
            player.xLoc++;
            socketServer.postUpdate("Move Player Down-right:" + playerName);
        }
        map[player.yLoc - 1][player.xLoc - 1].newNum = 6;
        map[player.yLoc - 1][player.xLoc].newNum = 6;
        map[player.yLoc][player.xLoc - 1].newNum = 6;
        map[player.yLoc][player.xLoc].newNum = 6;
        updateScreen();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        displayState(ConnectionDisplayState.DISCONNECTED);
//        sentMsgsData = FXCollections.observableArrayList();
//        sentMsgsListView.setItems(sentMsgsData);
//        sentMsgsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        sentMsgsListView.setOnMouseClicked((Event event) -> {
//            String selectedItem
//                    = sentMsgsListView.getSelectionModel().getSelectedItem();
//            if (selectedItem != null && !selectedItem.equals("null")) {
//                selectedTextField.setText("Sent: " + selectedItem);
//                lastSelectedListView = sentMsgsListView;
//            }
//        });
//
//        rcvdMsgsData = FXCollections.observableArrayList();
//        rcvdMsgsListView.setItems(rcvdMsgsData);
//        rcvdMsgsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        rcvdMsgsListView.setOnMouseClicked((Event event) -> {
//            String selectedItem
//                    = rcvdMsgsListView.getSelectionModel().getSelectedItem();
//            if (selectedItem != null && !selectedItem.equals("null")) {
//                selectedTextField.setText("Received: " + selectedItem);
//                lastSelectedListView = rcvdMsgsListView;
//            }
//        });

        portTooltip = new Tooltip("Port number cannot be modified once\n" +
        "the first connection attempt is initiated.\n" +
        "Restart application in order to change.");

        portTextField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                Integer.parseInt(newText);
            } catch (NumberFormatException e) {
                portTextField.setText(oldText);
            }
        });

        MAP.setPadding(new Insets(0));
        MAP.setHgap(0);
        MAP.setVgap(0);
//        MAP.setGridLinesVisible(true);

        for (int i = 0; i < displayButtons.length; i++) {
            for (int j = 0; j < displayButtons[0].length; j++) {
                displayButtons[i][j] = new Button();
                displayButtons[i][j].setPrefHeight(25);
                displayButtons[i][j].setPrefWidth(25);
                displayButtons[i][j].setMaxSize(50, 50);
                MAP.add(displayButtons[i][j], j, i);
            }
        }

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                buttons[i][j] = new Button();
//                        buttons[i][j].setPrefSize(10, 10);
                buttons[i][j].setPrefHeight(50);
                buttons[i][j].setPrefWidth(50);
                buttons[i][j].setMaxSize(50, 50);

                map[i][j] = new Map(i, j, 4, false);

                if (i > 20 && i < 30 && j < 10){
                    map[i][j].Orignum = 1;
                    map[i][j].newNum = 1;
                } else if (i > 20 && i < 30 && j > 89){
                    map[i][j].Orignum = 2;
                    map[i][j].newNum = 2;
                } else if ((i < 10 && j >= 20 && j <= 79) || (i > 39 && j >= 20 && j <= 79)){
                    map[i][j].Orignum = 3;
                    map[i][j].newNum = 3;
                }
//                MAP.add(buttons[i][j], j, i);
            }
        }

        for (int i = 10; i <= 39; i++) {
            for (int j = 45; j < 55; j++) {
                map[i][j].Orignum = 1;
                map[i][j].newNum = 1;
            }
        }

        int x = 10;
        for (int i = 30; i <= 39; i++) {
            for (int k = 0; k <= 9; k++) {
//                map[i + k][j] = new Map(i + k, j, 3, false);
//                map[i - k][j] = new Map(i - k, j, 3, false);
                map[i + k][x].Orignum = 3;
                map[i + k][x].newNum = 3;
                map[i - k][x].Orignum = 3;
                map[i - k][x].newNum = 3;
                map[i][x + k].Orignum = 3;
                map[i][x + k].newNum = 3;
                map[i][x - k].Orignum = 3;
                map[i][x - k].newNum = 3;
//                map[i + k][j].isWall = false;
//                map[i - k][j].isWall = false;
            }
            x++;
        }

        int j = 89;
        for (int i = 20; i >= 10; i--) {
            for (int k = 0; k <= 9; k++) {
//                map[i + k][j] = new Map(i + k, j, 3, false);
//                map[i - k][j] = new Map(i - k, j, 3, false);
                map[i + k][j].Orignum = 3;
                map[i + k][j].newNum = 3;
                map[i - k][j].Orignum = 3;
                map[i - k][j].newNum = 3;
                map[i][j + k].Orignum = 3;
                map[i][j + k].newNum = 3;
                map[i][j - k].Orignum = 3;
                map[i][j - k].newNum = 3;
//                map[i + k][j].isWall = false;
//                map[i - k][j].isWall = false;
            }
            j--;
        }

        int y = 89;
        for (int i = 30; i <= 39; i++) {
            for (int k = 0; k <= 9; k++) {
//                map[i + k][j] = new Map(i + k, j, 3, false);
//                map[i - k][j] = new Map(i - k, j, 3, false);
                map[i + k][y].Orignum = 3;
                map[i + k][y].newNum = 3;
                map[i - k][y].Orignum = 3;
                map[i - k][y].newNum = 3;
                map[i][y + k].Orignum = 3;
                map[i][y + k].newNum = 3;
                map[i][y - k].Orignum = 3;
                map[i][y - k].newNum = 3;
//                map[i + k][j].isWall = false;
//                map[i - k][j].isWall = false;
            }
            y--;
        }

        int z = 10;
        for (int i = 20; i >= 10; i--) {
            for (int k = 0; k <= 9; k++) {
//                map[i + k][j] = new Map(i + k, j, 3, false);
//                map[i - k][j] = new Map(i - k, j, 3, false);
                map[i + k][z].Orignum = 3;
                map[i + k][z].newNum = 3;
                map[i - k][z].Orignum = 3;
                map[i - k][z].newNum = 3;
                map[i][z + k].Orignum = 3;
                map[i][z + k].newNum = 3;
                map[i][z - k].Orignum = 3;
                map[i][z - k].newNum = 3;
//                map[i + k][j].isWall = false;
//                map[i - k][j].isWall = false;
            }
            z++;
        }

        dragon = new Monsters(50, 500, 10, .25, 43, 32, buttons);
    }

    public void start(){
        updateScreen();
        System.out.println("called start");
        new AnimationTimer(){
            @Override
            public void handle(long now) {
                if(startTime>0){
                    if (now - startTime > (900000000.0 * .1)) {
//                        System.out.println("ANIMATION TIMER IS WORKING");
                        if (frame < 9) {
                            frame++;
                        } else if (frame == 9) {
                            frame = 1;
                        }
                        dragon.changeImage(buttons, frame);
                        startTime = System.nanoTime();
                    }
                }
            }
        }.start();

        System.out.println("here");

        EventHandler<MouseEvent> z = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked something");
                if (event.getButton() == MouseButton.PRIMARY){
                    System.out.println("clicked primary");
                    Bullets bullet = new Bullets(player.xLoc, player.yLoc);
                    currentlyUsingWeapon.squaresTravelled = 0;
                    for (int i = 0; i < 26; i++) {
                        for (int j = 0; j < 26; j++) {
                            if (((Button) event.getSource()) == displayButtons[i][j]){
//                                int rowTo = player.yLoc + (i - (player.yLoc + 13));
//                                int colTo = player.xLoc + (j - (player.xLoc + 13));

                                int rowTo = player.yLoc + (i - 13);
                                int colTo = player.xLoc + (j - 13);

                                socketServer.postUpdate("Player shot:" + playerName + "r:" + rowTo + "c:" + colTo);

//                                int finalJ = j;
//                                int finalI = i;
                                new AnimationTimer(){
                                    @Override
                                    public void handle(long now) {
//                                        System.out.println("in animation timer");
                                        if (currentlyUsingWeapon.startTime > 0){
//                                            System.out.println("lollolololol");
                                            if (now - currentlyUsingWeapon.startTime > (900000000.0 * 2) && currentlyUsingWeapon.squaresTravelled < currentlyUsingWeapon.range){
                                                System.out.println("range: " + currentlyUsingWeapon.range);
                                                bullet.fire(colTo, rowTo, buttons, map, this);
                                                updateScreen();
                                                bullet.startTime = System.nanoTime();
                                            } else {
                                                this.stop();
                                            }
                                        }
                                    }
                                }.start();
                                System.out.println(i + " " + j);
//                            System.out.println("oc:"+i+"or:"+j);

                            }
                        }
                    }
                } else if (event.getButton() == MouseButton.SECONDARY){
                    System.out.println("clicked secondary");
                }
            }
        };
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
//                btn[i][j].setOnMouseClicked(z);
                displayButtons[i][j].setOnMouseClicked(z);
            }
        }
//            EventHandler<MouseEvent> z = new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    //all button code goes here
//                    for (int i = 0; i < 50; i++) {
//                        for (int j = 0; j < 100; j++) {
//                            if (((Button) event.getSource()) == buttons[i][j]){
////                                System.out.println("oc:"+i+"or:"+j);
//                                player.changeLoc(map, i, j);
//                                socketServer.postUpdate("PlayerMoved:" + playerName + "i:" + i + "j:" + j);
//                                startTime = System.nanoTime();
//                                System.out.println(startTime);
//                                System.out.println("end");
//                            }
//                        }
//                    }
//                }
//            };
//            for (int i = 0; i < 5; i++) {
//                for (int j = 0; j < 4; j++) {
////                btn[i][j].setOnMouseClicked(z);
//                    buttons[i][j].setOnMouseClicked(z);
//                }
//            }



    }

    private void fire(MouseEvent event){


    }


    private double startTime = System.nanoTime();
//    private void inGame(ActionEvent event){
//        EventHandler<MouseEvent> z = new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                //all button code goes here
//                for (int i = 0; i < 5; i++) {
//                    for (int j = 0; j < 4; j++) {
//                        if (((Button) event.getSource()) == buttons[i][j]){
////                            System.out.println("oc:"+i+"or:"+j);
//                            startTime = System.nanoTime();
//                            new AnimationTimer(){
//                                @Override
//                                public void handle(long now) {
//                                    if(startTime>0){
//                                        if (now - startTime > (900000000.0 * 2)){
//                                            this.stop();
//                                        }
//                                    }
//                                }
//                            }.start();
//                        }
//                    }
//                }
//            }
//        };
//    }

    @FXML
    private void handleClearRcvdMsgsButton(ActionEvent event) {
        rcvdMsgsData.clear();
        if (lastSelectedListView == rcvdMsgsListView) {
            selectedTextField.clear();
        }
    }

    @FXML
    private void handleClearSentMsgsButton(ActionEvent event) {
        sentMsgsData.clear();
        if (lastSelectedListView == sentMsgsListView) {
            selectedTextField.clear();
        }
    }

    @FXML
    private void handleSendMessageButton(ActionEvent event) {
        if (!sendTextField.getText().equals("")) {
            socketServer.postUpdate(sendTextField.getText());
            sentMsgsData.add(sendTextField.getText());
        }
    }

    @FXML
    private void handleConnectButton(ActionEvent event) {
        displayState(ConnectionDisplayState.WAITING);
        portTextField.setEditable(false);
        portTextField.setTooltip(portTooltip);
        socketServer = new FxMultipleSocketServer(new FxSocketListener(),
                Integer.valueOf(portTextField.getText()));
        new Thread(socketServer).start();
    }

    @FXML
    private void handleDisconnectButton(ActionEvent event) {
        socketServer.shutdown();
    }

//    ImageView[][] map = new ImageView[200][100];

    @FXML
    private GridPane MAP;

    private void updateScreen(){

//        for (int i = 0; i < buttons.length; i++) {
//            for (int j = 0; j < buttons[0].length; j++) {
//                map[i][j].num = 4;
//                if (i > 20 && i < 30 && j < 10){
//                    map[i][j].num = 1;
//                } else if (i > 20 && i < 30 && j > 89){
//                    map[i][j].num = 2;
//                } else if ((i < 10 && j >= 20 && j <= 79) || (i > 39 && i < 49 && j >= 20 && j <= 79)){
//                    map[i][j].num = 3;
//                }
//            }
//        }
//
//        for (int i = 10; i <= 39; i++) {
//            for (int j = 45; j < 55; j++) {
//                map[i][j].num = 1;
//            }
//        }
//
//        int x = 10;
//        for (int i = 30; i <= 39; i++) {
//            for (int k = 0; k <= 9; k++) {
//                map[i + k][x].num = 3;
//                map[i - k][x].num = 3;
//                map[i][x + k].num = 3;
//                map[i][x - k].num = 3;
//            }
//            x++;
//        }
//
//        int j = 89;
//        for (int i = 20; i >= 10; i--) {
//            for (int k = 0; k <= 9; k++) {
//                map[i + k][j].num = 3;
//                map[i - k][j].num = 3;
//                map[i][j + k].num = 3;
//                map[i][j - k].num = 3;
//            }
//            j--;
//        }
//
//        int y = 89;
//        for (int i = 30; i <= 39; i++) {
//            for (int k = 0; k <= 9; k++) {
//                map[i + k][y].num = 3;
//                map[i - k][y].num = 3;
//                map[i][y + k].num = 3;
//                map[i][y - k].num = 3;
//            }
//            y--;
//        }
//
//        int z = 10;
//        for (int i = 20; i >= 10; i--) {
//            for (int k = 0; k <= 9; k++) {
//                map[i + k][z].num = 3;
//                map[i - k][z].num = 3;
//                map[i][z + k].num = 3;
//                map[i][z - k].num = 3;
//            }
//            z++;
//        }

        for (Player player : players) {
            map[player.yLoc][player.xLoc].newNum = 6;
            map[player.yLoc - 1][player.xLoc - 1].newNum = 6;
            map[player.yLoc - 1][player.xLoc].newNum = 6;
            map[player.yLoc][player.xLoc - 1].newNum = 6;
        }

        for (int i = 0; i < map.length; i++) {
            for (int c = 0; c < map[0].length; c++) {
                if (map[i][c].newNum == 1){
                    buttons[i][c].setStyle("-fx-background-color: blue");
                } else if (map[i][c].newNum == 2){
                    buttons[i][c].setStyle("-fx-background-color: red");
                } else if (map[i][c].newNum == 3){
                    buttons[i][c].setStyle("-fx-background-color: yellow");
                } else if (map[i][c].newNum == 4){
                    buttons[i][c].setStyle("-fx-background-color: green");
                } else if (map[i][c].newNum == 5){
                    buttons[i][c].setStyle("-fx-background-color: black");
                } else if (map[i][c].newNum == 6){
                    buttons[i][c].setStyle("-fx-background-color: grey");
                } else if (map[i][c].newNum == 7){
                    buttons[i][c].setStyle("-fx-background-color: brown");
                }
            }
        }

        for (int i = 0; i < 13; i++) {
            for (int k = 0; k < 13; k++) {
                if (player.yLoc - (13 - i) > 0 && player.xLoc - (13 - k) > 0){
                    displayButtons[i][k].setStyle(buttons[player.yLoc - (13 - i)][player.xLoc - (13 - k)].getStyle());
                    displayButtons[i][k].setGraphic(buttons[player.yLoc - (13 - i)][player.xLoc - (13 - k)].getGraphic());
                } else {
                    displayButtons[i][k].setStyle("-fx-background-color: black");
                }
            }
        }

        for (int i = 0; i < 13; i++) {
            for (int k = 0; k < 13; k++) {
                if (player.yLoc - (13 - i) > 0 && player.xLoc + k < 99){
                    displayButtons[i][k + 13].setStyle(buttons[player.yLoc - (13 - i)][player.xLoc + k].getStyle());
                    displayButtons[i][k + 13].setGraphic(buttons[player.yLoc - (13 - i)][player.xLoc + k].getGraphic());
                } else {
                    displayButtons[i][k + 13].setStyle("-fx-background-color: black");
                }
            }
        }

        for (int i = 0; i < 13; i++) {
            for (int k = 0; k < 13; k++) {
                if (player.yLoc + i < 49 && player.xLoc + k < 99){
                    displayButtons[i + 13][k + 13].setStyle(buttons[player.yLoc + i][player.xLoc + k].getStyle());
                    displayButtons[i + 13][k + 13].setGraphic(buttons[player.yLoc + i][player.xLoc + k].getGraphic());
                } else {
                    displayButtons[i + 13][k + 13].setStyle("-fx-background-color: black");
                }
            }
        }

        for (int i = 0; i < 13; i++) {
            for (int k = 0; k < 13; k++) {
                if (player.yLoc + i < 49 && player.xLoc - (13 - k) > 0){
                    displayButtons[i + 13][k].setStyle(buttons[player.yLoc + i][player.xLoc - (13 - k)].getStyle());
                    displayButtons[i + 13][k].setGraphic(buttons[player.yLoc + i][player.xLoc - (13 - k)].getGraphic());
                } else {
                    displayButtons[i + 13][k].setStyle("-fx-background-color: black");
                }
            }
        }
    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
//            if (line != null && !line.equals("")) {
//                rcvdMsgsData.add(line);
//            } else
            if (line.startsWith("Ready")){
                numPlayersReady++;
                socketServer.postUpdate("Ready" + numPlayersReady);
                if (numPlayersReady == 2){
                    btnReady.setVisible(false);
                    lstPrimaryWeapon.setVisible(false);
                    lstSecondaryWeapon.setVisible(false);
                    lstItems.setVisible(false);
//                    scrollPane.setVisible(true);
                    txtName.setVisible(false);
                    btnEnterName.setVisible(false);
                    lblPickLoadout.setVisible(false);
                    start();
                }
            } else if (line.startsWith("Create Player:")){
                players.add(new Player(line.substring(line.indexOf(":") + 1), 1, 250, 25, .5, 5, 23, map));
//                players.get(players.size() - 1).primary = primaryWeapon;
//                updateScreen();
            } else if (line.startsWith("Move Player Left:")){
                for (Player player : players) {
                    if (player.name.equals(line.substring(line.indexOf(":") + 1))){
                        map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
                        map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
                        map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
                        map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
                        player.xLoc--;
                    }
                }
            } else if (line.startsWith("Move Player Right:")){
                for (Player player : players) {
                    if (player.name.equals(line.substring(line.indexOf(":") + 1))){
                        map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
                        map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
                        map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
                        map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
                        player.xLoc++;
                    }
                }
            } else if (line.startsWith("Move Player Downn")){
                for (Player player : players) {
                    if (player.name.equals(line.substring(line.indexOf(":") + 1))){
                        map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
                        map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
                        map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
                        map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
                        player.yLoc++;
                    }
                }
            } else if (line.startsWith("Move Player Upp")){
                for (Player player : players) {
                    if (player.name.equals(line.substring(line.indexOf(":") + 1))){
                        map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
                        map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
                        map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
                        map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
                        player.yLoc--;
                    }
                }
            } else if (line.startsWith("Move Player Down-right")){
                for (Player player : players) {
                    if (player.name.equals(line.substring(line.indexOf(":") + 1))){
                        map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
                        map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
                        map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
                        map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
                        player.yLoc++;
                        player.xLoc++;
                    }
                }
            } else if (line.startsWith("Move Player Down-left")){
                for (Player player : players) {
                    if (player.name.equals(line.substring(line.indexOf(":") + 1))){
                        map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
                        map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
                        map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
                        map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
                        player.yLoc++;
                        player.xLoc--;
                    }
                }
            } else if (line.startsWith("Move Player Up-right")){
                for (Player player : players) {
                    if (player.name.equals(line.substring(line.indexOf(":") + 1))){
                        map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
                        map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
                        map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
                        map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
                        player.yLoc--;
                        player.xLoc++;
                    }
                }
            } else if (line.startsWith("Move Player Up-left")){
                for (Player player : players) {
                    if (player.name.equals(line.substring(line.indexOf(":") + 1))){
                        map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
                        map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
                        map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
                        map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
                        player.yLoc--;
                        player.xLoc--;
                    }
                }
            } else if (line.startsWith("Player shot")){
                int rowTo = Integer.parseInt(line.substring(line.indexOf("r:") + 2, line.indexOf("c")));
                int colTo = Integer.parseInt(line.substring(line.indexOf("c:") + 2));
                String playerName = line.substring(line.indexOf("shot:") + 5, line.indexOf("r:"));

                for (Player player : players) {
                    if (player.name.equals(playerName)){
                        Bullets bullet = new Bullets(player.xLoc, player.yLoc);
                        new AnimationTimer(){
                            @Override
                            public void handle(long now) {
//                                        System.out.println("in animation timer");
                                if (currentlyUsingWeapon.startTime > 0){
//                                            System.out.println("lollolololol");
                                    if (now - currentlyUsingWeapon.startTime > (900000000.0 * 2) && currentlyUsingWeapon.squaresTravelled < currentlyUsingWeapon.range){
                                        System.out.println("range: " + currentlyUsingWeapon.range);
                                        bullet.fire(colTo, rowTo, buttons, map, this);
                                        updateScreen();
                                        bullet.startTime = System.nanoTime();
                                    } else {
                                        this.stop();
                                    }
                                }
                            }
                        }.start();
                    }
                }
            }
            updateScreen();
            updateScreen();
        }

        @Override
        public void onClosedStatus(boolean isClosed) {
            if (socketServer.isServerSocketClosed()) {
                displayState(ConnectionDisplayState.DISCONNECTED);
            } else {
                displayState(ConnectionDisplayState.CONNECTED);
            }
        }
    }
}
