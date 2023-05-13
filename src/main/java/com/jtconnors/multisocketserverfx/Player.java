package com.jtconnors.multisocketserverfx;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Player {

    ArrayList<Image> images = new ArrayList<>();
    String name;
    int level;
    int health;
    int maxHealth;
    int healthIncrease;
    double speed;
    int xLoc;
    int yLoc;

    ProgressBar healthBar = new ProgressBar(1);

    Weapon primary;
    Weapon secondary;
    Weapon currentlyUsingWeapon;

    public Player(String name, int level, int health, int healthIncrease, double speed, int xLoc, int yLoc, Map[][] map){
        this.level = level;
        this.health = health;
        this.maxHealth = health;
        this.healthIncrease = healthIncrease;
        this.speed = speed;
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.name = name;
        map[yLoc][xLoc].newNum = 6;
        map[yLoc - 1][xLoc].newNum = 6;
        map[yLoc][xLoc - 1].newNum = 6;
        map[yLoc - 1][xLoc - 1].newNum = 6;
    }

    public void changeHealth(int amount){
        health += amount;
        double progress = health/maxHealth;
        healthBar.setProgress(progress);
    }

    long startTime = System.nanoTime();
    public void changeLoc(Map [][] map, int targetX, int targetY){

        if (xLoc != targetX && yLoc != targetY){
            new AnimationTimer(){
                @Override
                public void handle(long now) {
                    System.out.println("now" + now);
                    System.out.println("starttime" + startTime);
                    if(startTime>0){
                        System.out.println("YUHHHHHHHHHHHHHHHHHHHHHHHHH");
                        if (now - startTime > (900000000.0 * speed)) {
                            int tempx = xLoc;
                            int tempy = yLoc;
                            if (targetY > tempy && targetX > tempx && map[tempx + 1][tempy + 1].newNum != 5){
                                tempy++;
                                tempx++;
                            } else if (targetY == tempy && targetX > tempx && map[tempx + 1][tempy].newNum != 5) {
                                tempx++;
                            } else if (targetY < tempy && targetX > tempx && map[tempx + 1][tempy - 1].newNum != 5) {
                                tempy--;
                                tempx++;
                            } else if (targetY < tempy && targetX < tempx && map[tempx - 1][tempy - 1].newNum != 5) {
                                tempy--;
                                tempx--;
                            } else if (targetY < tempy && targetX == tempx && map[tempx][tempy - 1].newNum != 5) {
                                tempy--;
                            } else if (targetY > tempy && targetX == tempx && map[tempx][tempy + 1].newNum != 5) {
                                tempy++;
                            } else if (targetY == tempy && targetX < tempx && map[tempx - 1][tempy].newNum != 5) {
                                tempx--;
                            } else if (targetY > tempy && targetX < tempx && map[tempx - 1][tempy + 1].newNum != 5) {
                                tempy++;
                                tempx--;
                            }

                            if (tempy == targetY && tempx == targetX) {
                                map[tempx][tempy].newNum = 6;
                            } else {
                                map[tempx][tempy].newNum = 6;
                                map[xLoc][yLoc].newNum = 0;
                                xLoc = tempx;
                                yLoc = tempy;
                            }
                            startTime = System.nanoTime();
                        }
                    }
                }
            }.start();
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

    public void changeImage(Button[][] buttons, int framenewNum){
        ImageView img = new ImageView();
        Image tempCard;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 30; k < 33; k++) {
                    for (int l = 42; l < 45; l++) {
                        String pathName = "src/main/resources/Images/frame" + framenewNum + "/" + i + "" + j + ".png";
                        try {
                            tempCard = new Image(new FileInputStream(pathName));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        img.setImage(tempCard);
                        buttons[k][l].setGraphic(img);
                    }

                }

            }
        }
    }
}