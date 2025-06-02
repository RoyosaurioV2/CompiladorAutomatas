import java.util.ArrayList;

public final class Reporte {

    private int _maxLineaLength;

    private boolean _esError;
    private ArrayList<String> _lineas;

    public Reporte() {
        _esError = false;
        _maxLineaLength = 0;
        _lineas = new ArrayList<>();
    }

    public Reporte addLinea(String linea) {

        if (linea == null || linea.isBlank())
            return this;

        String[] split = linea.replace("\t", "    ").split("\n");

        for (String l : split) {

            _maxLineaLength = Math.max(_maxLineaLength, l.length());

            _lineas.add(l);

        }

        return this;
    }

    public Reporte addLinea(String formato, Object... args) {

        return addLinea(String.format(formato, args));

    }

    public Reporte addSalto() {

        _lineas.add("");

        return this;
    }

    public Reporte limpiar() {
        _lineas.clear();
        return this;
    }

    public boolean esError() {
        return _esError;
    }

    public Reporte conError(boolean esError) {
        _esError = esError;

        return this;
    }

    public int getMaxLineaLength() {
        return _maxLineaLength;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        // Top border
        sb.append('╔');
        for (int i = 0; i < _maxLineaLength + 2; i++) {
            sb.append('═');
        }
        sb.append('╗').append('\n');

        // Content with side borders
        for (String linea : _lineas) {
            sb.append(String.format("║ %-" + _maxLineaLength + "s ║", linea)).append('\n');
        }

        // Bottom border
        sb.append('╚');
        for (int i = 0; i < _maxLineaLength + 2; i++) {
            sb.append('═');
        }
        sb.append('╝');

        return sb.toString();
    }
}
