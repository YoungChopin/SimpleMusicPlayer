package me.mikrowelle.player;
/**
 * Author: Julius
 * Description: Plays mp3 only atm
 * Thanks to several People
 * created on 22/07/23
 **/

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicPlayer implements ActionListener {

    private JFrame frame;
    private final JLabel songNameLabel = new JLabel();
    private final JButton selectButton = new JButton("Select Mp3");
    private final JButton playButton = new JButton("▶ Play");
    private final JButton pauseButton = new JButton("⏯ Pause");
    private final JButton resumeButton = new JButton("🔊 Resume");
    private final JButton stopButton = new JButton("⏹ Exit");

    private File selectedFile;
    private int buttonPressed = 0;

    private FileInputStream fileInputStream;
    private BufferedInputStream bufferedInputStream;
    private long totalLength;
    private long pausePosition;
    private Player player;
    private Thread playThread;
    private Thread resumeThread;
    private final Font basicFont = new Font("Test", Font.ITALIC, 12);

    public MusicPlayer() {
        prepareGUI();
        addActionEvents();
        playThread = new Thread(runnablePlay);
        resumeThread = new Thread(runnableResume);
    }

    private void prepareGUI() {
        frame = new JFrame();
        frame.setTitle("Music Player");
        frame.setSize(440, 200);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        selectButton.setBounds(160, 10, 100, 30);
        selectButton.setFont(new Font("Test", Font.ITALIC, 15));
        frame.add(selectButton);

        songNameLabel.setBounds(100, 50, 300, 30);
        songNameLabel.setFont(basicFont);
        frame.add(songNameLabel);

        playButton.setBounds(30, 110, 100, 30);
        playButton.setFont(basicFont);
        frame.add(playButton);

        pauseButton.setBounds(120, 110, 100, 30);
        pauseButton.setFont(basicFont);
        frame.add(pauseButton);

        resumeButton.setBounds(210, 110, 100, 30);
        resumeButton.setFont(basicFont);
        frame.add(resumeButton);

        stopButton.setBounds(300, 110, 100, 30);
        stopButton.setFont(basicFont);
        frame.add(stopButton);
    }

    private void addActionEvents() {
        selectButton.addActionListener(this);
        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resumeButton.addActionListener(this);
        stopButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton) {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setDialogTitle("Select Mp3");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files", "mp3"));

            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                songNameLabel.setText("Selected : " + selectedFile.getName());
            }
        } else if (e.getSource() == playButton) {
            if (player != null) {
                player.close();
            }
            if(selectedFile == null) {
                songNameLabel.setText("Select a mp3 first!");
            }
            else
                songNameLabel.setText("Playing : " + selectedFile.getName());
            playThread = new Thread(runnablePlay);
            playThread.start();
        } else if (e.getSource() == pauseButton) {
            if (player != null) {
                try {
                    pausePosition = fileInputStream.available();
                    player.close();
                    songNameLabel.setText("Paused : " + selectedFile.getName());
                } catch (Exception ignored) {

                }
            }
        } else if (e.getSource() == resumeButton && selectedFile != null) {
            if (resumeThread == null || !resumeThread.isAlive()) {
                resumeThread = new Thread(runnableResume);
                resumeThread.start();
                songNameLabel.setText("Playing : " + selectedFile.getName());
            }
        } else if (e.getSource() == stopButton) {
            buttonPressed += 1;
            songNameLabel.setText("Press again to exit");
            if(buttonPressed == 2)
                System.exit(0);

            }
        }


    private final Runnable runnablePlay = new Runnable() {
        public void run() {
            try {
                if(selectedFile != null) {
                    fileInputStream = new FileInputStream(selectedFile);
                    bufferedInputStream = new BufferedInputStream(fileInputStream);
                    player = new Player(bufferedInputStream);
                    totalLength = fileInputStream.available();
                    player.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final Runnable runnableResume = new Runnable() {
        public void run() {
            try {
                fileInputStream = new FileInputStream(selectedFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                player = new Player(bufferedInputStream);
                fileInputStream.skip(totalLength - pausePosition);
                player.play();
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        }
    };
}
