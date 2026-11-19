package prueba.i.s6;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 *
 * @author gigiw
 */
public class DuplicateFinder {
    private Map<String, List<File>> archivosAgrupados;
    private Set<String> rutasVisitadas;

    public DuplicateFinder() {
        archivosAgrupados = new HashMap<>();
        rutasVisitadas = new HashSet<>();
    }

    public Map<String, List<File>> encontrarDuplicados(File directorio) {
        if (directorio == null || !directorio.exists() || !directorio.isDirectory()) {
            throw new IllegalArgumentException("El directorio seleccionado no es válido.");
        }
        archivosAgrupados.clear();
        rutasVisitadas.clear();
        recorrer(directorio);
        Map<String, List<File>> duplicados = new HashMap<>();
        
        for (String clave : archivosAgrupados.keySet()) {
            List<File> archivos = archivosAgrupados.get(clave);
            Set<String> carpetas = new HashSet<>();
            for (File archivo : archivos) {
                try {
                    String rutaPadre = archivo.getParentFile().getCanonicalPath();
                    carpetas.add(rutaPadre);
                } catch (IOException e) {
                    System.out.println("No se pudo obtener la ruta de: " + archivo.getName());
                }
            }
            if (carpetas.size() >= 2) {
                duplicados.put(clave, archivos);
            }
        }
        return duplicados;
    }

    private void recorrer(File carpeta) {
        try {
            String ruta = carpeta.getCanonicalPath();
            if (rutasVisitadas.contains(ruta)) {
                return;
            }
            rutasVisitadas.add(ruta);
        } catch (IOException e) {
            System.out.println("No se pudo acceder a: " + carpeta.getAbsolutePath());
            return;
        }
        File[] elementos = carpeta.listFiles();
        if (elementos == null) {
            System.out.println("No se pudo leer la carpeta: " + carpeta.getAbsolutePath());
            return;
        }
        for (File elemento : elementos) {
            if (elemento.isDirectory()) {
                recorrer(elemento);
            } else if (elemento.isFile()) {
                agruparArchivo(elemento);
            }
        }
    }

    private void agruparArchivo(File archivo) {
        String nombre = archivo.getName().toLowerCase();
        long tamano = archivo.length();
        String clave = nombre + "|" + tamano;

        if (archivosAgrupados.containsKey(clave)) {
            archivosAgrupados.get(clave).add(archivo);
        } else {
            List<File> lista = new ArrayList<>();
            lista.add(archivo);
            archivosAgrupados.put(clave, lista);
        }
    }

    public void imprimirDuplicados(Map<String, List<File>> duplicados) {
        System.out.println("\n========== ARCHIVOS DUPLICADOS ==========");
        if (duplicados.isEmpty()) {
            System.out.println("No se encontraron archivos duplicados.");
            return;
        }
        int numeroGrupo = 1;
        for (String clave : duplicados.keySet()) {
            List<File> archivos = duplicados.get(clave);
            System.out.println("\nGRUPO " + numeroGrupo);
            System.out.println("Nombre y tamaño: " + clave);
            
            for (File archivo : archivos) {
                System.out.println("Archivo: " + archivo.getName());
                System.out.println("Ubicación: " + archivo.getAbsolutePath());
                System.out.println("Tamaño: " + archivo.length() + " bytes");
                System.out.println("-------------------------");
            }
            numeroGrupo++;
        }
        System.out.println("==========================================");
    }
}
  
