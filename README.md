# EC (EasyCompiler)

EC es un compilador para el lenguaje de programación *ELL*, creado con **JavaCC**.

**Solo funciona en Windows 64 bits.**

## Software utilizado
- [Java 22.0.1](https://www.oracle.com/mx/java/technologies/downloads/)
- [Javacc](https://javacc.github.io/javacc/)
- [Nasm 2.16.03](https://www.nasm.us/)
- [GoLink](http://www.godevtool.com/)


## Pasos para compilar el compilador

- Abrir una **terminal**.
- Ejecutar los siguientes comandos:
```console
$ ./setup.bat
```

```console
$ javac *.java
```

## Pasos para compilar archivo fuente
- Abrir una **terminal**.
- Ejecutar el siguientes comandos:
```console
$ java EC <codigo_fuente>
```
Los archivos resultantes se van a crear en el directorio "out".
