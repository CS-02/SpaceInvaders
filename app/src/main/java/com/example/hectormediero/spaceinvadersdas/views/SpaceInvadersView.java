package com.example.hectormediero.spaceinvadersdas.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.hectormediero.spaceinvadersdas.Activities.ScoreActivity;
import com.example.hectormediero.spaceinvadersdas.Models.Ambusher;
import com.example.hectormediero.spaceinvadersdas.Models.Bullet;
import com.example.hectormediero.spaceinvadersdas.Models.DefenceBrick;
import com.example.hectormediero.spaceinvadersdas.Models.Invader;
import com.example.hectormediero.spaceinvadersdas.Models.PlayerShip;
import com.example.hectormediero.spaceinvadersdas.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;


// Hay que refactorizar esta clase, la complejidad ciclomática
// es de aproximadamente 220, cuando el límite está en 15.

@SuppressLint("ViewConstructor")
public class SpaceInvadersView extends SurfaceView implements Runnable {

    private static final int INVADERS_NUM = 60;
    private static final int AMBUSHERS_NUM = 60;
    private static final int BRICKS_NUM = 400;
    private static final int INVADER_BULLET_NUM = 200;
    private static final int MAX_INVADER_BULLET = 4;
    private static final int MAX_AMBUSHER_BULLET = 20;

    /*************************
     *    GAME CONTEXT       *
     *************************/
    private Context context;
    private final Intent scoreGame;
    private Thread gameThread = null;// Esta es nuestra secuencia


    /*************************
     *    GAME RULES         *
     *************************/
    private volatile boolean playing;// Un booleano el cual vamos a activar y desactivar cuando el juego este activo- o no.
    private boolean paused = true;  // El juego esta pausado al iniciar
    private long fps;               // Esta variable rastrea los cuadros por segundo del juego
    private boolean hanCambiado = false;
    public int clock = -1;          // Contador para el Timer de los Ambushers
    int score = 0;                  // La puntuación
    private int lives = 1;          // Vidas
    private String username;

    /*************************
     *    GAME ENTITIES      *
     *************************/
    private PlayerShip playerShip;  // La nave del jugador
    private Bullet bullet;          // La bala del jugador
    //Invaders
    private Invader[] invaders = new Invader[INVADERS_NUM];
    private int numInvaders = 0;
    private Bullet[] invadersBullets = new Bullet[INVADER_BULLET_NUM]; // Las balas de los Invaders
    private int nextInvaderBullet;
    //Ambushers
    private Ambusher[] ambusher = new Ambusher[AMBUSHERS_NUM];
    private int numAmbusher = 0;
    private Bullet[] ambusherBullets = new Bullet[INVADER_BULLET_NUM]; // Las balas de los Ambushers
    private int nextAmbusherBullet;
    //Barriers
    private DefenceBrick[] bricks = new DefenceBrick[BRICKS_NUM]; // Las guaridas del jugador están construidas a base de ladrillos
    private int numBricks;

    /**********************
     *       DRAWING      *
     **********************/
    private SurfaceHolder ourHolder;    // Nuestro SurfaceHolder para bloquear la superficie antes de que dibujemos nuestros gráficos
    private Paint paint;                // Un objeto de lienzo (Canvas) y de pintar (Paint)
    private int screenX;                // El tamaño x de la pantalla en pixeles
    private int screenY;                // El tamaño y de la pantalla en pixeles
    private Bitmap dch;
    private Bitmap izq;
    private Bitmap arr;
    private Bitmap abj;

    // Este método especial de constructor se ejecuta
    public SpaceInvadersView(Context context, int x, int y, String nombreUsuario) {
        super(context);

        // Hace una copia del "context" disponible globalmete para que la usemos en otro método
        this.context = context;
        scoreGame = new Intent(context.getApplicationContext(), ScoreActivity.class);
        username = nombreUsuario;
        // Inicializa los objetos de ourHolder y paint
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        prepareLevel();
    }

    private void prepareLevel() {

        // Aquí vamos a inicializar todos los objetos del juego

        // Hacer una nueva nave espacial del jugador
        playerShip = new PlayerShip(context, screenX, screenY);

        // Preparar las balas del jugador
        bullet = new Bullet(screenY);

        // Inicializa la formación de invadersBullets
        for (int i = 0; i < invadersBullets.length; i++) {
            invadersBullets[i] = new Bullet(screenY);
        }

        //Prepara las balas de los Ambushers
        for (int i = 0; i < ambusherBullets.length; i++) {
            ambusherBullets[i] = new Bullet(screenY);
        }

        // Construir un ejercito de invaders
        numInvaders = 0;
        for (int column = 0; column < 5; column++) {
            for (int row = 0; row < 4; row++) {
                invaders[numInvaders] = new Invader(context, row, column, screenX, screenY);
                numInvaders++;
            }
        }

        // Prepara los Ambushers
        for (numAmbusher = 0; numAmbusher < 10; numAmbusher++) {
            ambusher[numAmbusher] = new Ambusher(context, 0, 0, screenX, screenY);
        }

        // Construir las guaridas
        numBricks = 0;
        for (int shelterNumber = 0; shelterNumber < 4; shelterNumber++) {
            for (int column = 0; column < 10; column++) {
                for (int row = 0; row < 5; row++) {
                    bricks[numBricks] = new DefenceBrick(row, column, shelterNumber, screenX, screenY - (int) invaders[1].getHeight());
                    numBricks++;
                }
            }
        }
    }


    //Timer para los Ambushers, cada 10 segundos aparece uno, durante 10 minutos
    CountDownTimer ambush = new CountDownTimer(600000, 10000) {
        public void onTick(long millisUntilFinished) {
            if (clock >= 0) {
                ambusher[clock].setVisible();
            }
            clock++;
        }

        @Override
        public void onFinish() {
            // Code Smell..
        }
    };

    //Timer para el Teletransporte , cada 3 segundos,durante 10 minutos
    CountDownTimer tp = new CountDownTimer(6000000, 9000) {
        public void onTick(long millisUntilFinished) {
            SecureRandom sr = new SecureRandom();
            int contador = 0;
            int nVertical = (int) (sr.nextFloat() * (screenY - playerShip.getHeight())) + 1;
            int nHorizontal = (int) (sr.nextFloat() * (screenX - playerShip.getLength())) + 1;
            RectF rectaux = new RectF();
            rectaux.top = nVertical;
            rectaux.bottom = nVertical + playerShip.getHeight();
            rectaux.left = nHorizontal;
            rectaux.right = nHorizontal + playerShip.getLength();

            //Implementar relatividad general, aplicando un campo magnético a la nave del jugador,
            // desde el punto de vista físico la lógica carece de sentido.  ????????????????

            for (Bullet invadersBullet : invadersBullets) {
                if (rectaux.intersect(invadersBullet.getRect())) {
                    contador++;
                } else {
                    for (int j = 0; j < numInvaders; j++) {
                        if (rectaux.intersect(invaders[j].getRect())) {
                            contador++;
                        } else {
                            for (int k = 0; k < numBricks; k++) {
                                if (rectaux.intersect(bricks[k].getRect())) {
                                    contador++;
                                }
                            }
                        }
                    }
                }
            }
            if (contador == 0) {
                playerShip.actualizarRectangulo(nHorizontal, nVertical);
            }
        }

        @Override
        public void onFinish() {
            // Code Smell..
        }
    };


    // Este método se ejecuta cuando el jugador empieza el juego

    @Override
    public void run() {
        long timeThisFrame;
        Looper.prepare();
        while (playing) {

            // Captura el tiempo actual en milisegundos en startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Actualiza el cuadro
            if (!paused) {
                update();
            }

            // Dibuja el cuadro
            draw();

            // Calcula los cuadros por segundo de este cuadro
            // Ahora podemos usar los resultados para
            // medir el tiempo de animaciones y otras cosas más.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }
    }


    private void update() {
        int contadorColor;
        boolean bumped;
        contadorColor = 0;
        boolean lost = false;

        playerShip.update(fps);         // Mueve la nave espacial del jugador

        bumped = updateInvaderShow();   //Actualiza los invaders si se ven
        updateActiveAmbusher();         //Actualiza a los Ambushers si están activos, y si llegan al final, desaparecen

        if (bumped) {
            lost = moveDownAndChangeDirection();
        }
        if (lost) {
            gameOver(false);
        }

        updateBullets();                //Actualiza las balas de todos los elementos en pantalla
        bulletBounce(invadersBullets);//Comprueba que las balas toquen con los bordes y reboten
        bulletBounce(ambusherBullets);

        contadorColor = collisionCheck(contadorColor); //Comprobación de colisiones y cambio de color
        if (contadorColor > 1) {
            cambioColorAleatorio();
        } else if (contadorColor == 1) {
            cambioColorUnico();
        }

    }

    // Actualiza a los invaders si se ven
    private boolean updateInvaderShow() {
        for (int i = 0; i < numInvaders; i++) {
            if (invaders[i].getVisibility()) {
                // Mueve el siguiente invader
                invaders[i].update(fps);
                // ¿Quiere hacer un disparo?
                if (invaders[i].takeAim(playerShip.getX(), playerShip.getLength()) && (!invadersBullets[i].getStatus() && nextInvaderBullet <= MAX_INVADER_BULLET) && (invadersBullets[i].shoot(invaders[i].getX() + invaders[i].getLength() / 2, invaders[i].getY(), bullet.DOWN))) {
                    nextInvaderBullet++;
                }
                // Si ese movimiento causó que golpearan la pantalla,
                // cambia bumped a true.
                if (invaders[i].getX() > screenX - invaders[i].getLength() || invaders[i].getX() < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //Actualiza a los Ambushers si están activos, y si llegan al final, desaparecen
    private void updateActiveAmbusher() {
        for (int i = 0; i < numAmbusher; i++) {
            if (ambusher[i].getVisibility()) {
                ambusher[i].update(fps);  //Actualiza la posición del ambusher
                if (ambusher[i].TakeAim() && ambusherBullets[nextAmbusherBullet].shoot(ambusher[i].getX() + ambusher[i].getLength() / 2, ambusher[i].getY(), bullet.DOWN)) {
                    nextAmbusherBullet++;
                    if (nextAmbusherBullet == MAX_AMBUSHER_BULLET) {
                        nextAmbusherBullet = 0;
                    }
                }
                if (ambusher[i].getX() > screenX - ambusher[i].getLength()
                        || ambusher[i].getX() < 0) {
                    ambusher[i].setInvisible();
                }
            }

        }
    }

    //Los invaders se mueven hacia abajo y cambian de dirección
    private boolean moveDownAndChangeDirection() {
        for (int i = 0; i < numInvaders; i++) {
            invaders[i].dropDownAndReverse();
            // Han aterrizado los invaders
            if (invaders[i].getY() > screenY - screenY / 10) {
                return true;
            }
        }
        return false;
    }

    //Actualiza las balas de todos los elementos en pantalla
    private void updateBullets() {
        // Actualiza las balas del jugador
        if (bullet.getStatus()) {
            bullet.update(fps);
        }
        // Actualiza todas las balas de los invaders si están activas
        for (Bullet invadersBullet : invadersBullets) {
            if (invadersBullet.getStatus()) {
                invadersBullet.update(fps);
            }
        }
        //Actualiza las balas del Ambusher
        for (Bullet ambusherBullet : ambusherBullets) {
            if (ambusherBullet.getStatus()) {
                ambusherBullet.update(fps);
            }
        }
    }

    //Comprueba que las balas toquen con los bordes y reboten
    private void bulletBounce(Bullet[] bullets) {
        // Ha tocado la parte alta de la pantalla la bala del jugador
        if (bullet.getImpactPointY() < 0) {
            bullet.changeDirection(1);
        } else if (bullet.getImpactPointY() > screenY) {
            bullet.changeDirection(0);
        }

        // Ha tocado la parte baja de la pantalla la bala del invader
        for (Bullet invadersBullet : bullets) {
            if (invadersBullet.getImpactPointY() > screenY) {
                invadersBullet.changeDirection(0);
            } else if (invadersBullet.getImpactPointY() < 0) {
                invadersBullet.changeDirection(1);
            }
        }
    }

    //Comprueba las colisiones con todos los elementos de la pantalla
    private int collisionCheck(int contadorColor) {
        int color;
        collisionInvaderBrick();
        collisionPlayerBrick();
        collisionPlayerInvader();
        collisionPlayerBulletInvader();

        collisionBulletBarrier(invadersBullets, false, contadorColor);
        collisionBulletBarrier(ambusherBullets, true, contadorColor);
        Bullet[] player = new Bullet[1];
        player[0] = bullet;
        color = collisionBulletBarrier(player, false, contadorColor);

        collisionBulletPlayer(invadersBullets, false);
        collisionBulletPlayer(invadersBullets, true);

        return color;
    }

    private void collisionInvaderBrick() {
        for (int i = 0; i < numInvaders; i++) {
            if (invaders[i].getVisibility()) {
                for (int j = 0; j < numBricks; j++) {
                    if (bricks[i].getVisibility() && (RectF.intersects(invaders[i].getRect(), bricks[j].getRect()))) {
                        bricks[j].setInvisible();
                    }
                }
            }
        }
    }

    private void collisionPlayerBrick() {
        for (int j = 0; j < numBricks; j++) {
            if (bricks[j].getVisibility() && (RectF.intersects(playerShip.getRect(), bricks[j].getRect()))) {
                gameOver(false);
            }
        }
    }

    private void collisionPlayerInvader() {
        for (int j = 0; j < numInvaders; j++) {
            if (invaders[j].getVisibility() && (RectF.intersects(playerShip.getRect(), invaders[j].getRect()))) {
                gameOver(false); //Intersección queda demasiado ampliada
            }
        }
    }

    private void collisionPlayerBulletInvader() {
        if (bullet.getStatus()) {
            for (int i = 0; i < numInvaders; i++) {
                if (invaders[i].getVisibility() && (RectF.intersects(bullet.getRect(), invaders[i].getRect()))) {
                    invaders[i].setInvisible();
                    bullet.setInactive();
                    score = score + 100;
                    // Ha ganado el jugador
                    if (score == numInvaders * 100) {
                        gameOver(true);
                    }
                }
            }
        }
    }

    private int collisionBulletBarrier(Bullet[] bullets, boolean ambusher, int color) {
        int contadorColor = color;
        // Ha impactado una bala alienígena a un ladrillo de la guarida
        for (Bullet invadersBullet : bullets) {
            for (int j = 0; j < numBricks; j++) {
                if (invadersBullet.getStatus() && bricks[j].getVisibility() && RectF.intersects(invadersBullet.getRect(), bricks[j].getRect())) {
                    // A collision has occurred
                    invadersBullet.setInactive();
                    if (ambusher) nextInvaderBullet--;
                    bricks[j].setInvisible();
                    contadorColor++;
                }
            }
        }
        return contadorColor;
    }

    private void collisionBulletPlayer(Bullet[] bullet, boolean ambusher) {
        // Ha impactado una bala de un invader a la nave espacial del jugador
        for (Bullet invadersBullet : bullet) {
            if (invadersBullet.getStatus() && (RectF.intersects(playerShip.getRect(), invadersBullet.getRect()))) {
                invadersBullet.setInactive();
                if (!ambusher) nextInvaderBullet--;
                lives--;
                // ¿Se acabó el juego?
                if (lives == 0) {
                    gameOver(false);
                }
            }
        }
    }

    //Vuelve al color original
    private void cambioColorUnico() {
        SecureRandom sr = new SecureRandom();
        int color = sr.nextInt(5);
        for (int i = 0; i < numInvaders; i++) {
            invaders[i].cambiarColor(hanCambiado, color);
        }
        hanCambiado = !hanCambiado;
    }

    //Cambia de color aleatoriamente
    private void cambioColorAleatorio() {
        SecureRandom sr = new SecureRandom();
        for (int i = 0; i < numInvaders; i++) {
            invaders[i].cambiarColor(sr.nextInt(5));
        }
        hanCambiado = !hanCambiado;
    }

    //Gestiona el final del juego
    private void gameOver(boolean win) {
        tp.cancel();
        ambush.cancel();
        scoreGame.putExtra("mayor13", "true");
        if (win) {
            scoreGame.putExtra("result", "YOU WON");
        } else {
            scoreGame.putExtra("result", "GAME OVER");
        }
        scoreGame.putExtra("score", score);
        scoreGame.putExtra("username", username);
        Log.d("Puntuación:", String.valueOf(score));
        String lineaAleer = "";
        try (BufferedReader fin = new BufferedReader(new InputStreamReader(context.openFileInput("nueva_puntuacioness2.txt")))) {
            lineaAleer = fin.readLine();
        } catch (IOException e) {
            Log.d(e.getLocalizedMessage(), e.getMessage());
        }
        try (OutputStreamWriter fout = new OutputStreamWriter(context.openFileOutput("nueva_puntuacioness2.txt", Context.MODE_PRIVATE))) {
            if (lineaAleer != null)
                fout.write(lineaAleer + "" + username + "¬" + score + "¬" + username + "image#");
            else
                fout.write(username + "¬" + score + "¬" + username + "image#");
            Log.i("Ficheros", "Fichero creado!");
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }

        context.startActivity(scoreGame);
    }


    private void draw() {
        Canvas canvas;

        // Asegurate de que la superficie del dibujo sea valida o tronamos
        if (ourHolder.getSurface().isValid()) {
            // Bloquea el lienzo para que este listo para dibujar
            canvas = ourHolder.lockCanvas();

            // Dibuja el color del fondo
            canvas.drawColor(Color.argb(255, 48, 45, 44));

            // Escoje el color de la brocha para dibujar
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Dibuja a la nave espacial del jugador
            canvas.drawBitmap(playerShip.getBitmap(), playerShip.getX(), playerShip.getY() - playerShip.getHeight(), paint);

            dch = BitmapFactory.decodeResource(context.getResources(), R.drawable.dcha);
            izq = BitmapFactory.decodeResource(context.getResources(), R.drawable.izq);
            arr = BitmapFactory.decodeResource(context.getResources(), R.drawable.arr);
            abj = BitmapFactory.decodeResource(context.getResources(), R.drawable.abj);

            canvas.drawBitmap(dch, 150, (float) screenY - 150, paint);
            canvas.drawBitmap(izq, 0, (float) screenY - 150, paint);
            canvas.drawBitmap(arr, 75, (float) screenY - 200, paint);
            canvas.drawBitmap(abj, 75, (float) screenY - 100, paint);

            drawEntities(canvas);

            // Dibuja a las balas del jugador si están activas
            if (bullet.getStatus()) {
                canvas.drawRect(bullet.getRect(), paint);
            }


            // Actualiza todas las balas de los invaders si están activas
            for (Bullet invadersBullet : invadersBullets) {
                if (invadersBullet.getStatus()) {
                    canvas.drawRect(invadersBullet.getRect(), paint);
                }
            }

            //Lo mismo para Ambushers
            for (Bullet ambusherBullet : ambusherBullets) {
                if (ambusherBullet.getStatus()) {
                    canvas.drawRect(ambusherBullet.getRect(), paint);
                }
            }

            // Dibuja la puntuación y las vidas restantes
            // Cambia el color de la brocha
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score, 10, 50, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawEntities(Canvas canvas){
        // Dibuja a los invaders
        for (int i = 0; i < numInvaders; i++) {
            if (invaders[i].getVisibility()) {
                canvas.drawBitmap(invaders[i].getBitmap2(), invaders[i].getX(), invaders[i].getY() + invaders[i].getHeight(), paint);
            }
        }

        //Dibuja los Ambushers, si son visibles
        for (int i = 0; i < numAmbusher; i++) {
            if (ambusher[i].getVisibility()) {
                canvas.drawBitmap(ambusher[i].getBitmap(), ambusher[i].getX(), ambusher[i].getY(), paint);

            }
        }
        // Dibuja los ladrillos si están visibles
        for (int i = 0; i < numBricks; i++) {
            if (bricks[i].getVisibility()) {
                canvas.drawRect(bricks[i].getRect(), paint);
            }
        }
    }

    // Si SpaceInvadersActivity es pausado/detenido
    // apaga nuestra secuencia.
    public void pause() throws InterruptedException {
        playing = false;
        ambush.cancel();
        tp.cancel();
        gameThread.join();
    }

    // Si SpaceInvadersActivity es iniciado entonces
    // inicia nuestra secuencia.
    public void resume() {
        ambush.start();
        tp.start();
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // La clase de SurfaceView implementa a onTouchListener
    // Así es que podemos anular este método y detectar toques a la pantalla.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // El jugador ha tocado la pantalla
            case MotionEvent.ACTION_DOWN:
                paused = false;

                if (x > 0 && x < izq.getWidth() && y > screenY - 140 && y < screenY - 150 + izq.getHeight()) {
                    //IZQ
                    playerShip.setMovementState(playerShip.LEFT);
                } else if (x > 150 && x < 150 + dch.getWidth() && y > screenY - 150 && y < screenY - 140 + izq.getHeight()) {
                    //DCHA
                    playerShip.setMovementState(playerShip.RIGHT);
                } else if (x > 75 && x < 75 + arr.getWidth() && y > screenY - 200 && y < screenY - 190 + izq.getHeight()) {
                    //UP
                    playerShip.setMovementState(playerShip.UP);
                } else if (x > 75 && x < 75 + abj.getWidth() && y > screenY - 100 && y < screenY - 90 + izq.getHeight()) {
                    //DOWN
                    playerShip.setMovementState(playerShip.DOWN);
                }
                break;
            // El jugador a retirado el dedo de la pantalla
            case MotionEvent.ACTION_UP:
                //se para
                playerShip.setMovementState(playerShip.STOPPED);
                break;

            default:
                break;
        }
        return true;
    }

}