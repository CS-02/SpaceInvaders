package com.example.hectormediero.spaceinvadersdas.Resources.stepDefinitions;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.constraint.Barrier;
import android.view.ContextThemeWrapper;

import com.example.hectormediero.spaceinvadersdas.Models.Ambusher;
import com.example.hectormediero.spaceinvadersdas.Models.Bullet;
import com.example.hectormediero.spaceinvadersdas.Models.DefenceBrick;
import com.example.hectormediero.spaceinvadersdas.Models.Invader;
import com.example.hectormediero.spaceinvadersdas.Models.PlayerShip;
import com.example.hectormediero.spaceinvadersdas.Views.SpaceInvadersView;

import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class steps {

    private int screenY = 1000;
    private int screenX = 1000;
    private Context context;

    private int numBullets;
    private int numBricks;

    private PlayerShip playerShip;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<DefenceBrick> bricks = new ArrayList<>();
    private Invader[] invaders = new Invader[60];
    private Ambusher ambusher;
    private int numInvaders;
    private int numAmbusher;

    @Before
    public void before(){
        this.context = new ContextThemeWrapper();
    }

    @Given("there are initially {int} bullets")
    public void initial_invaders(Integer numBullets){
        this.numBullets = numBullets;
    }


    @When("I initialize the app")
    public void initialize_invaders(){
        Bullet b = new Bullet(screenY);
        bullets.add(b);
        DefenceBrick brick = new DefenceBrick(1,2,3,screenX,screenY);
        bricks.add(brick);
        playerShip = new PlayerShip(context,screenX,screenY);

        int initialInvaders = 0;
        for (int column = 0; column < 6; column++) {
            for (int row = 0; row < 5; row++) {
                invaders[initialInvaders] = new Invader(context, row, column, screenX, screenY);
                initialInvaders++;
            }
        }
        ambusher = new Ambusher(context,0,0,screenX,screenY);

    }

    @Then("there should be more than {int} bullets")
    public void more_than_one(Integer number){
        assertTrue(bullets.size() >= numBullets);
    }

    @Given("there are initially {int} bricks")
    public void thereAreInitiallyBricks(int arg0) {
        this.numBricks = arg0;
    }

    @Then("there should be more than {int} bricks")
    public void thereShouldBeMoreThanBricks(int arg0) {
        assertTrue(bricks.size() >= numBricks);
    }

    @Given("there's no player ship in the beginning")
    public void thereSNoPlayerShipInTheBeginning() {

    }

    @Then("there should be a player ship ready")
    public void thereShouldBeAPlayerShipReady() {
        assertNotNull(playerShip);
    }

    @Given("there should be initially {int} invaders")
    public void thereAreInitiallyInvaders(int arg0) {
        this.numInvaders = arg0;
    }

    @Then("there should be more than {int} invaders")
    public void thereShouldBeMoreThanInvaders(int arg0) {
        assertTrue(invaders.length >= arg0);
    }

    @Given("there should be initially {int} ambushers")
    public void thereShouldBeInitiallyAmbushers(int arg0) {
        this.numAmbusher = arg0;
    }

    @Then("there should be more than {int} ambusher")
    public void thereShouldBeMoreThanAmbusher(int arg0) {
        assertNotNull(ambusher);
    }
}
