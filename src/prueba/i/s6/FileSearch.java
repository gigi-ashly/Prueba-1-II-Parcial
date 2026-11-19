package prueba.i.s6;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author gigiw
 */
public class FileSearch {
    private List<File> resultados;
    private Set<String> rutasVisitadas;
    private String nombreBuscado;
    private String extensionBuscada;
    private long tamanoMinimo;
    private long tamanoMaximo;
    private Date fechaDesde;
    private Date fechaHasta;
    private int profundidadMaxima;

    public FileSearch() {
        resultados = new ArrayList<>();
        rutasVisitadas = new HashSet<>();
    }

    public List<File> buscar(File directorio, String nombre, String extension, long minimoKB, long maximoKB, Date desde, Date hasta, int profundidad) {
        if (directorio == null || !directorio.exists() || !directorio.isDirectory()) {
            throw new IllegalArgumentException("El directorio seleccionado no es válido.");
        }
        if (minimoKB < -1 || maximoKB < -1) {
            throw new IllegalArgumentException("Los tamaños no pueden ser negativos.");
        }
        if (minimoKB >= 0 && maximoKB >= 0 && minimoKB > maximoKB) {
            throw new IllegalArgumentException("El tamaño mínimo no puede superar al máximo.");
        }
        if (desde != null && hasta != null && desde.after(hasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final.");
        }
        if (profundidad < 0) {
            throw new IllegalArgumentException("La profundidad no puede ser negativa.");
        }
        if (nombre == null) {
            this.nombreBuscado = "";
        } else {
            this.nombreBuscado = nombre.trim().toLowerCase();
        }
        this.extensionBuscada = normalizarExtension(extension);

        if (minimoKB == -1) {
            this.tamanoMinimo = -1;
        } else {
            this.tamanoMinimo = minimoKB * 1024;
        }
        if (maximoKB == -1) {
            this.tamanoMaximo = -1;
        } else {
            this.tamanoMaximo = maximoKB * 1024;
        }
        this.fechaDesde = desde;
        this.fechaHasta = hasta;
        this.profundidadMaxima = profundidad;
        resultados.clear();
        rutasVisitadas.clear();
        recorrer(directorio, 0);
        return new ArrayList<>(resultados);
    }

    private String normalizarExtension(String extension) {
        if (extension == null || extension.trim().isEmpty()) {
            return "";
        }
        String resultado = extension.trim().toLowerCase();

        if (!resultado.startsWith(".")) {
            resultado = "." + resultado;
        }
        return resultado;
    }

    private void recorrer(File carpeta, int nivel) {
        if (profundidadMaxima > 0 && nivel > profundidadMaxima) {
            return;
        }
        try {
            String ruta = carpeta.getCanonicalPath();
            if (rutasVisitadas.contains(ruta)) {
                return;
            }
            rutasVisitadas.add(ruta);
        } catch (IOException e) {
            System.out.println("Error al acceder a: " + carpeta.getAbsolutePath());
            return;
        }
        File[] elementos = carpeta.listFiles();
        if (elementos == null) {
            System.out.println("No se pudo leer: " + carpeta.getAbsolutePath());
            return;
        }
        for (File elemento : elementos) {
            if (elemento.isDirectory()) {
                recorrer(elemento, nivel + 1);
            } else if (elemento.isFile()) {
                if (cumpleFiltros(elemento)) {
                    resultados.add(elemento);
                }
            }
        }
    }

    private boolean cumpleFiltros(File archivo) {
        if (!archivo.getName().toLowerCase().contains(nombreBuscado)) {
            return false;
        }
        if (!extensionBuscada.isEmpty() && !archivo.getName().toLowerCase().endsWith(extensionBuscada)) {
            return false;
        }
        long tamano = archivo.length();
        if (tamanoMinimo >= 0 && tamano < tamanoMinimo) {
            return false;
        }
        if (tamanoMaximo >= 0 && tamano > tamanoMaximo) {
            return false;
        }
        long fechaModificacion = archivo.lastModified();
        
        if (fechaDesde != null && fechaModificacion < fechaDesde.getTime()) {
            return false;
        }
        if (fechaHasta != null && fechaModificacion > fechaHasta.getTime()) {
            return false;
        }
        return true;
    }

}
  

