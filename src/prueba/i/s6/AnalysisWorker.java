/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba.i.s6;
import javax.swing.SwingWorker;

/**
 *
 * @author gigiw
 */

public class AnalysisWorker extends SwingWorker<Void, String> {

    private FileController controlador;

    public AnalysisWorker(FileController controlador) {
        this.controlador = controlador;
    }

    @Override
    protected Void doInBackground() throws Exception {

        setProgress(0);

        publish("Iniciando análisis...");

        if (isCancelled()) {
            return null;
        }

        setProgress(30);

        controlador.analizar();

        if (isCancelled()) {
            return null;
        }

        setProgress(100);

        publish("Análisis completado.");

        return null;
    }

    @Override
    protected void process(java.util.List<String> mensajes) {

        for (String mensaje : mensajes) {
            System.out.println(mensaje);
        }
    }

    @Override
    protected void done() {

        if (isCancelled()) {
            System.out.println("Análisis cancelado.");
        } else {
            System.out.println("Proceso finalizado.");
        }
    }
}

