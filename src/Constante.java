public final class Constante extends Simbolo {

    public Constante(String nombre, TipoDato tipoDato) {
        super(nombre, tipoDato);
    }

    @Override
    public String getDeclaracion() {
        return "";
    }

    @Override
    public void usar() {

    }

    @Override
    public void liberar() {

    }

    @Override
    public String toString() {
        return _nombre;
    }

    @Override
    public String dereferenciar() {
        return _nombre;
    }

    @Override
    public String dereferenciarConDato() {
        return _nombre;
    }

}
