public final class ExpresionCondicional extends Expresion {

    private final int _id;

    private final int _bloqueId;
    private final Expresion _condicion;
    private final ExpresionBloque _bloqueVerdadero;
    private final ExpresionBloque _bloqueFalso;

    public ExpresionCondicional(int id, int bloqueId, Expresion condicion, ExpresionBloque bloqueVerdadero,
            ExpresionBloque bloqueFalso) {
        _id = id;
        _bloqueId = bloqueId;
        _condicion = condicion;
        _bloqueVerdadero = bloqueVerdadero;
        _bloqueFalso = bloqueFalso;
    }

    public ExpresionCondicional(int id, int bloqueId, Expresion condicion, ExpresionBloque bloqueVerdadero) {
        this(id, bloqueId, condicion, bloqueVerdadero, null);
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return Expresion.Tipo.Condicional;
    }

    @Override
    public TipoDato getTipoDato() {
        return TipoDato.Vacio;
    }

    @Override
    public int getBloqueId() {
        return _bloqueId;
    }

    public Expresion getCondicion() {
        return _condicion;
    }

    public ExpresionBloque getBloqueVerdadero() {
        return _bloqueVerdadero;
    }

    public ExpresionBloque getBloqueFalso() {
        return _bloqueFalso;
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        bloque.addExpresion(this);
    }

    @Override
    protected int compare(Expresion o) {
        return -1;
    }

    @Override
    public void toAssambly(Assambly assambly) {

        _condicion.toAssambly(assambly);
        var condicion = assambly.popUltimoResultado();

        if (!condicion.getTipoDato().equals(TipoDato.Booleano))
            return;

        var si = String.format(".%d_si%d", _bloqueId, _id);
        var sino = String.format(".%d_sino%d", _bloqueId, _id);
        var finSi = String.format(".%d_fin_si%d", _bloqueId, _id);

        var rArg1 = assambly.getRegistroArg1(TipoDato.Booleano);
        var respaldado1 = assambly.usarRegistro(rArg1);

        assambly.addInstruccion("%s: nop ; Inicio condicional Si", si);
        assambly.addInstruccion("mov %s, %s ; Condicion", rArg1, condicion.dereferenciar());
        assambly.addInstruccion("cmp %s, [falso]", rArg1);
        assambly.addInstruccion("je %s", sino);
        
        assambly.liberarRegistro(rArg1, respaldado1);
        assambly.addInstruccion("");
        assambly.addInstruccion("; BloqueVerdadero");
        assambly.addInstruccion("");
        _bloqueVerdadero.toAssambly(assambly);
        assambly.addInstruccion("");
        assambly.addInstruccion("jmp %s", finSi);
        assambly.addInstruccion("%s: nop ; BloqueFalso", sino);
        if (_bloqueFalso != null) {
            assambly.liberarRegistro(rArg1, respaldado1);
            assambly.addInstruccion("");
            _bloqueFalso.toAssambly(assambly);
            assambly.addInstruccion("");
        }
        assambly.addInstruccion("%s: nop ; FinSi", finSi);
        assambly.liberarRegistro(rArg1, respaldado1);
        assambly.addInstruccion("");
    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {

        String espacios = "    ".repeat(_bloqueVerdadero.getProfundidad() - 1);

        sb.append(espacios).append("Si (");
        _condicion.mostrar(sb, 0);
        sb.append(')').append(' ');
        _bloqueVerdadero.mostrar(sb, 0);

        if (_bloqueFalso != null) {
            sb.append(" Sino ");
            _bloqueFalso.mostrar(sb, 0);
        }

    }

}
