public class VariableTemporal extends Variable {

    public VariableTemporal(String nombre, TipoDato tipoDato) {
        super(nombre, tipoDato);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void liberar() {
        _disponible = true;
    }

    @Override
    public void usar() {
        _disponible = false;
    }

}
