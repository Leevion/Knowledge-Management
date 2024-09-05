import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public class FileMoverApp {
    private static final List<String> AUDIO_EXTENSIONS = Arrays.asList(".mp3", ".wav", ".ogg", ".flac", ".aac", ".wma",
            ".m4a", ".ape", ".pcm", ".aiff", ".au", ".midi", ".ac3");
    private static final List<String> PICTURE_EXTENSIONS = Arrays.asList(".jpg", ".png", ".gif", ".bmp", ".svg",
            ".jpeg", ".heic", ".tif", ".tiff", ".webp", ".ico");
    private static final List<String> ZIP_EXTENSIONS = Arrays.asList(".zip", ".rar", ".tar", ".gz", ".z", ".iso");
    private static final List<String> DOC_EXTENSIONS = Arrays.asList(".docx", ".doc", ".txt");
    private static final List<String> POWERPOINT_EXTENSIONS = Arrays.asList(".pptx", ".ppt");
    private static final List<String> PDF_EXTENSIONS = Arrays.asList(".pdf", ".epub", ".mobi", ".azw", ".djvu", ".xps");
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv",
            ".webm", ".mpeg", ".3gp", ".m4v", ".rmvb", ".ts", ".vob", ".ogv", ".mpg", ".m2ts", ".divx");

    private JFrame frame;
    private JTextField sourceTextField;
    private JTextField destinationTextField;

    public FileMoverApp() {
        frame = new JFrame("Knowledge Management Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5); // Padding

        JLabel sourceLabel = new JLabel("Source Directory:");
        sourceTextField = new JTextField(30);
        JButton sourceBrowseButton = new JButton("Browse");
        sourceBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                JFileChooser sourceChooser = new JFileChooser();
                sourceChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = sourceChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    sourceTextField.setText(sourceChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        JLabel destinationLabel = new JLabel("Destination Directory:");
        destinationTextField = new JTextField(30);
        JButton destinationBrowseButton = new JButton("Browse");
        destinationBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser destinationChooser = new JFileChooser();
                destinationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = destinationChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    destinationTextField.setText(destinationChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        JButton moveButton = new JButton("Organize my files");
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFiles();

                // Notify the user that the operation is completed
                JOptionPane.showMessageDialog(frame, "Completed", "Operation Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        frame.add(sourceLabel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        frame.add(sourceTextField, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        frame.add(sourceBrowseButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        frame.add(destinationLabel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        frame.add(destinationTextField, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        frame.add(destinationBrowseButton, constraints);

        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        frame.add(moveButton, constraints);

        frame.setVisible(true);
    }

    private void moveFiles() {
        String sourcePath = sourceTextField.getText();
        String destinationPath = destinationTextField.getText();

        File source = new File(sourcePath);
        File general = new File(destinationPath);
        File audio = new File(general, "Music");
        File pictures = new File(general, "Pictures");
        File docs = new File(general, "Docs");
        File pdfs = new File(docs, "PDFs");
        File powerPoints = new File(docs, "PowerPoints");
        File zip = new File(general, "ZipFiles");
        File video = new File(general, "video");

        File[] directories = { source, general, audio, pictures, docs, pdfs, powerPoints, zip, video };
        createDirectories(directories);

        moveFilesBasedOnType(source, general, audio, pictures, docs, pdfs, powerPoints, zip, video);
    }

    private void createDirectories(File[] directories) {
        for (File dir : directories) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    private void moveFilesBasedOnType(File source, File general, File audio, File pictures, File docs, File pdfs,
                                      File powerPoints, File zip, File video) {
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                moveFile(file,
                        determineDestination(file, general, audio, pictures, docs, pdfs, powerPoints, zip, video));
            }
        }
    }

    private void moveFile(File sourceFile, File destination) {
        try {
            Files.move(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved file: " + sourceFile.getName());
        } catch (IOException e) {
            System.err.println("Error moving file: " + sourceFile.getName());
            e.printStackTrace();
        }
    }

    private File determineDestination(File file, File general, File audio, File pictures, File docs, File pdfs,
                                      File powerPoints, File zip, File video) {
        String fileName = file.getName().toLowerCase();

        if (isExtensionMatch(fileName, AUDIO_EXTENSIONS)) {
            return new File(audio, file.getName());
        } else if (isExtensionMatch(fileName, PICTURE_EXTENSIONS)) {
            return new File(pictures, file.getName());
        } else if (isExtensionMatch(fileName, ZIP_EXTENSIONS)) {
            return new File(zip, file.getName());
        } else if (isExtensionMatch(fileName, DOC_EXTENSIONS)) {
            return new File(docs, file.getName());
        } else if (isExtensionMatch(fileName, POWERPOINT_EXTENSIONS)) {
            return new File(powerPoints, file.getName());
        } else if (isExtensionMatch(fileName, PDF_EXTENSIONS)) {
            return new File(pdfs, file.getName());
        } else if (isExtensionMatch(fileName, VIDEO_EXTENSIONS)) {
            return new File(video, file.getName());
        } else {
            return new File(general, file.getName());
        }
    }

    private boolean isExtensionMatch(String fileName, List<String> extensions) {
        return extensions.stream().anyMatch(fileName::endsWith);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileMoverApp();
            }
        });
    }
}
