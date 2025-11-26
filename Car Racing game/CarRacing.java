import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class CarRacing extends JPanel implements ActionListener {
    private static final int W = 480, H = 700;
    private int playerX = W/2 - 20, playerY = H - 140;
    private int playerW = 40, playerH = 80;
    private int laneWidth = 120;
    private java.util.List<Rectangle> enemies = new ArrayList<>();
    private Random rnd = new Random();

    private double speed = 4.0;        // base scroll speed
    private double speedIncreaseRate = 0.001; // increases over time
    private int spawnTimer = 0, spawnInterval = 90;
    private int score = 0;
    private int fuel = 100;            // fuel percent
    private boolean crashed = false;
    private int crashTimer = 0;

    // use Swing Timer explicitly
    private javax.swing.Timer timer;

    public CarRacing() {
        setPreferredSize(new Dimension(W,H));
        setBackground(new Color(30,130,60));

        setFocusable(true);
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if (crashed) return;
                if (e.getKeyCode()==KeyEvent.VK_LEFT){
                    playerX = Math.max(40, playerX - laneWidth/2);
                } else if (e.getKeyCode()==KeyEvent.VK_RIGHT){
                    playerX = Math.min(W - 40 - playerW, playerX + laneWidth/2);
                } else if (e.getKeyCode()==KeyEvent.VK_UP){
                    // temporary speed boost, costs fuel
                    if (fuel>5) { speed += 2; fuel = Math.max(0, fuel - 5); }
                }
            }
        });

        timer = new javax.swing.Timer(20, this);
        timer.start();
    }

    private void spawnEnemy(){
        int lane = rnd.nextInt(3); // 3 lanes
        int ex = 40 + lane*laneWidth + (laneWidth - 60)/2;
        enemies.add(new Rectangle(ex, -100, 60, 100));
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if (!crashed){
            speed += speedIncreaseRate;
            spawnTimer++;
            if (spawnTimer >= spawnInterval){
                spawnEnemy();
                spawnTimer = 0;
                // slowly decrease spawn interval for difficulty
                spawnInterval = Math.max(45, spawnInterval - (int)(speed/2));
            }
            // move enemies
            Iterator<Rectangle> it = enemies.iterator();
            while (it.hasNext()){
                Rectangle r = it.next();
                r.y += (int)Math.ceil(speed);
                if (r.y > H) { it.remove(); score += 10; fuel = Math.max(0, fuel - 1); }
                // collision
                Rectangle playerRect = new Rectangle(playerX, playerY, playerW, playerH);
                if (r.intersects(playerRect)){
                    crashed = true;
                    crashTimer = 0;
                }
            }
            // passive fuel drain
            if (rnd.nextInt(30)==0) fuel = Math.max(0, fuel - 1);
            if (fuel == 0) { crashed = true; } // out of fuel => crash/stop
        } else {
            crashTimer++;
            if (crashTimer > 80){
                // restart automatically after crash
                resetGame();
            }
        }
        repaint();
    }

    private void resetGame(){
        enemies.clear();
        playerX = W/2 - 20;
        speed = 4.0;
        spawnInterval = 90;
        score = 0;
        fuel = 100;
        crashed = false;
    }

    @Override
    protected void paintComponent(Graphics g0){
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D)g0;
        // draw road
        g.setColor(new Color(60,60,60));
        g.fillRect(30,0,W-60,H);
        // lane markers
        g.setColor(Color.WHITE);
        for (int i=0;i<4;i++){
            int y = (int)((i*120 + (System.currentTimeMillis()/8)%120));
            g.fillRect(W/2 - 5, y, 10, 60);
        }
        // player car
        if (!crashed){
            g.setColor(Color.BLUE);
            g.fillRoundRect(playerX, playerY, playerW, playerH, 8,8);
        } else {
            // crash animation: rotate fragments
            g.setColor(Color.RED);
            g.fillOval(playerX-10, playerY-10 + crashTimer/2, playerW+20, playerH+20);
            g.setColor(Color.ORANGE);
            g.drawString("CRASH!", W/2 - 20, H/2);
        }
        // enemies
        g.setColor(Color.DARK_GRAY);
        for (Rectangle r: enemies){
            g.setColor(Color.RED);
            g.fillRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.BLACK);
            g.drawRect(r.x, r.y, r.width, r.height);
        }
        // HUD
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Speed: " + String.format("%.1f", speed), 10, 38);
        g.drawString("Fuel: " + fuel + "%", 10, 56);
        g.drawString("Use ← → to move, ↑ for boost", 10, H-10);
    }

    public static void main(String[] args){
        JFrame f = new JFrame("Car Racing - Top Down");
        CarRacing game = new CarRacing();
        f.add(game);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
