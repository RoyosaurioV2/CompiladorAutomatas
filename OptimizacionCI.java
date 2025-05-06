import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class OptimizacionCI {
    private static final Set<String> OPERADORES = Set.of("+", "-", "*", "/", "<", ">", "<=", ">=", "==", "!=", "&&", "||");
    private static final Set<String> SALTOS = Set.of("goto", "if_false", "if_true", "IrSino");

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Uso: java OptimizacionCI archivoIntermedio.txt");
            return;
        }

        List<String> codigo = limpiarComentarios(Files.readAllLines(new File(args[0]).toPath()));
        List<String> optimizado = optimizarCodigo(codigo);

        try (PrintWriter writer = new PrintWriter("intermedioOptimizado.txt")) {
            for (String linea : optimizado) {
                writer.println(linea);
            }
        }

        System.out.println("Optimización completada. Resultado en intermedioOptimizado.txt");
    }

    private static List<String> limpiarComentarios(List<String> lineas) {
        List<String> limpio = new ArrayList<>();
        for (String linea : lineas) {
            // Eliminar líneas que son puramente comentarios
            if (!linea.trim().startsWith("#") && !linea.trim().isEmpty()) {
                // Eliminar comentarios al final de la línea
                String[] partes = linea.split("#", 2);
                limpio.add(partes[0].trim());
            }
        }
        return limpio;
    }

    private static List<String> optimizarCodigo(List<String> codigo) {
        List<String> optimizado = new ArrayList<>();
        Map<String, String> constantes = new HashMap<>();
        Map<String, String> expresiones = new HashMap<>();
        Set<String> temporalesUsados = new HashSet<>();

        // Primera pasada: identificar temporales usados
        for (String linea : codigo) {
            String[] tokens = linea.trim().split("\\s+");
            for (String token : tokens) {
                if (token.matches("t\\d+") || token.matches("@[\\w]+")) {
                    temporalesUsados.add(token);
                }
            }
        }

        // Segunda pasada: aplicar optimizaciones
        for (String linea : codigo) {
            String lineaOriginal = linea.trim();
            if (lineaOriginal.isEmpty()) {
                continue;
            }

            String[] tokens = lineaOriginal.split("\\s+");
            String operacion = tokens[0];

            // Manejo de etiquetas
            if (lineaOriginal.endsWith(":")) {
                optimizado.add(lineaOriginal);
                expresiones.clear();
                continue;
            }

            // Optimización 1: Folding constante
            if (OPERADORES.contains(operacion)) {
                String arg1 = constantes.getOrDefault(tokens[1], tokens[1]);
                String arg2 = constantes.getOrDefault(tokens[2], tokens[2]);
                String destino = tokens[3];

                if (esConstante(arg1) && esConstante(arg2)) {
                    String resultado = evaluarOperacion(operacion, arg1, arg2);
                    optimizado.add("= " + resultado + " " + destino);
                    constantes.put(destino, resultado);
                    continue;
                }
            }

            // Optimización 2: Eliminación de asignaciones redundantes
            if (operacion.equals("=") && tokens.length == 3) {
                String valor = tokens[1];
                String destino = tokens[2];

                if (destino.matches("t\\d+") && !temporalesUsados.contains(destino)) {
                    continue;
                }

                if (esConstante(valor)) {
                    constantes.put(destino, valor);
                } else if (constantes.containsKey(valor)) {
                    optimizado.add("= " + constantes.get(valor) + " " + destino);
                    continue;
                }
            }

            // Optimización 3: Eliminación de expresiones redundantes
            if (OPERADORES.contains(operacion)) {
                String arg1 = tokens[1];
                String arg2 = tokens[2];
                String destino = tokens[3];
                String claveExpresion = operacion + " " + arg1 + " " + arg2;

                if (expresiones.containsKey(claveExpresion)) {
                    optimizado.add("= " + expresiones.get(claveExpresion) + " " + destino);
                    continue;
                } else {
                    expresiones.put(claveExpresion, destino);
                }
            }

            optimizado.add(lineaOriginal);
        }

        return optimizado;
    }

    private static boolean esConstante(String token) {
        return token.matches("-?\\d+") || token.matches("\".*\"") ||
                token.equals("true") || token.equals("false");
    }

    private static String evaluarOperacion(String operador, String arg1, String arg2) {
        try {
            if (arg1.matches("-?\\d+") && arg2.matches("-?\\d+")) {
                int num1 = Integer.parseInt(arg1);
                int num2 = Integer.parseInt(arg2);

                switch (operador) {
                    case "+": return String.valueOf(num1 + num2);
                    case "-": return String.valueOf(num1 - num2);
                    case "*": return String.valueOf(num1 * num2);
                    case "/": return num2 != 0 ? String.valueOf(num1 / num2) : "0";
                    case "<": return num1 < num2 ? "true" : "false";
                    case ">": return num1 > num2 ? "true" : "false";
                    case "<=": return num1 <= num2 ? "true" : "false";
                    case ">=": return num1 >= num2 ? "true" : "false";
                    case "==": return num1 == num2 ? "true" : "false";
                    case "!=": return num1 != num2 ? "true" : "false";
                }
            }

            if ((arg1.equals("true") || arg1.equals("false")) &&
                    (arg2.equals("true") || arg2.equals("false"))) {
                boolean bool1 = arg1.equals("true");
                boolean bool2 = arg2.equals("true");

                switch (operador) {
                    case "&&": return bool1 && bool2 ? "true" : "false";
                    case "||": return bool1 || bool2 ? "true" : "false";
                    case "==": return bool1 == bool2 ? "true" : "false";
                    case "!=": return bool1 != bool2 ? "true" : "false";
                }
            }

            return arg1;
        } catch (Exception e) {
            return arg1;
        }
    }
}