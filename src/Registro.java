public final class Registro extends Simbolo {

    public static final Registro rax = new Registro("rax");
    public static final Registro rcx = new Registro("rcx");
    public static final Registro rdx = new Registro("rdx");
    public static final Registro r8 = new Registro("r8");
    public static final Registro r9 = new Registro("r9");

    public static final Registro eax = new Registro("eax", rax);
    public static final Registro ecx = new Registro("ecx", rcx);
    public static final Registro edx = new Registro("edx", rdx);
    public static final Registro r8d = new Registro("r8d", r8);
    public static final Registro r9d = new Registro("r9d", r9);

    public static final Registro al = new Registro("al", eax);
    public static final Registro cl = new Registro("cl", ecx);
    public static final Registro dl = new Registro("dl", edx);
    public static final Registro r8b = new Registro("r8b", r8d);
    public static final Registro r9b = new Registro("r9b", r9d);

    private final Registro _superior;

    private Registro(String nombre, Registro superior) {
        super(nombre, null);
        _superior = superior;
    }

    private Registro(String nombre) {
        this(nombre, null);

    }

    @Override
    public String getDeclaracion() {
        return "";
    }

    public Registro conTipoDato(TipoDato tipoDato) {

        _tipoDato = tipoDato;

        return this;
    }

    @Override
    public void usar() {

        if (_superior != null)
            _superior.usar();

        _disponible = false;
    }

    @Override
    public void liberar() {

        if (_superior != null)
            _superior.liberar();

        _disponible = true;
    }

    public Registro getUltimo() {

        if (_superior != null) {
            return _superior.getUltimo();
        }

        return this;
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

    @Override
    public boolean estaDisponible() {

        if (_superior != null) {
            return _superior.estaDisponible();
        }

        return _disponible;

    }

}
