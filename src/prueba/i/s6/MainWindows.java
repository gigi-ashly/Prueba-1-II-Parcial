
package prueba.i.s6;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author gigiw
 */

public class MainWindows extends JFrame {
    private FileController controlador;
    private JTextField txtRuta;
    private JTextField txtNombre;
    private JTextField txtExtension;
    private JTextField txtMinimo;
    private JTextField txtMaximo;

    private JTree arbol;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JTextArea areaEstadisticas;

    private JProgressBar progreso;
    private JButton btnCancelar;

    private AnalysisWorker trabajador;

    public MainWindows() {

        controlador = new FileController();

        configurarVentana();
        construirInterfaz();
        cargarConfiguracionInicial();
    }

    private void configurarVentana() {

        setTitle("FileExplorer - Gestor de archivos");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(
                    java.awt.event.WindowEvent e) {

                cerrarAplicacion();
            }
        });
    }

    private void construirInterfaz() {

        setLayout(new BorderLayout(8, 8));

        add(crearPanelSuperior(), BorderLayout.NORTH);

        JTabbedPane pestañas = new JTabbedPane();

        pestañas.addTab("Explorador", crearPanelExplorador());

        pestañas.addTab("Búsqueda", crearPanelBusqueda());

        pestañas.addTab("Estadísticas", crearPanelEstadisticas());

        pestañas.addTab("Herramientas", crearPanelHerramientas());

        add(pestañas, BorderLayout.CENTER);

        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {

        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.setBorder(BorderFactory.createTitledBorder(
                "Directorio raíz"));

        txtRuta = new JTextField();

        JButton btnSeleccionar = new JButton("Seleccionar");

        JButton btnCargar = new JButton("Explorar");

        btnSeleccionar.addActionListener(e -> seleccionarDirectorio());

        btnCargar.addActionListener(e -> cargarDirectorio());

        JPanel botones = new JPanel(new FlowLayout());

        botones.add(btnSeleccionar);
        botones.add(btnCargar);

        panel.add(txtRuta, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelExplorador() {

        JPanel panel = new JPanel(new BorderLayout());

        arbol = new JTree(
                new DefaultMutableTreeNode("Selecciona un directorio"));

        arbol.addTreeSelectionListener(e -> {

            DefaultMutableTreeNode nodo =
                    (DefaultMutableTreeNode) arbol
                            .getLastSelectedPathComponent();

            if (nodo != null) {

                Object objeto = nodo.getUserObject();

                if (objeto instanceof File) {
                    mostrarInformacionArchivo((File) objeto);
                }
            }
        });

        panel.add(new JScrollPane(arbol), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Panel de búsqueda avanzada.
     */
    private JPanel crearPanelBusqueda() {

        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel filtros = new JPanel(new GridLayout(2, 5, 5, 5));

        txtNombre = new JTextField();
        txtExtension = new JTextField();

        txtMinimo = new JTextField("-1");
        txtMaximo = new JTextField("-1");

        JButton btnBuscar = new JButton("Buscar");

        filtros.add(new JLabel("Nombre"));
        filtros.add(new JLabel("Extensión"));
        filtros.add(new JLabel("Mínimo KB"));
        filtros.add(new JLabel("Máximo KB"));
        filtros.add(new JLabel("Acción"));

        filtros.add(txtNombre);
        filtros.add(txtExtension);
        filtros.add(txtMinimo);
        filtros.add(txtMaximo);
        filtros.add(btnBuscar);

        String[] columnas = {
            "Nombre", "Ruta absoluta", "Tamaño (bytes)", "Modificado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        tabla.setAutoCreateRowSorter(true);

        btnBuscar.addActionListener(e -> buscarArchivos());

        panel.add(filtros, BorderLayout.NORTH);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Panel de estadísticas.
     */
    private JPanel crearPanelEstadisticas() {

        JPanel panel = new JPanel(new BorderLayout());

        areaEstadisticas = new JTextArea();

        areaEstadisticas.setEditable(false);

        JButton btnAnalizar = new JButton("Analizar directorio");

        btnAnalizar.addActionListener(e -> iniciarAnalisis());

        panel.add(btnAnalizar, BorderLayout.NORTH);

        panel.add(new JScrollPane(areaEstadisticas),
                BorderLayout.CENTER);

        return panel;
    }

    /**
     * Panel de herramientas.
     */
    private JPanel crearPanelHerramientas() {

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JButton btnCarpeta = new JButton("Crear carpeta");

        JButton btnArchivo = new JButton("Crear archivo de texto");

        JButton btnRenombrar = new JButton("Renombrar seleccionado");

        JButton btnCopiar = new JButton("Copiar archivo");

        JButton btnMover = new JButton("Mover archivo");

        JButton btnEliminar = new JButton("Eliminar seleccionado");

        JButton btnDuplicados = new JButton("Buscar duplicados");

        JButton btnBitacora = new JButton("Ver bitácora");

        btnCarpeta.addActionListener(e -> crearCarpeta());

        btnArchivo.addActionListener(e -> crearArchivo());

        btnRenombrar.addActionListener(e -> renombrarArchivo());

        btnCopiar.addActionListener(e -> copiarArchivo());

        btnMover.addActionListener(e -> moverArchivo());

        btnEliminar.addActionListener(e -> eliminarArchivo());

        btnDuplicados.addActionListener(e -> mostrarDuplicados());

        btnBitacora.addActionListener(e -> mostrarBitacora());

        panel.add(btnCarpeta);
        panel.add(btnArchivo);
        panel.add(btnRenombrar);
        panel.add(btnCopiar);
        panel.add(btnMover);
        panel.add(btnEliminar);
        panel.add(btnDuplicados);
        panel.add(btnBitacora);

        return panel;
    }

    /**
     * Panel inferior con progreso y cancelación.
     */
    private JPanel crearPanelInferior() {

        JPanel panel = new JPanel(new BorderLayout(5, 5));

        progreso = new JProgressBar(0, 100);

        progreso.setStringPainted(true);

        btnCancelar = new JButton("Cancelar");

        btnCancelar.setEnabled(false);

        btnCancelar.addActionListener(e -> {

            if (trabajador != null) {
                trabajador.cancel(true);
            }
        });

        panel.add(progreso, BorderLayout.CENTER);

        panel.add(btnCancelar, BorderLayout.EAST);

        return panel;
    }

    /**
     * Permite seleccionar una carpeta.
     */
    private void seleccionarDirectorio() {

        JFileChooser selector = new JFileChooser();

        selector.setFileSelectionMode(
                JFileChooser.DIRECTORIES_ONLY);

        int resultado = selector.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {

            txtRuta.setText(
                    selector.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Establece la ruta y carga el árbol.
     */
    private void cargarDirectorio() {

        try {

            controlador.establecerRuta(txtRuta.getText());

            cargarArbol();

            JOptionPane.showMessageDialog(
                    this, "Directorio cargado correctamente.");

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    /**
     * Construye el árbol de directorios recursivamente.
     */
    private void cargarArbol() {

        File raiz = controlador.getDirectorioRaiz();

        DefaultMutableTreeNode nodoRaiz =
                new DefaultMutableTreeNode(raiz);

        Set<String> visitadas = new HashSet<>();

        construirNodos(raiz, nodoRaiz, visitadas);

        arbol.setModel(new javax.swing.tree.DefaultTreeModel(nodoRaiz));

    }

    private void construirNodos(
            File carpeta,
            DefaultMutableTreeNode nodo,
            Set<String> visitadas) {

        try {

            String ruta = carpeta.getCanonicalPath();

            if (visitadas.contains(ruta)) {
                return;
            }

            visitadas.add(ruta);

        } catch (IOException e) {
            return;
        }

        File[] elementos = carpeta.listFiles();

        if (elementos == null) {
            return;
        }

        for (File elemento : elementos) {

            DefaultMutableTreeNode hijo =
                    new DefaultMutableTreeNode(elemento);

            nodo.add(hijo);

            if (elemento.isDirectory()) {

                construirNodos(elemento, hijo, visitadas);
            }
        }
    }

    /**
     * Ejecuta la búsqueda avanzada.
     */
    private void buscarArchivos() {

        try {

            long minimo = Long.parseLong(txtMinimo.getText());

            long maximo = Long.parseLong(txtMaximo.getText());

            List<File> encontrados = controlador.buscar(
                    txtNombre.getText(),
                    txtExtension.getText(),
                    minimo,
                    maximo,
                    null,
                    null,
                    0
            );

            modeloTabla.setRowCount(0);

            for (File archivo : encontrados) {

                modeloTabla.addRow(new Object[]{
                    archivo.getName(),
                    archivo.getAbsolutePath(),
                    archivo.length(),
                    new Date(archivo.lastModified())
                });
            }

            if (encontrados.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontraron archivos que coincidan con los criterios."
                );
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    /**
     * Inicia el análisis en segundo plano.
     */
    private void iniciarAnalisis() {

        if (controlador.getDirectorioRaiz() == null) {

            mostrarError(new Exception(
                    "Primero selecciona un directorio."));

            return;
        }

        if (trabajador != null && !trabajador.isDone()) {
            return;
        }

        progreso.setValue(0);

        btnCancelar.setEnabled(true);

        trabajador = new AnalysisWorker(controlador);

        trabajador.addPropertyChangeListener(evento -> {

            if ("progress".equals(evento.getPropertyName())) {

                progreso.setValue((Integer) evento.getNewValue());
            }
        });

        trabajador.execute();

        // Esperar la finalización sin bloquear la interfaz
        new javax.swing.Timer(200, e -> {

            if (trabajador == null || trabajador.isDone()) {

                ((javax.swing.Timer) e.getSource()).stop();

                btnCancelar.setEnabled(false);

                if (trabajador != null && !trabajador.isCancelled()) {

                    areaEstadisticas.setText(
                            controlador.getEstadisticas()
                                    .generarReporte());
                }
            }

        }).start();
    }

    /**
     * Crea una carpeta.
     */
    private void crearCarpeta() {

        String nombre = JOptionPane.showInputDialog(
                this, "Nombre o ruta de la carpeta:");

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        try {

            File destino = new File(
                    controlador.getDirectorioRaiz(), nombre);

            if (controlador.getGestor()
                    .crearCarpeta(destino.getPath())) {

                registrar("CREAR CARPETA", destino.getAbsolutePath());

                cargarArbol();
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    /**
     * Crea un archivo de texto.
     */
    private void crearArchivo() {

        String nombre = JOptionPane.showInputDialog(
                this, "Nombre del archivo (ej. notas.txt):");

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        JTextArea contenido = new JTextArea(10, 30);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(contenido),
                "Contenido del archivo",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        File destino = new File(
                controlador.getDirectorioRaiz(), nombre);

        try {

            boolean creado = controlador.getGestor()
                    .crearArchivoTexto(
                            destino.getPath(),
                            contenido.getText());

            if (creado) {

                registrar("CREAR ARCHIVO",
                        destino.getAbsolutePath());

                cargarArbol();
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    /**
     * Obtiene el archivo seleccionado en el árbol.
     */
    private File obtenerSeleccionado() {

        Object seleccion = arbol.getLastSelectedPathComponent();

        if (seleccion == null) {
            return null;
        }

        DefaultMutableTreeNode nodo =
                (DefaultMutableTreeNode) seleccion;

        Object objeto = nodo.getUserObject();

        if (objeto instanceof File) {
            return (File) objeto;
        }

        return null;
    }

    /**
     * Renombra el elemento seleccionado.
     */
    private void renombrarArchivo() {

        File archivo = obtenerSeleccionado();

        if (archivo == null) {
            return;
        }

        String nombre = JOptionPane.showInputDialog(
                this, "Nuevo nombre:", archivo.getName());

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        if (controlador.getGestor().renombrar(archivo, nombre)) {

            registrar("RENOMBRAR", archivo.getAbsolutePath());

            cargarArbol();
        }
    }

    /**
     * Copia el archivo seleccionado.
     */
    private void copiarArchivo() {

        File archivo = obtenerSeleccionado();

        if (archivo == null || !archivo.isFile()) {
            return;
        }

        JFileChooser selector = new JFileChooser();

        selector.setSelectedFile(
                new File(archivo.getName()));

        if (selector.showSaveDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            File destino = selector.getSelectedFile();

            if (destino.exists()) {

                int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        "El archivo existe. ¿Deseas sobrescribirlo?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION);

                if (confirmacion != JOptionPane.YES_OPTION) {
                    return;
                }

                // El gestor no sobrescribe; eliminamos el destino
                // únicamente después de la confirmación.
                if (!destino.delete()) {
                    mostrarError(new Exception(
                            "No se pudo reemplazar el destino."));
                    return;
                }
            }

            try {

                if (controlador.getGestor().copiarArchivo(
                        archivo, destino.getAbsolutePath())) {

                    registrar("COPIAR", destino.getAbsolutePath());

                    cargarArbol();
                }

            } catch (Exception e) {

                mostrarError(e);
            }
        }
    }

    /**
     * Mueve el archivo seleccionado.
     */
    private void moverArchivo() {

        File archivo = obtenerSeleccionado();

        if (archivo == null || !archivo.isFile()) {
            return;
        }

        JFileChooser selector = new JFileChooser();

        selector.setSelectedFile(new File(archivo.getName()));

        if (selector.showSaveDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            File destino = selector.getSelectedFile();

            try {

                if (controlador.getGestor().moverArchivo(
                        archivo, destino.getAbsolutePath())) {

                    registrar("MOVER", destino.getAbsolutePath());

                    cargarArbol();
                }

            } catch (Exception e) {

                mostrarError(e);
            }
        }
    }

    /**
     * Elimina el elemento seleccionado.
     */
    private void eliminarArchivo() {

        File archivo = obtenerSeleccionado();

        if (archivo == null) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar permanentemente?\n"
                        + archivo.getAbsolutePath(),
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {

            if (controlador.getGestor().eliminar(archivo)) {

                registrar("ELIMINAR", archivo.getAbsolutePath());

                cargarArbol();
            }
        }
    }

    /**
     * Busca y muestra los duplicados.
     */
    private void mostrarDuplicados() {

        try {

            Map<String, List<File>> duplicados =
                    controlador.buscarDuplicados();

            StringBuilder texto = new StringBuilder();

            for (String clave : duplicados.keySet()) {

                texto.append("\n").append(clave).append("\n");

                for (File archivo : duplicados.get(clave)) {

                    texto.append(archivo.getAbsolutePath())
                            .append("\n");
                }
            }

            if (duplicados.isEmpty()) {
                texto.append("No se encontraron duplicados.");
            }

            JTextArea area = new JTextArea(
                    texto.toString(), 20, 50);

            area.setEditable(false);

            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(area),
                    "Archivos duplicados",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    /**
     * Abre el editor de texto.
     */
    private void mostrarInformacionArchivo(File archivo) {

        if (!archivo.isFile()) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas abrir este archivo en el editor?",
                "Editor de texto",
                JOptionPane.YES_NO_OPTION);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            String contenido = controlador.getEditor()
                    .leerArchivo(archivo);

            JTextArea editor = new JTextArea(contenido, 25, 60);

            int resultado = JOptionPane.showConfirmDialog(
                    this,
                    new JScrollPane(editor),
                    archivo.getName(),
                    JOptionPane.OK_CANCEL_OPTION);

            if (resultado == JOptionPane.OK_OPTION) {

                int confirmar = JOptionPane.showConfirmDialog(
                        this,
                        "¿Guardar los cambios?",
                        "Confirmar guardado",
                        JOptionPane.YES_NO_OPTION);

                if (confirmar == JOptionPane.YES_OPTION) {

                    controlador.getEditor()
                            .guardarArchivo(archivo, editor.getText());

                    registrar("EDITAR", archivo.getAbsolutePath());
                }
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    /**
     * Muestra la bitácora.
     */
    private void mostrarBitacora() {

        try {

            String contenido = controlador.getBitacora()
                    .leerBitacora();

            JTextArea area = new JTextArea(contenido, 20, 50);

            area.setEditable(false);

            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(area),
                    "Bitácora de operaciones",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    /**
     * Registra una operación.
     */
    private void registrar(String accion, String ruta) {

        try {

            controlador.getBitacora().registrar(accion, ruta);

        } catch (IOException e) {

            mostrarError(e);
        }
    }

    /**
     * Carga la configuración anterior.
     */
    private void cargarConfiguracionInicial() {

        try {

            controlador.getConfiguracion()
                    .cargarConfiguracion();

            txtRuta.setText(
                    controlador.getConfiguracion().getRuta());

        } catch (Exception e) {

            System.out.println(
                    "No se pudo cargar la configuración: "
                    + e.getMessage());
        }
    }

    /**
     * Guarda la configuración al cerrar.
     */
    private void cerrarAplicacion() {

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas guardar la configuración y salir?",
                "Salir",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (respuesta == JOptionPane.CANCEL_OPTION
                || respuesta == JOptionPane.CLOSED_OPTION) {
            return;
        }

        if (respuesta == JOptionPane.YES_OPTION) {

            try {

                ConfigManager config =
                        controlador.getConfiguracion();

                config.setRuta(txtRuta.getText());

                if (txtNombre != null) {
                    config.setNombre(txtNombre.getText());
                    config.setExtension(txtExtension.getText());

                    config.setTamanoMinimo(
                            Integer.parseInt(txtMinimo.getText()));

                    config.setTamanoMaximo(
                            Integer.parseInt(txtMaximo.getText()));
                }

                config.guardarConfiguracion();

            } catch (Exception e) {

                mostrarError(e);
                return;
            }
        }

        if (trabajador != null && !trabajador.isDone()) {
            trabajador.cancel(true);
        }

        dispose();
    }

    private void mostrarError(Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MainWindows ventana = new MainWindows();

            ventana.setVisible(true);
        });
    }
}


