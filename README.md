# CompiladorAutomatas

#Pasos para compilar
javacc EC.jj para generar los archivos .java

javac *.java para compilar todos los archivos .java y generar los archivos.class

java EC Ejemplos\ejemplo.txt para compilar algun txt de prueba y comprobar el funcionamiento del compilador



#Adicional

java ECV2 -arb Ejemplos\ejemplo.txt para generar un arbol del codigo fuente

java ECV2 -pil Ejemplos\ejemplo.txt para generar una pila semantica del codigo fuente

java ManejoSimbolos Ejemplos\ejemplo.txt para mostrar la informacion de la tabla de simbolos 

java ManejoErrores -mostrar errores.log para mostrar la informacion de los errores al compilar (con java EC Ejemplos\ejemplo.txt)



#Codigo intermedio

java OptimizacionCI EjemplosOptimizables\ejemploOP.txt para optimizar una variante del codigo fuente con variables numericas que se puedan optimizar

