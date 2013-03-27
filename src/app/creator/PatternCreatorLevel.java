/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.creator;


import datatypes.DestinationPattern;
import datatypes.ModelDetails;
import datatypes.PatternLine;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author vaisagh
 */
public class PatternCreatorLevel extends AbstractLevel implements MouseListener, MouseMotionListener, ListSelectionListener, ActionListener {

    private DrawingPanel interactionArea;

    private Point2d prevPoint;
    private Point2d currentSelectedPoint;
    private JList<DestinationPattern> listOfPatternNamesInFrame;
    private JButton addPatternButton;
    private DestinationPattern currentPattern;
    private DefaultListModel<DestinationPattern> listModel;
    private JButton removePatternButton;
    private Point currentPoint;
    private JFrame patternChoosingFrame;
    private boolean setupComplete = false;


    public PatternCreatorLevel(ModelDetails model, JFrame frame, JLabel statusBar, JPanel buttonArea, DrawingPanel interactionArea) {
        super(model, frame, statusBar, buttonArea);
        this.interactionArea = interactionArea;


    }

    @Override
    public void setUpLevel() {
        prevPoint = null;
        currentSelectedPoint = null;

        if (model.getPatterns() == null || model.getPatterns().isEmpty()) {
            listModel = new DefaultListModel<DestinationPattern>();
            interactionArea.setEnabled(false);
        } else {
            listModel = new DefaultListModel<DestinationPattern>();
            for (DestinationPattern pattern : model.getPatterns()) {
                listModel.addElement(pattern);
            }
            interactionArea.setEnabled(true);
        }


        previousButton.setEnabled(true);
        clearButton.setEnabled(true);
        nextButton.setEnabled(true);


        frame.add(interactionArea, BorderLayout.CENTER);
        interactionArea.setBackground(Color.white);
        interactionArea.setCurrentLevel(PatternCreatorLevel.this);
        interactionArea.setVisible(true);
        interactionArea.setSize(frame.getSize());
        interactionArea.repaint();
        interactionArea.addMouseListener(PatternCreatorLevel.this);
        interactionArea.addMouseMotionListener(PatternCreatorLevel.this);
        frame.repaint();
        frame.revalidate();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                patternChoosingFrame = new JFrame("Choose Pattern Number");
                patternChoosingFrame.setLayout(new BorderLayout());


                listOfPatternNamesInFrame = new JList<DestinationPattern>(listModel);
                listOfPatternNamesInFrame.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                listOfPatternNamesInFrame.setLayoutOrientation(JList.VERTICAL);
                listOfPatternNamesInFrame.setVisibleRowCount(-1);
                listOfPatternNamesInFrame.addListSelectionListener(PatternCreatorLevel.this);
                if (!listModel.isEmpty()) {
                    listOfPatternNamesInFrame.setSelectedIndex(0);
                    currentPattern = listOfPatternNamesInFrame.getSelectedValue();
                }

                JScrollPane listScroller = new JScrollPane(listOfPatternNamesInFrame);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new GridLayout(1, 2));

                addPatternButton = new JButton("Add new pattern");
                addPatternButton.addActionListener(PatternCreatorLevel.this);


                removePatternButton = new JButton("Remove Selected pattern");
                removePatternButton.addActionListener(PatternCreatorLevel.this);

                buttonPanel.add(addPatternButton);
                buttonPanel.add(removePatternButton);

                patternChoosingFrame.getContentPane().add(listScroller, BorderLayout.CENTER);
                patternChoosingFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

                patternChoosingFrame.setVisible(true);
                patternChoosingFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                patternChoosingFrame.setSize(400, 600);
                setupComplete = true;

                interactionArea.revalidate();
            }
        });


    }

    @Override
    public void clearUp() {
        model.setPatterns(listModel.elements());
        interactionArea.removeMouseListener(this);
        interactionArea.removeMouseMotionListener(this);

        interactionArea.setEnabled(false);
        frame.remove(interactionArea);
        patternChoosingFrame.dispose();
    }

    @Override
    public void draw(Graphics g) {
//        super.drawGridLines(g);

        if (currentPoint != null) {
            super.drawCurrentPoint(g, currentPoint);

        }

        ArrayList<Point2d> points = new ArrayList<Point2d>();
        if(currentSelectedPoint!=null){
            points.add(currentSelectedPoint);
            super.drawPoints(g, points);
        }

        if (currentPattern != null) {
            super.drawPattern(g, currentPattern);
        }


    }

    @Override
    public void clearAllPoints() {

        currentPattern.removeAllLines();
        interactionArea.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {



        if (interactionArea.isEnabled()) {
            int xSize = model.getxSize();
            int ySize = model.getySize();
            int scale = model.getScale();
            Point2d point = new Point2d();
            if(currentPoint==null){
                return;
            }
            if (currentPoint.getX() > xSize * scale || currentPoint.getY() > ySize * scale
                    || currentPoint.getX() < 0 || currentPoint.getY() < 0) {
                return;
            }


            point.x = currentPoint.getX() / scale;
            point.y = currentPoint.getY() / scale;

            if (currentSelectedPoint == null) {
                //i.e. This is first click
                currentSelectedPoint = point;
                statusBar.setText("First point added:"+currentSelectedPoint);
            } else {

                prevPoint = currentSelectedPoint;
                currentSelectedPoint = point;
                statusBar.setText("Second point added" + prevPoint + "," + currentSelectedPoint);

                PatternLine line = new PatternLine(prevPoint, currentSelectedPoint);
                currentPattern.addPatternLine(line);
                prevPoint = null;
                currentSelectedPoint = null;
            }


            interactionArea.repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (interactionArea.isEnabled()) {
            currentPoint = new Point(e.getX(), e.getY());
            interactionArea.repaint();
        }
    }


    @Override
    public String getName() {
        return "Pattern Creator Level";
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

        if (!e.getValueIsAdjusting()&&setupComplete) {

            if (listOfPatternNamesInFrame.getSelectedIndex() == -1) {
                //No selection, disable fire button.
                removePatternButton.setEnabled(false);
            } else {
                //Selection, enable the fire button.
                currentPattern = listOfPatternNamesInFrame.getSelectedValue();

                removePatternButton.setEnabled(true);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        interactionArea.repaint();
                    }
                });

            }
        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPatternButton) {

            String newPattern = JOptionPane.showInputDialog(patternChoosingFrame,
                    "New pattern name", "Add pattern", JOptionPane.QUESTION_MESSAGE);
            currentPattern = new DestinationPattern(newPattern);
            listModel.addElement(currentPattern);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    listOfPatternNamesInFrame.setSelectedIndex(listModel.size() - 1);
                    interactionArea.setEnabled(true);
                    frame.toFront();
                }
            });

        } else if (e.getSource() == removePatternButton) {
            final int selectedIndex = listOfPatternNamesInFrame.getSelectedIndex();
            listModel.removeElement(currentPattern);
            final int size = listModel.getSize();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {


                    int currentIndex = selectedIndex;
                    if (size == 0) { //Nobody's left, disable firing.
                        currentPattern = null;
                        interactionArea.repaint();
                        removePatternButton.setEnabled(false);
                         interactionArea.setEnabled(false);
                    } else { //Select an index.

                        if (selectedIndex == listModel.getSize()) {
                            //removed item in last position
                            currentIndex--;
                        }

                        listOfPatternNamesInFrame.setSelectedIndex(currentIndex);
                        listOfPatternNamesInFrame.ensureIndexIsVisible(currentIndex);
                        frame.toFront();
                    }
                    listOfPatternNamesInFrame.revalidate();
                    interactionArea.repaint();
                }
            });
        }

    }
}
