import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PingPong extends JPanel implements ActionListener {
    private static final int W = 800, H = 500;
    private Rectangle leftPaddle, rightPaddle;
    private int paddleW = 12, paddleH = 80;
    private int leftScore = 0, rightScore = 0;

    private double ballX=W/2, ballY=H/2, ballVX=4, ballVY=2;
    private int ballSize = 14;
    private boolean wPressed=false, sPressed=false, up=false, down=false;
    private boolean singlePlayer = true; // AI on by default
    private double aiSpeed = 3.0;

    private javax.swing.Timer timer;

    public PingPong(){
        setPreferredSize(new Dimension(W,H));
        setBackground(Color.BLACK);
        leftPaddle = new Rectangle(30, H/2 - paddleH/2, paddleW, paddleH);
        rightPaddle = new Rectangle(W-30-paddleW, H/2 - paddleH/2, paddleW, paddleH);

        setFocusable(true);
        addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_W) wPressed=true;
                if (e.getKeyCode()==KeyEvent.VK_S) sPressed=true;
                if (e.getKeyCode()==KeyEvent.VK_UP) up=true;
                if (e.getKeyCode()==KeyEvent.VK_DOWN) down=true;
                if (e.getKeyCode()==KeyEvent.VK_A) singlePlayer = !singlePlayer;
                if (e.getKeyCode()==KeyEvent.VK_R){ reset(); }
            }
            public void keyReleased(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_W) wPressed=false;
                if (e.getKeyCode()==KeyEvent.VK_S) sPressed=false;
                if (e.getKeyCode()==KeyEvent.VK_UP) up=false;
                if (e.getKeyCode()==KeyEvent.VK_DOWN) down=false;
            }
        });

        timer = new javax.swing.Timer(16, this);
        timer.start();
    }

    private void resetBall(boolean toLeft){
        ballX = W/2; ballY = H/2;
        ballVX = (toLeft? -4:4);
        ballVY = (Math.random()*4 - 2);
    }

    private void reset(){
        leftScore=0; rightScore=0; resetBall(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        // paddle movement
        if (wPressed) leftPaddle.y = Math.max(0, leftPaddle.y - 6);
        if (sPressed) leftPaddle.y = Math.min(H - paddleH, leftPaddle.y + 6);
        if (!singlePlayer){
            if (up) rightPaddle.y = Math.max(0, rightPaddle.y - 6);
            if (down) rightPaddle.y = Math.min(H - paddleH, rightPaddle.y + 6);
        } else {
            // simple AI: follow ball with limited speed
            if (rightPaddle.y + paddleH/2 < ballY) rightPaddle.y += aiSpeed;
            if (rightPaddle.y + paddleH/2 > ballY) rightPaddle.y -= aiSpeed;
            rightPaddle.y = Math.max(0, Math.min(H - paddleH, rightPaddle.y));
        }

        // ball physics
        ballX += ballVX;
        ballY += ballVY;

        if (ballY <= 0 || ballY + ballSize >= H) ballVY *= -1;

        Rectangle ballRect = new Rectangle((int)ballX, (int)ballY, ballSize, ballSize);
        if (ballRect.intersects(leftPaddle)){
            ballVX = Math.abs(ballVX) + 0.2;
            // change angle based on hit position
            double hit = ((ballY + ballSize/2) - (leftPaddle.y + paddleH/2)) / (paddleH/2);
            ballVY = hit * 5;
        }
        if (ballRect.intersects(rightPaddle)){
            ballVX = -Math.abs(ballVX) - 0.2;
            double hit = ((ballY + ballSize/2) - (rightPaddle.y + paddleH/2)) / (paddleH/2);
            ballVY = hit * 5;
        }

        // scoring
        if (ballX < 0){
            rightScore++;
            resetBall(false);
        } else if (ballX > W){
            leftScore++;
            resetBall(true);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0){
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D)g0;
        g.setColor(Color.WHITE);
        g.fillRect(leftPaddle.x, leftPaddle.y, leftPaddle.width, leftPaddle.height);
        g.fillRect(rightPaddle.x, rightPaddle.y, rightPaddle.width, rightPaddle.height);
        g.fillOval((int)ballX, (int)ballY, ballSize, ballSize);

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(leftScore + "   |   " + rightScore, W/2 - 40, 30);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("W/S = Left | Up/Down = Right (press A to toggle AI)", 10, H-10);
    }

    public static void main(String[] args){
        JFrame f = new JFrame("Ping Pong");
        PingPong p = new PingPong();
        f.add(p);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
