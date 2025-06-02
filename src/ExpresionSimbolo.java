public class ExpresionSimbolo extends Expresion {

    private final int _bloqueId;
    protected final String _nombre;
    protected final TipoDato _tipoDato;
    protected Expresion _valor;

    public ExpresionSimbolo(int bloqueId, String nombre, TipoDato tipoDato) {
        this(bloqueId, nombre, tipoDato, Expresion.predeterminada(tipoDato));
    }

    public ExpresionSimbolo(int bloqueId, String nombre, TipoDato tipoDato, Expresion valor) {
        _bloqueId = bloqueId;
        _nombre = nombre;
        _tipoDato = tipoDato;
        _valor = valor;
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return Expresion.Tipo.Variable;
    }

    @Override
    public TipoDato getTipoDato() {
        return _tipoDato;
    }

    public String getNombre() {
        return _nombre;
    }

    public Expresion getValor() {
        return _valor;
    }

    @Override
    public int getBloqueId() {
        return _bloqueId;
    }

    public void setValor(Expresion valor) {
        _valor = valor;
    }

    @Override
    protected int compare(Expresion o) {

        ExpresionSimbolo ep = (ExpresionSimbolo) o;

        if (_nombre.equals(ep._nombre))
            return 0;

        return -1;

        // return _nombre.compareTo(ep._nombre);
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        bloque.addExpresion(this);
    }

    @Override
    public void toAssambly(Assambly assambly) {
        assambly.variable(this);
    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {
        sb.append(_nombre);
    }

}
