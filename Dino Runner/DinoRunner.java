import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DinoRunner extends JPanel implements ActionListener {
    private static final int W = 800, H = 300;
    private int dinoX = 80, dinoY = H - 80;
    private int dinoW = 44, dinoH = 44;
    private boolean jumping = false;
    private double velY = 0;
    private final double GRAV = 0.9;
    private final double JUMP_V = -14;

    private java.util.List<Rectangle> obstacles = new ArrayList<>();
    private Random rnd = new Random();
    private double speed = 6;
    private int spawnTimer = 0, spawnInterval = 80;
    private int score = 0;
    private boolean gameOver = false;

    private javax.swing.Timer timer;

    public DinoRunner(){
        setPreferredSize(new Dimension(W,H));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_SPACE){
                    if (!gameOver){
                        if (!jumping){ jumping = true; velY = JUMP_V; }
                    } else {
                        restart();
                    }
                }
            }
        });
        timer = new javax.swing.Timer(16, this);
        timer.start();
    }

    private void spawnObstacle(){
        int h = 30 + rnd.nextInt(30);
        obstacles.add(new Rectangle(W, H - 50 - h, 20 + rnd.nextInt(20), h));
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if (!gameOver){
            // jump physics
            if (jumping){
                velY += GRAV;
                dinoY += velY;
                if (dinoY >= H - 80){
                    dinoY = H - 80;
                    jumping = false;
                    velY = 0;
                }
            }

            spawnTimer++;
            if (spawnTimer >= spawnInterval){
                spawnObstacle();
                spawnTimer = 0;
                // increase difficulty
                spawnInterval = Math.max(40, spawnInterval - 1);
                speed += 0.1;
            }
            // move obstacles
            Iterator<Rectangle> it = obstacles.iterator();
            while (it.hasNext()){
                Rectangle r = it.next();
                r.x -= (int)Math.ceil(speed);
                if (r.x + r.width < 0) { it.remove(); score += 5; }
                Rectangle d = new Rectangle(dinoX, dinoY, dinoW, dinoH);
                if (r.intersects(d)) { gameOver = true; }
            }
        }
        repaint();
    }

    private void restart(){
        obstacles.clear();
        dinoY = H - 80;
        jumping = false; velY = 0;
        speed = 6; spawnInterval = 80; score = 0; gameOver = false;
    }

    @Override
    protected void paintComponent(Graphics g0){
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D)g0;
        g.setColor(Color.GRAY);
        g.fillRect(0, H - 40, W, 40); // ground

        // Dino (simple running animation: alternate legs)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(dinoX, dinoY, dinoW, dinoH);
        g.setColor(Color.BLACK);
        if (!jumping){
            int leg = (score/5)%2;
            if (leg==0){
                g.fillRect(dinoX+5, dinoY + dinoH, 8, 10);
                g.fillRect(dinoX+30, dinoY + dinoH, 8, 6);
            } else {
                g.fillRect(dinoX+5, dinoY + dinoH, 8, 6);
                g.fillRect(dinoX+30, dinoY + dinoH, 8, 10);
            }
        }

        // obstacles
        g.setColor(new Color(34,139,34));
        for (Rectangle r : obstacles){
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
        if (gameOver){
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("GAME OVER - Press SPACE to restart", W/2 - 220, H/2);
        }
    }

    public static void main(String[] args){
        JFrame f = new JFrame("Dino Runner");
        DinoRunner g = new DinoRunner();
        f.add(g);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
