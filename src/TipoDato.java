
import java.util.HashMap;
import java.util.Objects;

public final class TipoDato implements Comparable<TipoDato> {

    private final static HashMap<Integer, TipoDato> _tipoDatoCache;

    public final static TipoDato Indefinido = new TipoDato(null, -1, "Indefinido");
    public final static TipoDato Vacio = new TipoDato(void.class, 0, "Vacío");
    public final static TipoDato Entero = new TipoDato(int.class, ECConstants.ENTERO << 1, "Entero");
    public final static TipoDato Flotante = new TipoDato(double.class, ECConstants.FLOTANTE << 1, "Flotante");
    public final static TipoDato Cadena = new TipoDato(String.class, ECConstants.CADENA << 1, "Cadena");
    public final static TipoDato Caracter = new TipoDato(char.class, ECConstants.CARACTER << 1, "Caracter");
    public final static TipoDato Booleano = new TipoDato(boolean.class, ECConstants.BOOLEANO << 1, "Booleano");

    static {

        _tipoDatoCache = new HashMap<>(5);
        _tipoDatoCache.put(Entero.hashCode(), Entero);
        _tipoDatoCache.put(Flotante.hashCode(), Flotante);
        _tipoDatoCache.put(Cadena.hashCode(), Cadena);
        _tipoDatoCache.put(Caracter.hashCode(), Caracter);
        _tipoDatoCache.put(Booleano.hashCode(), Booleano);

    }

    public static int hashCode(int id, int dimension) {
        return Objects.hash(id, dimension);
    }

    public static TipoDato desdeInt(int tipo) {

        switch (tipo) {
            case 0:
                return Vacio;
            case ECConstants.ENTERO:
                return Entero;
            case ECConstants.FLOTANTE:
                return Flotante;
            case ECConstants.CADENA:
                return Cadena;
            case ECConstants.CARACTER:
                return Caracter;
            case ECConstants.BOOLEANO:
                return Booleano;
            default:
                return Indefinido;
        }

    }

    private final Class<?> _class;
    private int _hash;
    private String _nombre;
    private int _dimension;

    private TipoDato(Class<?> clazz, int hash, String nombre, int dimension) {
        _class = clazz;
        _hash = hash;
        _nombre = nombre;
        _dimension = dimension;

    }

    private TipoDato(Class<?> clazz, int hash, String nombre) {
        this(clazz, hash, nombre, 0);
    }

    public <T> Class<T> getClase() {
        return (Class<T>)_class;
    }

    @Override
    public int compareTo(TipoDato o) {

        int compare = Integer.compare(_hash, o._hash);

        if (compare != 0)
            return compare;

        return Integer.compare(_dimension, o._dimension);
    }

    public int getBits() {

        if (equals(Entero)) {
            return 32;
        } else if (equals(Flotante)) {
            return 64;
        } else if (equals(Cadena)) {
            return 32;
        } else if (equals(Caracter)) {
            return 8;
        } else if (equals(Booleano)) {
            return 8;
        }

        return 0;
    }

    public int getBytes() {
        return getBits() / 8;
    }

    public boolean esArreglo() {
        return (_hash & 1) != 0;
    }

    public String getNombre() {
        return String.format("%s%s", _nombre, "[]".repeat(_dimension));
    }

    public int getDimension() {
        return _dimension;
    }

    @Override
    public String toString() {
        return _nombre;
    }

    public TipoDato getTipoArreglo() {

        if (this.equals(TipoDato.Indefinido) || this.equals(TipoDato.Vacio))
            return this;

        return new TipoDato(_class, _hash | 1, _nombre, _dimension + 1);
    }

    public TipoDato getTipoArreglo(int dimensiones) {

        if (this.equals(TipoDato.Indefinido) || this.equals(TipoDato.Vacio))
            return this;

        int h = _hash | 1;
        int d = _dimension + dimensiones;

        if (d <= 0)
            return this;

        int hash = hashCode(h, d);

        if (_tipoDatoCache.containsKey(hash))
            return _tipoDatoCache.get(hash);

        return _tipoDatoCache.put(hash, new TipoDato(_class, h, _nombre, d));
    }

    public TipoDato getTipoInterno() {
        return getTipoInterno(_dimension - 1);
    }

    public TipoDato getTipoInterno(int dimensiones) {

        if (this.equals(TipoDato.Indefinido) || this.equals(TipoDato.Vacio))
            return this;

        int d = _dimension - dimensiones;

        if (d < 0)
            return this;

        int h = (d != 0) ? _hash : _hash & 0xFFFFFFFE;

        int hash = hashCode(h, d);

        if (_tipoDatoCache.containsKey(hash))
            return _tipoDatoCache.get(hash);

        return _tipoDatoCache.put(hash, new TipoDato(_class, h, _nombre, d));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        TipoDato tipoDato = (TipoDato) o;
        return _hash == tipoDato._hash && _dimension == tipoDato._dimension;
    }

    @Override
    public int hashCode() {
        return TipoDato.hashCode(_hash, _dimension);
    }

    public String getRegistroIzquierdo() {

        if (this.equals(Entero)) {
            return "eax";
        } else if (this.equals(Flotante)) {
            return "rax";
        } else if (this.equals(Cadena)) {
            return "rdi";
        } else if (this.equals(Caracter)) {
            return "al";
        } else if (this.equals(Booleano)) {
            return "al";
        }

        return "";

    }

    public String getRegistroDerecho() {

        if (this.equals(Entero)) {
            return "ebx";
        } else if (this.equals(Flotante)) {
            return "rbx";
        } else if (this.equals(Cadena)) {
            return "rsi";
        } else if (this.equals(Caracter)) {
            return "bl";
        } else if (this.equals(Booleano)) {
            return "bl";
        }

        return "";

    }

    public boolean puedeUtilizar(int operador, boolean unario) {

        if (esArreglo())
            return false;

        int tipoDato = _hash;

        switch (operador) {
            case ECConstants.SUMA:
                return tipoDato == Entero._hash || tipoDato == Flotante._hash
                        || (tipoDato == Cadena._hash && !unario);

            case ECConstants.RESTA:
            case ECConstants.MULTIPLICACION:
            case ECConstants.DIVISION:
                return tipoDato == Entero._hash || tipoDato == Flotante._hash;
            case ECConstants.MODULO:
                return tipoDato == Entero._hash;

            case ECConstants.LOGICO_NOT:
            case ECConstants.LOGICO_OR:
            case ECConstants.LOGICO_AND:
                return tipoDato == Booleano._hash;

            case ECConstants.OPERADOR_IGUAL:
            case ECConstants.OPERADOR_DIFERENTE:
                return tipoDato != Vacio._hash && tipoDato != Indefinido._hash;

            case ECConstants.OPERADOR_MAYOR:
            case ECConstants.OPERADOR_MAYOR_IGUAL:
            case ECConstants.OPERADOR_MENOR:
            case ECConstants.OPERADOR_MENOR_IGUAL:
                return tipoDato != Vacio._hash
                        && tipoDato != Indefinido._hash
                        && tipoDato != Cadena._hash
                        && tipoDato != Booleano._hash;
        }

        return false;
    }

}
