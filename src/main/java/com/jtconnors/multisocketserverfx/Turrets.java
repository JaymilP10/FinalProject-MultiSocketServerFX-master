package com.jtconnors.multisocketserverfx;

import java.math.BigInteger;
import java.nio.MappedByteBuffer;

public class Turrets {

    String name;
    int health;
    int damage;
    int range;
    boolean isDestroyed = false;
    double startTime;
    int turretX;
    int turretY;
    int x;
    int y;

    public Turrets(String name, int health, int damage, int range, Map[][] map){
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.range = range;
        if (name.equals("blueTopFrontTurret")){
            turretX = 42;
            turretY = 5;
//            map[5][42].newNum = 8;
        } else if (name.equals("blueTopMidTurret")){
            turretX = 24;
            turretY = 6;
//            map[5][32].newNum = 8;
        } else if (name.equals("blueTopInhibitorTurret")){
            turretX = 12;
            turretY = 18;
//            map[18][12].newNum = 8;
        } else if (name.equals("blueBotFrontTurret")){
            turretX = 42;
            turretY = 45;
//            map[45][42].newNum = 8;
        } else if (name.equals("blueBotMidTurret")){
            turretX = 24;
            turretY = 44;
//            map[45][32].newNum = 8;
        } else if (name.equals("blueBotInhibitorTurret")){
            turretX = 12;
            turretY = 32;
//            map[32][12].newNum = 8;
        } else if (name.equals("redTopFrontTurret")){
            turretX = 48;
            turretY = 5;
//            map[5][42].newNum = 8;
        } else if (name.equals("redTopMidTurret")){
            turretX = 76;
            turretY = 6;
//            map[5][32].newNum = 8;
        } else if (name.equals("redTopInhibitorTurret")){
            turretX = 12;
            turretY = 18;
//            map[18][12].newNum = 8;
        } else if (name.equals("redBotFrontTurret")){
            turretX = 42;
            turretY = 45;
//            map[45][42].newNum = 8;
        } else if (name.equals("redBotMidTurret")){
            turretX = 76;
            turretY = 44;
//            map[45][32].newNum = 8;
        } else if (name.equals("redBotInhibitorTurret")){
            turretX = 88;
            turretY = 32;
//            map[32][12].newNum = 8;
        }
        startTime = System.nanoTime();
    }

    public void shoot(int targetX, int targetY, Map[][] map){
        if (y > 0 && y < 50 && x > 0 && x < 100)
            map[y][x].newNum = map[y][x].Orignum;
        System.out.println("called fire");
        System.out.println("ty:" + targetY + " tx:" + targetX + " y:" + y + " x:" + x);
        int[] slope = new int[2];

        if (targetY == y && targetX > x){
            x++;
        } else if (targetY == y && targetX < x){
            x--;
        } else if (targetX == x && targetY > y){
            y++;
        } else if (targetX == x && targetY < y){
            y--;
        } else if (targetY > y && targetY - y == slope[0]){
            y += 1;
        } else if (targetY < y && targetY - y == slope[0]){
            y -= 1;
        } else if (targetY > y && targetX > x){
            slope = reduceFraction(targetY - y, targetX - x);
            y += slope[0];
            x += slope[1];
        } else if (targetY > y && targetX < x){
            slope = reduceFraction(targetY - y, targetX - x);
            y += slope[0];
            x -= slope[1];
        } else if (targetY < y && targetX > x){
            slope = reduceFraction(y - targetY, targetX - x);
            y -= slope[0];
            x += slope[1];
        } else if (targetY < y && targetX < x){
            slope = reduceFraction(y - targetY, targetX - x);
            y -= slope[0];
            x -= slope[1];
        }

        if (x < 100 && y < 50 && x > 0 && y > 0){
//            buttons[x][y].setStyle("-fx-background-color: brown");
            map[y][x].newNum = 7;
        }
    }

    public static int[] reduceFraction(int numerator, int denominator) {
        System.out.println("nume: " + numerator + " deno:" + denominator);
        int[] result = new int[2];

        int gcd = findGCD(numerator, denominator);
        System.out.println("GCD: " + gcd);
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
