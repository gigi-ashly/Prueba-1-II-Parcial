package prueba.i.s6;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 *
 * @author gigiw
 */
public class FileManager {
    public boolean crearCarpeta(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) {
            System.out.println("La ruta no puede estar vacía.");
            return false;
        }
        File carpeta = new File(ruta);
        if (carpeta.exists()) {
            System.out.println("El elemento ya existe.");
            return false;
        }
        if (carpeta.mkdirs()) {
            System.out.println("Carpeta creada correctamente.");
            return true;
        }
        System.out.println("No se pudo crear la carpeta.");
        return false;
    }

    public boolean crearArchivoTexto(String ruta, String contenido) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) {
            System.out.println("La ruta no puede estar vacía.");
            return false;
        }
        File archivo = new File(ruta);
        if (archivo.exists()) {
            System.out.println("El archivo ya existe.");
            return false;
        }
        File padre = archivo.getParentFile();
        if (padre != null && !padre.exists()) {
            System.out.println("La carpeta de destino no existe.");
            return false;
        }
        if (archivo.createNewFile()) {
            FileWriter escritor = new FileWriter(archivo);
            try {
                escritor.write(contenido);
            } finally {
                escritor.close();
            }
            System.out.println("Archivo creado correctamente.");
            return true;
        }
        return false;
    }

    public boolean renombrar(File original, String nuevoNombre) {
        if (original == null || !original.exists()) {
            System.out.println("El elemento no existe.");
            return false;
        }

        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            System.out.println("El nuevo nombre no es válido.");
            return false;
        }

        if (nuevoNombre.contains("/") || nuevoNombre.contains("\\") || nuevoNombre.equals(".") || nuevoNombre.equals("..")) {
            System.out.println("El nombre contiene caracteres no permitidos.");
            return false;
        }
        File nuevo = new File(original.getParentFile(), nuevoNombre.trim());

        if (nuevo.exists()) {
            System.out.println("Ya existe un elemento con ese nombre.");
            return false;
        }

        if (original.renameTo(nuevo)) {
            System.out.println("Renombrado correctamente.");
            return true;
        }
        System.out.println("No se pudo renombrar el elemento.");
        return false;
    }

    public boolean copiarArchivo(File origen, String destino) throws IOException {
        if (origen == null || !origen.exists() || !origen.isFile()) {
            System.out.println("El archivo de origen no es válido.");
            return false;
        }

        if (destino == null || destino.trim().isEmpty()) {
            System.out.println("La ruta de destino no es válida.");
            return false;
        }

        File archivoDestino = new File(destino);

        if (archivoDestino.exists()) {
            System.out.println("El destino ya existe. No se sobrescribirá.");
            return false;
        }

        if (archivoDestino.isDirectory()) {
            System.out.println("El destino debe ser un archivo.");
            return false;
        }

        File padre = archivoDestino.getParentFile();

        if (padre != null && !padre.isDirectory()) {
            System.out.println("La carpeta de destino no existe.");
            return false;
        }

        if (origen.getCanonicalFile().equals(archivoDestino.getCanonicalFile())) {
            System.out.println("El origen y destino son iguales.");
            return false;
        }
        FileInputStream entrada = null;
        FileOutputStream salida = null;

        try {
            entrada = new FileInputStream(origen);
            salida = new FileOutputStream(archivoDestino);
            byte[] buffer = new byte[4096];
            int cantidad;

            while ((cantidad = entrada.read(buffer)) != -1) {
                salida.write(buffer, 0, cantidad);
            }
            System.out.println("Archivo copiado correctamente.");
            return true;
        } catch (IOException e) {
            if (archivoDestino.exists()) {
                archivoDestino.delete();
            }
            throw e;
        } finally {
            if (entrada != null) {
                entrada.close();
            }
            if (salida != null) {
                salida.close();
            }
        }
    }

    public boolean moverArchivo(File origen, String destino) throws IOException {
        if (origen == null || !origen.exists() || !origen.isFile()) {
            System.out.println("El archivo de origen no es válido.");
            return false;
        }

        if (destino == null || destino.trim().isEmpty()) {
            System.out.println("El destino no es válido.");
            return false;
        }

        File archivoDestino = new File(destino);

        if (archivoDestino.exists()) {
            System.out.println("El destino ya existe.");
            return false;
        }

        File padre = archivoDestino.getParentFile();

        if (padre != null && !padre.isDirectory()) {
            System.out.println("La carpeta de destino no existe.");
            return false;
        }

        if (origen.getCanonicalFile().equals(archivoDestino.getCanonicalFile())) {
            System.out.println("El origen y destino son iguales.");
            return false;
        }

        if (origen.renameTo(archivoDestino)) {
            System.out.println("Archivo movido correctamente.");
            return true;
        }

        if (copiarArchivo(origen, destino)) {
            if (origen.delete()) {
                System.out.println("Archivo movido correctamente.");
                return true;
            } else {
                archivoDestino.delete();
                System.out.println("No se pudo eliminar el original.");
                return false;
            }
        }
        return false;
    }

    public boolean eliminar(File elemento) {
        if (elemento == null || !elemento.exists()) {
            System.out.println("El elemento no existe.");
            return false;
        }

        if (elemento.isDirectory()) {
            File[] elementos = elemento.listFiles();
            if (elementos == null) {
                System.out.println("No se pudo acceder a la carpeta.");
                return false;
            }
            
            for (File hijo : elementos) {
                if (!eliminar(hijo)) {
                    return false;
                }
            }
        }

        if (elemento.delete()) {
            return true;
        }

        System.out.println("No se pudo eliminar: " + elemento.getAbsolutePath());
        return false;
    }
}
  

