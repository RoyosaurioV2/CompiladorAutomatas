
import java.io.*;
import java.util.*;

public class ECV2 {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ECV2 [-arb|-pil] <archivo>");
            return;
        }
        
        String modo = args[0];
        String archivo = args[1];
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            StringBuilder contenido = new StringBuilder();
            
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
            br.close();
            
            if ("-arb".equals(modo)) {
                generarArbol(contenido.toString());
            } else if ("-pil".equals(modo)) {
                ejecutarConPila(contenido.toString());
            } else {
                System.out.println("Modo desconocido: " + modo);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
    
    private static void generarArbol(String codigo) {
        System.out.println("[Árbol de Derivación Semántico]");
        List<String> tokens = analizarTokens(codigo); //divide el codigo
        imprimirArbol(tokens, "", true); //muestra visual
    }
    
    private static void imprimirArbol(List<String> tokens, String prefijo, boolean esUltimo) {
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i); // Obtiene el token actual de la lista
            boolean ultimo = (i == tokens.size() - 1); //Verifica si el toekn actual es el ultimo del nivel si i es igual a final de list sera true de lo contrario false
            System.out.println(prefijo + (ultimo ? "└── " : "├── ") + token);
            //muestra el nivel axtual del arbol
            if (!ultimo) { //Sugue generando el arbol
                imprimirArbol(Collections.singletonList("Hijo de " + token), prefijo + (ultimo ? "    " : "│   "), false);
            }
        }
    }
    
    private static void ejecutarConPila(String codigo) {
        System.out.println("[Ejecución con Pila Semántica]"); //encabezadp
        Stack<String> pila = new Stack<>(); //crea la pila, almacena tokens, tipo string
        
        List<String> tokens = analizarTokens(codigo); //Divide el codigo en tokens
        for (String token : tokens) { //itera sobre los tokens
            if (esOperador(token)) { //Verifica si es un operador
                if (pila.size() >= 2) { //Verfica almenos dos elementos de la pila
                    String b = pila.pop(); //Extrae el ultimo elemento
                    String a = pila.pop(); //Extrae el elemento anterior
                    //combina los operandos y operadores y los vuelve a apilar
                    pila.push("(" + a + " " + token + " " + b + ")");
                }
            } else { //si el token es un operador, lo trata como un operando. lo apila de nuevo
                pila.push(token);
            }
        }
        
        System.out.println("Pila final: " + pila);
    }
    
    private static boolean esOperador(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    } //si el token es un simbolo de operacion retorna true si es un num o variable retorna false
    
    private static List<String> analizarTokens(String codigo) {
        List<String> tokens = new ArrayList<>(); //Lista vacia para alamcenar los tokens encontrandos
        StringTokenizer tokenizer = new StringTokenizer(codigo, " \t\n\r.,;:()[]{}+=-*/<>!|&", true);
        //Divide la cadena de codigo en tokens
        while (tokenizer.hasMoreTokens()) { //Bucle que recorre los tokens mienstrs hay mas disponibles
            String token = tokenizer.nextToken().trim();
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }
}
