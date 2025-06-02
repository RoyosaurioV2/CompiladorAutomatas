public class Variable extends Simbolo {

    public Variable(String nombre, TipoDato tipoDato) {
        super(nombre, tipoDato);

        _disponible = false;
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
    public String getDeclaracion() {

        StringBuilder sb = new StringBuilder();
        sb.append(_nombre).append(':').append('\n')
                .append("    .tipoDato db ").append('\'').append(_tipoDato).append('\'').append(", 0").append('\n')
                .append("    .valor ").append(getDirectivaValor());

        if (_tipoDato.equals(TipoDato.Cadena)) {
            sb.append(" 1024");
        } else {
            sb.append(' ').append(Assambly.toHex(_tipoDato, 0));
        }
        sb.append('\n');
        sb.append("    .formato db ").append(getFormato()).append('\n');

        return sb.toString();

    }

    public Etiqueta getEtiquetaTipoDato() {
        var nombre = String.format("%s.tipoDato", _nombre);
        return new Etiqueta(nombre, TipoDato.Cadena, this);
    }

    public Etiqueta getEtiquetaValor() {
        var nombre = String.format("%s.valor", _nombre);
        return new Etiqueta(nombre, _tipoDato, this);
    }

    public Etiqueta getEtiquetaFormato() {
        var nombre = String.format("%s.formato", _nombre);
        return new Etiqueta(nombre, TipoDato.Cadena, this);
    }

}
