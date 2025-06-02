public final class ExpresionCicloMientras extends Expresion {

    private final int _id;

    private final int _bloqueId;
    private final Expresion _condicion;
    private final ExpresionBloque _cuerpo;

    public ExpresionCicloMientras(int id, int bloqueId, Expresion condicion, ExpresionBloque cuerpo) {

        assert condicion.getTipoDato().equals(TipoDato.Booleano);

        _id = id;
        _bloqueId = bloqueId;

        _condicion = condicion;
        _cuerpo = cuerpo;
    }

    public ExpresionBloque getCuerpo() {
        return _cuerpo;
    }

    public Expresion getCondicion() {
        return _condicion;
    }

    public boolean esInfinito() {

        if (!_condicion.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return false;

        assert _condicion.getTipoDato().equals(TipoDato.Booleano);

        var cCond = (ExpresionConstante) _condicion;

        return cCond.getValor();
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return Expresion.Tipo.CicloMientras;
    }

    @Override
    public TipoDato getTipoDato() {
        return TipoDato.Vacio;
    }

    @Override
    public int getBloqueId() {
        return _bloqueId;
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        bloque.addExpresion(this);
    }

    @Override
    protected int compare(Expresion o) {

        var mO = (ExpresionCicloMientras) o;

        int compare = _condicion.compareTo(mO._condicion);

        if (compare != 0)
            return compare;

        return _cuerpo.compareTo(mO._cuerpo);
    }

    @Override
    public void toAssambly(Assambly assambly) {

        var mientras = String.format(".%d_mientras%d", _bloqueId, _id);
        var finMientras = String.format(".%d_fin_mientras%d", _bloqueId, _id);

        assambly.addInstruccion("%s: nop ; Inicio ciclo mientras", mientras);

        _condicion.toAssambly(assambly);
        var condicion = assambly.popUltimoResultado();
        condicion.usar();

        if (!condicion.getTipoDato().equals(TipoDato.Booleano))
            return;

        var rArg1 = assambly.getRegistroArg1(TipoDato.Booleano);
        assambly.respaldarRegistro(rArg1);
        assambly.addInstruccion("mov %s, %s ; Condicion", rArg1, condicion.dereferenciar());
        assambly.addInstruccion("cmp %s, [falso]", rArg1);
        assambly.addInstruccion("je %s", finMientras);
        
        assambly.recuperarRegistro(rArg1);
        assambly.addInstruccion("");

        assambly.addInstruccion("; Cuerpo Mientras");
        assambly.addInstruccion("");
        _cuerpo.toAssambly(assambly);
        assambly.addInstruccion("");
        
        assambly.addInstruccion("jmp %s", mientras);
        assambly.addInstruccion("%s: nop ; FinMientras", finMientras);
        assambly.recuperarRegistro(rArg1);
        assambly.addInstruccion("");

        condicion.liberar();

    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {

        String espacios = "    ".repeat(_cuerpo.getProfundidad() - 1);

        sb.append(espacios).append("Mientras (");
        _condicion.mostrar(sb, 0);
        sb.append(')').append(' ');
        _cuerpo.mostrar(sb, 0);

    }

}
