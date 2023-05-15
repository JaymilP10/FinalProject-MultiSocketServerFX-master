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
    private Button connectButton, btnEnterName, btnFindMatch, btnReady, btnBuyItems;
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
    private ListView lstPrimaryWeapon, lstSecondaryWeapon, lstInventory, lstStats, lstStore, lstHealth;

    @FXML
    private ProgressBar pbPlayerHealth;

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
                    lstStats.setVisible(true);
                    pbPlayerHealth.setVisible(true);
                    pbPlayerHealth.setProgress(1);
//                    scrollPane.setVisible(false);
                    Weapon Shotgun = new Weapon("Shotgun", "Shotgun", 25, 10, 100, .1);
                    Weapon RPG = new Weapon("RPG", "Rocket Launcher", 100, 400, 5, .25);
                    Weapon AR = new Weapon("AR", "Assault Rifle", 40, 50, 30, .25);
                    Weapon Sniper = new Weapon("Sniper", "Sniper", 100, 200, 10, .25);
                    weapons.add(Shotgun);
                    weapons.add(RPG);
                    weapons.add(AR);
                    weapons.add(Sniper);
                    lstPrimaryWeapon.getItems().add(Shotgun.weaponName);
                    lstPrimaryWeapon.getItems().add(AR.weaponName);
                    lstSecondaryWeapon.getItems().add(RPG.weaponName);
                    lstSecondaryWeapon.getItems().add(Sniper.weaponName);

                    Items grenade = new Items("Grenade", 15, 300, 200);
                    Items shieldPotion = new Items("Shield Potion", 150);
                    Items ammo = new Items("Ammo", 50);
                    shieldPotion.shield = 100;
                    Items smite = new Items("Smite", 200);
                    items.add(grenade);
                    items.add(shieldPotion);
                    items.add(smite);

                    lstStore.getItems().clear();

                    for (Items item : items) {
                        if (item.name.equals("Grenade"))
                            lstStore.getItems().add(item.name + ";Damage: " + item.damage + ";Price: " + item.price);
                        if (item.name.equals("Shield Potion"))
                            lstStore.getItems().add(item.name + ";Shield: " + item.shield + ";Price: " + item.price);
                        if (item.name.equals("Ammo"))
                            lstStore.getItems().add(item.name + "; Price: " + ammo.price + " Amt: " + 30);

                    }
//                    connectionsSB.append("s");
                }
//                connectionsLabel.setText(new String(connectionsSB));
                break;
        }
    }

    ArrayList<Weapon> weapons = new ArrayList<>();
    Bullets[] bullets = new Bullets[100];
    Weapon primaryWeapon = new Weapon();
    Weapon secondaryWeapon = new Weapon();
    Weapon currentlyUsingWeapon = new Weapon();

    ArrayList<Items> items = new ArrayList<>();
    Items currentlyUsingItem = new Items();

    ArrayList<Turrets> blueTurrets = new ArrayList<>();
    Turrets blueTopFrontTurret;
    Turrets blueTopMidTurret;
    Turrets blueTopInhibitorTurret;
    Turrets blueBotFrontTurret;
    Turrets blueBotMidTurret;
    Turrets blueBotInhibitorTurret;

    ArrayList<Turrets> redTurrets = new ArrayList<>();
    Turrets redTopFrontTurret;
    Turrets redTopMidTurret;
    Turrets redTopInhibitorTurret;
    Turrets redBotFrontTurret;
    Turrets redBotMidTurret;
    Turrets redBotInhibitorTurret;

    int numPlayersReady = 0;

    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Monsters> monsters = new ArrayList<>();

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
        player = new Player(playerName, 1, 500, 25, .5, 5, 23, map, "blue");
        player.gold = 10000;
        player.primary = primaryWeapon;
        player.secondary = secondaryWeapon;
        lstInventory.getItems().clear();
        lstInventory.getItems().add("Inventory:");
        lstInventory.getItems().add("Gold:" + player.gold);
        lstInventory.getItems().add(primaryWeapon.weaponName + " ; Damage:" + primaryWeapon.damage);
        lstInventory.getItems().add(secondaryWeapon.weaponName + "; Damage:" + secondaryWeapon.damage);
        updateScreen();
        players.add(player);
        socketServer.postUpdate("Create Player:" + playerName + "team:" + player.team + "x:" + player.xLoc + "y:" + player.yLoc);
        socketServer.postUpdate("Ready" + numPlayersReady);
        if (numPlayersReady == 2){
            lstPrimaryWeapon.setVisible(false);
            lstSecondaryWeapon.setVisible(false);
            lstStats.setVisible(false);
            txtName.setVisible(false);
            btnEnterName.setVisible(false);
            btnFindMatch.setVisible(false);
            pbPlayerHealth.setVisible(true);
            lblPickLoadout.setVisible(false);
            MAP.setVisible(true);
            lstStore.setVisible(true);
            lstInventory.setVisible(true);
            lstHealth.setVisible(true);
            btnBuyItems.setVisible(true);
            start();
        }
    }

    @FXML
    private void showStatsPrimary(){
        lstStats.getItems().clear();
        String weaponSelected = lstPrimaryWeapon.getSelectionModel().getSelectedItem().toString();
        for (Weapon weapon : weapons) {
            if (weapon.weaponName.equals(weaponSelected)){
                lstStats.getItems().add(weapon.weaponName);
                lstStats.getItems().add("Range: " + weapon.range);
                lstStats.getItems().add("Damage: " + weapon.damage);
            }
        }
    }

    @FXML
    private void showStatsSecondary(){
        lstStats.getItems().clear();
        String weaponSelected = lstSecondaryWeapon.getSelectionModel().getSelectedItem().toString();
        for (Weapon weapon : weapons) {
            if (weapon.weaponName.equals(weaponSelected)){
                lstStats.getItems().add(weapon.weaponName);
                lstStats.getItems().add("Range: " + weapon.range);
                lstStats.getItems().add("Damage: " + weapon.damage);
            }
        }
    }

    @FXML
    private void buyItems(){
        String itemBought = lstStore.getSelectionModel().getSelectedItem().toString();
        itemBought = itemBought.substring(0, itemBought.indexOf(";"));
        for (Items item : items) {
            if (item.name.equals(itemBought)){
                if (player.gold >= item.price){
                    System.out.println(player.gold);
                    player.gold -= item.price;
                    System.out.println(player.gold);
                    player.itemsOwned.add(item);
                    System.out.println(item.name);
                    if (item.name.equals("Ammo")){
                        player.ammo += 30;
                    }
                }
            }
        }
        lstInventory.getItems().clear();
        lstInventory.getItems().add("Inventory:");
        lstInventory.getItems().add("Gold:" + player.gold);
        lstInventory.getItems().add(primaryWeapon.weaponName + "; Damage: " + primaryWeapon.damage);
        lstInventory.getItems().add(secondaryWeapon.weaponName + "; Damage: " + secondaryWeapon.damage);
        for (Items item : player.itemsOwned) {
            if (item.name.equals("Grenade"))
                lstInventory.getItems().add(item.name + "; Damage:" + item.damage);
            if (item.name.equals("Ammo"))
                lstInventory.getItems().add(item.name + "; Amt:" + player.ammo);
        }

    }

    @FXML
    private void useItem(){
        String itemUsed = lstInventory.getSelectionModel().getSelectedItem().toString();
        itemUsed = itemUsed.substring(0, itemUsed.indexOf(";"));
        for (Items item : items) {
            if (item.name.equals(itemUsed)){
                player.currentlyUsingItem = item;
                player.isUsingItem = true;
                currentlyUsingItem = item;
            }
        }
        for (Weapon weapon : weapons) {
            if (weapon.weaponName.equals(itemUsed)){
                player.isUsingItem = false;
                player.currentlyUsingWeapon = weapon;
                currentlyUsingWeapon = weapon;
            }
        }
    }

    @FXML
    private void move(KeyEvent keyEvent){
//        System.out.println("works");
        KeyCode key = keyEvent.getCode();
//        System.out.println("Key Pressed: " + key);
//        if (keyEvent.getCode().equals(KeyCode.D) || keyEvent.getCode().equals(KeyCode.A) || keyEvent.getCode().equals(KeyCode.W) || key  == KeyCode.S || key == KeyCode.Q)
        if (keyEvent.getCode().equals(KeyCode.D) && player.xLoc < 99 && (map[player.yLoc][player.xLoc + 1].Orignum != 5 && map[player.yLoc - 1][player.xLoc + 1].newNum != 5 && map[player.yLoc - 1][player.xLoc].Orignum != 5 && map[player.yLoc][player.xLoc].Orignum != 5)) {  // left arrow key
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.xLoc++;
            socketServer.postUpdate("Move Player Right:" + playerName);
        }
        else if (keyEvent.getCode().equals(KeyCode.A) && player.xLoc > 0 && player.xLoc - 1 > 1 && (map[player.yLoc][player.xLoc - 1].Orignum != 5 && map[player.yLoc - 1][player.xLoc - 1].newNum != 5 && map[player.yLoc - 1][player.xLoc - 2].Orignum != 5 && map[player.yLoc][player.xLoc - 2].Orignum != 5)) {
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
//            System.out.println("rk");
            player.xLoc--;
            socketServer.postUpdate("Move Player Left:" + playerName);
        } else if (keyEvent.getCode().equals(KeyCode.W) && player.yLoc > 0 && player.yLoc - 1 > 1 && (map[player.yLoc - 1][player.xLoc].Orignum != 5 && map[player.yLoc - 1][player.xLoc - 1].newNum != 5 && map[player.yLoc - 2][player.xLoc].Orignum != 5 && map[player.yLoc - 2][player.xLoc - 1].Orignum != 5)) {
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
//            System.out.println("w");
            player.yLoc--;
            socketServer.postUpdate("Move Player Upp:" + playerName);
        } else if (key == KeyCode.S && player.yLoc < 49 && (map[player.yLoc + 1][player.xLoc].Orignum != 5 && map[player.yLoc + 1][player.xLoc - 1].newNum != 5 && map[player.yLoc][player.xLoc].Orignum != 5 && map[player.yLoc][player.xLoc - 1].Orignum != 5)) {
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc++;
            socketServer.postUpdate("Move Player Downn:" + playerName);
        } else if (key == KeyCode.Q && player.yLoc > 0 && player.xLoc > 0 && player.yLoc - 1 > 1 && player.xLoc - 1 > 1 && (map[player.yLoc - 1][player.xLoc - 1].Orignum != 5 && map[player.yLoc - 2][player.xLoc - 1].newNum != 5 && map[player.yLoc - 1][player.xLoc - 2].Orignum != 5 && map[player.yLoc - 2][player.xLoc - 2].Orignum != 5)){
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc--;
            player.xLoc--;
            socketServer.postUpdate("Move Player Up-left:" + playerName);
        } else if (key == KeyCode.E && player.yLoc > 0 && player.xLoc < 99 && player.yLoc - 1 > 1 && (map[player.yLoc - 1][player.xLoc + 1].Orignum != 5 && map[player.yLoc - 1][player.xLoc + 2].newNum != 5 && map[player.yLoc - 2][player.xLoc + 1].Orignum != 5 && map[player.yLoc - 2][player.xLoc + 2].Orignum != 5)){
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc--;
            player.xLoc++;
            socketServer.postUpdate("Move Player Up-right:" + playerName);
        } else if (key == KeyCode.Z && player.yLoc < 99 && player.xLoc > 0 && player.xLoc - 1 > 1 && (map[player.yLoc + 1][player.xLoc - 1].Orignum != 5 && map[player.yLoc + 1][player.xLoc - 2].newNum != 5 && map[player.yLoc + 2][player.xLoc - 1].Orignum != 5 && map[player.yLoc + 2][player.xLoc - 2].Orignum != 5)){
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc++;
            player.xLoc--;
            socketServer.postUpdate("Move Player Down-left:" + playerName);
        } else if (key == KeyCode.C && player.yLoc < 49 && player.xLoc < 99 && (map[player.yLoc + 1][player.xLoc + 1].Orignum != 5 && map[player.yLoc + 1][player.xLoc + 2].newNum != 5 && map[player.yLoc + 2][player.xLoc + 1].Orignum != 5 && map[player.yLoc + 2][player.xLoc + 2].Orignum != 5)){
            map[player.yLoc - 1][player.xLoc - 1].newNum = map[player.yLoc - 1][player.xLoc - 1].Orignum;
            map[player.yLoc - 1][player.xLoc].newNum = map[player.yLoc - 1][player.xLoc].Orignum;
            map[player.yLoc][player.xLoc - 1].newNum = map[player.yLoc][player.xLoc - 1].Orignum;
            map[player.yLoc][player.xLoc].newNum = map[player.yLoc][player.xLoc].Orignum;
            player.yLoc++;
            player.xLoc++;
            socketServer.postUpdate("Move Player Down-right:" + playerName);
        }
        if (player.team.equals("blue")){
            map[player.yLoc - 1][player.xLoc - 1].newNum = 6;
            map[player.yLoc - 1][player.xLoc].newNum = 6;
            map[player.yLoc][player.xLoc - 1].newNum = 6;
            map[player.yLoc][player.xLoc].newNum = 6;
        } else if (player.team.equals("red")){
            map[player.yLoc - 1][player.xLoc - 1].newNum = 9;
            map[player.yLoc - 1][player.xLoc].newNum = 9;
            map[player.yLoc][player.xLoc - 1].newNum = 9;
            map[player.yLoc][player.xLoc].newNum = 9;
        }

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

        int k = 0;
        for (int i = 20; i >= 0; i--) {
            if (k <= 19){
                map[i][k].Orignum = 5;
                map[i][k].newNum = 5;
                map[i - 1][k].Orignum = 5;
                map[i - 1][k].newNum = 5;
                k++;
            }
        }

        k = 0;
        for (int i = 30; i <= 49; i++) {
            if (k <= 18){
                map[i][k].Orignum = 5;
                map[i][k].newNum = 5;
                map[i + 1][k].Orignum = 5;
                map[i + 1][k].newNum = 5;
                k++;
            }
        }

        k = 99;
        for (int i = 20; i >= 0; i--) {
            if (k >= 80){
                map[i][k].Orignum = 5;
                map[i][k].newNum = 5;
                map[i - 1][k].Orignum = 5;
                map[i - 1][k].newNum = 5;
                k--;
            }
        }

        k = 99;
        for (int i = 30; i <= 49; i++) {
            if (k >= 81){
                map[i][k].Orignum = 5;
                map[i][k].newNum = 5;
                map[i + 1][k].Orignum = 5;
                map[i + 1][k].newNum = 5;
                k--;
            }
        }

        k = 21;
        for (int i = 19; i >= 10; i--) {
            if (k <= 33){
                map[i][k].Orignum = 5;
                map[i][k].newNum = 5;
                map[i][k + 1].Orignum = 5;
                map[i][k + 1].newNum = 5;
                k++;
            }
        }

        k = 21;
        for (int i = 31; i <= 39; i++) {
            if (k <= 33){
                map[i][k].Orignum = 5;
                map[i][k].newNum = 5;
                map[i][k + 1].Orignum = 5;
                map[i][k + 1].newNum = 5;
                k++;
            }
        }

        k = 78;
        for (int i = 19; i >= 10; i--) {
            if (k >= 66){
                map[i][k].Orignum = 5;
                map[i][k].newNum = 5;
                map[i][k - 1].Orignum = 5;
                map[i][k - 1].newNum = 5;
                k--;
            }
        }

        k = 78;
        for (int i = 31; i <= 39; i++) {
            if (k >= 66){
                map[i][k].Orignum = 5;
                map[i][k].newNum = 5;
                map[i][k - 1].Orignum = 5;
                map[i][k - 1].newNum = 5;
                k--;
            }
        }

        for (int i = 31; i < 36; i++) {
            map[10][i].Orignum = 5;
            map[10][i].newNum = 5;
            map[39][i].Orignum = 5;
            map[39][i].newNum = 5;
            map[10][i + 24].Orignum = 5;
            map[10][i + 24].newNum = 5;
            map[39][i + 24].Orignum = 5;
            map[39][i + 24].newNum = 5;
//            map[11][i].Orignum = 5;
//            map[11][i].newNum = 5;
//            map[38][i].Orignum = 5;
//            map[38][i].newNum = 5;
//            map[11][i + 24].Orignum = 5;
//            map[11][i + 24].newNum = 5;
//            map[38][i + 24].Orignum = 5;
//            map[38][i + 24].newNum = 5;
        }

        for (int i = 40; i < 45; i++) {
            map[10][i].Orignum = 5;
            map[10][i].newNum = 5;
            map[39][i].Orignum = 5;
            map[39][i].newNum = 5;
            map[10][i + 24].Orignum = 5;
            map[10][i + 24].newNum = 5;
            map[39][i + 24].Orignum = 5;
            map[39][i + 24].newNum = 5;
//            map[11][i].Orignum = 5;
//            map[11][i].newNum = 5;
//            map[38][i].Orignum = 5;
//            map[38][i].newNum = 5;
//            map[11][i + 24].Orignum = 5;
//            map[11][i + 24].newNum = 5;
//            map[38][i + 24].Orignum = 5;
//            map[38][i + 24].newNum = 5;


            for (int l = 24; l < 27; l++) {
                for (int m = 4; m < 7; m++) {
                    map[l][m].Orignum = 11;
                    map[l][m].Orignum = 11;
                }
            }
        }

        dragon = new Monsters("Dragon", 50, 500, 10, .25, 43, 32, buttons, 10);
        monsters.add(dragon);
        blueBotFrontTurret = new Turrets("blueBotFrontTurret", 5000, 100, 5, map);
        blueBotMidTurret = new Turrets("blueBotMidTurret", 5000, 100, 5, map);
        blueBotInhibitorTurret = new Turrets("blueBotInhibitorTurret", 5000, 100, 5, map);
        blueTopFrontTurret = new Turrets("blueTopFrontTurret", 5000, 100, 5, map);
        blueTopMidTurret = new Turrets("blueTopMidTurret", 5000, 100, 5, map);
        blueTopInhibitorTurret = new Turrets("blueTopInhibitorTurret", 5000, 100, 5, map);
        blueTurrets.add(blueTopFrontTurret);
        blueTurrets.add(blueTopMidTurret);
        blueTurrets.add(blueTopInhibitorTurret);
        blueTurrets.add(blueBotFrontTurret);
        blueTurrets.add(blueBotMidTurret);
        blueTurrets.add(blueBotInhibitorTurret);

        redBotFrontTurret = new Turrets("redBotFrontTurret", 5000, 100, 5, map);
        redBotMidTurret = new Turrets("redBotMidTurret", 5000, 100, 5, map);
        redBotInhibitorTurret = new Turrets("redBotInhibitorTurret", 5000, 100, 5, map);
        redTopFrontTurret = new Turrets("redTopFrontTurret", 5000, 100, 5, map);
        redTopMidTurret = new Turrets("redTopMidTurret", 5000, 100, 5, map);
        redTopInhibitorTurret = new Turrets("redTopInhibitorTurret", 5000, 100, 5, map);
        redTurrets.add(redTopFrontTurret);
        redTurrets.add(redTopMidTurret);
        redTurrets.add(redTopInhibitorTurret);
        redTurrets.add(redBotFrontTurret);
        redTurrets.add(redBotMidTurret);
        redTurrets.add(redBotInhibitorTurret);

        for (Bullets bullet : bullets) {
            bullet = new Bullets();
        }
    }

    AnimationTimer grenadeTimer;

    public void start(){
        updateScreen();
        System.out.println("called start");
        new AnimationTimer(){
            @Override
            public void handle(long noww) {
                if(startTime>0){
                    for (Player player: players) {
                        for (Turrets turret : redTurrets) {
                            if (Math.abs(turret.turretX - player.xLoc) <= turret.range && Math.abs(turret.turretY - player.yLoc) <= turret.range && !turret.targetHit && player.team.equals("blue")){
                                new AnimationTimer(){
                                    @Override
                                    public void handle(long now) {
//                                        System.out.println(turret.name);
                                        if (now - turret.startTime > (900000000.0 * .3)) {
//                                            System.out.println("ANIMATION TIMER IS WORKING");
                                            if (Math.abs(turret.turretX - player.xLoc) <= turret.range && Math.abs(turret.turretY - player.yLoc) <= turret.range && !turret.targetHit){
                                                turret.isShooting = true;
                                            }
                                            if (turret.isShooting){
                                                if (now - turret.startTime > (900000000.0 * .3)) {
                                                    turret.shoot(player, map, this);
                                                    updateScreen();
                                                    if (turret.targetHit){
                                                        System.out.println("TARGET HIT");
                                                        turret.x = turret.turretX;
                                                        turret.y = turret.turretY;
                                                        turret.targetHit = false;
                                                        turret.isShooting = false;
                                                    }
                                                }
                                            }
//                                            System.out.println("resetting turret starttime");
                                            turret.startTime = System.nanoTime();
                                        }
                                    }
                                }.start();
                            }
                        }

                        for (Turrets turret : blueTurrets) {
                            if (Math.abs(turret.turretX - player.xLoc) <= turret.range && Math.abs(turret.turretY - player.yLoc) <= turret.range && !turret.targetHit && player.team.equals("red")){
                                System.out.println("2y:" + player.yLoc);
                                System.out.println("2x:" + player.xLoc);
                                System.out.println("x:");
                                System.out.println(turret.turretX - player.xLoc);
                                System.out.println("y:");
                                System.out.println(turret.turretY - player.yLoc);
                                new AnimationTimer(){
                                    @Override
                                    public void handle(long now) {
//                                        System.out.println(turret.name);
                                        if (now - turret.startTime > (900000000.0 * .3)) {
//                                            System.out.println("ANIMATION TIMER IS WORKING");
                                            if (Math.abs(turret.turretX - player.xLoc) <= turret.range && Math.abs(turret.turretY - player.yLoc) <= turret.range && !turret.targetHit){
                                                turret.isShooting = true;
                                            }
                                            if (turret.isShooting){
                                                if (now - turret.startTime > (900000000.0 * .2)) {
                                                    turret.shoot(player, map, this);
                                                    updateScreen();
                                                    if (turret.targetHit){
                                                        System.out.println("TARGET HIT");
                                                        turret.x = turret.turretX;
                                                        turret.y = turret.turretY;
                                                        turret.targetHit = false;
                                                        turret.isShooting = false;
                                                    }
                                                }
                                            }
//                                            System.out.println("resetting turret starttime");
                                            turret.startTime = System.nanoTime();
                                        }
                                    }
                                }.start();
                            }
                        }
                    }
                    for (Monsters monster : monsters) {
                        if (noww - startTime > (900000000.0 * monster.respawnTime)) {
//                        if (now - startTime > (900000000.0 * .1)) {
                            new AnimationTimer(){
                                @Override
                                public void handle(long now){
//                                   System.out.println("ANIMATION TIMER IS WORKING");
                                   if (now - monster.startTime > (900000000.0 * .1)){
//                                    System.out.println("inside second animation tmer");
                                        if (frame < 9) {
                                            frame++;
                                        } else if (frame == 9) {
                                            frame = 1;
                                        }
                                        dragon.changeImage(buttons, frame);
                                        monster.startTime = System.nanoTime();
                                    }
//                                    startTime = System.nanoTime();
                                }
                            }.start();
                        }
                    }
                }
                lstHealth.getItems().clear();
                for (Player player : players) {
                    lstHealth.getItems().add(player.name + " Health:" + player.health);
                }
                for (Monsters monster : monsters) {
                    lstHealth.getItems().add(monster.name + " Health:" + monster.health);
                }
                for (Turrets turret : redTurrets) {
                    lstHealth.getItems().add(turret.name + " Health:" + turret.health);
                }
                for (Turrets turret : blueTurrets) {
                    lstHealth.getItems().add(turret.name + " Health:" + turret.health);
                }
            }
        }.start();

//        System.out.println("here");

        EventHandler<MouseEvent> z = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked something");
                if (event.getButton() == MouseButton.PRIMARY){
                    System.out.println("clicked primary");
//                    Bullets bulletUsing = null;
//                    for (Bullets bullet: bullets) {
//                      if (!bullet.isBeingUsed){
//                          bulletUsing = bullet;
//                          break;
//                      }
//                    }
                    Bullets bullet = new Bullets(player.xLoc, player.yLoc);
                    currentlyUsingWeapon.squaresTravelled = 0;
                    for (int i = 0; i < 26; i++) {
                        for (int j = 0; j < 26; j++) {
                            if (((Button) event.getSource()) == displayButtons[i][j] && !player.isUsingItem){
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
                                            if (now - bullet.startTime > (900000000.0 * .01) && currentlyUsingWeapon.squaresTravelled < currentlyUsingWeapon.range){
                                                System.out.println("range: " + currentlyUsingWeapon.range);
                                                bullet.fire(colTo, rowTo, map, this, currentlyUsingWeapon, players, player, monsters, blueTurrets, redTurrets);
                                                if (bullet.x == colTo && bullet.y == rowTo){
                                                    bullet.targetReached = true;
                                                }
                                                if (currentlyUsingWeapon.squaresTravelled >= currentlyUsingWeapon.range){
                                                    bullet.isBeingUsed = false;
                                                }
                                                updateScreen();
                                                bullet.startTime = System.nanoTime();
                                            }
                                        }
                                        lstHealth.getItems().clear();
                                        for (Player player : players) {
                                            lstHealth.getItems().add(player.name + " Health:" + player.health);
                                        }
                                        for (Monsters monster : monsters) {
                                            lstHealth.getItems().add(monster.name + " Health:" + monster.health);
                                        }
                                        for (Turrets turret : redTurrets) {
                                            lstHealth.getItems().add(turret.name + " Health:" + turret.health);
                                        }
                                        for (Turrets turret : blueTurrets) {
                                            lstHealth.getItems().add(turret.name + " Health:" + turret.health);
                                        }
                                    }
                                }.start();
//                                System.out.println(i + " " + j);
//                            System.out.println("oc:"+i+"or:"+j);

                            } else if (((Button) event.getSource()) == displayButtons[i][j] && player.isUsingItem && player.currentlyUsingItem.name.equals("Grenade")){
                                int rowTo = player.yLoc + (i - 13);
                                int colTo = player.xLoc + (j - 13);

                                socketServer.postUpdate("Grenade:" + playerName + "r:" + rowTo + "c:" + colTo);
                                System.out.println("is using grenade");

                                new AnimationTimer(){
                                    @Override
                                    public void handle(long now) {
//                                        System.out.println("in animation timer");
                                        if (!player.isUsingItem){
                                            this.stop();
                                        }
                                        if (currentlyUsingItem.startTime > 0){
                                            if (now - bullet.startTime > (900000000.0 * .1) && currentlyUsingItem.squaresTravelled < currentlyUsingItem.range){
//                                                System.out.println("range: " + currentlyUsingWeapon.range);
                                                bullet.throwGrenade(colTo, rowTo, map);
                                                if (bullet.x == colTo && bullet.y == rowTo){
                                                    bullet.targetReached = true;
                                                }
                                                if (currentlyUsingWeapon.squaresTravelled >= currentlyUsingWeapon.range){
                                                    bullet.isBeingUsed = false;
                                                }
                                                if (bullet.squaresTravelled >= currentlyUsingItem.range || bullet.targetReached) {
                                                    new AnimationTimer() {
                                                        @Override
                                                        public void handle(long now) {
                                                            for (int k = 1; k < 5; k++) {
                                                                System.out.println(k);
                                                                if (now - currentlyUsingItem.startTime > (900000000 * 1.5)) {
                                                                    System.out.println("in second animation timer");

//                                                                    map[rowTo][colTo].newNum = map[rowTo][colTo].Orignum;
//                                                                    map[rowTo - k][colTo - k].newNum = 5;

                                                                    map[rowTo - k][colTo].newNum = 8;
                                                                    map[rowTo + k][colTo].newNum = 8;
                                                                    map[rowTo - k][colTo - k].newNum = 8;
                                                                    map[rowTo - k][colTo + k].newNum = 8;
                                                                    map[rowTo + k][colTo + k].newNum = 8;
                                                                    map[rowTo + k][colTo - k].newNum = 8;
                                                                    map[rowTo][colTo + k].newNum = 8;
                                                                    map[rowTo][colTo - k].newNum = 8;

                                                                    for (Player player : players) {
                                                                        if ((player.yLoc == rowTo - k && player.xLoc == colTo) || (player.yLoc == rowTo + k && player.xLoc == colTo) || (player.yLoc == rowTo - k && player.xLoc == colTo - k) || (player.yLoc == rowTo - k && player.xLoc == colTo + k) || (player.yLoc == rowTo + k && player.xLoc == colTo + k) || (player.yLoc == rowTo + k && player.xLoc == colTo - k) || (player.yLoc == rowTo  && player.xLoc == colTo - k) || (player.yLoc == rowTo  && player.xLoc == colTo + k) || (player.yLoc == rowTo  && player.xLoc == colTo)) {
                                                                            player.changeHealth(-300);
                                                                        }
                                                                    }

//                                                                    map[rowTo - k][colTo].newNum = 8;
//                                                                    map[rowTo - k][colTo - 1].newNum = 8;
//                                                                    map[rowTo - k][colTo + 1].newNum = 8;
//                                                                    map[rowTo + k][colTo].newNum = 8;
//                                                                    map[rowTo + k][colTo - 1].newNum = 8;
//                                                                    map[rowTo + k][colTo + 1].newNum = 8;

//                                                                    map[rowTo - k - 1][colTo].newNum = map[rowTo - k - 1][colTo].Orignum;
//                                                                    map[rowTo - k - 1][colTo - 1].newNum = map[rowTo - k - 1][colTo].Orignum;
//                                                                    map[rowTo - k - 1][colTo + 1].newNum = map[rowTo - k - 1][colTo].Orignum;
//                                                                    map[rowTo + k][colTo].newNum = map[rowTo - k - 1][colTo].Orignum;
//                                                                    map[rowTo + k][colTo - 1].newNum = map[rowTo - k - 1][colTo - 1].Orignum;
//                                                                    map[rowTo + k][colTo + 1].newNum = map[rowTo - k - 1][colTo + 1].Orignum;
                                                                }

                                                            }
//                                                            for (int l = 0; l < 5; l++) {
//                                                                if (now - currentlyUsingItem.startTime > (900000000 * .5)) {
//                                                                    map[rowTo][colTo - l].newNum = 8;
//                                                                    map[rowTo - 1][colTo - l].newNum = 8;
//                                                                    map[rowTo + 1][colTo - l].newNum = 8;
//                                                                    map[rowTo][colTo + l].newNum = 8;
//                                                                    map[rowTo - 1][colTo + l].newNum = 8;
//                                                                    map[rowTo + 1][colTo + l].newNum = 8;
//                                                                    map[rowTo - l][colTo - l].newNum = 8;
//                                                                    map[rowTo - l][colTo - l].newNum = 8;
//                                                                    map[rowTo - l][colTo - l].newNum = 8;
//                                                                }
//                                                            }

                                                            lstHealth.getItems().clear();
                                                            for (Player player : players) {
                                                                lstHealth.getItems().add(player.name + " Health:" + player.health);
                                                            }
                                                            for (Monsters monster : monsters) {
                                                                lstHealth.getItems().add(monster.name + " Health:" + monster.health);
                                                            }
                                                            for (Turrets turret : redTurrets) {
                                                                lstHealth.getItems().add(turret.name + " Health:" + turret.health);
                                                            }
                                                            for (Turrets turret : blueTurrets) {
                                                                lstHealth.getItems().add(turret.name + " Health:" + turret.health);
                                                            }
                                                        }
                                                    }.start();
                                                }
                                                updateScreen();
                                                bullet.startTime = System.nanoTime();
                                            }
                                        }
                                        pbPlayerHealth.setProgress(player.healthBar.getProgress());
                                    }
                                }.start();
//                                System.out.println(i + " " + j);
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
    }

    private double startTime = System.nanoTime();

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

        for (Player player : players) {
            if (player.team.equals("blue")){
                map[player.yLoc - 1][player.xLoc - 1].newNum = 6;
                map[player.yLoc - 1][player.xLoc].newNum = 6;
                map[player.yLoc][player.xLoc - 1].newNum = 6;
                map[player.yLoc][player.xLoc].newNum = 6;
            } else if (player.team.equals("red")){
                map[player.yLoc - 1][player.xLoc - 1].newNum = 9;
                map[player.yLoc - 1][player.xLoc].newNum = 9;
                map[player.yLoc][player.xLoc - 1].newNum = 9;
                map[player.yLoc][player.xLoc].newNum = 9;
            }
        }

        for (Turrets turret : blueTurrets) {
            if (!turret.isDestroyed){
                map[turret.turretY][turret.turretX].newNum = 1;
                map[turret.turretY - 1][turret.turretX - 1].newNum = 1;
                map[turret.turretY - 1][turret.turretX].newNum = 1;
                map[turret.turretY - 1][turret.turretX + 1].newNum = 1;
                map[turret.turretY + 1][turret.turretX - 1].newNum = 1;
                map[turret.turretY + 1][turret.turretX].newNum = 1;
                map[turret.turretY + 1][turret.turretX + 1].newNum = 1;
                map[turret.turretY][turret.turretX - 1].newNum = 1;
                map[turret.turretY][turret.turretX + 1].newNum = 1;
            }
        }

        for (Turrets turret : redTurrets) {
            map[turret.turretY][turret.turretX].newNum = 2;
            map[turret.turretY - 1][turret.turretX - 1].newNum = 2;
            map[turret.turretY - 1][turret.turretX].newNum = 2;
            map[turret.turretY - 1][turret.turretX + 1].newNum = 2;
            map[turret.turretY + 1][turret.turretX - 1].newNum = 2;
            map[turret.turretY + 1][turret.turretX].newNum = 2;
            map[turret.turretY + 1][turret.turretX + 1].newNum = 2;
            map[turret.turretY][turret.turretX - 1].newNum = 2;
            map[turret.turretY][turret.turretX + 1].newNum = 2;
        }

        for (int i = 0; i < map.length; i++) {
            for (int c = 0; c < map[0].length; c++) {
                if (map[i][c].newNum == 1){
                    buttons[i][c].setStyle("-fx-background-color: blue");
                } else if (map[i][c].newNum == 2){
                    buttons[i][c].setStyle("-fx-background-color: red");
                } else if (map[i][c].newNum == 3){
                    buttons[i][c].setStyle("-fx-background-color: #edca3e");
                } else if (map[i][c].newNum == 4){
                    buttons[i][c].setStyle("-fx-background-color: #608750");
                } else if (map[i][c].newNum == 5){
                    buttons[i][c].setStyle("-fx-background-color: black");
                } else if (map[i][c].newNum == 6){
                    //blue
                    buttons[i][c].setStyle("-fx-background-color: #3274d1");
                } else if (map[i][c].newNum == 7){
                    buttons[i][c].setStyle("-fx-background-color: brown");
                } else if (map[i][c].newNum == 8){
                    buttons[i][c].setStyle("-fx-background-color: grey");
                } else if (map[i][c].newNum == 9){
                    //red
                    buttons[i][c].setStyle("-fx-background-color: #f7534a");
                } else if (map[i][c].newNum == 11){
                    //red
                    buttons[i][c].setStyle("-fx-background-color: #30486e");
                }
            }
        }

        pbPlayerHealth.setProgress(player.healthBar.getProgress());

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
            socketServer.postUpdate(line);
            if (line.startsWith("Ready")){
                numPlayersReady++;
                socketServer.postUpdate("Ready" + numPlayersReady);
                if (numPlayersReady == 2){
                    lstPrimaryWeapon.setVisible(false);
                    lstSecondaryWeapon.setVisible(false);
                    lstStats.setVisible(false);
                    txtName.setVisible(false);
                    btnEnterName.setVisible(false);
                    btnFindMatch.setVisible(false);
                    pbPlayerHealth.setVisible(true);
                    lblPickLoadout.setVisible(false);
                    MAP.setVisible(true);
                    lstStore.setVisible(true);
                    lstInventory.setVisible(true);
                    lstHealth.setVisible(true);
                    start();
                }
            } else if (line.startsWith("Create Player:")){
//                for (Player player : players) {
//                    if (!player.name.equals(line.substring(line.indexOf(":") + 1, line.indexOf("team:")))){
//                        players.add(new Player(line.substring(line.indexOf(":") + 1, line.indexOf("team:")), 1, 250, 25, .5, Integer.parseInt(line.substring(line.indexOf("x:") + 2, line.indexOf("y:"))), Integer.parseInt(line.substring(line.indexOf("y:") + 2)), map, line.substring(line.indexOf("m:") + 2, line.indexOf("x"))));
//                    }
//                }
                players.add(new Player(line.substring(line.indexOf(":") + 1, line.indexOf("team:")), 1, 250, 25, .5, Integer.parseInt(line.substring(line.indexOf("x:") + 2, line.indexOf("y:"))), Integer.parseInt(line.substring(line.indexOf("y:") + 2)), map, line.substring(line.indexOf("m:") + 2, line.indexOf("x"))));
                socketServer.postUpdate(line);

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
                socketServer.postUpdate(line);
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
                System.out.println(playerName);

                for (Player player : players) {
                    System.out.println(player.name);
//                    System.out.println(player.currentlyUsingWeapon.weaponName);
                    if (player.name.equals(playerName)){
                        Bullets bullet = new Bullets(player.xLoc, player.yLoc);
                        new AnimationTimer(){
                            @Override
                            public void handle(long now) {
                                System.out.println("in animation timer");
                                if (player.primary.startTime > 0){
//                                            System.out.println("lollolololol");
                                    if (now - player.primary.startTime > (900000000.0 * 2) && player.primary.squaresTravelled < player.primary.range){
                                        System.out.println("range: " + currentlyUsingWeapon.range);
                                        bullet.fire(colTo, rowTo, map, this, player.primary, players, player, monsters, blueTurrets, redTurrets);
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
            } else if (line.startsWith("Player Weapons:")){
                for (Player player : players) {
//                    System.out.println(player.name);
                }
//                System.out.println(line);
                String playerName = line.substring(line.indexOf("s:") + 2, line.indexOf("primary"));
                System.out.println(playerName);
                String primaryWeapon = line.substring(line.indexOf("y:") + 2, line.indexOf("secondary:"));
                System.out.println(primaryWeapon);
                String secondaryWeapon = line.substring(line.indexOf("dary:") + 5, line.indexOf("cu"));
                System.out.println(secondaryWeapon);
                String current = line.substring(line.indexOf("current:") + 8);
                System.out.println(current);
                for (Player player : players) {
                    if (player.name.equals(playerName)){
//                        System.out.println(playerName);
                        for (Weapon weapon : weapons) {
                            if (weapon.weaponName.equals(primaryWeapon)){
                                player.primary = weapon;
                            }
                            if (weapon.weaponName.equals(secondaryWeapon)){
                                player.secondary = weapon;
                            }
                            if (weapon.weaponName.equals(current)){
                                player.currentlyUsingWeapon = weapon;
                            }
                        }
                    }
                }
                for (Player player : players) {
                    if (player.name.equals(playerName)){
                        System.out.println(player.currentlyUsingWeapon.weaponName);
                    }
                }

                socketServer.postUpdate(line);
            } else if (line.startsWith("Grenade:")){
                String playerName = line.substring(line.indexOf(":") + 1, line.indexOf("r:") + 2);
                int rowTo = Integer.parseInt(line.substring(line.indexOf("r:") + 2, line.indexOf("c:")));
                int colTo = Integer.parseInt(line.substring(line.indexOf("c:") + 2));

                for (Player player : players) {
                    if (player.name.equals(playerName)){
                        Bullets bullet = new Bullets(player.xLoc, player.yLoc);
                        new AnimationTimer(){
                            @Override
                            public void handle(long now) {
//                                        System.out.println("in animation timer");
                                if (!player.isUsingItem){
                                    this.stop();
                                }
                                if (currentlyUsingItem.startTime > 0){
                                    if (now - bullet.startTime > (900000000.0 * .1) && currentlyUsingItem.squaresTravelled < currentlyUsingItem.range){
//                                                System.out.println("range: " + currentlyUsingWeapon.range);
                                        bullet.throwGrenade(colTo, rowTo, map);
                                        if (bullet.x == colTo && bullet.y == rowTo){
                                            bullet.targetReached = true;
                                        }
                                        if (currentlyUsingWeapon.squaresTravelled >= currentlyUsingWeapon.range){
                                            bullet.isBeingUsed = false;
                                        }
                                        if (bullet.squaresTravelled >= currentlyUsingItem.range || bullet.targetReached) {
                                            new AnimationTimer() {
                                                @Override
                                                public void handle(long now) {
                                                    for (int k = 1; k < 5; k++) {
                                                        System.out.println(k);
                                                        if (now - currentlyUsingItem.startTime > (900000000 * 1.5)) {
                                                            System.out.println("in second animation timer");

                                                            map[rowTo - k][colTo].newNum = 8;
                                                            map[rowTo + k][colTo].newNum = 8;
                                                            map[rowTo - k][colTo - k].newNum = 8;
                                                            map[rowTo - k][colTo + k].newNum = 8;
                                                            map[rowTo + k][colTo + k].newNum = 8;
                                                            map[rowTo + k][colTo - k].newNum = 8;
                                                            map[rowTo][colTo + k].newNum = 8;
                                                            map[rowTo][colTo - k].newNum = 8;

                                                            for (Player player : players) {
                                                                if ((player.yLoc == rowTo - k && player.xLoc == colTo) || (player.yLoc == rowTo + k && player.xLoc == colTo) || (player.yLoc == rowTo - k && player.xLoc == colTo - k) || (player.yLoc == rowTo - k && player.xLoc == colTo + k) || (player.yLoc == rowTo + k && player.xLoc == colTo + k) || (player.yLoc == rowTo + k && player.xLoc == colTo - k) || (player.yLoc == rowTo  && player.xLoc == colTo - k) || (player.yLoc == rowTo  && player.xLoc == colTo + k) || (player.yLoc == rowTo  && player.xLoc == colTo)) {
                                                                    player.changeHealth(-300);
                                                                }
                                                            }

                                                            lstHealth.getItems().clear();
                                                            for (Player player : players) {
                                                                lstHealth.getItems().add(player.name + " Health:" + player.health);
                                                            }
                                                            for (Monsters monster : monsters) {
                                                                lstHealth.getItems().add(monster.name + " Health:" + monster.health);
                                                            }
                                                            for (Turrets turret : redTurrets) {
                                                                lstHealth.getItems().add(turret.name + " Health:" + turret.health);
                                                            }
                                                            for (Turrets turret : blueTurrets) {
                                                                lstHealth.getItems().add(turret.name + " Health:" + turret.health);
                                                            }
                                                        }
                                                    }
                                                    updateScreen();
                                                    bullet.startTime = System.nanoTime();
                                                }
                                            }.start();
                                        }
                                    }
                                }
                            }
                        }.start();
                    }
                }
            }

            if (line.startsWith("Move Player")){
//                for (Player player: players) {
//                    for (Turrets turret : redTurrets) {
//                        if (Math.abs(turret.turretX - player.xLoc) <= turret.range && Math.abs(turret.turretY - player.yLoc) <= turret.range && !turret.targetHit && player.team.equals("blue")){
////                            System.out.println("2y:" + player.yLoc);
////                            System.out.println("2x:" + player.xLoc);
////                            System.out.println("x:");
////                            System.out.println(turret.turretX - player.xLoc);
////                            System.out.println("y:");
////                            System.out.println(turret.turretY - player.yLoc);
//                            new AnimationTimer(){
//                                @Override
//                                public void handle(long now) {
////                                        System.out.println(turret.name);
//                                    if (now - turret.startTime > (900000000.0 * .3)) {
////                                            System.out.println("ANIMATION TIMER IS WORKING");
//                                        if (Math.abs(turret.turretX - player.xLoc) <= turret.range && Math.abs(turret.turretY - player.yLoc) <= turret.range && !turret.targetHit){
//                                            turret.isShooting = true;
//                                        }
//
//                                        if (turret.isShooting){
//                                            if (now - turret.startTime > (900000000.0 * .3)) {
//                                                turret.shoot(player, map, this);
//                                                updateScreen();
//                                                if (turret.targetHit){
//                                                    System.out.println("TARGET HIT");
//                                                    turret.x = turret.turretX;
//                                                    turret.y = turret.turretY;
//                                                    turret.targetHit = false;
//                                                    turret.isShooting = false;
//                                                }
//                                            }
//                                        }
//                                        System.out.println("resetting turret starttime");
//                                        turret.startTime = System.nanoTime();
//                                    }
//                                }
//                            }.start();
//                        }
//                    }
//
//                    for (Turrets turret : blueTurrets) {
//                        if (Math.abs(turret.turretX - player.xLoc) <= turret.range && Math.abs(turret.turretY - player.yLoc) <= turret.range && !turret.targetHit && player.team.equals("red")){
//                            System.out.println("2y:" + player.yLoc);
//                            System.out.println("2x:" + player.xLoc);
//                            System.out.println("x:");
//                            System.out.println(turret.turretX - player.xLoc);
//                            System.out.println("y:");
//                            System.out.println(turret.turretY - player.yLoc);
//                            new AnimationTimer(){
//                                @Override
//                                public void handle(long now) {
////                                        System.out.println(turret.name);
//                                    if (now - turret.startTime > (900000000.0 * .3)) {
//                                        System.out.println("ANIMATION TIMER IS WORKING");
//                                        if (Math.abs(turret.turretX - player.xLoc) <= turret.range && Math.abs(turret.turretY - player.yLoc) <= turret.range && !turret.targetHit){
//                                            turret.isShooting = true;
//                                        }
//                                        if (turret.isShooting){
//                                            if (now - turret.startTime > (900000000.0 * .3)) {
//                                                turret.shoot(player, map, this);
//                                                updateScreen();
//                                                if (turret.targetHit){
//                                                    System.out.println("TARGET HIT");
//                                                    turret.x = turret.turretX;
//                                                    turret.y = turret.turretY;
//                                                    turret.targetHit = false;
//                                                    turret.isShooting = false;
//                                                }
//                                            }
//                                        }
//                                        turret.startTime = System.nanoTime();
//                                    }
//                                }
//                            }.start();
//                        }
//                    }
//                }
            }
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
