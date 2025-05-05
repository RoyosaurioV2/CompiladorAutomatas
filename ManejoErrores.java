import java.io.*;
import java.util.*;

public class ManejoErrores {
    private static ArrayList<String> errores = new ArrayList<>();
    private static final String ARCHIVO_ERRORES = "errores.log";

    public static void registrarError(String mensaje) {
        System.out.println("Registrando error: " + mensaje); //  Verificar si entra aquí
        errores.add(mensaje);
    }

    public static void guardarErrores() {
        System.out.println("Guardando errores en el archivo..."); //  Verificar si se ejecuta
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_ERRORES))) {
            for (String error : errores) {
                writer.write(error);
                writer.newLine();
            }
            System.out.println("Errores guardados correctamente."); //  Confirmar si se guardó
        } catch (IOException e) {
            System.out.println("Error al guardar errores: " + e.getMessage());
        }
    }

    public static void mostrarErrores() {
        System.out.println("Mostrando errores..."); //  Verificar si se ejecuta
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_ERRORES))) {
            String linea;
            System.out.println("\nTabla de Errores:");
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            System.out.println("No hay errores registrados o no se puede leer el archivo.");
        }
    }

    // 🔥 Metodo main para ejecutar el comando `java ManejoErrores -mostrar`
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-mostrar")) {
            mostrarErrores();
        } else {
            System.out.println("Uso: java ManejoErrores -mostrar");
        }
    }
}
