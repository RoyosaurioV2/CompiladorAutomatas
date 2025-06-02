import java.util.Collections;
import java.util.List;

public final class ExpresionFuncion extends ExpresionSimbolo {

    private final Expresion.Tipo _tipoExpresion;
    private final List<Expresion> _argumentos;
    private final ExpresionBloque _cuerpo;

    public ExpresionFuncion(String nombre, TipoDato tipoDato, List<Expresion> argumentos, ExpresionBloque cuerpo) {
        super(-1, nombre, tipoDato);

        _tipoExpresion = (cuerpo == null) ? Expresion.Tipo.LlamadaFuncion : Expresion.Tipo.DefinicionArreglo;

        _argumentos = argumentos;
        _cuerpo = cuerpo;
    }

    public ExpresionFuncion(String nombre, TipoDato tipoDato, List<Expresion> argumentos) {
        this(nombre, tipoDato, argumentos, null);
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return _tipoExpresion;
    }

    public List<Expresion> getArgumentos() {
        return Collections.unmodifiableList(_argumentos);
    }

    public ExpresionBloque getCuerpo() {
        return _cuerpo;
    }

    public int compareArgumentos(List<Expresion> argumentos) {

        if (_argumentos == argumentos)
            return 0;

        int thisSize = _argumentos.size();
        int otherSize = argumentos.size();

        if (thisSize < otherSize)
            return -1;

        if (thisSize > otherSize)
            return 1;

        int compare = 0;
        for (int i = 0; i < thisSize; ++i) {

            compare = _argumentos.get(i).getTipoDato().compareTo(argumentos.get(i).getTipoDato());

            if (compare != 0)
                return compare;

        }

        return compare;
    }

    @Override
    protected int compare(Expresion o) {

        ExpresionFuncion ef = (ExpresionFuncion) o;

        int compare = _nombre.compareTo(ef._nombre);

        if (compare != 0)
            return compare;

        return compareArgumentos(ef._argumentos);
    }

}
