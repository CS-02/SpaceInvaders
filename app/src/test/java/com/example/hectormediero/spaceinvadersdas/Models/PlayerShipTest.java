package com.example.hectormediero.spaceinvadersdas.Models;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerShipTest {

    private PlayerShip  playerShip;
    private Context context;
    private int screenX;
    private int screenY;

    @Before
    public void setUp(){
        playerShip = new PlayerShip(context, screenX, screenY);
    }
    @Test
    public void update() {
        assertTrue(true);
    }

    @Test
    public void actualizarRectangulo() {
        assertTrue(true);
    }

    @After
    public void tearDown(){
        playerShip = null;
    }
}