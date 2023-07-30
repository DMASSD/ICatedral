package iCatedral;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class pruebas2 {

	public static void main(String[] args) {
		// Crear un objeto de JFileChooser
        JFileChooser fileChooser = new JFileChooser();

        // Agregar un filtro para mostrar solo archivos .txt
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto", "txt");
        fileChooser.setFileFilter(filter);

        // Mostrar el cuadro de diálogo para seleccionar un archivo .txt
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            // Obtener el archivo seleccionado y su ruta
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String rutaSeleccionada = selectedFile.getAbsolutePath();
            System.out.println("Ruta seleccionada: " + rutaSeleccionada);
        } else {
            System.out.println("El usuario canceló la selección.");
        }

	}

}
