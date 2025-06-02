public class ExpresionUnaria extends Expresion {

    private final int _bloqueId;
    protected final Expresion _operando;
    private final Expresion.Tipo _tipoExpresion;
    private final TipoDato _tipoDato;

    public ExpresionUnaria(int bloqueId, Expresion.Tipo tipoExpresion, TipoDato tipoDato, Expresion operando) {
        _bloqueId = bloqueId;
        _tipoExpresion = tipoExpresion;
        _tipoDato = tipoDato;
        _operando = operando;
    }

    public ExpresionUnaria(int bloqueId, Expresion.Tipo tipoExpresion, Expresion operando) {
        this(bloqueId, tipoExpresion, TipoDato.Vacio, operando);
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return _tipoExpresion;
    }

    @Override
    public TipoDato getTipoDato() {
        return _tipoDato;
    }

    public Expresion getOperando() {
        return _operando;
    }

    @Override
    public int getBloqueId() {
        return _bloqueId;
    }

    @Override
    protected int compare(Expresion o) {

        ExpresionUnaria eu = (ExpresionUnaria) o;

        return _operando.compareTo(eu._operando);
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        // _operando.addTo(bloque);
        bloque.addExpresion(this);
    }

    private String getOperador() {

        return switch (_tipoExpresion) {
            case Expresion.Tipo.Leer -> "Interpretar ";
            case Expresion.Tipo.Imprimir -> "Escribir ";
            case Expresion.Tipo.Positivo -> "+";
            case Expresion.Tipo.Negativo -> "-";
            case Expresion.Tipo.Not -> "!";
            default -> null;
        };

    }

    @Override
    public void toAssambly(Assambly assambly) {
        // Leer
        // Imprimir
        // Positivo
        // Negativo
        // Not

        if (_tipoExpresion.equals(Expresion.Tipo.Leer)) {

            var simbolo = (ExpresionSimbolo) _operando;
            var variable = assambly.variable(simbolo);
            assambly.popUltimoResultado();

            assambly.leer(variable);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Imprimir)) {

            _operando.toAssambly(assambly);
            var operando = assambly.popUltimoResultado();

            assambly.escribir(operando);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Positivo)) {

            _operando.toAssambly(assambly);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Negativo)) {

            _operando.toAssambly(assambly);
            var operando = assambly.peekUltimoResultado();

            assambly.negativo(operando);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Not)) {

            _operando.toAssambly(assambly);
            var operando = assambly.peekUltimoResultado();
            
            assambly.not(operando);

        }

    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {

        var op = getOperador();

        switch (_tipoExpresion) {

            case Leer:
            case Imprimir:
                sb.append(op);
                _operando.mostrar(sb, 0);
                break;
            case Positivo:
            case Negativo:

                sb.append(op);

                if (presedencia > 3)
                    sb.append('(');

                _operando.mostrar(sb, 4);

                if (presedencia > 3)
                    sb.append(')');

                break;
            case Not:

                sb.append(op);

                if (presedencia > 4)
                    sb.append('(');

                _operando.mostrar(sb, 4);

                if (presedencia > 4)
                    sb.append(')');

                break;

            default:
                break;
        }

    }

}
