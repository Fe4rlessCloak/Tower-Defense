package game.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import game.GameManager;

public class ControlPanel extends JPanel {
    private final JLabel goldLabel;
    private final JLabel scoreLabel;
    private final JToggleButton buildToggle;
    private final GameManager manager;


    public ControlPanel(GameManager manager) {
        this.manager = manager;
        this.setPreferredSize(new Dimension(200, 600));
        this.setBackground(new Color(30, 30, 30));


        goldLabel = new JLabel("Gold: 0");
        goldLabel.setForeground(Color.WHITE);
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);


        buildToggle = new JToggleButton("Build Mode");
        buildToggle.setFocusable(false);
        buildToggle.addActionListener(e -> {
        boolean enabled = buildToggle.isSelected();
        manager.setBuildMode(enabled);
        buildToggle.setText(enabled ? "Building: ON" : "Building: OFF");

    });


        this.add(goldLabel);
        this.add(scoreLabel);
        this.add(buildToggle);

        // Refresh UI every 200ms from EDT
        Timer refresh = new Timer(200, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (manager.getUser() != null) {
                    goldLabel.setText("Gold: " + manager.getUser().getGold());
                    scoreLabel.setText("Score: " + manager.getUser().getScore());
                }
            }
        });
        
        refresh.start();

    }
}