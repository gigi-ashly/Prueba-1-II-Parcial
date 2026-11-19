
package prueba.i.s6;
import java.io.File;

/**
 *
 * @author gigiw
 */
public class FileExplorer {
    private File directorioRaiz;

    public boolean establecerRuta(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) {
            System.out.println("La ruta no puede estar vacía.");
            return false;
        }
        File directorio = new File(ruta);

        if (!directorio.exists()) {
            System.out.println("La ruta no existe.");
            return false;
        }
        if (!directorio.isDirectory()) {
            System.out.println("La ruta no corresponde a una carpeta.");
            return false;
        }
        directorioRaiz = directorio;
        System.out.println("Directorio establecido correctamente.");
        return true;
    }
    
    public void explorar() {
        if (directorioRaiz == null) {
            System.out.println("Primero debe establecer una ruta.");
            return;
        }
        System.out.println("\nExplorando: " + directorioRaiz.getAbsolutePath());
        recorrerRecursivo(directorioRaiz, 0);
    }

    private void recorrerRecursivo(File carpeta, int nivel) {
        File[] elementos = carpeta.listFiles();
        if (elementos == null) {
            System.out.println("No se pudo acceder a: " + carpeta.getAbsolutePath());
            return;
        }

        for (File elemento : elementos) {
           String sangria = "";
           for (int i = 0; i < nivel; i++) {
                    sangria += "    ";
            }
                if (elemento.isDirectory()) {
                    System.out.println(sangria + "[CARPETA] " + elemento.getName());
                    recorrerRecursivo(elemento, nivel + 1);
                } else if (elemento.isFile()) {
                    System.out.println(sangria + "[ARCHIVO] " + elemento.getName());
                }
        }
    }

    public File getDirectorioRaiz() {
        return directorioRaiz;
    }
}
 

