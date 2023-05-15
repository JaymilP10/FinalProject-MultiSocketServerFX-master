package com.jtconnors.multisocketserverfx;

import javafx.animation.AnimationTimer;

import java.math.BigInteger;
import java.nio.MappedByteBuffer;

public class Turrets {

    String name;
    int health;
    int damage;
    int range;
    boolean isDestroyed = false;
    int turretX;
    int turretY;
    double startTime;
    int x;
    int y;
    boolean targetHit = false;
    boolean isShooting = false;
    
    public Turrets(){
        
    }

    public Turrets(String name, int health, int damage, int range, Map[][] map){
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.range = range;
        if (name.equals("blueTopFrontTurret")){
            turretX = 42;
            turretY = 5;
            x = 42;
            y = 5;
//            map[5][42].newNum = 8;
        } else if (name.equals("blueTopMidTurret")){
            turretX = 24;
            turretY = 6;
            x = 26;
            y = 6;
//            map[5][32].newNum = 8;
        } else if (name.equals("blueTopInhibitorTurret")){
            turretX = 12;
            turretY = 18;
            x = 12;
            y = 18;
//            map[18][12].newNum = 8;
        } else if (name.equals("blueBotFrontTurret")){
            turretX = 42;
            turretY = 45;
            x = 42;
            y = 45;
//            map[45][42].newNum = 8;
        } else if (name.equals("blueBotMidTurret")){
            turretX = 24;
            turretY = 44;
            x = 24;
            y = 44;
//            map[45][32].newNum = 8;
        } else if (name.equals("blueBotInhibitorTurret")){
            turretX = 12;
            turretY = 32;
            x = 12;
            y = 32;
//            map[32][12].newNum = 8;
        } else if (name.equals("redTopFrontTurret")){
            turretX = 58;
            turretY = 5;
            x = 58;
            y = 5;
//            map[5][42].newNum = 8;
        } else if (name.equals("redTopMidTurret")){
            turretX = 75;
            turretY = 6;
            x = 75;
            y = 6;
//            map[5][32].newNum = 8;
        } else if (name.equals("redTopInhibitorTurret")){
            turretX = 87;
            turretY = 18;
            x = 87;
            y = 18;
//            map[18][12].newNum = 8;
        } else if (name.equals("redBotFrontTurret")){
            turretX = 58;
            turretY = 45;
            x = 58;
            y = 45;
//            map[45][42].newNum = 8;
        } else if (name.equals("redBotMidTurret")){
            turretX = 75;
            turretY = 44;
            x = 75;
            y = 44;
//            map[45][32].newNum = 8;
        } else if (name.equals("redBotInhibitorTurret")){
            turretX = 87;
            turretY = 32;
            x = 87;
            y = 32;
//            map[32][12].newNum = 8;
        }
        startTime = System.nanoTime();
    }

    public void shoot(Player player, Map[][] map, AnimationTimer animationTimer){
        System.out.println("shooting");
        System.out.println("x:" + x);
        System.out.println("y:" + y);
        if (y > 0 && y < 50 && x > 0 && x < 100)
            map[y][x].newNum = map[y][x].Orignum;
//        System.out.println("called fire");
//        System.out.println("ty:" + player.yLoc + " tx:" + player.xLoc + " y:" + y + " x:" + x);
        int[] slope = new int[2];

        if (player.yLoc == y && player.xLoc > x){
            x++;
        } else if (player.yLoc == y && player.xLoc < x){
            x--;
        } else if (player.xLoc == x && player.yLoc > y){
            y++;
        } else if (player.xLoc == x && player.yLoc < y){
            y--;
        } else if (player.yLoc > y && player.yLoc - y == slope[0]){
            y += 1;
        } else if (player.yLoc < y && player.yLoc - y == slope[0]){
            y -= 1;
        } else if (player.yLoc > y && player.xLoc > x){
            slope = reduceFraction(player.yLoc - y, player.xLoc - x);
            y += slope[0];
            x += slope[1];
        } else if (player.yLoc > y && player.xLoc < x){
            slope = reduceFraction(player.yLoc - y, x - player.xLoc);
            y += slope[0];
            x -= slope[1];
        } else if (player.yLoc < y && player.xLoc > x){
            slope = reduceFraction(y - player.yLoc, player.xLoc - x);
            y -= slope[0];
            x += slope[1];
        } else if (player.yLoc < y && player.xLoc < x){
            slope = reduceFraction(y - player.yLoc, x - player.xLoc);
            y -= slope[0];
            x -= slope[1];
        }

        if (x < 100 && y < 50 && x > 0 && y > 0){
//            buttons[x][y].setStyle("-fx-background-color: brown");
            map[y][x].newNum = 7;
        }

        if ((y == player.yLoc && x == player.xLoc) || (y == player.yLoc - 1 && x == player.xLoc) || (y == player.yLoc - 1 && x == player.xLoc - 1) || (y == player.yLoc && x == player.xLoc - 1)){
            System.out.println("target hit");
            targetHit = true;
//            animationTimer.stop();
        }
    }

    public static int[] reduceFraction(int numerator, int denominator) {
//        System.out.println("nume: " + numerator + " deno:" + denominator);
        int[] result = new int[2];

        int gcd = findGCD(numerator, denominator);
//        System.out.println("GCD: " + gcd);
        if (gcd == 1){
//            gcd = findGCD(numerator - 1, denominator);
            return new int[] {1, 1};
        } else if (gcd == -1){
            gcd = findGCD(numerator - 1, denominator);
        } else if (gcd == 0){
            return result;
        }
        result[0] = numerator/gcd;
        result[1] = numerator/gcd;
        return result;
    }

    public static int findGCD(int x, int y) {
        BigInteger n = new BigInteger(String.valueOf(x));
        BigInteger d = new BigInteger(String.valueOf(y));
        return Integer.parseInt(String.valueOf(n.gcd(d)));
    }
}
