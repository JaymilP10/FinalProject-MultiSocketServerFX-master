package com.jtconnors.multisocketserverfx;

import com.jtconnors.socket.SocketListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import com.jtconnors.socketfx.FxMultipleSocketServer;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
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
    private Label connectionsLabel;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Label lblPort;
    @FXML
    private TextField portTextField;
    @FXML
    private ListView<String> rcvdMsgsListView;
    private ObservableList<String> sentMsgsData;
    @FXML
    private ListView<String> sentMsgsListView;
    private ObservableList<String> rcvdMsgsData;

    private FxMultipleSocketServer socketServer;
    private ListView<String> lastSelectedListView;
    private Tooltip portTooltip;

    public enum ConnectionDisplayState {

        DISCONNECTED, WAITING, CONNECTED
    }

    private void displayState(ConnectionDisplayState state) {
        switch (state) {
            case DISCONNECTED:
                connectButton.setDisable(false);
                disconnectButton.setDisable(true);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
                connectionsLabel.setText("Not connected");
                break;
            case WAITING:
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
                connectionsLabel.setText("Waiting for connections");
                break;
            case CONNECTED:
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
                int numConnections = socketServer.getListenerCount();
                StringBuilder connectionsSB
                        = new StringBuilder(numConnections + " connection");
                if (numConnections != 1) {
                    connectionsSB.append("s");
                }
                connectionsLabel.setText(new String(connectionsSB));

                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[0].length; j++) {
                        map[i][j] = new Button();
//                        map[i][j].setPrefSize(10, 10);
                        map[i][j].setPrefHeight(20);
                        map[i][j].setPrefWidth(20);
//                map[i][j] = new ImageView();
//                map[i][j].setEffect(c);
//                map[i][j].setFitHeight(70);
//                map[i][j].setFitWidth(70);

                        if (i > 89 && j < 10){
                            map[i][j].setStyle("-fx-background-color: blue");
                        } else if (i < 10 && j > 89){
                            map[i][j].setStyle("-fx-background-color: red");
                        } else if ((i < 10 && j >= 10 && j <= 89) || (j < 10 && i <= 89) || (i > 89 && j >= 10 && j <= 89) || (j > 89 && i >= 10)){
                            map[i][j].setStyle("-fx-background-color: yellow");
                        } else {
                            map[i][j].setStyle("-fx-background-color: green");
                        }

//                        if (i == 0 || j == 0 || i == 29 || j == 49){
//                            map[i][j].setStyle("-fx-background-color: black");
//                        }
//                        if (j < 7)
//                            map[i][j].setStyle("-fx-background-color: blue");
//                        else if (j > 42) {
//                            map[i][j].setStyle("-fx-background-color: red");
//                        }
//                        if (j >= 7 && i < 10 && j <= 42){
//                            map[i][j].setStyle("-fx-background-color: yellow");
//                        } else if (j >= 7 && j <= 42 && i > 20){
//                            map[i][j].setStyle("-fx-background-color: yellow");
//                        } else if (j >= 7 && j <= 42 && i <= 20 && i >= 10) {
//                            map[i][j].setStyle("-fx-background-color: green");
//                        }
                        MAP.add(map[i][j], j, i);
                    }
                }

                int x = 10;
                for (int i = 10; i <= 89; i++) {

                    for (int k = 0; k <= 7; k++) {
                        map[i + k][x].setStyle("-fx-background-color: blue");
                        map[i][x + k].setStyle("-fx-background-color: blue");
                    }
                    x++;
                }

                int j = 10;
                for (int i = 89; i >= 10; i--) {
                    for (int k = 0; k <= 7; k++) {
                        map[i + k][j].setStyle("-fx-background-color: yellow");
                        map[i - k][j].setStyle("-fx-background-color: yellow");
                    }
//                    map[i][j].setStyle("-fx-background-color: yellow");
//                    map[i-1][j].setStyle("-fx-background-color: yellow");
//                    map[i-2][j].setStyle("-fx-background-color: yellow");
//                    map[i-3][j].setStyle("-fx-background-color: yellow");
//                    map[i-4][j].setStyle("-fx-background-color: yellow");
//                    map[i+1][j].setStyle("-fx-background-color: yellow");
//                    map[i+2][j].setStyle("-fx-background-color: yellow");
//                    map[i+3][j].setStyle("-fx-background-color: yellow");
//                    map[i+4][j].setStyle("-fx-background-color: yellow");
                    j++;
                }
                break;
        }
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

    @FXML
    private void initialize(){
    }
}
