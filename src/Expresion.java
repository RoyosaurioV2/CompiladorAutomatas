public abstract class Expresion implements Comparable<Expresion> {

    public enum Tipo {

        // ExpresionConstante
        Constante,

        // ExpresionSimbolo
        Variable,

        // ExpresionDeclaracion
        Declaracion,

        // ExpresionBinaria
        Asignacion,

        // ExpresionUnaria
        Leer,
        Imprimir,

        Positivo,
        Negativo,
        Not,

        // ExpresionBinaria
        Multiplicacion,
        Division,
        Modulo,
        Suma,
        Resta,

        Igual,
        Diferente,
        MayorIgual,
        MenorIgual,
        Mayor,
        Menor,

        And,
        Or,

        // ExpresionCondicional
        Condicional,

        // ExpresionCicloMientras
        CicloMientras,

        // ELIMINADO
        DefinicionArreglo,
        AccesoArreglo,
        DefinicionFuncion,
        LlamadaFuncion,
        Indice,

        // ExpresionBloque
        Bloque,

        // ExpresionPredeterminada
        Predeterminado
    }

    private static final ExpresionPredeterminada _vacia = new ExpresionPredeterminada(TipoDato.Vacio);

    public static ExpresionPredeterminada vacia() {
        return _vacia;
    }

    public static ExpresionPredeterminada predeterminada(TipoDato tipoDato) {
        return new ExpresionPredeterminada(tipoDato);
    }

    public abstract void toAssambly(Assambly assambly);

    public abstract int getBloqueId();

    // protected boolean _optimizado;

    // Las optimizaciones que afectar el funcionamiento de al menos una sentencia
    // hacerlas explicitas.
    /*
     * public Expresion optimizar(ExpresionBloque bloque) {
     * 
     * if (_optimizado)
     * return this;
     * 
     * var optimizacion = optimiza(bloque);
     * _optimizado = true;
     * 
     * return optimizacion;
     * }
     */

    // Optimizaciones que pueden afectar otras Expresiones (Ej. Propagación de
    // contantes afecta a los ciclos
    // si el simbolo es editado en el ciclo)
    // protected abstract Expresion optimiza(ExpresionBloque bloque);

    public abstract Tipo getTipoExpresion();

    public abstract TipoDato getTipoDato();

    public abstract void addTo(ExpresionBloque bloque);

    public String toString() {

        StringBuilder sb = new StringBuilder();
        mostrar(sb, 0);
        return sb.toString();
    }

    protected abstract void mostrar(StringBuilder sb, int presedencia);

    public int compareTo(Expresion o) {

        if (this == o)
            return 0;

        int compare = getTipoExpresion().compareTo(o.getTipoExpresion());

        if (compare != 0)
            return compare;

        compare = getTipoDato().compareTo(o.getTipoDato());

        if (compare != 0)
            return compare;

        return compare(o);
    }

    protected abstract int compare(Expresion o);

}
