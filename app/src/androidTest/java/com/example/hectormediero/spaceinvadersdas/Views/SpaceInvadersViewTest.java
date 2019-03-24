package com.example.hectormediero.spaceinvadersdas.Views;

import android.content.Context;


import com.example.hectormediero.spaceinvadersdas.Models.Bullet;
import com.example.hectormediero.spaceinvadersdas.Models.DefenceBrick;
import com.example.hectormediero.spaceinvadersdas.Models.Invader;
import com.example.hectormediero.spaceinvadersdas.Models.PlayerShip;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SpaceInvadersViewTest {

    Context context;
    SpaceInvadersView sp2;

    int screenX = 1000;
    int screenY = 1000;

    int numInvaders;
    Invader[] invaders;
    PlayerShip playerShip;
    DefenceBrick[] bricks;
    int numBricks;


    @Before
    public void setup(){
        context = InstrumentationRegistry.getTargetContext();
        sp2 = new SpaceInvadersView(context,screenX,screenY, "jihg");
        playerShip = new PlayerShip(context, screenX, screenY);
        invaders = new Invader[60];
        bricks = new DefenceBrick[400];
    }



    @Test
    public void preparePlayerShip() {
        assertNotNull(playerShip);
    }

    @Test
    public void prepareBullet() {
        Bullet bullet = new Bullet(screenY);
        assertNotNull(bullet);
    }

    @Test
    public void prepareInvaders() {
        numInvaders = 0;
        for (int column = 0; column < 6; column++) {
            for (int row = 0; row < 5; row++) {
                invaders[numInvaders] = new Invader(context, row, column, screenX, screenY);
                numInvaders++;
            }
        }
        assertTrue(numInvaders!=0);
        assertNotNull(invaders);
    }

    @Test
    public void prepareShelter() {
        numBricks = 0;
        for (int shelterNumber = 0; shelterNumber < 4; shelterNumber++) {
            for (int column = 0; column < 10; column++) {
                for (int row = 0; row < 5; row++) {
                    bricks[numBricks] = new DefenceBrick(row, column, shelterNumber, screenX, screenY);
                    numBricks++;
                }
            }
        }
        assertTrue(numBricks!=0);
        assertNotNull(bricks);
    }

    @Test
    public void powerUp(){
        /*PRIMERA PARTE*/
        /*Generamos un test que falle a propósito buscando el aserto que necesitamos*/
        //RectF shipSize = playerShip.getRect();
        //assertEquals(shipSize,playerShip.getRect());
    }


}