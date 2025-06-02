import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Section {

    private final String _nombre;
    private final ArrayList<String> _contenido;

    public Section(String nombre) {

        _nombre = nombre;
        _contenido = new ArrayList<>();

    }

    public Section addInstruccion(String instruccion) {
        _contenido.add(instruccion);

        return this;
    }

    public Section addInstruccion(String format, Object... args) {
        _contenido.add(String.format(format, args));

        return this;
    }

    public String getNombre() {
        return _nombre;
    }

    public List<String> getContenido() {
        return Collections.unmodifiableList(_contenido);
    }

    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();

        sb.append("\nsection ").append(_nombre);

        for (var it : _contenido) {
            sb.append('\n').append("    ").append(it);
        }

        sb.append('\n');

        return sb.toString();
    }

}
