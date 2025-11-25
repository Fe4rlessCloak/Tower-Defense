package game.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Color;

public class ControlPanel extends JPanel {
    public ControlPanel() {
        this.setPreferredSize(new Dimension(200, 600));
        this.setBackground(new Color(30, 30, 30)); // Darker Grey
        
        JLabel label = new JLabel("Stats Panel");
        label.setForeground(Color.WHITE);
        this.add(label);
    }
}