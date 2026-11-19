
package prueba.i.s6;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gigiw
 */
public class FileStatistics {
    private Map<String, ExtensionStats> estadisticas;
    private long tamanoTotal;
    private int totalArchivos;
    private int totalCarpetas;
    private int carpetasVacias;
    private int profundidadMaxima;
    private File archivoMasGrande;
    private File archivoMasReciente;
    private Set<String> rutasVisitadas;

    public FileStatistics() {
        estadisticas = new HashMap<>();
        estadisticas.put("TXT", new ExtensionStats());
        estadisticas.put("JAVA", new ExtensionStats());
        estadisticas.put("PDF", new ExtensionStats());
        estadisticas.put("OTROS", new ExtensionStats());
        rutasVisitadas = new HashSet<>();
    }

    public void analizar(File directorio) {
        if (directorio == null || !directorio.exists() || !directorio.isDirectory()) {
            System.out.println("Directorio inválido.");
            return;
        }
        reiniciarEstadisticas();
        recorrer(directorio, 0);
    }

    private void reiniciarEstadisticas() {
        tamanoTotal = 0;
        totalArchivos = 0;
        totalCarpetas = 0;
        carpetasVacias = 0;
        profundidadMaxima = 0;
        archivoMasGrande = null;
        archivoMasReciente = null;
        rutasVisitadas.clear();

        for (ExtensionStats dato : estadisticas.values()) {
            dato.cantidad = 0;
            dato.tamano = 0;
        }
    }

    private void recorrer(File carpeta, int profundidad) {
        try {
            String rutaCanonica = carpeta.getCanonicalPath();
            if (rutasVisitadas.contains(rutaCanonica)) {
                return;
            }
            rutasVisitadas.add(rutaCanonica);
        } catch (IOException e) {
            System.out.println("No se pudo obtener la ruta: " + carpeta.getAbsolutePath());
            return;
        }
        totalCarpetas++;
        if (profundidad > profundidadMaxima) {
            profundidadMaxima = profundidad;
        }
        File[] elementos = carpeta.listFiles();
        if (elementos == null) {
            System.out.println("No se pudo acceder a: " + carpeta.getAbsolutePath());
            return;
        }
        if (elementos.length == 0) {
            carpetasVacias++;
        }
        for (File elemento : elementos) {
            if (elemento.isDirectory()) {
                recorrer(elemento, profundidad + 1);
            } else if (elemento.isFile()) {
                registrarArchivo(elemento);

            }
        }
    }

    private void registrarArchivo(File archivo) {
        long tamano = archivo.length();
        totalArchivos++;
        tamanoTotal += tamano;
        String categoria = obtenerCategoria(archivo);
        ExtensionStats dato = estadisticas.get(categoria);
        dato.cantidad++;
        dato.tamano += tamano;

        if (archivoMasGrande == null || tamano > archivoMasGrande.length()) {
            archivoMasGrande = archivo;
        }
        if (archivoMasReciente == null || archivo.lastModified() > archivoMasReciente.lastModified()) {
            archivoMasReciente = archivo;
        }
    }

    private String obtenerCategoria(File archivo) {
        String nombre = archivo.getName().toLowerCase();
        int posicion = nombre.lastIndexOf(".");

        if (posicion <= 0 || posicion == nombre.length() - 1) {
            return "OTROS";
        }
        String extension = nombre.substring(posicion + 1);
        switch (extension) {
            case "txt":
                return "TXT";
            case "java":
                return "JAVA";
            case "pdf":
                return "PDF";
            default:
                return "OTROS";
        }
    }

    private String formatearTamano(long bytes) {
        double tamano = bytes;
        if (tamano < 1024) {
            return bytes + " bytes";
        }
        tamano = tamano / 1024;
        if (tamano < 1024) {
            return String.format("%.2f KB", tamano);
        }
        tamano = tamano / 1024;
        if (tamano < 1024) {
            return String.format("%.2f MB", tamano);
        }
        tamano = tamano / 1024;
        return String.format("%.2f GB", tamano);
    }

    public void imprimirReporte() {
        System.out.println("\n========== ESTADÍSTICAS ==========");
        for (String categoria : estadisticas.keySet()) {
            ExtensionStats dato = estadisticas.get(categoria);
            double porcentaje = 0;
            if (tamanoTotal > 0) {
                porcentaje = (dato.tamano * 100.0) / tamanoTotal;
            }
            System.out.println("\nCategoría: " + categoria);
            System.out.println("Cantidad: " + dato.cantidad);
            System.out.println("Tamaño acumulado: " + formatearTamano(dato.tamano));
            System.out.printf("Porcentaje del tamaño total: %.2f%%\n", porcentaje);
        }
        System.out.println("\n========== ESTADÍSTICAS GENERALES ==========");
        System.out.println("Total de archivos: " + totalArchivos);
        System.out.println("Tamaño total: " + formatearTamano(tamanoTotal));
        System.out.println("Total de carpetas: " + totalCarpetas);
        System.out.println("Carpetas vacías: " + carpetasVacias);
        System.out.println("Profundidad máxima: " + profundidadMaxima);

        if (archivoMasGrande != null) {
            System.out.println("\nArchivo más grande:");
            System.out.println(archivoMasGrande.getAbsolutePath());
            System.out.println(formatearTamano(archivoMasGrande.length()));
        }

        if (archivoMasReciente != null) {
            System.out.println("\nArchivo más reciente:");
            System.out.println(archivoMasReciente.getAbsolutePath());
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            System.out.println(formato.format(new Date(archivoMasReciente.lastModified())));
        }
        System.out.println("\n============================================");
    }

    private static class ExtensionStats {
        int cantidad;
        long tamano;
    }

    
public String generarReporte() {

    StringBuilder reporte = new StringBuilder();

    reporte.append("========== ESTADÍSTICAS ==========\n\n");

    for (String categoria : estadisticas.keySet()) {

        ExtensionStats dato = estadisticas.get(categoria);

        double porcentaje = 0;

        if (tamanoTotal > 0) {
            porcentaje = dato.tamano * 100.0 / tamanoTotal;
        }

        reporte.append("Categoría: ")
                .append(categoria).append("\n");

        reporte.append("Cantidad: ")
                .append(dato.cantidad).append("\n");

        reporte.append("Tamaño: ")
                .append(formatearTamano(dato.tamano)).append("\n");

        reporte.append(String.format(
                "Porcentaje: %.2f%%\n\n", porcentaje));
    }

    reporte.append("========== GENERALES ==========\n");

    reporte.append("Archivos: ")
            .append(totalArchivos).append("\n");

    reporte.append("Tamaño total: ")
            .append(formatearTamano(tamanoTotal)).append("\n");

    reporte.append("Carpetas: ")
            .append(totalCarpetas).append("\n");

    reporte.append("Carpetas vacías: ")
            .append(carpetasVacias).append("\n");

    reporte.append("Profundidad máxima: ")
            .append(profundidadMaxima).append("\n");

    if (archivoMasGrande != null) {

        reporte.append("\nArchivo más grande:\n")
                .append(archivoMasGrande.getAbsolutePath())
                .append("\n");
    }

    if (archivoMasReciente != null) {

        reporte.append("\nArchivo más reciente:\n")
                .append(archivoMasReciente.getAbsolutePath())
                .append("\n");
    }

    return reporte.toString();
}

}
  

