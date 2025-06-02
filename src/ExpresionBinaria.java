
public class ExpresionBinaria extends Expresion {

    private final int _bloqueId;
    private final Expresion.Tipo _tipoExpresion;
    private final TipoDato _tipoDato;
    protected final Expresion _izq;
    protected final Expresion _der;

    public ExpresionBinaria(int bloqueId, Expresion.Tipo tipoExpresion, TipoDato tipoDato, Expresion izq,
            Expresion der) {
        _bloqueId = bloqueId;
        _tipoExpresion = tipoExpresion;
        _tipoDato = tipoDato;
        _izq = izq;
        _der = der;
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return _tipoExpresion;
    }

    @Override
    public TipoDato getTipoDato() {
        return _tipoDato;
    }

    public Expresion getIzquierda() {
        return _izq;
    }

    public Expresion getDerecha() {
        return _der;
    }

    @Override
    public int getBloqueId() {
        return _bloqueId;
    }

    @Override
    protected int compare(Expresion o) {

        ExpresionBinaria eb = (ExpresionBinaria) o;

        int compare = _izq.compareTo(eb._izq);

        if (compare != 0)
            return compare;

        return _der.compareTo(eb._der);
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        // _izq.addTo(bloque);
        // _der.addTo(bloque);
        bloque.addExpresion(this);
    }

    private String getOperador() {

        return switch (_tipoExpresion) {
            case Expresion.Tipo.Asignacion -> "=";
            case Expresion.Tipo.Multiplicacion -> "*";
            case Expresion.Tipo.Division -> "/";
            case Expresion.Tipo.Modulo -> "%";
            case Expresion.Tipo.Suma -> "+";
            case Expresion.Tipo.Resta -> "-";
            case Expresion.Tipo.Igual -> "==";
            case Expresion.Tipo.Diferente -> "!=";
            case Expresion.Tipo.MayorIgual -> ">=";
            case Expresion.Tipo.MenorIgual -> "<=";
            case Expresion.Tipo.Mayor -> ">";
            case Expresion.Tipo.Menor -> "<";
            case Expresion.Tipo.And -> "&&";
            case Expresion.Tipo.Or -> "||";
            default -> null;
        };

    }

    private String getMnemonico() {

        switch (_tipoExpresion) {
            case Expresion.Tipo.Multiplicacion:
                return "imul";
            case Expresion.Tipo.Division:
                return "idiv";
            case Expresion.Tipo.Modulo:
                return "idiv";
            case Expresion.Tipo.Suma:
                return "add";
            case Expresion.Tipo.Resta:
                return "sub";
            /*
             * case Expresion.Tipo.Igual:
             * return "";
             * case Expresion.Tipo.Diferente:
             * break;
             * case Expresion.Tipo.MayorIgual:
             * break;
             * case Expresion.Tipo.MenorIgual:
             * break;
             * case Expresion.Tipo.Mayor:
             * break;
             * case Expresion.Tipo.Menor:
             * break;
             * case Expresion.Tipo.And:
             * break;
             * case Expresion.Tipo.Or:
             * break;
             */
            default:
                return "";

        }

    }

    @Override
    public void toAssambly(Assambly assambly) {

        if (_tipoExpresion.equals(Expresion.Tipo.Asignacion)) {

            var izq = (ExpresionSimbolo) _izq;
            var variable = assambly.variable(izq);

            if (_der.getTipoExpresion().equals(Expresion.Tipo.Constante)) {

                var derConst = (ExpresionConstante) _der;

                if (_tipoDato.equals(TipoDato.Cadena)) {

                    String cadena = derConst.getValor();

                    if (!cadena.isEmpty()) {
                        var constante = assambly.cadenaConstante(cadena);

                        assambly.copiarCadena(variable.getEtiquetaValor(), constante);
                    }

                } else {

                    var valor = derConst.getAssamblyValorConstante();

                    assambly.asignarValorConstante(variable.getEtiquetaValor(), valor);
                }

            } else {

                // Asignación que no es constante
                _der.toAssambly(assambly);
                var resultado = assambly.popUltimoResultado();

                assambly.asignacion(variable.getEtiquetaValor(), resultado);
            }

        } else if (_tipoExpresion.equals(Expresion.Tipo.Suma)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            if (_tipoDato.equals(TipoDato.Cadena)) {
                assambly.concatenarCadenas(rIzq, rDer);
            } else {
                assambly.suma(rIzq, rDer);
            }

        } else if (_tipoExpresion.equals(Expresion.Tipo.Resta)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.resta(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Multiplicacion)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.multiplicacion(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Division)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.division(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Modulo)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.modulo(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Igual)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.igualdad(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Diferente)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.diferencia(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.MayorIgual)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.mayorIgual(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.MenorIgual)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.menorIgual(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Mayor)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.mayor(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Menor)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.menor(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.And)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.and(rIzq, rDer);

        } else if (_tipoExpresion.equals(Expresion.Tipo.Or)) {

            _izq.toAssambly(assambly);
            var rIzq = assambly.peekUltimoResultado();

            _der.toAssambly(assambly);
            var rDer = assambly.peekUltimoResultado();

            assambly.or(rIzq, rDer);

        }

    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {

        var op = getOperador();

        switch (_tipoExpresion) {
            case Asignacion:
                _izq.mostrar(sb, 0);
                sb.append(' ');
                sb.append(op);
                sb.append(' ');
                _der.mostrar(sb, 0);
                break;
            case Suma:
            case Resta:

                if (presedencia > 1)
                    sb.append('(');

                _izq.mostrar(sb, 1);
                sb.append(' ');
                sb.append(op);
                sb.append(' ');
                _der.mostrar(sb, 1);

                if (presedencia > 1)
                    sb.append(')');

                break;
            case Multiplicacion:
            case Division:
            case Modulo:

                if (presedencia > 2)
                    sb.append('(');

                _izq.mostrar(sb, 2);
                sb.append(' ');
                sb.append(op);
                sb.append(' ');
                _der.mostrar(sb, 2);

                if (presedencia > 2)
                    sb.append(')');

                break;
            case Igual:
            case Diferente:
            case MayorIgual:
            case MenorIgual:
            case Mayor:
            case Menor:
                _izq.mostrar(sb, 0); // a + b > 2 && 3 < 6 || Verdadero
                sb.append(' ');
                sb.append(op);
                sb.append(' ');
                _der.mostrar(sb, 0);

                break;
            case Or:

                if (presedencia > 1)
                    sb.append('(');

                _izq.mostrar(sb, 1);
                sb.append(' ');
                sb.append(op);
                sb.append(' ');
                _der.mostrar(sb, 1);

                if (presedencia > 1)
                    sb.append(')');

                break;
            case And:

                if (presedencia > 2)
                    sb.append('(');

                _izq.mostrar(sb, 2);
                sb.append(' ');
                sb.append(op);
                sb.append(' ');
                _der.mostrar(sb, 2);

                if (presedencia > 2)
                    sb.append(')');

                break;

            default:
                break;
        }

    }

}
