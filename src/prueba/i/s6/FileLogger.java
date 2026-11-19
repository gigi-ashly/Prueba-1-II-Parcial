
package prueba.i.s6;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author gigiw
 */
public class FileLogger {
    private File archivoBitacora;

    public FileLogger() {
        archivoBitacora = new File("bitacora.log");
    }

    public void registrar(String accion, String ruta) throws IOException {
        if (accion == null || accion.trim().isEmpty()) {
            throw new IllegalArgumentException("La acción no puede estar vacía.");
        }

        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta no puede estar vacía.");
        }

        Date fechaActual = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fechaHora = formato.format(fechaActual);
        String registro = fechaHora + " | " + accion.toUpperCase() + " | " + ruta + System.lineSeparator();

        FileWriter escritor = new FileWriter(archivoBitacora, true);

        try {
            escritor.write(registro);
            escritor.flush();
        } finally {
            escritor.close();
        }
    }

    public String leerBitacora() throws IOException {
        if (!archivoBitacora.exists()) {
            return "Todavía no existen registros.";
        }

        if (!archivoBitacora.isFile() || !archivoBitacora.canRead()) {
            throw new IOException("No se puede leer el archivo de bitácora.");
        }
        TextFileManager lector = new TextFileManager();
        return lector.leerArchivo(archivoBitacora);
    }

    public File getArchivoBitacora() {
        return archivoBitacora;
    }
}


