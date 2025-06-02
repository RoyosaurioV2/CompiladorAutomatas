public final class ExpresionArreglo extends ExpresionSimbolo {

    private final ExpresionIndice _indice;
    private final Expresion.Tipo _tipoExpresion;

    public ExpresionArreglo(String nombre, TipoDato tipoDato, ExpresionIndice indice, Expresion.Tipo tipoExpresion) {
        super(-1, nombre, tipoDato);

        assert _tipoDato.getDimension() == indice.getDimension();

        _indice = indice;
        _tipoExpresion = tipoExpresion;
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return _tipoExpresion;
    }

    public TipoDato getTipoDato(int indices) {
        return _tipoDato.getTipoInterno(indices);
    }

    @Override
    protected int compare(Expresion o) {

        ExpresionArreglo ea = (ExpresionArreglo) o;

        int compare = _nombre.compareTo(ea._nombre);

        if (compare != 0)
            return compare;

        return _indice.compare(ea._indice);
    }

}
