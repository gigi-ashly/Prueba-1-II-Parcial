/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba.i.s6;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gigiw
 */
public class FileController {
    private FileExplorer explorador;
    private FileStatistics estadisticas;
    private FileSearch buscador;
    private DuplicateFinder detector;
    private FileManager gestor;
    private TextFileManager editor;
    private FileLogger bitacora;
    private ConfigManager configuracion;

    public FileController() {

        explorador = new FileExplorer();
        estadisticas = new FileStatistics();
        buscador = new FileSearch();
        detector = new DuplicateFinder();
        gestor = new FileManager();
        editor = new TextFileManager();
        bitacora = new FileLogger();
        configuracion = new ConfigManager();
    }

    public void establecerRuta(String ruta)
            throws RutaInvalidaException {

        if (ruta == null || ruta.trim().isEmpty()) {
            throw new RutaInvalidaException(
                    "La ruta no puede estar vacía.");
        }

        File carpeta = new File(ruta);

        if (!carpeta.exists() || !carpeta.isDirectory()) {
            throw new RutaInvalidaException(
                    "La ruta no existe o no es un directorio.");
        }

        explorador.establecerRuta(ruta);
        configuracion.setRuta(ruta);
    }

    public void analizar() {

        estadisticas.analizar(
                explorador.getDirectorioRaiz());

        try {
            bitacora.registrar(
                    "ANALIZAR",
                    explorador.getDirectorioRaiz().getAbsolutePath());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<File> buscar(
            String nombre,
            String extension,
            long minimo,
            long maximo,
            Date desde,
            Date hasta,
            int profundidad) {

        List<File> resultados = buscador.buscar(
                explorador.getDirectorioRaiz(),
                nombre,
                extension,
                minimo,
                maximo,
                desde,
                hasta,
                profundidad);

        try {
            bitacora.registrar(
                    "BUSCAR",
                    explorador.getDirectorioRaiz().getAbsolutePath());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return resultados;
    }

    public Map<String, List<File>> buscarDuplicados() {

        return detector.encontrarDuplicados(
                explorador.getDirectorioRaiz());
    }

    public FileStatistics getEstadisticas() {
        return estadisticas;
    }

    public FileManager getGestor() {
        return gestor;
    }

    public TextFileManager getEditor() {
        return editor;
    }

    public FileLogger getBitacora() {
        return bitacora;
    }

    public ConfigManager getConfiguracion() {
        return configuracion;
    }

    public File getDirectorioRaiz() {
        return explorador.getDirectorioRaiz();
    }
}
   

