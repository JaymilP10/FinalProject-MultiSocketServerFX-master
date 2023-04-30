package com.jtconnors.multisocketserverfx;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Monsters {

    ArrayList<Image> images = new ArrayList<>();
    ImageView[][] img = new ImageView[3][3];
    String type;
    int level;
    int health;
    int maxHealth;
    int healthIncrease;
    double speed;
    int xLoc;
    int yLoc;
    int range;

    ProgressBar healthBar = new ProgressBar(1);

    Weapon primary;
    Weapon secondary;

    public Monsters(int range, int health, int healthIncrease, double speed, int xLoc, int yLoc, Button[][] buttons){
        this.range = range;
        this.health = health;
        this.maxHealth = health;
        this.healthIncrease = healthIncrease;
        this.xLoc = xLoc;
        this.yLoc = yLoc;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                img[i][j] = new ImageView();
                img[i][j].fitWidthProperty().bind(buttons[i + 30][j + 40].widthProperty().subtract(15));
                img[i][j].fitHeightProperty().bind(buttons[i + 30][j + 40].heightProperty().subtract(15));
//                img[i][j].setFitWidth(50);
//                img[i][j].setFitHeight(50);
            }
        }
    }

    public void changeHealth(int amount){
        health += amount;
        double progress = health/maxHealth;
        healthBar.setProgress(progress);
    }

    public void changeLoc(int [][] map, int targetX, int targetY){
        int tempx = xLoc;
        int tempy = yLoc;
        if (targetY > tempy && targetX > tempx && map[tempx + 1][tempy + 1] != 5){
            tempy++;
            tempx++;
        } else if (targetY == tempy && targetX > tempx && map[tempx + 1][tempy] != 5) {
            tempx++;
        } else if (targetY < tempy && targetX > tempx && map[tempx + 1][tempy - 1] != 5) {
            tempy--;
            tempx++;
        } else if (targetY < tempy && targetX < tempx && map[tempx - 1][tempy - 1] != 5) {
            tempy--;
            tempx--;
        } else if (targetY < tempy && targetX == tempx && map[tempx][tempy - 1] != 5) {
            tempy--;
        } else if (targetY > tempy && targetX == tempx && map[tempx][tempy + 1] != 5) {
            tempy++;
        } else if (targetY == tempy && targetX < tempx && map[tempx - 1][tempy] != 5) {
            tempx--;
        } else if (targetY > tempy && targetX < tempx && map[tempx - 1][tempy + 1] != 5) {
            tempy++;
            tempx--;
        }

        if (tempy == targetY && tempx == targetX) {
            map[tempx][tempy] = 6;
        } else {
            map[tempx][tempy] = 6;
            map[xLoc][yLoc] = 0;
            xLoc = tempx;
            yLoc = tempy;
        }
    }

    public void checkWalls(int [][] map){
        int count;
        int tempx = xLoc;
        int tempy = yLoc;
        if (map[tempx + 1][tempy + 1] == 5){
            tempy++;
            tempx++;
        } else if (map[tempx + 1][tempy] != 5) {
            tempx++;
        } else if (map[tempx + 1][tempy - 1] != 5) {
            tempy--;
            tempx++;
        } else if (map[tempx - 1][tempy - 1] != 5) {
            tempy--;
            tempx--;
        } else if (map[tempx][tempy - 1] != 5) {
            tempy--;
        } else if (map[tempx][tempy + 1] != 5) {
            tempy++;
        } else if (map[tempx - 1][tempy] != 5) {
            tempx--;
        } else if (map[tempx - 1][tempy + 1] != 5) {
            tempy++;
            tempx--;
        }
    }



    public void changeWeapon(Weapon primary, Weapon secondary){
        this.primary = primary;
        this.secondary = secondary;
    }

    public void changeImage(Button[][] buttons, int frameNum){
        Image tempCard;
//        img[0][0] = new ImageView();
//        String pathName = "src/main/resources/Images/frame" + frameNum + "/" + 0 + "" + 0 + ".png";
//        try {
//            tempCard = new Image(new FileInputStream(pathName));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        img[0][0].setImage(tempCard);
//        img[0][0].setFitWidth(40);
//        img[0][0].setFitHeight(40);
//        buttons[0][0].setGraphic(img[0][0]);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String pathName = "src/main/resources/Images/frame" + frameNum + "/" + j + "" + i + ".png";
                try {
                    FileInputStream fileInputStream = new FileInputStream(pathName);
                    tempCard = new Image(fileInputStream);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                img[i][j].setImage(tempCard);
//                img[i][j].setPreserveRatio(true);
//                img[i][j].setFitHeight(buttons[i + 30][j + 40].getPrefHeight());
//                img[i][j].setFitWidth(buttons[i + 30][j + 40].getPrefWidth());
                buttons[i + 30][j + 40].setGraphic(img[i][j]);
            }
        }
    }
}
