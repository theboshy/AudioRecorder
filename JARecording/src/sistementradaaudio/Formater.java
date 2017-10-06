package sistementradaaudio;

import java.awt.HeadlessException;
import java.io.File;
import javaFlacEncoder.FLAC_FileEncoder;
import javax.swing.JOptionPane;

/**
 *
 * @author theboshy
 */
public class Formater {

    public static void decodeFormater(String pathDirecFile, String pathDirec) {
        File inputFile = new File("boshy");
        try {
            FLAC_FileEncoder flacEncoder = new FLAC_FileEncoder();
            inputFile = new File(pathDirecFile);
            File outputFile = new File(pathDirec);

            flacEncoder.encode(inputFile, outputFile);
            JOptionPane.showMessageDialog(null, "Se formateo correctamente el archivo " + inputFile.getName(), "Formateado Completo", JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "No se pudo formatear el archivo : \n" + inputFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
        }
    }
}
