
public class ExpresionConstante extends Expresion {

    private final Object _valor;
    private final TipoDato _tipoDato;

    public ExpresionConstante(Object valor, TipoDato tipoDato) {
        _valor = valor;
        _tipoDato = tipoDato;
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return Expresion.Tipo.Constante;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValor() {
        return (T) _valor;
    }

    @Override
    public int getBloqueId() {
        return 0;
    }

    public long getAssamblyValorConstante() {

        if (_tipoDato.equals(TipoDato.Entero)) {

            int e = getValor();

            return e;
        }

        if (_tipoDato.equals(TipoDato.Flotante)) {

            double d = getValor();

            return Double.doubleToLongBits(d);
        }

        if (_tipoDato.equals(TipoDato.Booleano)) {

            boolean b = getValor();

            return b ? 0xFF : 0x00;
        }

        if (_tipoDato.equals(TipoDato.Caracter)) {

            char c = getValor();

            return (byte) c;
        }

        return 0;
    }

    public String getAssamblyValor() {

        if (_tipoDato.equals(TipoDato.Cadena)) {

            StringBuilder sb = new StringBuilder();

            String str = getValor();

            var bytes = str.getBytes();

            for (byte b : bytes) {
                sb.append(String.format("0x%00X, ", b));
            }
            sb.append('0');

            return sb.toString();
        }

        return Long.toString(getAssamblyValorConstante());
    }

    @Override
    public TipoDato getTipoDato() {
        return _tipoDato;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected int compare(Expresion o) {

        ExpresionConstante ec = (ExpresionConstante) o;

        if (_valor.equals(ec._valor))
            return 0;

        if (_tipoDato.equals(TipoDato.Cadena))
            return -1;

        Comparable<? super Object> v = (Comparable<? super Object>) _valor;
        Comparable<? super Object> u = (Comparable<? super Object>) ec._valor;

        return v.compareTo(u);
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        bloque.addExpresion(this);
    }

    @Override
    public void toAssambly(Assambly assambly) {
        // assambly.addInstruccion(String.format("mov %s, %s ; %s",
        // assambly.getRegistroDisponible(_tipoDato), getAssamblyValor(), this));
        assambly.constante(this);
        
    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {

        if (_tipoDato.equals(TipoDato.Booleano)) {

            boolean bool = getValor();

            sb.append(bool ? "Verdadero" : "Falso");
            return;
        }

        if (_tipoDato.equals(TipoDato.Cadena)) {
            sb.append('"').append(_valor).append('"');
            return;
        }

        if (_tipoDato.equals(TipoDato.Caracter)) {
            sb.append('\'').append(_valor).append('\'');
            return;
        }

        sb.append(_valor);
    }

}
