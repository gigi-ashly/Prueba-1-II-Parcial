package prueba.i.s6;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author gigiw
 */
public class ConfigManager {
    private File archivoConfiguracion;
    private String ruta;
    private String nombre;
    private String extension;
    private int tamanoMinimo;
    private int tamanoMaximo;
    private int profundidad;

    public ConfigManager() {
        archivoConfiguracion = new File("config.txt");
        ruta = "";
        nombre = "";
        extension = "";
        tamanoMinimo = -1;
        tamanoMaximo = -1;
        profundidad = 0;
    }

    public void guardarConfiguracion() throws IOException {
        FileWriter escritor = new FileWriter(archivoConfiguracion);

        try {
            escritor.write("RUTA=" + ruta + System.lineSeparator());
            escritor.write("NOMBRE=" + nombre + System.lineSeparator());
            escritor.write("EXTENSION=" + extension + System.lineSeparator());
            escritor.write("TAMANO_MINIMO=" + tamanoMinimo + System.lineSeparator());
            escritor.write("TAMANO_MAXIMO=" + tamanoMaximo + System.lineSeparator());
            escritor.write("PROFUNDIDAD=" + profundidad + System.lineSeparator());
            escritor.flush();
            System.out.println("Configuración guardada correctamente.");
        } finally {
            escritor.close();
        }
    }

    public void cargarConfiguracion() throws IOException {
        if (!archivoConfiguracion.exists()) {
            System.out.println("No existe configuración anterior. " + "Se utilizarán los valores predeterminados.");
            return;
        }
        FileReader lector = new FileReader(archivoConfiguracion);

        try {
            Map<String, String> datos = new HashMap<>();
            StringBuilder linea = new StringBuilder();
            int caracter;

            while ((caracter = lector.read()) != -1) {
                if (caracter == '\n') {
                    procesarLinea(linea.toString(), datos);
                    linea.setLength(0);
                } else if (caracter != '\r') {
                    linea.append((char) caracter);
                }
            }

            if (linea.length() > 0) {
                procesarLinea(linea.toString(), datos);
            }

            if (datos.containsKey("RUTA")) {
                ruta = datos.get("RUTA");
            }

            if (datos.containsKey("NOMBRE")) {
                nombre = datos.get("NOMBRE");
            }

            if (datos.containsKey("EXTENSION")) {
                extension = datos.get("EXTENSION");
            }

            try {
                if (datos.containsKey("TAMANO_MINIMO")) {
                    tamanoMinimo = Integer.parseInt(datos.get("TAMANO_MINIMO"));
                }

                if (datos.containsKey("TAMANO_MAXIMO")) {
                    tamanoMaximo = Integer.parseInt(datos.get("TAMANO_MAXIMO"));
                }

                if (datos.containsKey("PROFUNDIDAD")) {
                    profundidad = Integer.parseInt(datos.get("PROFUNDIDAD"));
                }
            } catch (NumberFormatException e) {
                throw new IOException("La configuración contiene valores numéricos inválidos.", e);
            }
            validarConfiguracion();
            System.out.println("Configuración cargada correctamente.");
        } finally {
            lector.close();
        }
    }

    private void procesarLinea(String linea, Map<String, String> datos) {
        int posicion = linea.indexOf("=");
        if (posicion <= 0) {
            return;
        }
        String clave = linea.substring(0, posicion).trim();
        String valor = linea.substring(posicion + 1);
        datos.put(clave, valor);
    }

    private void validarConfiguracion() throws IOException {
        if (tamanoMinimo < -1 || tamanoMaximo < -1) {
            throw new IOException("Los tamaños guardados no son válidos.");
        }

        if (tamanoMinimo >= 0 && tamanoMaximo >= 0 && tamanoMinimo > tamanoMaximo) {
            throw new IOException("El tamaño mínimo supera al máximo.");
        }

        if (profundidad < 0) {
            throw new IOException("La profundidad no puede ser negativa.");
        }
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta == null ? "" : ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? "" : nombre;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension == null ? "" : extension;
    }

    public int getTamanoMinimo() {
        return tamanoMinimo;
    }

    public void setTamanoMinimo(int tamanoMinimo) {
        this.tamanoMinimo = tamanoMinimo;
    }

    public int getTamanoMaximo() {
        return tamanoMaximo;
    }

    public void setTamanoMaximo(int tamanoMaximo) {
        this.tamanoMaximo = tamanoMaximo;
    }

    public int getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(int profundidad) {
        this.profundidad = profundidad;
    }

}


