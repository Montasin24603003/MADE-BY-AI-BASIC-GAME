import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DinoGame extends JPanel implements ActionListener, KeyListener {

    int dinoY = 300;
    int dinoDY = 0;
    boolean jumping = false;

    Rectangle cactus = new Rectangle(600, 300, 30, 50);

    javax.swing.Timer timer = new javax.swing.Timer(10, this);

    public DinoGame() {
        setFocusable(true);
        addKeyListener(this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.black);
        g.fillRect(100, dinoY, 40, 50);

        g.setColor(Color.green);
        g.fillRect(cactus.x, cactus.y, cactus.width, cactus.height);
    }

    public void actionPerformed(ActionEvent e) {
        cactus.x -= 7;

        if (cactus.x < -50)
            cactus.x = 700;

        if (jumping) {
            dinoY += dinoDY;
            dinoDY += 1;

            if (dinoY >= 300) {
                jumping = false;
                dinoY = 300;
            }
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !jumping) {
            jumping = true;
            dinoDY = -18;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame f = new JFrame("Dino Runner");
        f.setSize(700, 400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new DinoGame());
        f.setVisible(true);
    }
}
