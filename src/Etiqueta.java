public final class Etiqueta extends Simbolo {

    private final Simbolo _padre;

    public Etiqueta(String nombre, TipoDato tipoDato) {
        this(nombre, tipoDato, null);
    }

    public Etiqueta(String nombre, TipoDato tipoDato, Simbolo padre) {
        super(nombre, tipoDato);

        _padre = padre;
    }

    @Override
    public String getDeclaracion() {
        return "";
    }

    @Override
    public void usar() {

        if (_padre != null)
            _padre.usar();

    }

    @Override
    public void liberar() {

        if (_padre != null)
            _padre.liberar();
    }

    @Override
    public String toString() {
        return _nombre;
    }

}
