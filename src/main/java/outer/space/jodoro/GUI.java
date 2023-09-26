package outer.space.jodoro;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;

public class GUI {

    private final JPanel settingsBottomPanel = new JPanel();
    private final JCheckBox showSettingsCheckBox = new JCheckBox();

    private final JTextField workLengthField = new JTextField();
    private final JTextField breakLengthField = new JTextField();
    private final JTextField longBreakLengthField = new JTextField();

    private final JButton setSettingsBtn = new JButton();
    private final JButton timerBtn = new JButton(); // Start/Stop button
    private final JButton resetBtn = new JButton();

    private final JLabel sessionLabel = new JLabel();
    private final JLabel timeLabel = new JLabel();

    private int workLength = 30 * 60;
    private int breakLength = 5 * 60;
    private int longBreakLength = 3 * breakLength;
    private int remTime = workLength;

    private final PomodoroTimer pomodoroTimer = new PomodoroTimer();
    private int workNum = 1; // current working session
    private boolean onBreak = false;

    public static MaskFormatter formatter;

    static {
        // TODO: remove this
        try {
            formatter = new MaskFormatter("##:##");
        } catch (ParseException e) {
            System.out.println(
                "ERROR: something went wrong while creating formatter: " +
                    e.getMessage()
            );
            System.exit(-1);
        }
    }

    public ActionListener actionListener = (ActionEvent e) -> {
        if (e.getSource() == timerBtn) {
            handleTimerBtnClick();
        } else if (e.getSource() == showSettingsCheckBox) {
            settingsBottomPanel.setVisible(showSettingsCheckBox.isSelected());
        } else if (e.getSource() == setSettingsBtn) {
            handleSetSettingsBtnClick();
        } else if (e.getSource() == resetBtn) {
            handleResetBtnClick();
        }
    };

    /**
     * Components have been set up in the order they appear in the GUI so it's easier to
     * visualize how everything will look like.
     */
    public GUI() {
        JFrame frame = new JFrame("jodoro");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 600);

        // customizing components of settings panel
        showSettingsCheckBox.setText("Show advanced settings");
        showSettingsCheckBox.addActionListener(actionListener);

        // TODO: add `MaskFormatter` to fields in settings pane

        workLengthField.setPreferredSize(new Dimension(100, 25));
        breakLengthField.setPreferredSize(new Dimension(100, 25));
        longBreakLengthField.setPreferredSize(new Dimension(100, 25));
        setSettingsBtn.setText("Set");
        setSettingsBtn.addActionListener(actionListener);

        JPanel settingsTopPanel = new JPanel();
        settingsTopPanel.add(showSettingsCheckBox);

        // TODO: loop through components when adding them to panels
        settingsBottomPanel.setVisible(false);
        settingsBottomPanel.setLayout(new BoxLayout(settingsBottomPanel, BoxLayout.PAGE_AXIS));
        settingsBottomPanel.add(new JLabel("Work session length: "));
        settingsBottomPanel.add(workLengthField);
        settingsBottomPanel.add(new JLabel("Break session length: "));
        settingsBottomPanel.add(breakLengthField);
        settingsBottomPanel.add(new JLabel("Long break session length: "));
        settingsBottomPanel.add(longBreakLengthField);
        settingsBottomPanel.add(setSettingsBtn);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        settingsPanel.add(settingsTopPanel);
        settingsPanel.add(settingsBottomPanel);

        // main panel
        sessionLabel.setText("Work (1/4)");
        timeLabel.setText(secsToMinsString(remTime));
        timeLabel.setFont(new Font("serif", Font.PLAIN, 80));
        timerBtn.setText("Start");
        resetBtn.setText("Reset");
        resetBtn.setEnabled(false);
        timerBtn.addActionListener(actionListener);
        resetBtn.addActionListener(actionListener);

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
        // TODO: make this public
        // TODO: use `ScheduledExecutorService` instead
        public Timer timer;

        void startTimer(Runnable everySecond, Runnable onZero) {
            timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    everySecond.run();

                    if (remTime <= 0) {
                        timer.cancel();
                        onZero.run();
                    }
                }
            }, 1000, 1000);
        }

        void cancel() {
            timer.cancel();
        }
    }

    private void handleTimerBtnClick() {
        if (timerBtn.getText().equals("Start")) {
            timerBtn.setText("Pause");

            pomodoroTimer.startTimer(
                () -> { // run this every second
                    remTime--;
                    timeLabel.setText(secsToMinsString(remTime));
                },
                () -> { // run this when timer goes to zero
                    timerBtn.setText("Start");

                    if (onBreak) {
                        onBreak = false;
                        remTime = longBreakLength;
                        sessionLabel.setText("Work (" + (++workNum) + "/4)");
                    } else {
                        onBreak = true;

                        if (workNum >= 4) {
                            remTime = longBreakLength;
                            sessionLabel.setText("Long Break");
                        } else {
                            remTime = breakLength;
                            sessionLabel.setText("Break");
                        }

                        workNum = 0;
                    }

                    timeLabel.setText(secsToMinsString(remTime));
                    makeSound();
                }
            );
        } else {
            timerBtn.setText("Start");
            pomodoroTimer.cancel();
        }

        resetBtn.setEnabled(true);
    }

    private void handleSetSettingsBtnClick() {
        // validate inputs
        workLength = Integer.parseInt(workLengthField.getText());
        breakLength = Integer.parseInt(breakLengthField.getText());
        longBreakLength = Integer.parseInt(longBreakLengthField.getText());
        remTime = workLength;

        timeLabel.setText(secsToMinsString(remTime));
    }

    public void handleResetBtnClick() {
        if (timerBtn.getText().equals("Pause")) {
            timerBtn.setText("Start");
        }

        remTime = workLength;
        workNum = 1;
        onBreak = false;
        sessionLabel.setText("Work (1/4)");

        pomodoroTimer.cancel();
        timeLabel.setText(secsToMinsString(remTime));
    }

    /**
     * Returns a {@code String} representing minutes and seconds from the given number of seconds.
     */
    private static String secsToMinsString(int n) {
        String mins = Integer.toString(n / 60);
        String secs = Integer.toString(n % 60);

        if (Integer.parseInt(mins) < 10) {
            mins = "0" + mins;
        }
        if (Integer.parseInt(secs) < 10) {
            secs = "0" + secs;
        }

        return mins + ":" + secs;
    }

    /**
     * Plays the default audio file.
     */
    public static void makeSound() {
        try {
            File f = new File("./src/main/resources/ding.wav");
            boolean runningClassDirectly = f.exists();

            if (!runningClassDirectly) {

                if ("/".equals(System.getProperty("user.dir"))) {
                    // TODO: use a flag to determine OS
                    f = new File("/Applications/jodoro.app/Contents/app/classes/ding.wav");
                } else {
                    f = new File("./classes/ding.wav");
                    System.out.println("Using ./classes/ding.wav");
                }
            }

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
       SwingUtilities.invokeLater(GUI::new);
    }
}