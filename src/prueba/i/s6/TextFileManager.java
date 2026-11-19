
package prueba.i.s6;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author gigiw
 */
public class TextFileManager {
    private static final long TAMANO_MAXIMO = 1024 * 1024;

    private boolean validarArchivo(File archivo) {
        if (archivo == null) {
            System.out.println("No se seleccionó ningún archivo.");
            return false;
        }
        
        if (!archivo.exists()) {
            System.out.println("El archivo no existe.");
            return false;
        }

        if (!archivo.isFile()) {
            System.out.println("La ruta no corresponde a un archivo.");
            return false;
        }

        if (!archivo.canRead()) {
            System.out.println("No se tienen permisos de lectura.");
            return false;
        }
        return true;
    }

    public boolean verificarTamano(File archivo) {
        if (!validarArchivo(archivo)) {
            return false;
        }
        if (archivo.length() > TAMANO_MAXIMO) {
            System.out.println("El archivo supera el límite de 1 MB.");
            return false;
        }
        return true;
    }

    public boolean esArchivoTexto(File archivo) throws IOException {
        if (!validarArchivo(archivo)) {
            return false;
        }

        FileReader lector = new FileReader(archivo);

        try {
            int caracter;
            int contador = 0;
            while ((caracter = lector.read()) != -1 && contador < 8192) {
                if (caracter == 0) {
                    return false;
                }

                if (caracter < 32 && caracter != '\n' && caracter != '\r' && caracter != '\t' && caracter != '\f') {
                    return false;
                }
                contador++;
            }
            return true;
        } finally {
            lector.close();
        }
    }

    public String leerArchivo(File archivo) throws IOException {
        if (!verificarTamano(archivo)) {
            throw new IOException("El archivo no es válido o supera el tamaño permitido.");
        }

        if (!esArchivoTexto(archivo)) {
            throw new IOException("El archivo no parece ser un documento de texto.");
        }

        FileReader lector = new FileReader(archivo);

        try {
            StringBuilder contenido = new StringBuilder();
            int caracter;
            while ((caracter = lector.read()) != -1) {
                contenido.append((char) caracter);
            }
            return contenido.toString();
        } finally {
            lector.close();
        }
    }

    public void guardarArchivo(File archivo, String contenido) throws IOException {
        if (archivo == null) {
            throw new IOException("No se seleccionó un archivo.");
        }

        if (contenido == null) {
            throw new IOException("El contenido no puede ser null.");
        }

        File padre = archivo.getParentFile();

        if (padre != null && !padre.isDirectory()) {
            throw new IOException("La carpeta de destino no existe.");
        }

        if (archivo.exists() && !archivo.isFile()) {
            throw new IOException("La ruta seleccionada no es un archivo.");
        }

        if (archivo.exists() && !archivo.canWrite()) {
            throw new IOException("No se tienen permisos de escritura.");
        }

        FileWriter escritor = new FileWriter(archivo);

        try {
            escritor.write(contenido);
            escritor.flush();
            System.out.println("Archivo guardado correctamente.");
        } finally {
            escritor.close();
        }
    }
}


