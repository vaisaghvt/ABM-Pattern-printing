package app.creator;

import datatypes.ModelDetails;

import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: vaisaghvt
 * Date: 29/3/13
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentInformationDialog extends JDialog implements ActionListener {

    private JTextField maxSpeedText = new JTextField(5);
    private JTextField minSpeedText = new JTextField(5);
    private JTextField meanSpeedText = new JTextField(5);
    private JTextField sDevSpeedText = new JTextField(5);
    private JButton setButton = new JButton("set");

    double minSpeedValue =0.0;
    double maxSpeedValue =2.6;
    double meanSpeedValue = 1.3;
    double sDevSpeedValue =0.0;
    private final ModelDetails details;

    public AgentInformationDialog(ModelDetails details){
        this.details = details;
        details.setMaxSpeed(maxSpeedValue);
        details.setMinSpeed(minSpeedValue);
        details.setMeanSpeed(meanSpeedValue);
        details.setSDevSpeed(sDevSpeedValue);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getContentPane().setLayout(new BorderLayout());
                JPanel mainPanel = new JPanel(new GridLayout(4,2));

                mainPanel.add(new JLabel("Max Speed:"));
                mainPanel.add(maxSpeedText);
                maxSpeedText.setText(String.valueOf(maxSpeedValue));


                mainPanel.add(new JLabel("Min Speed:"));
                mainPanel.add(minSpeedText);
                minSpeedText.setText(String.valueOf(minSpeedValue));

                mainPanel.add(new JLabel("Mean Speed:"));
                mainPanel.add(meanSpeedText);
                meanSpeedText.setText(String.valueOf(meanSpeedValue));

                mainPanel.add(new JLabel("Sdev Speed:"));
                mainPanel.add(sDevSpeedText);
                sDevSpeedText.setText(String.valueOf(sDevSpeedValue));

                mainPanel.add(setButton);


                getContentPane().add(mainPanel, BorderLayout.CENTER);
                getContentPane().add(setButton, BorderLayout.SOUTH);

                setButton.addActionListener(AgentInformationDialog.this);

                setSize(400, 300);
                setVisible(true);
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == setButton){
            try{
                maxSpeedValue = Double.parseDouble(maxSpeedText.getText());
                minSpeedValue = Double.parseDouble(minSpeedText.getText());
                meanSpeedValue = Double.parseDouble(meanSpeedText.getText());
                sDevSpeedValue = Double.parseDouble(sDevSpeedText.getText());

                if(minSpeedValue<=meanSpeedValue && meanSpeedValue<=maxSpeedValue){
                    details.setMaxSpeed(maxSpeedValue);
                    details.setMinSpeed(minSpeedValue);
                    details.setMeanSpeed(meanSpeedValue);
                    details.setSDevSpeed(sDevSpeedValue);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            dispose();
                        }
                    });
                    return;
                }

                JOptionPane.showMessageDialog(this, "Invalid values", "Error dialog",JOptionPane.ERROR_MESSAGE);
            }catch(NumberFormatException exception){
                JOptionPane.showMessageDialog(this, "Invalid values", "Error dialog",JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

    }


}
