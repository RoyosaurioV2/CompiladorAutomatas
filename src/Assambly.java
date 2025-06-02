import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Assambly {

    private int _temp;
    private int _pila;

    private Registro _ultimoRegistro;

    public final String DATA = ".data";
    public final String BSS = ".bss";
    public final String TEXT = ".text";

    public final Section dataSection;
    public final Section textSection;

    private final HashMap<String, Constante> _cadenas;

    private final HashMap<String, Variable> _variables;
    private final ArrayList<VariableTemporal> _variablesTemporales;
    private final ArrayList<String> _main;

    private final ArrayList<Simbolo> _resultados;

    public Assambly() {

        _cadenas = new HashMap<>();
        _resultados = new ArrayList<>();
        _variables = new HashMap<>();
        _variablesTemporales = new ArrayList<>();

        dataSection = new Section(DATA);
        textSection = new Section(TEXT);

        dataSection
                .addInstruccion("formato_bool db '%s', 0")
                .addInstruccion("formato_cadena db '%s', 0")
                .addInstruccion("formato_char db '%c', 0")
                .addInstruccion("formato_int db '%d', 0")
                .addInstruccion("formato_float db '%lf', 0")
                .addInstruccion("")
                .addInstruccion("booleano_verdadero db 'Verdadero', 0")
                .addInstruccion("booleano_falso db 'Falso', 0")
                .addInstruccion("")
                .addInstruccion("verdadero db 0xFF")
                .addInstruccion("falso db 0x00")
                .addInstruccion("")
                .addInstruccion(
                        "error_bool db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se ley', 0xA2,' \"%s\" y', 0xA, '    se esperaba Verdadero o Falso.', 0xA, 0")
                .addInstruccion(
                        "error_scanf db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se esperaba un \"%s\" en la lectura.', 0xA, 0")
                .addInstruccion(
                        "error_div db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, no se puede dividir por 0.', 0xA, 0")
                .addInstruccion("")
                .addInstruccion("; Constantes cadena");

        textSection
                .addInstruccion("extern printf, scanf, exit, strcmp, strcpy, strcat, _flushall");

        _main = new ArrayList<>();
    }

    public Assambly addInstruccion(String instruccion) {
        _main.add(instruccion);
        return this;
    }

    public Assambly addInstruccion(String format, Object... args) {
        _main.add(String.format(format, args));
        return this;
    }

    public Constante constante(ExpresionConstante constante) {

        if (constante.getTipoDato().equals(TipoDato.Cadena))
            return cadenaConstante(constante.getValor());

        var valor = constante.getAssamblyValorConstante();

        var resultado = new Constante(toHex(constante.getTipoDato(), valor), constante.getTipoDato());

        pushResultado(resultado);

        return resultado;
    }

    public Constante cadenaConstante(String cadena) {

        if (_cadenas.containsKey(cadena)) {

            var etiqueta = _cadenas.get(cadena);

            pushResultado(etiqueta);
            return etiqueta;
        }

        StringBuilder sb = new StringBuilder();

        var bytes = unescape(cadena).getBytes();

        for (byte b : bytes) {
            sb.append(String.format("0x%02X, ", b));
        }
        sb.append('0');

        var nombre = String.format("str%d", _cadenas.size());
        var etiqueta = new Constante(nombre, TipoDato.Cadena);

        dataSection.addInstruccion("%s db %s", nombre, sb.toString());

        _cadenas.put(cadena, etiqueta);

        pushResultado(etiqueta);

        return etiqueta;
    }

    public Variable variable(ExpresionSimbolo simbolo) {

        var nombreAssambly = String.format("@%d_%s", simbolo.getBloqueId(), simbolo.getNombre());

        Variable variable;

        if (_variables.containsKey(nombreAssambly)) {
            variable = _variables.get(nombreAssambly);
            pushResultado(variable.getEtiquetaValor());
            return variable;
        }

        variable = new Variable(nombreAssambly, simbolo.getTipoDato());

        _variables.put(nombreAssambly, variable);

        pushResultado(variable.getEtiquetaValor());

        return variable;
    }

    public VariableTemporal variableTemporal(TipoDato tipoDato) {

        VariableTemporal temporal;

        var opcional = _variablesTemporales.stream()
                .filter(v -> v.getTipoDato().equals(tipoDato) && v.estaDisponible())
                .findFirst();

        if (opcional.isPresent()) {

            temporal = opcional.get();

            pushResultado(temporal.getEtiquetaValor());

            return temporal;
        }

        var id = _variablesTemporales.size();
        var nombre = String.format("t%d", id);

        temporal = new VariableTemporal(nombre, tipoDato);

        _variablesTemporales.add(temporal);

        pushResultado(temporal.getEtiquetaValor());

        return temporal;
    }

    public Simbolo peekUltimoResultado() {
        return _resultados.getLast();
    }

    public Simbolo popUltimoResultado() {

        var resultado = _resultados.removeLast();

        resultado.liberar();

        return resultado;
    }

    public void pushResultado(Simbolo resultado) {

        resultado.usar();

        _resultados.add(resultado);
    }

    public void copiarCadena(Simbolo destino, Simbolo fuente) {

        if (!destino.getTipoDato().equals(fuente.getTipoDato()))
            return;

        if (!destino.getTipoDato().equals(TipoDato.Cadena))
            return;

        var rArg1 = getRegistroArg1(destino.getTipoDato());
        var rArg2 = getRegistroArg2(fuente.getTipoDato());

        var respaldado1 = usarRegistro(rArg1);
        var respaldado2 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Destino", rArg1, destino);
        addInstruccion("mov %s, %s ; Fuente", rArg2, fuente);
        addInstruccion("call copiar_cadena");

        liberarRegistro(rArg2, respaldado2);
        liberarRegistro(rArg1, respaldado1);
        addInstruccion("");

        pushResultado(destino);

    }

    public void asignarValorConstante(Simbolo destino, long constante) {

        var tipoDato = destino.getTipoDato();

        if (tipoDato.equals(TipoDato.Entero) || tipoDato.equals(TipoDato.Caracter)) {

            addInstruccion("mov %s, %s", destino.dereferenciarConDato(), toHex(tipoDato, constante));

        } else if (tipoDato.equals(TipoDato.Flotante)) {

            var rA = getRegistroA(destino.getTipoDato());

            var respaldado1 = usarRegistro(rA);
            addInstruccion("mov rax, %s", toHex(tipoDato, constante));
            addInstruccion("movq xmm0, rax");
            addInstruccion("movsd %s, xmm0", destino.dereferenciarConDato());
            liberarRegistro(rA, respaldado1);

        } else if (tipoDato.equals(TipoDato.Booleano)) {

            addInstruccion("mov %s, %s", destino.dereferenciarConDato(), toHex(tipoDato, constante));

        }

        pushResultado(destino);

        addInstruccion("");
    }

    public void asignacion(Simbolo destino, Simbolo fuente) {

        var tipoDato = destino.getTipoDato();

        if (!tipoDato.equals(fuente.getTipoDato()))
            return;

        if (tipoDato.equals(TipoDato.Cadena)) {
            copiarCadena(destino, fuente);
            return;
        }

        var rA = getRegistroA(tipoDato);
        boolean respaldado1 = false;

        respaldado1 = usarRegistro(rA);

        addInstruccion("mov %s, %s ; Fuente", rA, fuente.dereferenciar());

        addInstruccion("mov %s, %s ; Destino", destino.dereferenciarConDato(), rA);

        liberarRegistro(rA, respaldado1);

        rA.liberar();

        addInstruccion("");

        pushResultado(destino);

        // mov rax, [@0_f1.valor] ; Fuente
        // mov qword [t3.valor], rax ; Destino

        // mov eax, [@0_e1.valor]
        // mov dword [t1.valor], eax

        // mov al, [@0_c1.valor]
        // mov byte [t1.valor], al

        // mov al, [@0_b1.valor]
        // mov byte [t1.valor], al

    }

    public void concatenarCadenas(Simbolo cadenaIzq, Simbolo cadenaDer) {

        if (!cadenaIzq.getTipoDato().equals(cadenaDer.getTipoDato()))
            return;

        if (!cadenaIzq.getTipoDato().equals(TipoDato.Cadena))
            return;

        variableTemporal(cadenaIzq.getTipoDato());
        var destino = popUltimoResultado();

        var rArg1 = getRegistroArg1(TipoDato.Cadena);
        var rArg2 = getRegistroArg2(TipoDato.Cadena);
        var rArg3 = getRegistroArg3(TipoDato.Cadena);

        var respaldado1 = usarRegistro(rArg1);
        var respaldado2 = usarRegistro(rArg2);
        var respaldado3 = usarRegistro(rArg3);

        addInstruccion("mov %s, %s ; Izq", rArg1, cadenaIzq);
        addInstruccion("mov %s, %s ; Der", rArg2, cadenaDer);
        addInstruccion("mov %s, %s ; Destino", rArg2, destino);
        addInstruccion("call concat_cadena");

        liberarRegistro(rArg1, respaldado1);
        liberarRegistro(rArg2, respaldado2);
        liberarRegistro(rArg3, respaldado3);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq
        // mov rcx, @0_s1.valor ; Izq
        // mov rdx, @0_s2.valor ; Der
        // mov r8, t4.valor ; Destino
        // call concat_cadena

        pushResultado(destino);
    }

    public void suma(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(tipoDato);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call suma_entera");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call suma_flotante");

        variableTemporal(izq.getTipoDato());
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado suma", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);
    }

    public void resta(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(tipoDato);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call resta_entera");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call resta_flotante");

        variableTemporal(izq.getTipoDato());
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado resta", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void multiplicacion(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(tipoDato);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call multiplicacion_entera");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call multiplicacion_flotante");

        variableTemporal(izq.getTipoDato());
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado multiplicación", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);
    }

    public void division(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(tipoDato);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call division_modulo_entera");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call division_flotante");

        variableTemporal(izq.getTipoDato());
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado division", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);
    }

    public void modulo(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        addInstruccion("call division_modulo_entera");

        variableTemporal(izq.getTipoDato());
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado modulo", destino.dereferenciarConDato(), rArg2);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);
    }

    public void igualdad(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(TipoDato.Booleano);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        if (tipoDato.equals(TipoDato.Cadena)) {
            addInstruccion("mov %s, %s ; Izq", rArg1, izq);
            addInstruccion("mov %s, %s ; Der", rArg2, der);
        } else {
            addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
            addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());
        }

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call igualdad_entera");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call igualdad_flotante");
        else if (tipoDato.equals(TipoDato.Cadena))
            addInstruccion("call igualdad_cadena");
        else if (tipoDato.equals(TipoDato.Caracter))
            addInstruccion("call igualdad_caracter");
        else if (tipoDato.equals(TipoDato.Booleano))
            addInstruccion("call igualdad_booleana");

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado igualdad", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void diferencia(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(TipoDato.Booleano);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        if (tipoDato.equals(TipoDato.Cadena)) {
            addInstruccion("mov %s, %s ; Izq", rArg1, izq);
            addInstruccion("mov %s, %s ; Der", rArg2, der);
        } else {
            addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
            addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());
        }

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call diferencia_entera");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call diferencia_flotante");
        else if (tipoDato.equals(TipoDato.Cadena))
            addInstruccion("call diferencia_cadena");
        else if (tipoDato.equals(TipoDato.Caracter))
            addInstruccion("call diferencia_caracter");
        else if (tipoDato.equals(TipoDato.Booleano))
            addInstruccion("call diferencia_booleana");

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado diferencia", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void mayor(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(TipoDato.Booleano);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call mayor_entero");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call mayor_flotante");
        else if (tipoDato.equals(TipoDato.Caracter))
            addInstruccion("call mayor_caracter");

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado mayor", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void menor(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(TipoDato.Booleano);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call menor_entero");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call menor_flotante");
        else if (tipoDato.equals(TipoDato.Caracter))
            addInstruccion("call menor_caracter");

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado menor", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void mayorIgual(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(TipoDato.Booleano);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call mayor_igual_entero");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call mayor_igual_flotante");
        else if (tipoDato.equals(TipoDato.Caracter))
            addInstruccion("call mayor_igual_caracter");

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado mayor igual", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void menorIgual(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        var tipoDato = izq.getTipoDato();
        var rA = getRegistroA(TipoDato.Booleano);
        var rArg1 = getRegistroArg1(tipoDato);
        var rArg2 = getRegistroArg2(tipoDato);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call menor_igual_entero");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call menor_igual_flotante");
        else if (tipoDato.equals(TipoDato.Caracter))
            addInstruccion("call menor_igual_caracter");

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado menor igual", destino.dereferenciarConDato(), rA);

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void and(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        if (!izq.getTipoDato().equals(TipoDato.Booleano))
            return;

        var rArg1 = getRegistroArg1(TipoDato.Booleano);
        var rArg2 = getRegistroArg2(TipoDato.Booleano);

        var respaldado1 = usarRegistro(rArg1);
        var respaldado2 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());
        addInstruccion("and %s, %s", rArg1, rArg2);

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado and", destino.dereferenciarConDato(), rArg1);

        liberarRegistro(rArg2, respaldado2);
        liberarRegistro(rArg1, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void or(Simbolo izq, Simbolo der) {

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return;

        if (!izq.getTipoDato().equals(TipoDato.Booleano))
            return;

        var rArg1 = getRegistroArg1(TipoDato.Booleano);
        var rArg2 = getRegistroArg2(TipoDato.Booleano);

        var respaldado1 = usarRegistro(rArg1);
        var respaldado2 = usarRegistro(rArg2);

        addInstruccion("mov %s, %s ; Izq", rArg1, izq.dereferenciar());
        addInstruccion("mov %s, %s ; Der", rArg2, der.dereferenciar());
        addInstruccion("or %s, %s", rArg1, rArg2);

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado or", destino.dereferenciarConDato(), rArg1);

        liberarRegistro(rArg2, respaldado2);
        liberarRegistro(rArg1, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _der
        popUltimoResultado(); // Descartar _izq

        pushResultado(destino);

    }

    public void leer(Variable variable) {

        var rA = getRegistroA(TipoDato.Cadena);
        var rArg1 = getRegistroArg1(TipoDato.Cadena);
        var rArg2 = getRegistroArg2(TipoDato.Cadena);

        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);
        var respaldado3 = usarRegistro(rArg2);

        addInstruccion("; Leer %s", variable.getTipoDato());
        addInstruccion("call _flushall");
        addInstruccion("mov %s, %s", rArg1, variable.getEtiquetaFormato());
        addInstruccion("mov %s, %s", rArg2, variable.getEtiquetaValor());
        addInstruccion("sub rsp, 32");
        addInstruccion("xor rax, rax");
        addInstruccion("call scanf");
        addInstruccion("add rsp, 32");
        addInstruccion("mov %s, %s", rArg1, rA);
        addInstruccion("mov %s, %s", rArg2, variable.getEtiquetaTipoDato());
        addInstruccion("call check_scanf");

        if (variable.getTipoDato().equals(TipoDato.Booleano)) {

            var rArg3 = getRegistroArg3(TipoDato.Cadena);
            var rAL = getRegistroA(TipoDato.Booleano);
            var respaldado4 = usarRegistro(rArg3);

            addInstruccion("");
            addInstruccion("mov %s, %s", rArg3, variable.getEtiquetaValor());
            addInstruccion("call str_to_bool");
            addInstruccion("mov %s, %s", variable.getEtiquetaValor().dereferenciarConDato(), rAL);

            liberarRegistro(rArg3, respaldado4);
        }

        liberarRegistro(rArg2, respaldado3);
        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);
        addInstruccion("");

    }

    public void escribir(Simbolo operando) {

        var tipoDato = operando.getTipoDato();

        var rArg1 = getRegistroArg1(tipoDato);
        var respaldado1 = usarRegistro(rArg1);

        if (tipoDato.equals(TipoDato.Cadena))
            addInstruccion("mov %s, %s", rArg1, operando);
        else
            addInstruccion("mov %s, %s", rArg1, operando.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call imprimir_entero");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call imprimir_flotante");
        else if (tipoDato.equals(TipoDato.Caracter))
            addInstruccion("call imprimir_caracter");
        else if (tipoDato.equals(TipoDato.Cadena))
            addInstruccion("call imprimir_cadena");
        else if (tipoDato.equals(TipoDato.Booleano))
            addInstruccion("call imprimir_bool");

        addInstruccion("");

        liberarRegistro(rArg1, respaldado1);

    }

    public void negativo(Simbolo operando) {

        var tipoDato = operando.getTipoDato();

        var rA = getRegistroA(tipoDato);
        var rArg1 = getRegistroArg1(tipoDato);
        var respaldado1 = usarRegistro(rA);
        var respaldado2 = usarRegistro(rArg1);

        addInstruccion("mov %s, %s; Operando", rArg1, operando.dereferenciar());

        if (tipoDato.equals(TipoDato.Entero))
            addInstruccion("call entero_negativo");
        else if (tipoDato.equals(TipoDato.Flotante))
            addInstruccion("call flotante_negativo");

        variableTemporal(tipoDato);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado negativo", destino.dereferenciarConDato(), rA);

        addInstruccion("");

        liberarRegistro(rArg1, respaldado2);
        liberarRegistro(rA, respaldado1);

        popUltimoResultado(); // Descartar _operando

        pushResultado(destino);

    }

    public void not(Simbolo operando) {

        if (!operando.getTipoDato().equals(TipoDato.Booleano))
            return;

        var rArg1 = getRegistroArg1(TipoDato.Booleano);

        var respaldado1 = usarRegistro(rArg1);

        addInstruccion("mov %s, %s ; Operando", rArg1, operando.dereferenciar());
        addInstruccion("not %s", rArg1);

        variableTemporal(TipoDato.Booleano);
        var destino = popUltimoResultado();

        addInstruccion("mov %s, %s ; Resultado not", destino.dereferenciarConDato(), rArg1);

        liberarRegistro(rArg1, respaldado1);
        addInstruccion("");

        popUltimoResultado(); // Descartar _operando

        pushResultado(destino);
    }

    public void respaldarRegistro(Registro r) {

        r = r.getUltimo();

        addInstruccion("push %s ; Respaldar %s", r, r);
        r.usar();
    }

    public void recuperarRegistro(Registro r) {

        r = r.getUltimo();

        addInstruccion("pop %s ; Restaurar %s", r, r);
        r.liberar();
    }

    public boolean usarRegistro(Registro r) {

        r = r.getUltimo();

        if (!r.estaDisponible()) {
            addInstruccion("push %s ; Respaldar %s", r, r);
            return true;
        }

        r.usar();
        return false;
    }

    public void liberarRegistro(Registro r, boolean respaldado) {

        r = r.getUltimo();

        if (respaldado) {
            addInstruccion("pop %s ; Restaurar %s", r, r);
        }

        r.liberar();
    }

    public Registro getRegistroA(TipoDato tipoDato) {

        if (tipoDato.equals(TipoDato.Entero)) {
            return Registro.eax.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Flotante)) {
            return Registro.rax.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Caracter)) {
            return Registro.al.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Booleano)) {
            return Registro.al.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Cadena)) {
            return Registro.rax.conTipoDato(tipoDato);
        }

        return Registro.rax.conTipoDato(tipoDato);
    }

    public Registro getRegistroArg1(TipoDato tipoDato) {

        if (tipoDato.equals(TipoDato.Entero)) {
            return Registro.ecx.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Flotante)) {
            return Registro.rcx.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Caracter)) {
            return Registro.cl.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Booleano)) {
            return Registro.cl.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Cadena)) {
            return Registro.rcx.conTipoDato(tipoDato);
        }

        return Registro.rcx.conTipoDato(tipoDato);
    }

    public Registro getRegistroArg2(TipoDato tipoDato) {

        if (tipoDato.equals(TipoDato.Entero)) {
            return Registro.edx.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Flotante)) {
            return Registro.rdx.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Caracter)) {
            return Registro.dl.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Booleano)) {
            return Registro.dl.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Cadena)) {
            return Registro.rdx.conTipoDato(tipoDato);
        }

        return Registro.rdx.conTipoDato(tipoDato);
    }

    public Registro getRegistroArg3(TipoDato tipoDato) {

        if (tipoDato.equals(TipoDato.Entero)) {
            return Registro.r8d.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Flotante)) {
            return Registro.r8.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Caracter)) {
            return Registro.r8b.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Booleano)) {
            return Registro.r8b.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Cadena)) {
            return Registro.r8.conTipoDato(tipoDato);
        }

        return Registro.r8.conTipoDato(tipoDato);
    }

    public Registro getRegistroArg4(TipoDato tipoDato) {

        if (tipoDato.equals(TipoDato.Entero)) {
            return Registro.r9d.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Flotante)) {
            return Registro.r9.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Caracter)) {
            return Registro.r9b.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Booleano)) {
            return Registro.r9b.conTipoDato(tipoDato);
        } else if (tipoDato.equals(TipoDato.Cadena)) {
            return Registro.r9.conTipoDato(tipoDato);
        }

        return Registro.r9.conTipoDato(tipoDato);
    }

    public static String toHex(TipoDato tipoDato, long valor) {
        return String.format("0x%0" + tipoDato.getBytes() * 2 + "X", valor);
    }

    public static String unescape(String texto) {
        StringBuilder builder = new StringBuilder();
        boolean escape = false;
        for (char c : texto.toCharArray()) {
            if (escape) {
                switch (c) {
                    case 'n':
                        builder.append('\n');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case '\\':
                        builder.append('\\');
                        break;
                    case '\'':
                        builder.append('\'');
                        break;
                    case '\"':
                        builder.append('\"');
                        break;
                    default:
                        builder.append(c);
                        break;
                }
                escape = false;
            } else {
                if (c == '\\') {
                    escape = true;
                } else {
                    builder.append(c);
                }
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(dataSection);
        // sb.append(bssSection)
        sb.append("\n; Variables:\n");
        _variables.values().forEach(v -> sb.append(v.getDeclaracion()).append('\n'));

        sb.append("\n; Variables temporales:\n");
        _variablesTemporales.forEach(v -> sb.append(v.getDeclaracion()).append('\n'));

        sb.append(textSection);

        sb.append("\nmain:   ; Inicio\n");

        sb.append("    push rbp\n")
                .append("    mov rbp, rsp\n\n");

        for (var exp : _main) {
            sb.append("    ").append(exp).append('\n');
        }

        sb.append("    mov rsp, rbp\n")
                .append("    pop rbp\n")
                .append('\n')
                .append("    xor rcx, rcx\n")
                .append("    call exit\n")
                .append('\n')
                .append("    ; Fin\n")
                .append("    xor rax, rax\n")
                .append("    ret\n\n");

        sb.append("success:").append('\n')
                .append("    mov rax, 0").append('\n')
                .append("    ret\n\n");

        sb.append("fail:").append('\n')
                .append("    mov rax, 1").append('\n')
                .append("    ret\n\n");

        sb.append("true:").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    mov al, [verdadero]").append('\n')
                .append("    ret\n\n");

        sb.append("false:").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    mov al, [falso]").append('\n')
                .append("    ret\n\n");

        sb.append("error:").append('\n')
                .append("    mov rcx, 1").append('\n')
                .append("    call exit\n\n");

        sb.append("imprimir:").append('\n')
                .append("    ; rcx: Formato").append('\n')
                .append("    ; rdx: Valor").append('\n')
                .append("    sub rsp, 32").append('\n')
                .append("    call printf").append('\n')
                .append("    add rsp, 32").append('\n')
                .append("    ret\n\n");

        sb.append("check_scanf:").append('\n')
                .append("    ; rcx: Retorno de scanf").append('\n')
                .append("    ; rdx: Tipo de dato").append('\n')
                .append("    test rcx, rcx").append('\n')
                .append("    jnz success").append('\n')
                .append("    mov rcx, error_scanf").append('\n')
                .append("    mov rdx, rdx").append('\n')
                .append("    call imprimir").append('\n')
                .append("    mov rax, 1").append('\n')
                .append("    jmp error\n\n");

        sb.append("imprimir_cadena:").append('\n')
                .append("    ; rcx: Cadena a imprimir").append('\n')
                .append("    mov rdx, rcx").append('\n')
                .append("    mov rcx, formato_cadena").append('\n')
                .append("    call imprimir").append('\n')
                .append("    ret\n\n");

        sb.append("imprimir_bool:").append('\n')
                .append("    ; cl: booleano a imprimir").append('\n')
                .append("    cmp cl, 0").append('\n')
                .append("    test cl, cl").append('\n')
                .append("    jz .false").append('\n')
                .append("    mov rcx, booleano_verdadero").append('\n')
                .append("    call imprimir_cadena").append('\n')
                .append("    ret").append('\n')
                .append("    .false:").append('\n')
                .append("        mov rcx, booleano_falso").append('\n')
                .append("        call imprimir_cadena").append('\n')
                .append("        ret\n\n");

        sb.append("imprimir_caracter:").append('\n')
                .append("    ; cl: Caracter a imprimir").append('\n')
                .append("    xor rdx, rdx").append('\n')
                .append("    mov dl, cl").append('\n')
                .append("    mov rcx, formato_char").append('\n')
                .append("    call imprimir").append('\n')
                .append("    ret\n\n");

        sb.append("imprimir_entero:").append('\n')
                .append("    ; ecx: Entero a imprimir").append('\n')
                .append("    mov edx, ecx").append('\n')
                .append("    mov rcx, formato_int").append('\n')
                .append("    call imprimir").append('\n')
                .append("    ret\n\n");

        sb.append("imprimir_flotante:").append('\n')
                .append("    ; rcx: Flotante a imprimir").append('\n')
                .append("    mov rdx, rcx").append('\n')
                .append("    mov rcx, formato_float").append('\n')
                .append("    call imprimir").append('\n')
                .append("    ret\n\n");

        sb.append("entero_negativo:").append('\n')
                .append("    ; ecx: Operando").append('\n')
                .append("    ; eax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    mov eax, ecx ; Guardar operando en eax").append('\n')
                .append("    neg eax").append('\n')
                .append("    ret\n\n");

        sb.append("suma_entera:").append('\n')
                .append("    ; ecx: Entero izq").append('\n')
                .append("    ; edx: Entero der").append('\n')
                .append("    ; eax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    mov eax, ecx ; Guardar operacion izquierda en eax").append('\n')
                .append("    mov ecx, edx ; Guardar operacion derecha en ecx").append('\n')
                .append("    add eax, ecx    ").append('\n')
                .append("    ret\n\n");

        sb.append("resta_entera:").append('\n')
                .append("    ; ecx: Entero izq").append('\n')
                .append("    ; edx: Entero der").append('\n')
                .append("    ; eax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    mov eax, ecx ; Guardar operacion izquierda en eax").append('\n')
                .append("    mov ecx, edx ; Guardar operacion derecha en ecx").append('\n')
                .append("    sub eax, ecx").append('\n')
                .append("    ret\n\n");

        sb.append("multiplicacion_entera:").append('\n')
                .append("    ; ecx: Entero izq").append('\n')
                .append("    ; edx: Entero der").append('\n')
                .append("    ; eax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    mov eax, ecx ; Guardar operacion izquierda en eax").append('\n')
                .append("    mov ecx, edx ; Guardar operacion derecha en ecx").append('\n')
                .append("    imul eax, ecx").append('\n')
                .append("    ret\n\n");

        sb.append("division_modulo_entera:").append('\n')
                .append("    ; ecx: Entero izq").append('\n')
                .append("    ; edx: Entero der").append('\n')
                .append("    ; eax: Cociente").append('\n')
                .append("    ; edx: Residuo").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    mov eax, ecx ; Guardar operacion izquierda en eax").append('\n')
                .append("    mov ecx, edx ; Guardar operacion derecha en ecx").append('\n')
                .append("    cdq").append('\n')
                .append("    ; Hacer checkeo si ecx es 0").append('\n')
                .append("    test ecx, ecx").append('\n')
                .append("    jz .error ").append('\n')
                .append("    idiv ecx").append('\n')
                .append("    ret").append('\n')
                .append("    .error:").append('\n')
                .append("        mov rcx, error_div").append('\n')
                .append("        call imprimir_cadena").append('\n')
                .append("        jmp error").append('\n')
                .append("        ret\n\n");

        sb.append("igualdad_entera:").append('\n')
                .append("    ; ecx: Entero izq").append('\n')
                .append("    ; edx: Entero der").append('\n')
                .append("    ; al:  Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    cmp ecx, edx").append('\n')
                .append("    je true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("diferencia_entera:").append('\n')
                .append("    ; ecx: Entero izq").append('\n')
                .append("    ; edx: Entero der").append('\n')
                .append("    ; al:  Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    cmp ecx, edx").append('\n')
                .append("    jne true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("mayor_entero:").append('\n')
                .append("    ; ecx: Entero izq").append('\n')
                .append("    ; edx: Entero der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    cmp ecx, edx").append('\n')
                .append("    jg true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("mayor_igual_entero:").append('\n')
                .append("    ; ecx <- Entero izq").append('\n')
                .append("    ; edx <- Entero der").append('\n')
                .append("    ; al <- Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    cmp ecx, edx").append('\n')
                .append("    jge true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("menor_entero:").append('\n')
                .append("    ; ecx <- Entero izq").append('\n')
                .append("    ; edx <- Entero der").append('\n')
                .append("    ; al <- Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    cmp ecx, edx").append('\n')
                .append("    jl true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("menor_igual_entero:").append('\n')
                .append("    ; ecx <- Entero izq").append('\n')
                .append("    ; edx <- Entero der").append('\n')
                .append("    ; al <- Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    cmp ecx, edx").append('\n')
                .append("    jle true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("flotante_negativo:").append('\n')
                .append("    ; rcx: Operando").append('\n')
                .append("    ; rax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    mov rax, 0x8000000000000000").append('\n')
                .append("    movq xmm0, rcx ; Guardar operando en xmm0").append('\n')
                .append("    movq xmm1, rax ").append('\n')
                .append("    xorpd xmm0, xmm1").append('\n')
                .append("    movq rax, xmm0").append('\n')
                .append("    ret\n\n");

        sb.append("suma_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; rax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    addsd xmm0, xmm1").append('\n')
                .append("    movq rax, xmm0").append('\n')
                .append("    ret\n\n");

        sb.append("resta_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; rax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    subsd xmm0, xmm1").append('\n')
                .append("    movq rax, xmm0").append('\n')
                .append("    ret\n\n");

        sb.append("multiplicacion_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; rax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    mulsd xmm0, xmm1").append('\n')
                .append("    movq rax, xmm0").append('\n')
                .append("    ret\n\n");

        sb.append("division_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; rax: Resultado").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    test rdx, rdx").append('\n')
                .append("    jz .error").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    ; Hacer checkeo si xmm1 es 0").append('\n')
                .append("    ; pxor xmm2, xmm2").append('\n')
                .append("    ; ucomisd xmm1, xmm2").append('\n')
                .append("    divsd xmm0, xmm1").append('\n')
                .append("    movq rax, xmm0").append('\n')
                .append("    ret        ").append('\n')
                .append("    .error:").append('\n')
                .append("        mov rcx, error_div").append('\n')
                .append("        call imprimir_cadena").append('\n')
                .append("        jmp error").append('\n')
                .append("    ret\n\n");

        sb.append("igualdad_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    ucomisd xmm0, xmm1").append('\n')
                .append("    je true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("diferencia_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; al:  Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    ucomisd xmm0, xmm1").append('\n')
                .append("    jne true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("mayor_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; al:  Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    ucomisd xmm0, xmm1").append('\n')
                .append("    ja true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("mayor_igual_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; al:  Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    ucomisd xmm0, xmm1").append('\n')
                .append("    jae true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("menor_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; al:  Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    ucomisd xmm0, xmm1").append('\n')
                .append("    jb true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("menor_igual_flotante:").append('\n')
                .append("    ; rcx: Flotante izq").append('\n')
                .append("    ; rdx: Flotante der").append('\n')
                .append("    ; al:  Resultado booleano").append('\n')
                .append("    xor rax, rax").append('\n')
                .append("    movq xmm0, rcx ; Guardar operacion izquierda en xmm0").append('\n')
                .append("    movq xmm1, rdx ; Guardar operacion derecha en xmm1 ").append('\n')
                .append("    ucomisd xmm0, xmm1").append('\n')
                .append("    jbe true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("concat_cadena:").append('\n')
                .append("    ; rcx: izq").append('\n')
                .append("    ; rdx: der").append('\n')
                .append("    ; r8:  Destino").append('\n')
                .append("    push rdx").append('\n')
                .append("    mov rdx, rcx").append('\n')
                .append("    mov rcx, r8").append('\n')
                .append("    call copiar_cadena").append('\n')
                .append("    mov rcx, r8").append('\n')
                .append("    pop rdx").append('\n')
                .append("    sub rsp, 32").append('\n')
                .append("    call strcat").append('\n')
                .append("    add rsp, 32").append('\n')
                .append("    ret\n\n");

        sb.append("copiar_cadena:").append('\n')
                .append("    ; rcx: Destino").append('\n')
                .append("    ; rdx: Fuente").append('\n')
                .append("    sub rsp, 32").append('\n')
                .append("    call strcpy").append('\n')
                .append("    add rsp, 32").append('\n')
                .append("    ret\n\n");

        sb.append("str_to_bool:").append('\n')
                .append("    ; r8: Cadena a convertir").append('\n')
                .append("    ; rcx: Booleano candena").append('\n')
                .append("    mov rcx, r8").append('\n')
                .append("    mov rdx, booleano_verdadero").append('\n')
                .append("    sub rsp, 32").append('\n')
                .append("    call strcmp").append('\n')
                .append("    add rsp, 32").append('\n')
                .append("    cmp rax, 0").append('\n')
                .append("    je true").append('\n')
                .append("    mov rcx, r8").append('\n')
                .append("    mov rdx, booleano_falso").append('\n')
                .append("    sub rsp, 32").append('\n')
                .append("    call strcmp").append('\n')
                .append("    add rsp, 32").append('\n')
                .append("    cmp rax, 0").append('\n')
                .append("    je false").append('\n')
                .append("    mov rcx, error_bool").append('\n')
                .append("    mov rdx, r8").append('\n')
                .append("    call imprimir").append('\n')
                .append("    jmp error\n\n");

        sb.append("igualdad_cadena:").append('\n')
                .append("    ; rcx: Cadena izq").append('\n')
                .append("    ; rdx: Cadena der").append('\n')
                .append("    ; al:  Resultado").append('\n')
                .append("    mov rdi, rcx").append('\n')
                .append("    mov rsi, rdx").append('\n')
                .append("    call strcmp").append('\n')
                .append("    cmp rax, 0").append('\n')
                .append("    je true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("diferencia_cadena:").append('\n')
                .append("    ; rcx: Cadena izq").append('\n')
                .append("    ; rdx: Cadena der").append('\n')
                .append("    ; al:  Resultado").append('\n')
                .append("    mov rdi, rcx").append('\n')
                .append("    mov rsi, rdx").append('\n')
                .append("    call strcmp").append('\n')
                .append("    cmp rax, 0").append('\n')
                .append("    jne true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("igualdad_caracter:").append('\n')
                .append("    ; cl: Caracter izq").append('\n')
                .append("    ; dl: Caracter der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    cmp cl, dl").append('\n')
                .append("    je true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("diferencia_caracter:").append('\n')
                .append("    ; cl: Caracter izq").append('\n')
                .append("    ; dl: Caracter der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    cmp cl, dl").append('\n')
                .append("    jne true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("mayor_caracter:").append('\n')
                .append("    ; cl: Caracter izq").append('\n')
                .append("    ; dl: Caracter der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    cmp cl, dl").append('\n')
                .append("    jg true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("mayor_igual_caracter:").append('\n')
                .append("    ; cl: Caracter izq").append('\n')
                .append("    ; dl: Caracter der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    cmp cl, dl").append('\n')
                .append("    jge true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("menor_caracter:").append('\n')
                .append("    ; cl: Caracter izq").append('\n')
                .append("    ; dl: Caracter der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    cmp cl, dl").append('\n')
                .append("    jl true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("menor_igual_caracter:").append('\n')
                .append("    ; cl: Caracter izq").append('\n')
                .append("    ; dl: Caracter der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    cmp cl, dl").append('\n')
                .append("    jle true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("igualdad_booleana:").append('\n')
                .append("    ; cl: Caracter izq").append('\n')
                .append("    ; dl: Caracter der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    cmp cl, dl").append('\n')
                .append("    je true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        sb.append("diferencia_booleana:").append('\n')
                .append("    ; cl: Caracter izq").append('\n')
                .append("    ; dl: Caracter der").append('\n')
                .append("    ; al: Resultado booleano").append('\n')
                .append("    cmp cl, dl").append('\n')
                .append("    jne true").append('\n')
                .append("    jmp false").append('\n')
                .append("    ret\n\n");

        return sb.toString();
    }

}
