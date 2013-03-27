/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.creator;


import datatypes.AgentGroup;
import datatypes.ModelDetails;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Point2d;

/**
 *
 * @author vaisagh
 */
public class AgentGroupCreatorLevel extends AbstractLevel implements MouseListener, MouseMotionListener{

    private DrawingPanel interactionArea;
    private ArrayList<Point2d> points;
    private List<AgentGroup> agentGroups;
    private Point2d point;
    private Point2d prevPoint;
    private Point currentPoint;

    public AgentGroupCreatorLevel(ModelDetails model, JFrame frame, JLabel statusBar, JPanel buttonArea, DrawingPanel interactionArea) {
        super(model, frame, statusBar, buttonArea);
        this.interactionArea = interactionArea;

    }
    
    @Override
    public void setUpLevel() {
        points = new ArrayList<Point2d>();
        prevPoint = new Point2d(-1.0,-1.0);
        point = new Point2d(-1.0,-1.0);

        currentPoint = new Point(0, 0);

            if (model.getAgentGroups().isEmpty()) {
                agentGroups = new ArrayList<AgentGroup>();
            } else {
                agentGroups = model.getAgentGroups();
            }


        previousButton.setEnabled(true);
        clearButton.setEnabled(true);
        nextButton.setEnabled(true);

       


        frame.add(interactionArea, BorderLayout.CENTER);
        interactionArea.setBackground(Color.white);
        interactionArea.setCurrentLevel(this);
        interactionArea.setEnabled(true);
        interactionArea.repaint();
        interactionArea.addMouseListener(this);
        interactionArea.addMouseMotionListener(this);
         frame.repaint();
    }

    @Override
    public void clearUp() {
        model.setAgentGroups(agentGroups);
        interactionArea.removeMouseListener(this);
        interactionArea.removeMouseMotionListener(this);

        interactionArea.setEnabled(false);
        frame.remove(interactionArea);
    }

    @Override
    public void draw(Graphics g) {
//         super.drawGridLines(g);

        super.drawCurrentPoint(g, currentPoint);



        if (!points.isEmpty()) {
            super.drawPoints(g, points);

        }
        
        if(!agentGroups.isEmpty()){
            super.drawAgentGroups(g, agentGroups);
        }
    }

    @Override
    public void clearAllPoints() {
        points.clear();
        agentGroups.clear();
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
        boolean validityCheck = super.mouseReleaseDefaultActions(e, currentPoint, point, prevPoint);

        if (!validityCheck) {
            return;
        }

        //If the click is not in the same line as the previous one then reset X and Y vluaes to previous values
        // If it is near enough to approximate then do that... 
        
        if (prevPoint.x >= 0) {
            if (Math.abs(point.x - prevPoint.x) < (CreatorMain.AGENT_RADIUS * 2.0) ||
                    Math.abs(point.y - prevPoint.y) < (CreatorMain.AGENT_RADIUS * 2.0) ) {
                point.x = prevPoint.x;
                point.y = prevPoint.y;
                return;
            }
            
            int xDifference = (((int)(Math.abs(point.x - prevPoint.x)*100.0))
                                %((int)(CreatorMain.AGENT_RADIUS *200.0)));
            int yDifference = (((int)(Math.abs(point.y - prevPoint.y)*100.0))
                                %((int)(CreatorMain.AGENT_RADIUS *200.0)));
            boolean success = false;
            
            if ( xDifference!=0 && (double)xDifference/100.0 < CreatorMain.AGENT_RADIUS){
                point.x= point.x + (double)xDifference/100.0;
                success = true;
            }
            if ( yDifference !=0 && (double)yDifference/100.0 < CreatorMain.AGENT_RADIUS){
                point.y =point.y + (double)yDifference/100.0;
                success = true;
            }
            if(!(success||(xDifference ==0 && yDifference ==0))){
                return;
            }
          
        }

        if (points.size() < 1) {
            statusBar.setText("Agent Group started at " + point.x + "," + point.y + ". Please select the other corner.");
            Point2d tempStorage = new Point2d(point.x, point.y);

            points.add(tempStorage);
      } else if (points.size() == 1) {
            
            
            
            
            AgentGroup tempAgentGroup = new AgentGroup();
            
            int size=0;
            double minSpeed= 0.0, maxSpeed = 2.6;
            double meanSpeed =1.3, sDev =0.0;
            
            
            Point2d tempStorage = new Point2d();
            tempStorage.x = (point.x > points.get(0).x) ? point.x : points.get(0).x;
            tempStorage.y = (point.y > points.get(0).y) ? point.y : points.get(0).y;
            tempAgentGroup.setEndPoint(tempStorage);
            
            tempStorage = new Point2d();
            tempStorage.x = point.x < points.get(0).x ? point.x : points.get(0).x;
            tempStorage.y = point.y < points.get(0).y ? point.y : points.get(0).y;
            tempAgentGroup.setStartPoint(tempStorage);

         
            statusBar.setText("Agent Group end corner set at  " + point.x + "," + point.y);
            
                       
            
            String tempString;
            do {
                tempString = (String) JOptionPane.showInputDialog(
                        null,
                        "How many agents should be put in this area?",
                        "Input",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "1");
               try {
                    size = Integer.parseInt(tempString);
                } catch (NumberFormatException numException) {
                    continue;
                }
            } while (!isValidSize(size, tempAgentGroup));
            

            do {
                tempString = (String) JOptionPane.showInputDialog(
                        null,
                        "Minimum Speed",
                        "Input",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "0");
               try {
                    minSpeed = Double.parseDouble(tempString);
                } catch (NumberFormatException numException) {
                    continue;
                }
            } while (minSpeed<0||minSpeed>2.6);
            
        
            do {
                tempString = (String) JOptionPane.showInputDialog(
                        null,
                        "Maximum Speed?",
                        "Input",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "2.6");
               try {
                    maxSpeed = Double.parseDouble(tempString);
                } catch (NumberFormatException numException) {
                    continue;
                }
            } while (maxSpeed<minSpeed||maxSpeed>2.6);
            meanSpeed = (minSpeed + maxSpeed) /2.0;
            do {
                tempString = (String) JOptionPane.showInputDialog(
                        null,
                        "Average Speed?",
                        "Input",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        Double.toString(meanSpeed));
               try {
                    meanSpeed = Double.parseDouble(tempString);
                } catch (NumberFormatException numException) {
                    continue;
                }
            } while (meanSpeed<minSpeed||meanSpeed>maxSpeed);
            
            do {
                tempString = (String) JOptionPane.showInputDialog(
                        null,
                        "Standard Deviation:",
                        "Input",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "0.0");
               try {
                    sDev = Double.parseDouble(tempString);
                } catch (NumberFormatException numException) {
                    continue;
                }
            } while (false);
            
            tempAgentGroup.setSize(size);
            tempAgentGroup.setMinSpeed(minSpeed);
            tempAgentGroup.setMaxSpeed(maxSpeed);
            tempAgentGroup.setMeanSpeed(meanSpeed);
            tempAgentGroup.setSDevSpeed(sDev);
            agentGroups.add(tempAgentGroup);

            prevPoint.x = -1.0;
            prevPoint.y = -1.0;
            point.x = -1.0;
            point.y = -1.0;
            points.clear();


        }

        interactionArea.repaint();
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
         super.calculateCurrentPoint(e, currentPoint,false);
        interactionArea.repaint();
    }

    private boolean isValidSize(int numberOfAgents, AgentGroup tempAgentGroup) {
        double width = tempAgentGroup.getEndPoint().x - tempAgentGroup.getStartPoint().x;
        double height = tempAgentGroup.getEndPoint().y - tempAgentGroup.getStartPoint().y;
        
        // TODO : Checks if so many agents can possible be created in this area.
        double minAreaRequired 
                = Math.pow((Math.ceil(Math.sqrt(numberOfAgents))),2.0) 
                    * (Math.pow(CreatorMain.AGENT_RADIUS*2, 2.0));
        
              
        if(minAreaRequired > width * height){
            
             statusBar.setText("Cannot fit in this area. Please choose a lower number");
            
            return false;
            
        }
        
        return true;
    }

    @Override
    public String getName() {
        return "Group Creator Level";
    }
}
