import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;

public class GUI {
    private JFrame frame;
    private JTextField kField;
    private JTextField tiField;
    private JTextField tdField;
    
    // To be attached with actionlisteners
    private JTextField hysteresisField;		
    private JTextField amplitudeField;
    private JButton startButton;
    private JButton stopButton;
    private JButton beamButton;
    private JButton tank1Button;
    private JButton tank2Button;
    
    //Attributes to be sent to Regul.
    private double relayAmp = 0;
    private double relayHysteresis = 0;
    private int processType = 0; // 1, 2, 3 (beam, upper tank, lower tank)
	
	// Public gui should take parameters that correspond to the objects to be used in the project
    public GUI() {

        // create the frame
        frame = new JFrame("Tune PID Controller");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        // create the text fields for the controller parameters
        kField = new JTextField("0.0", 10);
        tiField = new JTextField("0.0", 10);
        tdField = new JTextField("0.0", 10);

        // Create the text fields for the relay
        hysteresisField = new JTextField("0.0", 10);
        amplitudeField = new JTextField("0.0", 10);

        // create the start button
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        beamButton = new JButton("Beam");
        tank1Button = new JButton("Upper Tank");
        tank2Button = new JButton("Lower Tank");

        
        ImageIcon sineIcon = new ImageIcon("sineWave.png");
        JLabel graphIcon = new JLabel(sineIcon);
        

        // create the panel and add components to it
        JPanel controllerPanel = new JPanel(new GridLayout(4, 5, 10, 10));

        JLabel descrContr = new JLabel("Controller Parameters");
        descrContr.setFont(new Font("Serif", Font.BOLD, 14));
        JLabel descrRelay = new JLabel("Relay Parameters");
        descrRelay.setFont(new Font("Serif", Font.BOLD, 14));

        

        controllerPanel.setBackground(Color.lightGray);

        controllerPanel.add(new JLabel("FRTN01"));
        controllerPanel.add(descrContr);
        controllerPanel.add(new JLabel()); // Add a blank label to take up the second cell
        controllerPanel.add(descrRelay);
        controllerPanel.add(new JLabel());

        controllerPanel.add(new JLabel("Real-time Systems"));
        controllerPanel.add(new JLabel("K:"));
        controllerPanel.add(kField);
        controllerPanel.add(new JLabel("Hysteresis:"));
        controllerPanel.add(hysteresisField);
        
        controllerPanel.add(new JLabel("Project - PID Autotuner"));
        controllerPanel.add(new JLabel("Ti:"));
        controllerPanel.add(tiField);
        controllerPanel.add(new JLabel("Amplitude:"));
        controllerPanel.add(amplitudeField);
        
        controllerPanel.add(new JLabel());
        controllerPanel.add(new JLabel("Td:"));
        controllerPanel.add(tdField);
        controllerPanel.add(new JLabel());
        controllerPanel.add(new JLabel());


        JPanel relayPanel = new JPanel(new GridLayout(7, 1, 10, 10));

        relayPanel.setBackground(Color.lightGray);
        relayPanel.setPreferredSize(new Dimension(200, 300));
        
        JLabel descrStart = new JLabel("Important:");
        descrStart.setFont(new Font("Serif", Font.BOLD, 15));
        
        relayPanel.add(descrStart);
        relayPanel.add(new JLabel("<html>Please make sure you<br/>set the amplitude<br/> and the frequency of the<br/>relay before initializing<br/>the tuner<html>")); 
        relayPanel.add(startButton);
        relayPanel.add(stopButton);
        relayPanel.add(beamButton);
        relayPanel.add(tank1Button);
        relayPanel.add(tank2Button);
       
        
        frame.add(controllerPanel, BorderLayout.NORTH);
        frame.add(relayPanel, BorderLayout.WEST);
        //frame.add(iconLabel, BorderLayout.SOUTH);
        frame.add(graphIcon, BorderLayout.EAST);
        
		// -----------------RelayListeners----------------------
        hysteresisField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                relayHysteresis = Double.parseDouble(hysteresisField.getText());
                System.out.println("The value of the relayHysteresis attribute is no" + relayHysteresis);
                System.out.println("The value of the relayAmp attribute is no" + relayAmp);
            }
        });
        	
        amplitudeField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                relayAmp = Double.parseDouble(amplitudeField.getText());
                System.out.println("The value of the relayHysteresis attribute is no" + relayHysteresis);
                System.out.println("The value of the relayAmp attribute is no" + relayAmp);
            }
        });
        
        // -----------------ButtonListeners----------------------
        
        beamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processType = 1;
                beamButton.setBackground(new Color(144, 238, 144));
                tank1Button.setBackground(Color.LIGHT_GRAY);
                tank2Button.setBackground(Color.LIGHT_GRAY);
                System.out.println("The process type is now beam, if response is 1: " + processType);
            }
        });
        
        tank1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processType = 2;
                beamButton.setBackground(Color.LIGHT_GRAY);
                tank1Button.setBackground(new Color(144, 238, 144));
                tank2Button.setBackground(Color.LIGHT_GRAY);
                System.out.println("The process type is now upper tank, if response is 2: " + processType);
            }
        });
        
        tank2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processType = 3;
                beamButton.setBackground(Color.LIGHT_GRAY);
                tank1Button.setBackground(Color.LIGHT_GRAY);
                tank2Button.setBackground(new Color(144, 238, 144));
                System.out.println("The process type is now upper tank, if response is 2: " + processType);
            }
        });
        
        // add an action listener to the start button
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Check is starting conditions have been met
                if (relayAmp != 0 && relayHysteresis != 0 && processType != 0 ) {
					startButton.setBackground(new Color(144, 238, 144));
					// here the regul method should be initialized with the regul.init(method);
				} else {
					startButton.setBackground(Color.RED);
				}
            }
        });
        
              // add an action listener to the stop button
        tank1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Set attribute
            }
        });
              // add an action listener to the stop button
        tank2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Set attribute
            }
        });
        

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
    }
}
