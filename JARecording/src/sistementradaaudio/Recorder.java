package sistementradaaudio;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javaFlacEncoder.FLAC_FileEncoder;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Recorder extends javax.swing.JFrame {

    private TargetDataLine audioChannel;
    private AudioFileFormat.Type audioFormatType;
    private AudioInputStream audioOutPutStream;
    private File audioOutPutFile;
    private File principalDirectory;
    private File filePath;
    private String ruta = "C:/file_records/";

    private javax.swing.JButton startRecordButton;
    private javax.swing.JButton decoderButton;
    private javax.swing.JButton stopRecordButton;
    private javax.swing.JButton audioPath;

    public Recorder() {
        initComponents();
        //--
        decoderButton.setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        startRecordButton = new javax.swing.JButton();
        stopRecordButton = new javax.swing.JButton();
        audioPath = new javax.swing.JButton();
        decoderButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        startRecordButton.setText("Grabar");
        startRecordButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    startProcess(evt);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ocurrio un error interno : \n meth_name : " + this.getClass().getEnclosingMethod().getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println(ex.getMessage());
                }
            }
        });

        stopRecordButton.setText("Parar");
        stopRecordButton.setEnabled(false);
        stopRecordButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopRecord(evt);
            }
        });

        audioPath.setText("Ruta");
        audioPath.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setNewPath(evt);
            }
        });

        decoderButton.setText("Decode To Fac");
        decoderButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    decodeWavToFac();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ocurrio un error interno : \n meth_name : " + this.getClass().getEnclosingMethod().getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println(ex.getMessage());
                }
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(audioPath)
                                .addGap(32, 32, 32)
                                .addComponent(startRecordButton)
                                .addGap(26, 26, 26)
                                .addComponent(stopRecordButton)
                                .addGap(40, 40, 40)
                                .addComponent(decoderButton)
                                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(audioPath)
                                        .addComponent(startRecordButton)
                                        .addComponent(stopRecordButton)
                                        .addComponent(decoderButton))
                                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {
            new Recorder().setVisible(true);
        });
    }

    private void setNewPath(java.awt.event.ActionEvent evt) {
        try {
            JFileChooser explorador = new JFileChooser(getPrincipalDirectory());
            explorador.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            if (explorador.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                ruta = explorador.getSelectedFile().toString();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error interno : \n meth_name : " + this.getClass().getEnclosingMethod().getName(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
        }
    }

    private void startProcess(java.awt.event.ActionEvent evt) throws IOException {

        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                44100.0F, 16, 2, 4, 44100.0F, false);

        filePath = new File(getPrincipalDirectory().getCanonicalPath() + File.separator + "file" + System.currentTimeMillis() + ".wav");

        if (!getPrincipalDirectory().exists()) {
            Files.createDirectory(getPrincipalDirectory().toPath());
        }

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        TargetDataLine targetDataLine = null;
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error interno : \n meth_name : " + this.getClass().getEnclosingMethod().getName(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

        startRecord(targetDataLine, targetType, filePath);

    }

    public void startRecord(TargetDataLine channel, AudioFileFormat.Type targetType, File file) throws IOException {

        startRecordButton.setEnabled(false);
        stopRecordButton.setEnabled(true);
        audioChannel = channel;
        audioOutPutStream = new AudioInputStream(channel);
        audioFormatType = targetType;

        if (getPrincipalDirectory().exists()) {
            audioOutPutFile = file;

            Thread t = new Thread(() -> {
                audioChannel.start();

                try {
                    AudioSystem.write(audioOutPutStream, audioFormatType, audioOutPutFile);
                    //--
                } catch (IOException e) {
                   JOptionPane.showMessageDialog(null, "Ocurrio un error interno : \n meth_name : " + this.getClass().getEnclosingMethod().getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println(e.getMessage());
                }
                //System.exit(0);
            });

            t.start();
        } else {
            JOptionPane.showMessageDialog(null, "No se puede almacenar el archivo de audio no existe el directorio \n" + file.getCanonicalPath(), "Fallo al guardar audio", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopRecord(java.awt.event.ActionEvent evt) {

        startRecordButton.setEnabled(true);
        decoderButton.setEnabled(true);
//--
        stopRecordButton.setEnabled(false);

        audioChannel.stop();
        audioChannel.close();

    }

    public void decodeWavToFac() throws IOException {
        Formater.decodeFormater(filePath.getCanonicalPath(), getPrincipalDirectory().getCanonicalPath() + "\\decode" + System.currentTimeMillis() + ".flac");
    }

    //--
    private File getPrincipalDirectory() {
        if (principalDirectory == null) {
            if (!ruta.isEmpty()) {
                principalDirectory = new File(ruta);
            }
        }
        return principalDirectory;
    }
}
