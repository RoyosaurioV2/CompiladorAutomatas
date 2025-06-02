
public abstract class Simbolo {

    protected final String _nombre;
    protected TipoDato _tipoDato;
    protected boolean _disponible;

    protected Simbolo(String nombre, TipoDato tipoDato) {
        _nombre = nombre;
        _tipoDato = tipoDato;
        _disponible = true;
    }

    public String getNombre() {
        return _nombre;
    }

    public TipoDato getTipoDato() {
        return _tipoDato;
    }

    public boolean estaDisponible() {
        return _disponible;
    }

    public String dereferenciar() {
        return String.format("[%s]", _nombre);
    }

    public String getDirectivaValor() {

        if (_tipoDato.equals(TipoDato.Entero))
            return "dd";

        if (_tipoDato.equals(TipoDato.Flotante))
            return "dq";

        if (_tipoDato.equals(TipoDato.Caracter))
            return "dd";

        if (_tipoDato.equals(TipoDato.Cadena))
            return "resb";

        if (_tipoDato.equals(TipoDato.Booleano))
            return "db";

        return null;
    }

    public String getFormato() {

        if (_tipoDato.equals(TipoDato.Entero))
            return "'%d', 0";

        if (_tipoDato.equals(TipoDato.Flotante))
            return "'%lf', 0";

        if (_tipoDato.equals(TipoDato.Caracter))
            return "'%c', 0";

        if (_tipoDato.equals(TipoDato.Cadena))
            return "'%[^', 0xA, ']', 0";

        if (_tipoDato.equals(TipoDato.Booleano))
            return "'%s', 0";

        return null;

    }

    public String getDirectivaDato() {

        if (_tipoDato.equals(TipoDato.Entero))
            return "dword";

        if (_tipoDato.equals(TipoDato.Flotante))
            return "qword";

        if (_tipoDato.equals(TipoDato.Caracter))
            return "byte";

        if (_tipoDato.equals(TipoDato.Cadena))
            return "qword";

        if (_tipoDato.equals(TipoDato.Booleano))
            return "byte";

        return null;

    }

    public String dereferenciarConDato() {
        return String.format("%s [%s]",getDirectivaDato(), _nombre);
    }
    
    public abstract String getDeclaracion();
    public abstract void usar();
    public abstract void liberar();
    public abstract String toString();


}
