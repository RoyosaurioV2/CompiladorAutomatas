import java.util.ArrayList;

public class ExpresionIndice extends Expresion {

    private int _dimension;
    private TipoDato _tipoDato;
    private final ArrayList<Expresion> _indices;

    public ExpresionIndice(TipoDato tipoDato) {
        _dimension = 0;
        _indices = new ArrayList<>();
        _tipoDato = tipoDato;
    }

    public ExpresionIndice() {
        this(TipoDato.Vacio);
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return Expresion.Tipo.Indice;
    }

    @Override
    public TipoDato getTipoDato() {
        return _tipoDato.getTipoArreglo(_dimension);
    }

    public void setTipoDato(TipoDato tipoDato) {
        _tipoDato = tipoDato;
    }

    public int getDimension() {
        return _dimension;
    }

    public boolean addIndice(Expresion indice) {

        if (!indice.getTipoDato().equals(TipoDato.Entero))
            return false;

        _indices.add(indice);
        ++_dimension;

        return true;
    }

    @Override
    protected int compare(Expresion o) {

        ExpresionIndice indice = (ExpresionIndice) o;

        return _dimension - indice._dimension;
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        
        _indices.forEach(e -> e.addTo(bloque));
        bloque.addExpresiones(this);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toString'");
    }

    @Override
    public void toAssambly(Assambly assambly) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toAssambly'");
    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mostrar'");
    }

    @Override
    public int getBloqueId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBloqueId'");
    }

}
