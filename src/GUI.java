import javax.swing.*;
import javax.sound.sampled.*;

import java.awt.*;
import java.awt.event.*;

import java.util.Timer;
import java.util.TimerTask;

import java.io.*;

public class GUI implements ActionListener {

    private final JPanel settingsBottomPanel = new JPanel();
    private final JCheckBox showSettingsCheckBox = new JCheckBox();
    private final JTextField workLengthField = new JTextField();
    private final JTextField breakLengthField = new JTextField();
    private final JTextField longBreakLengthField = new JTextField();
    private final JButton setBtn = new JButton();
    private final JLabel sessionLabel = new JLabel();
    private final JLabel timeLabel = new JLabel();
    private final JButton timerBtn = new JButton(); // Start/Stop button
    private final JButton resetBtn = new JButton();

    private int workLength = 30 * 60;
    private int breakLength = 5 * 60;
    private int longBreakLength = 3 * breakLength;
    private int remTime = workLength;

    private final PomodoroTimer pomodoroTimer = new PomodoroTimer();
    private int workNum = 1;
    private boolean onBreak = false;

    public GUI() {
        JFrame frame = new JFrame("jodoro");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 600);

        // temporary values
        workLength = 60 * 60;
        breakLength = 10 * 60;
        longBreakLength = 3 * breakLength;
        remTime = workLength;

        // customizing components of settings panel
        showSettingsCheckBox.setText("Show advanced settings");
        showSettingsCheckBox.addActionListener(this);
        workLengthField.setPreferredSize(new Dimension(100, 25));
        breakLengthField.setPreferredSize(new Dimension(100, 25));
        longBreakLengthField.setPreferredSize(new Dimension(100, 25));
        setBtn.setText("Set");
        setBtn.addActionListener(this);

        JPanel settingsTopPanel = new JPanel();
        settingsTopPanel.add(showSettingsCheckBox);

        settingsBottomPanel.setVisible(false);
        settingsBottomPanel.setLayout(new BoxLayout(settingsBottomPanel, BoxLayout.PAGE_AXIS));
        settingsBottomPanel.add(new JLabel("Work session length: "));
        settingsBottomPanel.add(workLengthField);
        settingsBottomPanel.add(new JLabel("Break session length: "));
        settingsBottomPanel.add(breakLengthField);
        settingsBottomPanel.add(new JLabel("Long break session length: "));
        settingsBottomPanel.add(longBreakLengthField);
        settingsBottomPanel.add(setBtn);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        settingsPanel.add(settingsTopPanel);
        settingsPanel.add(settingsBottomPanel);

        // main panel
        sessionLabel.setText("Work (1/4)");
        timeLabel.setText(secToMin(remTime));
        timeLabel.setFont(new Font("serif", Font.PLAIN, 80));
        timerBtn.setText("Start");
        resetBtn.setText("Reset");
        resetBtn.setEnabled(false);
        timerBtn.addActionListener(this);
        resetBtn.addActionListener(this);

        // center main panel components
        sessionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
        mainPanel.add(sessionLabel);
        mainPanel.add(timeLabel);
        mainPanel.add(timerBtn);
        mainPanel.add(resetBtn);

        // add panels
        frame.getContentPane().add(BorderLayout.NORTH, settingsPanel);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setVisible(true);
    }

    class PomodoroTimer {
        private Timer timer; // TODO: make this public

        public Timer getInternalTimer() {
            return timer;
        }

        private void startTimer() {
            timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    remTime = remTime - 1;
                    timeLabel.setText(secToMin(remTime));

                    if (remTime <= 0) {
                        timer.cancel();

                        if (!onBreak) startBreak();
                        else          startWork();
                    }
                }
            }, 1000, 1000);
        }

        private void resetTimer() {
            if (timerBtn.getText().equals("Pause")) timerBtn.setText("Start");

            remTime = workLength;
            workNum = 1;
            onBreak = false;
            sessionLabel.setText("Work (1/4)");

            if (timer != null) timer.cancel();
            timeLabel.setText(secToMin(remTime));
        }

        public void startBreak() {
            if (workNum == 4) {
                sessionLabel.setText("Long Break");
                workNum = 0;

                remTime = longBreakLength;
                timeLabel.setText(secToMin(remTime));
            } else {
                sessionLabel.setText("Break");
                remTime = breakLength;
                timeLabel.setText(secToMin(remTime));
            }

            onBreak = true;
            makeSound();
            startTimer();
        }

        public void startWork() {
            sessionLabel.setText("Work (" + (++workNum) + "/4)");
            remTime = workLength;
            timeLabel.setText(secToMin(remTime));

            onBreak = false;
            makeSound();
            startTimer();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timerBtn) {
            if (timerBtn.getText().equals("Start")) {
                timerBtn.setText("Pause");
                pomodoroTimer.startTimer();
            } else {
                timerBtn.setText("Start");
                pomodoroTimer.getInternalTimer().cancel();
            }

            resetBtn.setEnabled(true);
        } else if (e.getSource() == showSettingsCheckBox) {
            settingsBottomPanel.setVisible(showSettingsCheckBox.isSelected());
        } else if (e.getSource() == setBtn) {
            workLength      = Integer.parseInt(workLengthField.getText());
            breakLength     = Integer.parseInt(breakLengthField.getText());
            longBreakLength = Integer.parseInt(longBreakLengthField.getText());
            remTime         = workLength;

            timeLabel.setText(secToMin(remTime));
        } else if (e.getSource() == resetBtn) {
            pomodoroTimer.resetTimer();
        }
    }

    /**
     * Returns a {@code String} representing minutes and seconds from the given number of
     * seconds.
     */
    private static String secToMin(int n) {
        String mins = Integer.toString(n / 60);
        String secs = Integer.toString(n % 60);

        if (Integer.parseInt(mins) < 10) mins = "0" + mins;
        if (Integer.parseInt(secs) < 10) secs = "0" + secs;

        return mins + ":" + secs;
    }

    /**
     * Plays the default audio file.
     */
    public static void makeSound() {
        try {
            File f = new File("../media/ding.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            AudioFormat format = ais.getFormat();
            Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, format));

            clip.open(ais);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
       new GUI();
    }
}
