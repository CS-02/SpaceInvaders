package com.example.hectormediero.spaceinvadersdas.Models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BulletTest {

    Bullet bullet;
    int screen;

    @Before
    public void setUp(){
        bullet = new Bullet(screen);
    }
    @Test
    public void shoot() {
    }

    @Test
    public void changeDirection() {
    }

    @Test
    public void getDir() {
    }

    @Test
    public void update() {
    }

    @After
    public void crearDonw(){
        bullet = null;
    }
}