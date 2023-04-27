import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;

public class GUI {
    
    private JFrame frame;
    
    private JTextField kField;
    private JTextField tiField;
    private JTextField tdField;
    private JTextField hysteresisField;		
    private JTextField amplitudeField;

    private Mode mode;

    private JButton startButton;
    private JButton stopButton;
    private JButton beamButton;
    private JButton tank1Button;
    private JButton tank2Button;
    
    // Attributes to be sent to Regul.
    private double  relayAmp         = -100_000;         // Arbitrary values unlikely to be chosen by the
    private double  relayHysteresis  = -100_000;         // user are to check for initialization
    private int     processType = 0;                     // 0 - OFF,  1 - beam, 2 - upper tank, 3 - lower tank
    private boolean running = true; 
	
    ModeMonitor modeMon;
    
    public GUI() {

        // create the frame
        frame = new JFrame("PID Control Autotuner");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // create the text fields for the controller parameters
        kField  = new JTextField("0.0", 10);
        tiField = new JTextField("0.0", 10);
        tdField = new JTextField("0.0", 10);

        // Create the text fields for the relay
        hysteresisField = new JTextField("NULL", 10);
        amplitudeField  = new JTextField("NULL", 10);

        // Create the relevant buttons
        startButton = new JButton("Start");
        stopButton  = new JButton("Stop");
        beamButton  = new JButton("Beam");
        tank1Button = new JButton("Upper Tank");
        tank2Button = new JButton("Lower Tank");

        // Temporary placeholder for plotting tool in control department toolbox
        ImageIcon sineIcon = new ImageIcon("sineWave.png");
        JLabel graphIcon = new JLabel(sineIcon);
        
        // Create a controllerpanel that is to be added to Borderlayout.NORTH
        JPanel controllerPanel = new JPanel(new GridLayout(4, 5, 10, 10));
        controllerPanel.setBackground(Color.lightGray);

        // Create the textfield descriptions and make the letter larger
        JLabel descrContr = new JLabel("Controller Parameters");
        JLabel descrRelay = new JLabel("Relay Parameters");
        descrContr.setFont(new Font("Serif", Font.BOLD, 14));
        descrRelay.setFont(new Font("Serif", Font.BOLD, 14));

        // Add 5 components to the first row of the control pannel
        controllerPanel.add(new JLabel("FRTN01"));
        controllerPanel.add(descrContr);
        controllerPanel.add(new JLabel()); 
        controllerPanel.add(descrRelay);
        controllerPanel.add(new JLabel());
        
        // Add 5 components to the secound row of the control pannel
        controllerPanel.add(new JLabel("Real-time Systems"));
        controllerPanel.add(new JLabel("K:"));
        controllerPanel.add(kField);
        controllerPanel.add(new JLabel("Hysteresis:"));
        controllerPanel.add(hysteresisField);
        
        // Add 5 components to the third row of the control pannel
        controllerPanel.add(new JLabel("Project - PID Autotuner"));
        controllerPanel.add(new JLabel("Ti:"));
        controllerPanel.add(tiField);
        controllerPanel.add(new JLabel("Amplitude:"));
        controllerPanel.add(amplitudeField);
        
        // Add 5 components to the fourth row of the control pannel
        controllerPanel.add(new JLabel());
        controllerPanel.add(new JLabel("Td:"));
        controllerPanel.add(tdField);
        controllerPanel.add(new JLabel());
        controllerPanel.add(new JLabel());

        // Create a relaypanel that is to be added to Borderlayout.NORTH
        // It will contain both textfields and relevant buttons
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
        
        // Ad the panels to the JFrame
        frame.add(controllerPanel, BorderLayout.NORTH);
        frame.add(relayPanel, BorderLayout.WEST);
        frame.add(graphIcon, BorderLayout.EAST); // To be replaced with the actual graph
        
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

                // Check if starting conditions have been met
                if (relayAmp != -100_000 && relayHysteresis != -100_000 && processType != 0 && running) {
					startButton.setBackground(new Color(144, 238, 144));
					// The modeMonitor is initialized with the values given
					modeMon.setMode(relayAmp, relayHysteresis, processType);
			        

				} else if (!running) {
                    // Do nothing

                } else {
                    // starting criteria not fullfilled
                    startButton.setBackground(Color.RED);
                    JOptionPane.showMessageDialog(frame, "Cannot start the process without the required information." +
                                                        "\nPlease make sure to provide the hysteresis value, amplitude value, " +
                                                        "\nand process type before starting. Please try again.", "Warning", JOptionPane.INFORMATION_MESSAGE);    
				}
            }
        });

        // add an action listener to the stop button
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // regul.stop();
                relayAmp = 0;
                relayHysteresis = 0;
                running = false;

	        processType = 0;

		modeMon.setMode(relayAmp, relayHysteresis, processType);

		startButton.setBackground(Color.RED);
                stopButton.setBackground(Color.RED);
                beamButton.setBackground(Color.RED);
                tank1Button.setBackground(Color.RED);
                tank2Button.setBackground(Color.RED);
                JOptionPane.showMessageDialog(frame, "Autotuning stopped: User pressed stop button.\n" +
                                                     "Please restart the program.", "Warning", JOptionPane.INFORMATION_MESSAGE);    
        
            }
        });
        
        frame.setVisible(true);
    }

    public void setModeMon(ModeMonitor modeMon) {
	this.modeMon = modeMon;
    }
	

    public static void main(String[] args) {
        GUI gui = new GUI();
    }

    public enum Mode{
	OFF, BEAM, UPPERTANK, LOWERTANK;
    }
}
