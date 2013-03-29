/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.creator;

import datatypes.ModelDetails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * The Main class for the environment creation engine. To add a new level extend
 * AbstractLevel and add the level in the initializeLevels(). You will also need 
 * to modify ModelDetails since that class controls what is written to the xml file.
 * Currently there are some levels that are obsolete. This application just creates 
 * an xml file. So in many cases it will be much easier to modify the xml file 
 * directly.
 * 
 * TODO : CLEAN THIS UP!!
 *
 * @author vaisagh
 */
public class CreatorMain implements ActionListener {

    public static final double AGENT_RADIUS = 0.15;
    public JLabel statusBar = new JLabel();
    JPanel buttonArea = new JPanel();
    JButton clearButton;
    JButton nextButton;
    JButton previousButton;
    JFrame frame;
    private JFileChooser fileChooser;
    private int currentLevel;
    ArrayList<AbstractLevel> listOfLevels;
    private ModelDetails model;
    private JProgressBar progress;

    public CreatorMain() {
        model = new ModelDetails();
        frame = new JFrame("Agent Simulation Environment Creator");
        frame.setLayout(new BorderLayout());


        fileChooser = new JFileChooser(new File(".").getAbsolutePath());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);


        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem rename = new MenuItem("Rename");
        MenuItem load = new MenuItem("Load");
        MenuItem about = new MenuItem("About");
        menuBar.add(fileMenu);
        about.setActionCommand("about");
        load.setActionCommand("load");
        rename.setActionCommand("rename");
        fileMenu.add(rename);
        fileMenu.add(about);
        fileMenu.add(load);


        about.addActionListener(this);
        load.addActionListener(this);
        rename.addActionListener(this);
        frame.setMenuBar(menuBar);


        clearButton = new JButton("Clear");
        nextButton = new JButton("Next");
        previousButton = new JButton("Previous");

        buttonArea.setLayout(new GridLayout(1, 3));
        buttonArea.setBackground(Color.lightGray);
        buttonArea.add(previousButton);
        buttonArea.add(clearButton);
        buttonArea.add(nextButton);

        previousButton.setToolTipText("Move to previous step");
        nextButton.setToolTipText("Move to next step");
        clearButton.setToolTipText("Clear all points on screen");

        nextButton.addActionListener(this);
        previousButton.addActionListener(this);
        clearButton.addActionListener(this);


        statusBar.setForeground(Color.BLUE);

        initializeLevels();

        progress = new JProgressBar();
        progress.setMinimum(0);
        progress.setMaximum(this.listOfLevels.size() - 1);
        progress.setValue(0);
        progress.setStringPainted(true);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(progress, BorderLayout.NORTH);
        bottomPanel.add(statusBar, BorderLayout.SOUTH);
        frame.add(buttonArea, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        currentLevel = 0;
        this.changeToLevel(currentLevel);


        frame.setResizable(false);
        frame.setSize(900, 700);
        frame.setLocation(10, 10);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String trigger = event.getActionCommand();
        if (trigger.equalsIgnoreCase("load")) {
//            if (currentLevel != 0) {
                listOfLevels.get(currentLevel).clearUp();
                currentLevel = 0;
            loadFile();
                listOfLevels.get(currentLevel).setUpLevel();
//            }
            assert currentLevel == 0;

            frame.validate();
        } else if(trigger.equals("rename")){
            String newName = JOptionPane.showInputDialog(frame, "What name do you want to change it to", "Rename file", JOptionPane.QUESTION_MESSAGE);
            if(newName!=null&&!newName.isEmpty()){
                model.setTitle(newName);
                frame.setTitle(newName);
            }
        }else if (trigger.equalsIgnoreCase("about")) {
            JOptionPane.showMessageDialog(frame, "This program is a surprise", "About", JOptionPane.INFORMATION_MESSAGE);
        } else if (trigger.equalsIgnoreCase("next")) {
            listOfLevels.get(currentLevel).clearUp();
            currentLevel++;

            assert currentLevel < listOfLevels.size();
            this.changeToLevel(currentLevel);



        } else if (trigger.equalsIgnoreCase("previous")) {
            listOfLevels.get(currentLevel).clearUp();
            currentLevel--;

            assert currentLevel > -1;
            this.changeToLevel(currentLevel);

        } else if (trigger.equalsIgnoreCase("finish and save")) {


            model.saveToFile();
            System.exit(0);

        } else if (trigger.equalsIgnoreCase("clear")) {
            listOfLevels.get(currentLevel).clearAllPoints();
        }
    }

    private void loadFile() {
        File file = null;
        int returnVal;



            returnVal = fileChooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();

            }


            model.loadFromFile(file);


        /**
         * Code to load from file.. Let this wait for a bit...
         */
    }

    /**
     * Function to change if a new level has to be added
     */
    private void initializeLevels() {
        listOfLevels = new ArrayList<AbstractLevel>();

        model.setTitle("Default");
        model.setScale(25);
        model.setXSize(40);
        model.setYSize(30);
//        listOfLevels.add(new IntroLevel(model, frame, statusBar, buttonArea));

        DrawingPanel interactionArea = new DrawingPanel();
        interactionArea.setBackground(Color.WHITE);
        listOfLevels.add(new PatternCreatorLevel(model, frame, statusBar,  buttonArea, interactionArea));
        listOfLevels.add(new FinalLevel(model, frame, statusBar, buttonArea, interactionArea));



    }

    public static void main(String[] args) {
        new CreatorMain();
    }

    private void changeToLevel(int currentLevel) {
        progress.setValue(currentLevel);
        listOfLevels.get(currentLevel).setUpLevel();
        frame.validate();
        frame.setTitle("-" + listOfLevels.get(currentLevel).getName() + "-" + model.getTitle());
        frame.setSize(model.getxSize() * model.getScale() + 8, model.getySize() * model.getScale() + 100);
        statusBar.setText(listOfLevels.get(currentLevel).getName());
        frame.validate();
    }
}
