import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class ExpresionBloque extends Expresion {

    private static int s_id = 0;

    private static int getNextId() {

        int id = s_id;

        ++s_id;

        return id;
    }

    private final int _id;
    private int _idCondicional;
    private int _idCiclo;

    private int _profundidad;
    private final ExpresionBloque _padre;

    private final TipoDato _tipoDato;

    private final ArrayList<Expresion> _cuerpo;
    private final HashMap<String, ExpresionSimbolo> _simbolos;

    private final TreeMap<Expresion, Integer> _ediciones;
    private final TreeSet<Expresion> _noOptimizar;

    /*
     * Ejemplo:
     * a = b + c * 3;
     * a2 = 3 * c + b;
     * 
     * se convierte en:
     * Key: Expresion(b + c * 3) | Expresion(c * 3 + b)
     * Valor: [Expresion(42), Expresion(a), %Key%]
     */
    private final TreeMap<Expresion, TreeSet<Expresion>> _expresiones;

    public ExpresionBloque() {
        this(0);
    }

    public ExpresionBloque(int profundidad) {
        this(TipoDato.Vacio);
        _profundidad = profundidad;
    }

    public ExpresionBloque(TipoDato tipoDato) {
        this(tipoDato, null, null);
    }

    public ExpresionBloque(ExpresionBloque padre) {
        this(TipoDato.Vacio, padre);
    }

    public ExpresionBloque(TipoDato tipoDato, ExpresionBloque padre) {
        this(tipoDato, padre, null);
    }

    public ExpresionBloque(ExpresionBloque padre, TreeSet<Expresion> noOptimizar) {
        this(TipoDato.Vacio, padre, noOptimizar);
    }

    public ExpresionBloque(TipoDato tipoDato, ExpresionBloque padre, TreeSet<Expresion> noOptimizar) {

        _idCondicional = 0;
        _idCiclo = 0;
        _id = getNextId();
        _padre = padre;
        _tipoDato = tipoDato;

        _cuerpo = new ArrayList<>();
        _ediciones = new TreeMap<>();

        if (padre != null) {
            _profundidad = padre._profundidad + 1;
            _simbolos = new HashMap<>(padre._simbolos);
        } else
            _simbolos = new HashMap<>();

        if (noOptimizar != null)
            _noOptimizar = noOptimizar;
        else
            _noOptimizar = new TreeSet<>();

        _expresiones = new TreeMap<>();
    }

    @Override
    public Expresion.Tipo getTipoExpresion() {
        return Expresion.Tipo.Bloque;
    }

    @Override
    public TipoDato getTipoDato() {
        return _tipoDato;
    }

    public int getProfundidad() {
        return _profundidad;
    }

    @Override
    public int getBloqueId() {
        return _id;
    }

    public boolean estaVacio() {
        return _cuerpo.isEmpty();
    }

    public List<Expresion> getCuerpo() {
        return Collections.unmodifiableList(_cuerpo);
    }

    private TreeMap<Expresion, TreeSet<Expresion>> getExpresiones(Expresion exp) {

        if (_padre == null)
            return _expresiones;

        var pExps = _padre.getExpresiones(exp);

        if (pExps.containsKey(exp)) {
            return pExps;
        }

        return _expresiones;
    }

    public Expresion getEquivalencia(Expresion exp) {

        if (exp == null)
            return Expresion.vacia();

        var expresiones = getExpresiones(exp);

        if (expresiones.containsKey(exp)) {

            var equivalencias = expresiones.get(exp);

            return equivalencias.getFirst();
        }

        TreeSet<Expresion> equivalencias = new TreeSet<>();
        equivalencias.add(exp);

        expresiones.put(exp, equivalencias);

        return exp;
    }

    public void setEquivalencia(Expresion exp, Expresion eq) {

        var expresiones = getExpresiones(exp);

        if (!expresiones.containsKey(exp))
            return;

        expresiones.get(exp)
                .add(eq);
    }

    public void quitarEquivalencia(Expresion exp, Expresion eq) {

        var expresiones = getExpresiones(exp);

        if (!expresiones.containsKey(exp))
            return;

        expresiones.get(exp)
                .remove(eq);
    }

    public void clearEquivalencias(Expresion exp) {

        var expresiones = getExpresiones(exp);

        if (!expresiones.containsKey(exp))
            return;

        var equivalencias = expresiones.get(exp);

        equivalencias.clear();
        equivalencias.add(exp);

        int count = _ediciones.getOrDefault(exp, 0);
        _ediciones.put(exp, count + 1);

        if (exp.getTipoExpresion().equals(Expresion.Tipo.Variable)) {

            var simbolo = (ExpresionSimbolo) exp;

            simbolo.setValor(Expresion.predeterminada(simbolo._tipoDato));
        }

    }

    public int getEdiciones(Expresion exp) {
        return _ediciones.getOrDefault(exp, 0);
    }

    public ExpresionConstante constante(Object valor, TipoDato tipoDato) {

        ExpresionConstante exp = new ExpresionConstante(valor, tipoDato);

        return (ExpresionConstante) getEquivalencia(exp);
    }

    public boolean existeSimbolo(String nombre) {
        return _simbolos.containsKey(nombre);
    }

    public ExpresionBloque bloque() {
        return new ExpresionBloque(this);
    }

    public ExpresionBloque bloque(TipoDato tipoDato) {
        return new ExpresionBloque(tipoDato, this);
    }

    public ExpresionBloque bloque(TipoDato tipoDato, TreeSet<Expresion> noOptimizar) {
        return new ExpresionBloque(tipoDato, this, noOptimizar);
    }

    public ExpresionBloque bloque(TreeSet<Expresion> noOptimizar) {
        return new ExpresionBloque(this, noOptimizar);
    }

    public ExpresionSimbolo variable(String nombre, TipoDato tipoDato) {

        if (existeSimbolo(nombre)) {
            return null;
        }

        var variable = new ExpresionSimbolo(_id, nombre, tipoDato);

        _simbolos.put(nombre, variable);
        getEquivalencia(variable);

        return variable;
    }

    public Expresion variable(String nombre) {

        if (existeSimbolo(nombre)) {

            var exp = getEquivalencia(_simbolos.get(nombre));

            return exp;
        }

        return null;
    }

    public ExpresionUnaria imprimir(Expresion exp) {

        if (exp == null)
            return null;

        // exp = optimizar(exp);

        return new ExpresionUnaria(_id, Expresion.Tipo.Imprimir, exp.getTipoDato(), exp);
    }

    public ExpresionUnaria interpretar(ExpresionSimbolo simbolo) {

        assert existeSimbolo(simbolo.getNombre());

        var leer = new ExpresionUnaria(_id, Expresion.Tipo.Leer, simbolo.getTipoDato(), simbolo);

        // Quitar cualquer equivalencia hecha ya que va a cambiar el valor de la
        // variable
        clearEquivalencias(simbolo);

        return leer;
    }

    public ExpresionBinaria asignacion(ExpresionSimbolo simbolo, Expresion exp) {

        if (simbolo == null || exp == null)
            return null;

        if (!existeSimbolo(simbolo.getNombre()))
            return null;

        if (!simbolo.getTipoDato().equals(exp.getTipoDato()))
            return null;

        // exp = optimizar(exp);

        var asignacion = new ExpresionBinaria(_id, Expresion.Tipo.Asignacion, simbolo.getTipoDato(), simbolo, exp);

        // Quitar cualquer equivalencia hecha ya que va a cambiar el valor de la
        // variable
        clearEquivalencias(simbolo);

        // setEquivalencia(simbolo, exp);

        simbolo.setValor(exp);
        // Propagación de constantes (Quitar para hacerla explicita)
        /*
         * if (exp.getTipoExpresion().equals(Expresion.Tipo.Constante)) {
         * 
         * setEquivalencia(simbolo, exp);
         * 
         * }
         */

        return asignacion;
    }

    public ExpresionSimbolo asignable(String nombre) {
        return _simbolos.getOrDefault(nombre, null);
    }

    public ExpresionDeclaracion declaracion(ExpresionSimbolo simbolo, ExpresionBinaria asignacion) {
        return new ExpresionDeclaracion(_id, simbolo, asignacion);
    }

    public Expresion not(Expresion operando) {

        if (!operando.getTipoDato().puedeUtilizar(ECConstants.LOGICO_NOT, true))
            return null;

        Expresion not = getEquivalencia(
                new ExpresionUnaria(_id, Expresion.Tipo.Not, TipoDato.Booleano, operando));

        // Optimización
        if (not.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return not;

        if (!operando.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return not;

        // Folding
        var cIzq = (ExpresionConstante) operando;

        Expresion optimizacion = null;

        if (operando.getTipoDato().equals(TipoDato.Booleano)) {

            boolean vIzq = cIzq.getValor();

            optimizacion = new ExpresionConstante(!vIzq, TipoDato.Booleano);
        }

        if (optimizacion != null) {

            setEquivalencia(not, optimizacion);

            return optimizacion;
        }

        return not;

    }

    public Expresion positivo(Expresion operando) {

        if (!operando.getTipoDato().puedeUtilizar(ECConstants.SUMA, true))
            return null;

        Expresion positivo = getEquivalencia(
                new ExpresionUnaria(_id, Expresion.Tipo.Positivo, operando.getTipoDato(), operando));

        // Optimización

        if (positivo.getTipoExpresion().equals(Expresion.Tipo.Positivo)) {

            setEquivalencia(positivo, operando);

            return operando;
        }

        return positivo;
    }

    public Expresion negativo(Expresion operando) {

        if (!operando.getTipoDato().puedeUtilizar(ECConstants.RESTA, true))
            return null;

        Expresion negativo = getEquivalencia(
                new ExpresionUnaria(_id, Expresion.Tipo.Negativo, operando.getTipoDato(), operando));

        // Optimización
        if (negativo.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return negativo;

        if (!operando.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return negativo;

        var cOprando = (ExpresionConstante) operando;
        Expresion optimizacion = null;

        if (cOprando.getTipoDato().equals(TipoDato.Entero)) {

            int vOperando = cOprando.getValor();

            optimizacion = new ExpresionConstante(-vOperando, operando.getTipoDato());

        } else if (operando.getTipoDato().equals(TipoDato.Flotante)) {

            double vOperando = cOprando.getValor();

            optimizacion = new ExpresionConstante(-vOperando, operando.getTipoDato());

        }

        if (optimizacion != null) {

            setEquivalencia(negativo, optimizacion);

            return optimizacion;
        }

        return negativo;
    }

    public Expresion multiplicacion(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var mult = getEquivalencia(
                new ExpresionBinaria(_id, Expresion.Tipo.Multiplicacion, izq.getTipoDato(), izq, der));

        // Optimización
        if (mult.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return mult;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return mult;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        if (cIzq.getTipoDato().equals(TipoDato.Entero)) {

            int vIzq = cIzq.getValor();
            int vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(vIzq * vDer, izq.getTipoDato());

        } else if (cIzq.getTipoDato().equals(TipoDato.Flotante)) {

            double vIzq = cIzq.getValor();
            double vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(vIzq * vDer, izq.getTipoDato());

        }

        if (optimizacion != null) {

            setEquivalencia(mult, optimizacion);

            return optimizacion;
        }

        return mult;
    }

    public Expresion division(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var div = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Division, izq.getTipoDato(), izq, der));

        // Optimización
        if (div.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return div;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return div;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        if (cIzq.getTipoDato().equals(TipoDato.Entero)) {

            int vIzq = cIzq.getValor();
            int vDer = cDer.getValor();

            if (vDer != 0)
                optimizacion = new ExpresionConstante(vIzq / vDer, izq.getTipoDato());

        } else if (cIzq.getTipoDato().equals(TipoDato.Flotante)) {

            double vIzq = cIzq.getValor();
            double vDer = cDer.getValor();

            if (vDer != 0)
                optimizacion = new ExpresionConstante(vIzq / vDer, izq.getTipoDato());

        }

        if (optimizacion != null) {

            setEquivalencia(div, optimizacion);

            return optimizacion;
        }

        return div;

    }

    public Expresion modulo(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var mod = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Modulo, izq.getTipoDato(), izq, der));

        // Optimización
        if (mod.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return mod;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return mod;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        if (cIzq.getTipoDato().equals(TipoDato.Entero)) {

            int vIzq = cIzq.getValor();
            int vDer = cDer.getValor();

            if (vDer != 0)
                optimizacion = new ExpresionConstante(vIzq % vDer, izq.getTipoDato());

        } else if (cIzq.getTipoDato().equals(TipoDato.Flotante)) {

            double vIzq = cIzq.getValor();
            double vDer = cDer.getValor();

            if (vDer != 0)
                optimizacion = new ExpresionConstante(vIzq % vDer, izq.getTipoDato());

        }

        if (optimizacion != null) {

            setEquivalencia(mod, optimizacion);

            return optimizacion;
        }

        return mod;

    }

    public Expresion suma(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var sum = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Suma, izq.getTipoDato(), izq, der));

        // Optimización
        if (sum.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return sum;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return sum;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        if (cIzq.getTipoDato().equals(TipoDato.Entero)) {

            int vIzq = cIzq.getValor();
            int vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(vIzq + vDer, izq.getTipoDato());

        } else if (cIzq.getTipoDato().equals(TipoDato.Flotante)) {

            double vIzq = cIzq.getValor();
            double vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(vIzq + vDer, izq.getTipoDato());

        } else if (cIzq.getTipoDato().equals(TipoDato.Cadena)) {

            String vIzq = cIzq.getValor();
            String vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(String.format("%s%s", vIzq, vDer), izq.getTipoDato());
        }

        if (optimizacion != null) {

            setEquivalencia(sum, optimizacion);

            return optimizacion;
        }

        return sum;
    }

    public Expresion resta(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var resta = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Resta, izq.getTipoDato(), izq, der));

        // Optimización
        if (resta.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return resta;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return resta;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        if (cIzq.getTipoDato().equals(TipoDato.Entero)) {

            int vIzq = cIzq.getValor();
            int vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(vIzq - vDer, izq.getTipoDato());

        } else if (cIzq.getTipoDato().equals(TipoDato.Flotante)) {

            double vIzq = cIzq.getValor();
            double vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(vIzq - vDer, izq.getTipoDato());

        }

        if (optimizacion != null) {

            setEquivalencia(resta, optimizacion);

            return optimizacion;
        }

        return resta;

    }

    public Expresion igual(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var igual = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Igual, TipoDato.Booleano, izq, der));

        // Optimización
        if (igual.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return igual;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return igual;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        var vIzq = cIzq.getValor();
        var vDer = cDer.getValor();

        optimizacion = new ExpresionConstante(vIzq.equals(vDer), TipoDato.Booleano);

        setEquivalencia(igual, optimizacion);

        return optimizacion;
    }

    public Expresion diferente(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var dif = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Diferente, TipoDato.Booleano, izq, der));

        // Optimización
        if (dif.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return dif;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return dif;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        var vIzq = cIzq.getValor();
        var vDer = cDer.getValor();

        optimizacion = new ExpresionConstante(!vIzq.equals(vDer), TipoDato.Booleano);

        setEquivalencia(dif, optimizacion);

        return optimizacion;
    }

    public Expresion mayorIgual(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var maI = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.MayorIgual, TipoDato.Booleano, izq, der));

        // Optimización
        if (maI.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return maI;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return maI;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        Comparable<Object> vIzq = cIzq.getValor();
        Comparable<Object> vDer = cDer.getValor();

        optimizacion = new ExpresionConstante(vIzq.compareTo(vDer) >= 0, TipoDato.Booleano);

        setEquivalencia(maI, optimizacion);

        return optimizacion;
    }

    public Expresion menorIgual(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var meI = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.MenorIgual, TipoDato.Booleano, izq, der));

        // Optimización
        if (meI.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return meI;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return meI;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        Comparable<Object> vIzq = cIzq.getValor();
        Comparable<Object> vDer = cDer.getValor();

        optimizacion = new ExpresionConstante(vIzq.compareTo(vDer) <= 0, TipoDato.Booleano);

        setEquivalencia(meI, optimizacion);

        return optimizacion;
    }

    public Expresion mayor(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var ma = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Mayor, TipoDato.Booleano, izq, der));

        // Optimización
        if (ma.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return ma;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return ma;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        Comparable<Object> vIzq = cIzq.getValor();
        Comparable<Object> vDer = cDer.getValor();

        optimizacion = new ExpresionConstante(vIzq.compareTo(vDer) > 0, TipoDato.Booleano);

        setEquivalencia(ma, optimizacion);

        return optimizacion;
    }

    public Expresion menor(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var me = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Menor, TipoDato.Booleano, izq, der));

        // Optimización
        if (me.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return me;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return me;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        Comparable<Object> vIzq = cIzq.getValor();
        Comparable<Object> vDer = cDer.getValor();

        optimizacion = new ExpresionConstante(vIzq.compareTo(vDer) < 0, TipoDato.Booleano);

        setEquivalencia(me, optimizacion);

        return optimizacion;
    }

    public Expresion and(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var and = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.And, izq.getTipoDato(), izq, der));

        // Optimización
        if (and.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return and;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return and;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        if (izq.getTipoDato().equals(TipoDato.Booleano)) {

            boolean vIzq = cIzq.getValor();
            boolean vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(vIzq && vDer, TipoDato.Booleano);
        }

        if (optimizacion != null) {

            setEquivalencia(and, optimizacion);

            return optimizacion;
        }

        return and;
    }

    public Expresion or(Expresion izq, Expresion der) {

        if (izq == null && der == null)
            return null;

        if (der == null)
            return getEquivalencia(izq);

        if (izq == null)
            return getEquivalencia(der);

        if (!izq.getTipoDato().equals(der.getTipoDato()))
            return null;

        var or = getEquivalencia(new ExpresionBinaria(_id, Expresion.Tipo.Or, izq.getTipoDato(), izq, der));

        // Optimización
        if (or.getTipoExpresion().equals(Expresion.Tipo.Constante))
            return or;

        if (!(izq.getTipoExpresion().equals(Expresion.Tipo.Constante)
                && der.getTipoExpresion().equals(Expresion.Tipo.Constante)))
            return or;

        // Folding
        var cIzq = (ExpresionConstante) izq;
        var cDer = (ExpresionConstante) der;

        Expresion optimizacion = null;

        if (izq.getTipoDato().equals(TipoDato.Booleano)) {

            boolean vIzq = cIzq.getValor();
            boolean vDer = cDer.getValor();

            optimizacion = new ExpresionConstante(vIzq || vDer, TipoDato.Booleano);
        }

        if (optimizacion != null) {

            setEquivalencia(or, optimizacion);

            return optimizacion;
        }

        return or;
    }

    public Expresion condicional(Expresion condicion, ExpresionBloque bloqueVerdadero, ExpresionBloque bloqueFalso) {

        if (condicion == null)
            return null;

        if (!condicion.getTipoDato().equals(TipoDato.Booleano))
            return null;

        if (condicion.getTipoExpresion().equals(Expresion.Tipo.Constante)) {

            var cCondicion = (ExpresionConstante) condicion;

            boolean test = cCondicion.getValor();

            if (test)
                return bloqueVerdadero;

            return bloqueFalso;
        }

        return new ExpresionCondicional(_idCondicional++, _id, condicion, bloqueVerdadero, bloqueFalso);
    }

    public Expresion condicional(Expresion condicion, ExpresionBloque bloqueVerdadero) {

        if (!condicion.getTipoDato().equals(TipoDato.Booleano))
            return null;

        if (condicion.getTipoExpresion().equals(Expresion.Tipo.Constante)) {

            var cCondicion = (ExpresionConstante) condicion;

            boolean test = cCondicion.getValor();

            if (test)
                return bloqueVerdadero;

            return Expresion.vacia();
        }

        return new ExpresionCondicional(_idCondicional++, _id, condicion, bloqueVerdadero);
    }

    public Expresion cicloMientras(Expresion condicion, ExpresionBloque cuerpo) {

        if (condicion == null)
            return null;

        if (cuerpo == null || cuerpo.estaVacio())
            return Expresion.vacia();

        if (!condicion.getTipoDato().equals(TipoDato.Booleano))
            return null;

        if (condicion.getTipoExpresion().equals(Expresion.Tipo.Constante)) {

            var cCondicion = (ExpresionConstante) condicion;

            boolean test = cCondicion.getValor();

            if (!test)
                return Expresion.vacia();
        }

        return new ExpresionCicloMientras(_idCiclo++, _id, condicion, cuerpo);
    }

    /*
     * public Expresion optimizar(Expresion exp) {
     * 
     * if (exp == null)
     * return null;
     * 
     * // Hacer diferentes optimizaciones
     * ExpresionCicloMientras cicloMientras;
     * ExpresionCondicional condicional;
     * ExpresionBloque bloque1;
     * ExpresionBloque bloque2;
     * 
     * ExpresionSimbolo simbolo;
     * ExpresionBinaria expBin;
     * ExpresionUnaria expUn;
     * Expresion oIzq;
     * Expresion oDer;
     * 
     * // Propagación de constantes
     * 
     * switch (exp.getTipoExpresion()) {
     * 
     * case CicloMientras:
     * cicloMientras = (ExpresionCicloMientras) exp;
     * 
     * bloque1 = (ExpresionBloque) optimizar(cicloMientras.getCuerpo());
     * oIzq = bloque1.optimizar(cicloMientras.getCondicion());
     * 
     * return cicloMientras(oIzq, bloque1);
     * 
     * case Condicional:
     * condicional = (ExpresionCondicional) exp;
     * 
     * oIzq = optimizar(condicional.getCondicion());
     * bloque1 = (ExpresionBloque) optimizar(condicional.getBloqueVerdadero());
     * bloque2 = (ExpresionBloque) optimizar(condicional.getBloqueFalso());
     * 
     * return condicional(oIzq, bloque1, bloque2);
     * 
     * case Bloque:
     * 
     * bloque1 = (ExpresionBloque) exp;
     * 
     * // Revisar
     * var cuerpo = bloque1._cuerpo;
     * for (int i = 0; i < cuerpo.size(); ++i) {
     * 
     * var it = cuerpo.get(i);
     * cuerpo.set(i, bloque1.optimizar(it));
     * }
     * 
     * return bloque1;
     * 
     * case Variable:
     * 
     * simbolo = (ExpresionSimbolo) exp;
     * 
     * if (_noOptimizar.contains(simbolo)) {
     * 
     * if (getEdiciones(simbolo) > 0) {
     * return simbolo;
     * }
     * 
     * }
     * 
     * var equivalencia = simbolo._valor;
     * System.out.printf("Equivalencia: %s\n", equivalencia);
     * 
     * if (!equivalencia.getTipoExpresion().equals(Expresion.Tipo.Variable)) {
     * 
     * var optimizacion = optimizar(equivalencia); // Problema
     * 
     * if (optimizacion.getTipoExpresion().equals(Expresion.Tipo.Constante))
     * return optimizacion;
     * 
     * }
     * 
     * break;
     * 
     * // ExpresionBinaria
     * case Asignacion:
     * 
     * expBin = (ExpresionBinaria) exp;
     * 
     * simbolo = (ExpresionSimbolo) expBin.getIzquierda();
     * oDer = optimizar(expBin.getDerecha());
     * 
     * if (simbolo._nombre == "a") {
     * 
     * System.out.println(oDer);
     * 
     * }
     * 
     * return asignacion(simbolo, oDer);
     * 
     * case Multiplicacion:
     * 
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return multiplicacion(oIzq, oDer);
     * case Division:
     * 
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return division(oIzq, oDer);
     * case Modulo:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return modulo(oIzq, oDer);
     * case Suma:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return suma(oIzq, oDer);
     * case Resta:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return resta(oIzq, oDer);
     * case Igual:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return igual(oIzq, oDer);
     * case Diferente:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return diferente(oIzq, oDer);
     * case MayorIgual:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return mayorIgual(oIzq, oDer);
     * case MenorIgual:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return menorIgual(oIzq, oDer);
     * case Mayor:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return mayor(oIzq, oDer);
     * case Menor:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return menor(oIzq, oDer);
     * case And:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return and(oIzq, oDer);
     * case Or:
     * expBin = (ExpresionBinaria) exp;
     * 
     * oIzq = optimizar(expBin.getIzquierda());
     * oDer = optimizar(expBin.getDerecha());
     * 
     * return or(oIzq, oDer);
     * 
     * case Imprimir:
     * expUn = (ExpresionUnaria) exp;
     * 
     * oDer = optimizar(expUn.getOperando());
     * 
     * return imprimir(oDer);
     * case Positivo:
     * expUn = (ExpresionUnaria) exp;
     * 
     * oDer = optimizar(expUn.getOperando());
     * 
     * return positivo(oDer);
     * case Negativo:
     * expUn = (ExpresionUnaria) exp;
     * 
     * oDer = optimizar(expUn.getOperando());
     * 
     * return negativo(oDer);
     * case Not:
     * expUn = (ExpresionUnaria) exp;
     * 
     * oDer = optimizar(expUn.getOperando());
     * 
     * return not(oDer);
     * 
     * }
     * 
     * return exp;
     * }
     */

    private void extraerSimbolos(Expresion exp, TreeSet<Expresion> simbolos) {

        switch (exp.getTipoExpresion()) {
            case Variable:
                var simbolo = (ExpresionSimbolo) exp;

                simbolos.add(simbolo);
                break;
            // ExpresionBinaria
            case Multiplicacion:
            case Division:
            case Modulo:
            case Suma:
            case Resta:
            case Igual:
            case Diferente:
            case MayorIgual:
            case MenorIgual:
            case Mayor:
            case Menor:
            case And:
            case Or:
                var expBin = (ExpresionBinaria) exp;

                extraerSimbolos(expBin.getIzquierda(), simbolos);
                extraerSimbolos(expBin.getDerecha(), simbolos);
                break;
            // ExpresionUnaria
            case Positivo:
            case Negativo:
            case Not:
                var expUn = (ExpresionUnaria) exp;

                extraerSimbolos(expUn.getOperando(), simbolos);
        }

    }

    public TreeSet<Expresion> getSimbolos(Expresion exp) {

        TreeSet<Expresion> simbolos = new TreeSet<>();

        if (exp == null)
            return simbolos;

        extraerSimbolos(exp, simbolos);

        return simbolos;
    }

    public ExpresionBloque addExpresion(Expresion exp) {

        _cuerpo.add(getEquivalencia(exp));

        return this;
    }

    public ExpresionBloque addExpresiones(Expresion... exps) {

        var stream = Arrays.stream(exps);
        var equivalencias = stream.map(this::getEquivalencia).toList();
        stream.close();

        _cuerpo.addAll(equivalencias);
        return this;
    }

    @Override
    protected int compare(Expresion o) {

        ExpresionBloque eb = (ExpresionBloque) o;

        return compareCuerpo(eb._cuerpo);
    }

    private int compareCuerpo(List<Expresion> cuerpo) {

        if (_cuerpo == cuerpo)
            return 0;

        int otherSize = cuerpo.size();
        int thisSize = _cuerpo.size();

        if (thisSize < otherSize)
            return -1;

        if (thisSize > otherSize)
            return 1;

        int compare = 0;
        for (int i = 0; i < thisSize; ++i) {

            compare = _cuerpo.get(i).compareTo(cuerpo.get(i));

            if (compare != 0)
                return compare;

        }

        return compare;
    }

    @Override
    public void addTo(ExpresionBloque bloque) {
        bloque.addExpresion(this);
    }

    @Override
    public void toAssambly(Assambly assambly) {

        _cuerpo.forEach(e -> e.toAssambly(assambly));

        Set<String> sim;
        var keys = _simbolos.keySet();

        if (_padre != null) {
            var padreKeys = _padre._simbolos.keySet();
            sim = new HashSet<>(keys);
            sim.removeAll(padreKeys);
        } else {
            sim = keys;
        }

        sim.forEach(s -> liberarSimbolos(s, assambly));
    }

    private void liberarSimbolos(String nombre, Assambly assambly) {

        /*
         * var mem = assambly.getMemoria(nombre);
         * 
         * if (mem != null)
         * mem.liberar();
         * 
         */

    }

    @Override
    protected void mostrar(StringBuilder sb, int presedencia) {

        if (_cuerpo.size() == 0)
            return;

        if (_profundidad != 0)
            sb.append('{').append('\n');

        for (var exp : _cuerpo) {

            if (_profundidad != 0)
                sb.append("    ".repeat(_profundidad));

            exp.mostrar(sb, 0);
            sb.append('\n');
        }

        if (_profundidad != 0)
            sb.append("    ".repeat(_profundidad - 1)).append('}');

    }

}
