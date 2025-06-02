public final class ExpresionPredeterminada extends Expresion {

    private final TipoDato _tipoDato;

    public ExpresionPredeterminada(TipoDato tipoDato) {
        _tipoDato = tipoDato;
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return Expresion.Tipo.Predeterminado;
    }

    @Override
    public int getBloqueId() {
        return 0;
    }


    @Override
    public TipoDato getTipoDato() {
        return _tipoDato;
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        return;
    }
    
    @Override
    protected int compare(Expresion o) {
        return _tipoDato.compareTo(o.getTipoDato());
    }

    @Override
    public void toAssambly(Assambly assambly) {
        
    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {

    }
    
}
