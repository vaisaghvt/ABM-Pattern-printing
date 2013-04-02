/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.creator;

import datatypes.DestinationPattern;
import datatypes.ModelDetails;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author vaisagh
 */
class FinalLevel extends AbstractLevel {

    private DrawingPanel interactionArea;
    private AgentInformationDialog agentInformationFrame;

    public FinalLevel(ModelDetails model, JFrame frame, JLabel statusBar, JPanel buttonArea, DrawingPanel interactionArea) {
        super(model, frame, statusBar, buttonArea);
        this.interactionArea = interactionArea;
    }

    @Override
    public void setUpLevel() {
        nextButton.setText("Finish and Save");
        clearButton.setEnabled(false);
        previousButton.setEnabled(true);
        nextButton.setEnabled(true);

        agentInformationFrame = new AgentInformationDialog(model);


        frame.setTitle(model.getTitle() + "  - Final Stage -");


        frame.setSize(model.getxSize() * model.getScale() + 8, model.getySize() * model.getScale() + 100);
        frame.repaint();


        frame.add(interactionArea, BorderLayout.CENTER);
        interactionArea.setBackground(Color.lightGray);
        interactionArea.setEnabled(false);
        statusBar.setText("Final stage");
        interactionArea.setCurrentLevel(this);

        interactionArea.repaint();
    }

    @Override
    public void clearUp() {
        nextButton.setText("Next");
        interactionArea.setEnabled(false);
        frame.remove(interactionArea);
    }

    @Override
    public void draw(Graphics g) {





        if(!model.getPatterns().isEmpty()){
            for(DestinationPattern pattern: model.getPatterns()){
                super.drawPattern(g, pattern);
            }
        }


    }

    @Override
    public void clearAllPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "Final Level";
    }
}
