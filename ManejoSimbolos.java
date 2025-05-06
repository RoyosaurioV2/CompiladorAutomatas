
import java.io.*;
import java.util.*;

public class ManejoSimbolos{
    private static Hashtable<String, Simbolo> tablaSimbolos = new Hashtable<>();
    private static int direccionMemoria = 1000;
    private static final Set<String> PALABRAS_RESERVADAS = new HashSet<>(Arrays.asList(
        "Inicio", "Fin", "Funcion", "FinFuncion", "Retornar", "Interpretar", "Escribir", "Establecer",
        "Entero", "Flotante", "Cadena", "Caracter", "Booleano", "Si", "Entonces", "Sino", "FinSi", "Para",
        "Hasta", "Con", "FinPara", "Mientras", "FinMientras", "Repetir", "Finaliza", "Conforme", "Caso",
        "FinConforme", "Hacer"
    ));

    static class Simbolo {
        String nombre;
        String tipo;
        int direccion;
        String atributo;
        int dimension;
        
        Simbolo(String nombre, String tipo, int direccion, String atributo, int dimension) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.direccion = direccion;
            this.atributo = atributo;
            this.dimension = dimension;
        }
    }

    public static void procesarArchivo(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                analizarLinea(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private static void analizarLinea(String linea) {
        String[] tokens = linea.split(" ");
        
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("Establecer") && i + 2 < tokens.length) {
                String tipo = tokens[i + 1];
                String identificador = tokens[i + 2];
                int dimension = 0;
                
                if (i + 3 < tokens.length && tokens[i + 3].startsWith("[")) {
                    dimension = contarDimensiones(tokens, i + 3);
                }
                
                if (!PALABRAS_RESERVADAS.contains(identificador)) {
                    insertarSimbolo(identificador, tipo, "Variable", dimension);
                }
            }
        }
    }

    private static int contarDimensiones(String[] tokens, int index) {
        int dimension = 0;
        while (index < tokens.length && tokens[index].startsWith("[")) {
            dimension++;
            index++;
        }
        return dimension;
    }

    public static void insertarSimbolo(String nombre, String tipo, String atributo, int dimension) {
        if (!tablaSimbolos.containsKey(nombre)) {
            tablaSimbolos.put(nombre, new Simbolo(nombre, tipo, direccionMemoria, atributo, dimension));
            direccionMemoria += 4 * (dimension > 0 ? dimension : 1); // Asegurar espacio para arreglos
        }
    }

    public static void mostrarTablaSimbolos() {
        System.out.println("\nTabla de Símbolos:");
        for (String clave : tablaSimbolos.keySet()) {
            Simbolo s = tablaSimbolos.get(clave);
            System.out.println("Nombre: " + s.nombre + ", Tipo: " + s.tipo + ", Dirección: " + s.direccion + ", Atributo: " + s.atributo + ", Dimensión: " + s.dimension);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java ManejoTablasInd <archivo_fuente.txt>");
            return;
        }
        
        procesarArchivo(args[0]);
        mostrarTablaSimbolos();
    }
}
