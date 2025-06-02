public class ExpresionDeclaracion extends ExpresionUnaria {

    private final ExpresionBinaria _asignacion;

    public ExpresionDeclaracion(int bloqueId, ExpresionSimbolo operando, ExpresionBinaria asignacion) {
        super(bloqueId, Expresion.Tipo.Declaracion, operando.getTipoDato(), operando);

        assert operando.equals(asignacion.getIzquierda());

        _asignacion = asignacion;

    }

    public ExpresionDeclaracion(int bloqueId, ExpresionSimbolo operando) {
        this(bloqueId, operando, null);
    }

    @Override
    public ExpresionSimbolo getOperando() {
        return (ExpresionSimbolo) _operando;
    }

    public ExpresionBinaria getAsignacion() {
        return _asignacion;
    }

    @Override
    protected int compare(Expresion o) {

        ExpresionDeclaracion ed = (ExpresionDeclaracion) o;

        int compare = _operando.compareTo(ed._operando);

        if (compare != 0)
            return compare;

        return _asignacion.compareTo(ed._asignacion);
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        // _asignacion.getDerecha().addTo(bloque);
        // _operando.addTo(bloque);
        bloque.addExpresion(this);
    }

    @Override
    public void toAssambly(Assambly assambly) {

        // Declaración
        _operando.toAssambly(assambly);

        if (_asignacion != null) {
            _asignacion.toAssambly(assambly);
        }
    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {

        sb.append("Establecer ").append(_operando.getTipoDato()).append(' ');
        _operando.mostrar(sb, 0);

        if (_asignacion != null) {
            sb.append('\n');
            _asignacion.mostrar(sb, 0);
        }

    }

}
