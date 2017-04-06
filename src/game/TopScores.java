package game;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TopScores extends JFrame{
    private Point point;
    private JPanel panel, panelEasy, panelIntermediate, panelExpert;
    
    public TopScores(Point point){
        this.point = point;
        movePoint();
        initComponents();
    }
    
    public void movePoint(){
        point.x += 20;
        point.y += 20;
    }
    
    public void initComponents(){
        setTitle("Top Scores");
        setSize(600, 400);
        setPreferredSize(new Dimension(600, 400));
        setLocation(point);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        
        panel = new JPanel(new CardLayout());
        panel.setBackground(Color.yellow);
        panel.setBounds(0, 0, 575, 355);
        
        panelEasy = new JPanel();
        panelIntermediate = new JPanel();
        panel.add(panelEasy, "Panel1");
        panel.add(panelIntermediate, "Panel2");
        add(panel);
        setVisible(true);
    }
    
    public static void main(String[] args){
        new TopScores(new Point(600, 300));
    }
}
