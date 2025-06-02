import java.util.ArrayList;

public final class ECError implements Comparable<ECError> {

    public enum Tipo {

        Lexico("Error léxico"),
        Sintactico("Error sintáctico"),
        Semantico("Error semántico");

        private final String nombre;

        private Tipo(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }

    }

    private Tipo _tipo;
    private int _linea;
    private int _columna;

    private String _razonMsg;
    private String _solucionMsg;

    private ArrayList<String> _razones;
    private ArrayList<String> _soluciones;

    private ECError() {
        _razones = new ArrayList<>();
        _soluciones = new ArrayList<>();
    }

    public ECError(Tipo tipo, int linea, int columna) {
        this(tipo, linea, columna, "", "");
    }

    public ECError(Tipo tipo, int linea, int columna, String razonMsg, String solucionMsg) {

        _tipo = tipo;
        _linea = linea;
        _columna = columna;

        _razonMsg = razonMsg;
        _solucionMsg = solucionMsg;

        _razones = new ArrayList<>();
        _soluciones = new ArrayList<>();

    }

    public ECError(Tipo tipo, int linea, int columna, String razonMsg, String razon, String solucionMsg,
            String solucion) {
        this(tipo, linea, columna);

        if (razonMsg != null && !razonMsg.isBlank())
            _razonMsg = razonMsg;

        if (solucionMsg != null && !solucionMsg.isBlank())
            _solucionMsg = solucionMsg;

        if (razon != null && !razon.isBlank())
            _razones.add(razon);

        if (solucion != null && !solucion.isBlank())
            _soluciones.add(solucion);
    }

    public ECError addRazon(String razon) {

        if (razon == null || razon.isBlank())
            return this;

        _razones.add(razon);

        return this;
    }

    public ECError addRazon(String format, Object... args) {

        if (format == null || format.isBlank())
            return this;

        _razones.add(String.format(format, args));

        return this;
    }

    public ECError addSolucion(String solucion) {

        if (solucion == null || solucion.isBlank())
            return this;

        _soluciones.add(solucion);

        return this;
    }

    public ECError addSolucion(String format, Object... args) {

        if (format == null || format.isBlank())
            return this;

        _soluciones.add(String.format(format, args));

        return this;
    }

    @Override
    public int compareTo(ECError o) {
        
        int compare = _tipo.compareTo(o._tipo);

        if (compare != 0) return compare;

        compare = Integer.compare(_linea, o._linea);

        if (compare != 0) return compare;

        return Integer.compare(_columna, o._columna);
    }

    public static ECError fromParseException(ParseException ex) {

        ArrayList<String> expected = new ArrayList<>();

        int maxSize = 0;

        for (int i = 0; i < ex.expectedTokenSequences.length; i++) {
            if (maxSize < ex.expectedTokenSequences[i].length) {
                maxSize = ex.expectedTokenSequences[i].length;
            }
            for (int j = 0; j < ex.expectedTokenSequences[i].length; j++) {
                expected.add(ex.tokenImage[ex.expectedTokenSequences[i][j]]);
            }

        }

        Token currentToken = ex.currentToken;
        Token tokenError = currentToken.next;

        // Tokens encontrados
        ArrayList<Token> tokens = new ArrayList<Token>();
        tokens.add(tokenError);

        for (Token t = tokenError; t.next != null; t = t.next)
            tokens.add(t);

        StringBuilder sb = new StringBuilder();
        ECError error = new ECError();
        error._tipo = ECError.Tipo.Sintactico;
        error._linea = tokenError.beginLine;
        error._columna = tokenError.beginColumn;

        if (tokens.size() == 1) {

            error._razonMsg = "Se encontró el símbolo";

            if (currentToken.kind == ECConstants.CADENA_TEXTO) {
                error.addRazon(ex.tokenImage[currentToken.kind]);
            } else {

                sb.append(String.format("\"%s\"", currentToken.image));

                if (!ex.tokenImage[tokenError.kind].regionMatches(1, tokenError.image, 0, tokenError.image.length()))
                    sb.append(' ').append('(').append(ex.tokenImage[tokenError.kind]).append(')');

                error.addRazon(sb.toString());

            }

        } else {

            error._razonMsg = "Se encontraron los símbolos";

            for (Token token : tokens) {

                if (token.kind == ECConstants.CADENA_TEXTO) {
                    error.addRazon(ex.tokenImage[token.kind]);
                } else {
                    sb.append(String.format("\"%s\"", token.image));

                    if (!ex.tokenImage[token.kind].regionMatches(1, token.image, 0, token.image.length()))
                        sb.append(' ').append('(').append(ex.tokenImage[token.kind]).append(')');

                }

                error.addRazon(sb.toString());
            }

        }

        sb.setLength(0);

        if (expected.size() == 1) {
            error._solucionMsg = "Se esperaba";

            error.addSolucion(expected.get(0));
        } else {
            error._solucionMsg = "Se esperaba uno de los siguientes";

            for (String e : expected)
                error.addSolucion(e);

        }

        return error;
    }

    @Override
    public final String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(_tipo)
                .append(", línea ").append(_linea)
                .append(", columna ").append(_columna).append('.');

        if (!_razones.isEmpty()) {

            sb.append('\n').append("    ");

            if (_razones.size() == 1) {

                if (!_razonMsg.isEmpty())
                    sb.append(_razonMsg).append(' ');

                sb.append(_razones.get(0)).append('.');

            } else {

                if (!_razonMsg.isEmpty())
                    sb.append(_razonMsg).append(':').append(' ');

                for (String r : _razones)
                    sb.append('\n').append("        ").append('-')
                            .append(' ').append(r);

            }
        }

        if (!_soluciones.isEmpty()) {

            sb.append('\n').append("    ");

            if (_soluciones.size() == 1) {

                if (!_solucionMsg.isEmpty())
                    sb.append(_solucionMsg).append(' ');

                sb.append(_soluciones.get(0)).append('.');
            } else {

                if (!_solucionMsg.isEmpty())
                    sb.append(_solucionMsg).append(':').append(' ');

                for (String s : _soluciones)
                    sb.append('\n').append("        ").append('-')
                            .append(' ').append(s);

            }

        }

        return sb.toString();
    }

    

}
