package com.jtconnors.multisocketserverfx;

import com.jtconnors.socket.SocketListener;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import com.jtconnors.socketfx.FxMultipleSocketServer;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
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
    private Button connectButton, btnEnterName, btnFindMatch;
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
                if (numConnections == 2) {
                    btnFindMatch.setDisable(true);
                    btnEnterName.setDisable(true);
                    btnEnterName.setVisible(false);
                    btnFindMatch.setVisible(false);
                    lblPickLoadout.setVisible(true);
                    lstPrimaryWeapon.setVisible(true);
                    lstSecondaryWeapon.setVisible(true);
                    lstItems.setVisible(true);
                    scrollPane.setVisible(false);
                    socketServer.postUpdate("Update Screen");
//                    connectionsSB.append("s");
                }
//                connectionsLabel.setText(new String(connectionsSB));

                break;
        }
    }

    @FXML
    private void pickLoadout(){
        lstPrimaryWeapon.setVisible(false);
        lstSecondaryWeapon.setVisible(false);
        lstItems.setVisible(false);
        scrollPane.setVisible(true);

    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            if (line != null && !line.equals("")) {
                rcvdMsgsData.add(line);
            }
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

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = new Button();
//                        map[i][j].setPrefSize(10, 10);
                map[i][j].setPrefHeight(20);
                map[i][j].setPrefWidth(20);

                if (i > 89 && j < 10){
                    intMap[i][j] = 1;
                } else if (i < 10 && j > 89){
                    intMap[i][j] = 2;
                } else if ((i < 10 && j >= 10 && j <= 89) || (j < 10 && i <= 89) || (i > 89 && j >= 10 && j <= 89) || (j > 89 && i >= 10)){
                    intMap[i][j] = 3;
                } else {
                    intMap[i][j] = 4;
                }
                MAP.add(map[i][j], j, i);
            }
        }

        int x = 10;
        for (int i = 10; i <= 89; i++) {
            for (int k = 0; k <= 7; k++) {
                intMap[i + k][x] = 1;
                intMap[i][x + k] = 1;
            }
            x++;
        }

        int j = 10;
        for (int i = 89; i >= 10; i--) {
            for (int k = 0; k <= 7; k++) {
                intMap[i + k][j] = 3;
                intMap[i - k][j] = 3;
            }
            j++;
        }
    }

    private String playerName;

    @FXML
    private void enterName(){
        playerName = txtName.getText();
        btnFindMatch.setDisable(false);
    }

    private double startTime;
    private void inGame(ActionEvent event){
        EventHandler<MouseEvent> z = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //all button code goes here
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (((Button) event.getSource()) == map[i][j]){
//                            System.out.println("oc:"+i+"or:"+j);
                            startTime = System.nanoTime();
                            new AnimationTimer(){
                                @Override
                                public void handle(long now) {
                                    if(startTime>0){
                                        if (now - startTime > (900000000.0 * 2)){
                                            this.stop();
                                        }
                                    }
                                }
                            }.start();
                        }
                    }
                }
            }
        };
    }

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

    Button[][] map = new Button[100][100];

    int[][] intMap = new int[100][100];

    @FXML
    private GridPane MAP;

    private void updateScreen(){
        for (int i = 0; i < intMap.length; i++) {
            for (int j = 0; j < intMap[0].length; j++) {
                if (intMap[i][j] == 1){
                    map[i][j].setStyle("-fx-background-color: blue");
                } else if (intMap[i][j] == 2){
                    map[i][j].setStyle("-fx-background-color: red");
                } else if (intMap[i][j] == 3){
                    map[i][j].setStyle("-fx-background-color: yellow");
                } else if (intMap[i][j] == 4){
                    map[i][j].setStyle("-fx-background-color: green");
                } else if (intMap[i][j] == 5){
                    map[i][j].setStyle("-fx-background-color: black");
                }
            }
        }
    }
}
