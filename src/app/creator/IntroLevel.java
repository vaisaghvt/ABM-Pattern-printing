/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.creator;

import datatypes.ModelDetails;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import javax.swing.*;

/**
 *
 * @author vaisagh
 */
class IntroLevel extends AbstractLevel {

    JPanel descriptionArea;
    JTextField name;
    private JTextField xSize;
    private JTextField ySize;
    private JTextField scale;
    private JCheckBox latticeModel;

    public IntroLevel(ModelDetails model, JFrame frame, JLabel statusBar, JPanel buttonArea) {
        super(model, frame, statusBar, buttonArea);
        descriptionArea = new JPanel();
          initializeDescriptionArea();
    }

    @Override
    public void setUpLevel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                previousButton.setEnabled(false);
                clearButton.setEnabled(false);
                nextButton.setEnabled(true);

                frame.add(descriptionArea, BorderLayout.CENTER);

                descriptionArea.setSize(frame.getSize());


                descriptionArea.setEnabled(true);
                descriptionArea.setVisible(true);

                descriptionArea.repaint();

                frame.repaint();
                frame.validate();
            }
        });

    }

    @Override
    public void clearUp() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                model.setTitle(name.getText());
                model.setXSize(xSize.getText());
                model.setYSize(ySize.getText());
                model.setScale(scale.getText());
                descriptionArea.setEnabled(false);
                descriptionArea.setVisible(false);
                frame.remove(descriptionArea);
            }
        });

    }

    @Override
    public void draw(Graphics g) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void clearAllPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void initializeDescriptionArea() {

        descriptionArea.setLayout(new GridLayout(4, 2));
        descriptionArea.setBackground(Color.lightGray);

        name = new JTextField();
        name.setColumns(20);

        descriptionArea.add(new JLabel("Name : "));
        descriptionArea.add(name);
        name.setText("Default");


        xSize = new JTextField();
        xSize.setColumns(20);
        descriptionArea.add(new JLabel("X size(meters) :"));
        descriptionArea.add(xSize);
        xSize.setText("3");


        ySize = new JTextField();
        ySize.setColumns(20);
        descriptionArea.add(new JLabel("Y size (meters) :"));
        descriptionArea.add(ySize);
        ySize.setText("3");


        scale = new JTextField();
        scale.setColumns(20);
        descriptionArea.add(new JLabel("Scale (pix / m) :"));
        descriptionArea.add(scale);
        scale.setText("100");




    }

    public void reloadValuesFromModel() {
        name.setText(model.getTitle());
        xSize.setText(Integer.toString(model.getxSize()));
        ySize.setText(Integer.toString(model.getySize()));
        scale.setText(Integer.toString(model.getScale()));
    }

    @Override
    public String getName() {
        return "Model Details Level";
    }
}
