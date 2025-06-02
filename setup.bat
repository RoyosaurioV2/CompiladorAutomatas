@echo off

set "source_folder=%cd%\src"
set "target_folder=%cd%"

copy "%target_folder%\Assambly.java" "%source_folder%\" /y
copy "%target_folder%\Section.java" "%source_folder%\" /y

copy "%target_folder%\Simbolo.java" "%source_folder%\" /y
copy "%target_folder%\Variable.java" "%source_folder%\" /y
copy "%target_folder%\VariableTemporal.java" "%source_folder%\" /y
copy "%target_folder%\Etiqueta.java" "%source_folder%\" /y
copy "%target_folder%\Registro.java" "%source_folder%\" /y
copy "%target_folder%\Constante.java" "%source_folder%\" /y

copy "%target_folder%\Compilador.java" "%source_folder%\" /y
copy "%target_folder%\Reporte.java" "%source_folder%\" /y
copy "%target_folder%\ECError.java" "%source_folder%\" /y
copy "%target_folder%\TipoDato.java" "%source_folder%\" /y
copy "%target_folder%\Expresion.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionPredeterminada.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionUnaria.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionBinaria.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionDeclaracion.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionCondicional.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionCicloMientras.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionIndice.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionArreglo.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionFuncion.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionConstante.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionSimbolo.java" "%source_folder%\" /y
copy "%target_folder%\ExpresionBloque.java" "%source_folder%\" /y

del /q "%target_folder%\*.java"
del /q "%target_folder%\*.class"

xcopy "%source_folder%\*.java" "%target_folder%\" /y

javacc "%target_folder%\EC.jj"