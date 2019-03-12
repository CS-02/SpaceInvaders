package com.example.hectormediero.spaceinvadersdas;

import android.app.Activity;
import android.support.test.*;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.example.hectormediero.spaceinvadersdas.Activities.ScoreActivity;
import com.example.hectormediero.spaceinvadersdas.BDD.BaseDeDatosPuntuaciones;
import com.example.hectormediero.spaceinvadersdas.Models.Score;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BaseDeDatosPuntuacionesTest {


    BaseDeDatosPuntuaciones db;
    Context context;


    @Before
    public void setUp() {
        context = InstrumentationRegistry.getContext();
        db = new BaseDeDatosPuntuaciones(context);
    }

    @Test
    public void addScore() {
        Score puntuacionTest = new Score(1000, "test");
        String esperado;
        db.addScore(puntuacionTest);

        SQLiteDatabase sqldb = db.getReadableDatabase();
        String[] projection = {"_id", "Nombre"};

        Cursor cursor =
                sqldb.query("Ranking",
                        projection,
                        " _id = ?",
                        new String[]{String.valueOf(db.getAllScore().getInt(db.getAllScore().getColumnIndex("_id")))},
                        null,
                        null,
                        null,
                        null);

        esperado = cursor.getString(1);

        sqldb.close();
        db.getAllScore().close();
        assertEquals("test",esperado);
    }

    @Test
    public void remove() {
    }

    @Test
    public void getScoreById() {
    }

    @Test
    public void getAllScore() {
    }
}