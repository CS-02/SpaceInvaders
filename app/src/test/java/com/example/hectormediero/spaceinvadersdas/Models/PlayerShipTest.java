package com.example.hectormediero.spaceinvadersdas.Models;

import android.app.Activity;
import android.content.Context;

import com.example.hectormediero.spaceinvadersdas.Activities.SpaceInvaderActivity;

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
        assertNotNull(playerShip);
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