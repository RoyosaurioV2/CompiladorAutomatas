import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class Compilador {

    private final String FASE = "Código objeto";

    private final File _archivo;
    private final Reporte _reporte;

    private final ArrayList<ECError> _errores;

    public final ExpresionBloque bloquePrincipal;

    public Compilador(String archivo) {

        bloquePrincipal = new ExpresionBloque(TipoDato.Vacio);
        _errores = new ArrayList<>();

        _reporte = new Reporte();

        _reporte.addLinea("Fase: %s", FASE);

        if (archivo != null && !archivo.isBlank())
            _archivo = new File(archivo);
        else
            _archivo = null;

    }

    public Reporte ejecutar() {

        if (_archivo == null || !(_archivo.isFile() && _archivo.canWrite())) {

            return _reporte.addLinea("Archivo: Desconocido")
                    .addSalto()
                    .addLinea("ERROR: Debe de ingresar el archivo del código fuente.")
                    .conError(true);

        }

        try {

            FileInputStream stream = new FileInputStream(_archivo);

            EC ec = new EC(this, stream);

            ec.programa(bloquePrincipal);

            if (!_errores.isEmpty()) {

                int nErrores = _errores.size();

                _reporte.conError(true)
                        .addLinea("Se %s %d %s durante la compilación: ",
                                nErrores == 1 ? "encontró" : "encontraron",
                                nErrores, nErrores == 1 ? "error" : "errores")
                        .addSalto();

                _errores.sort((x, y) -> x.compareTo(y));
                for (ECError error : _errores) {

                    _reporte.addLinea(error.toString())
                            .addSalto();

                }

            } else {

                Assambly asm = new Assambly();
                bloquePrincipal.toAssambly(asm);

                Path binDir = Paths.get("out");
                var archivoAsm = binDir
                        .resolve(String.format("%s.asm", _archivo.getName().replaceFirst("[.][^.]+$", "")));
                var archivoObj = binDir
                        .resolve(String.format("%s.obj", _archivo.getName().replaceFirst("[.][^.]+$", "")));
                var archivoExe = binDir
                        .resolve(String.format("%s.exe", _archivo.getName().replaceFirst("[.][^.]+$", "")));

                _reporte.addSalto();

                if (!Files.exists(binDir)) {
                    try {
                        Files.createDirectories(binDir);
                    } catch (IOException e) {

                        return _reporte.limpiar()
                                .addLinea("Ha ocurrido un error inesperado, intenta de nuevo.")
                                .conError(true);
                    }
                }

                try (FileWriter fw = new FileWriter(archivoAsm.toFile())) {

                    fw.append(asm.toString());

                } catch (Exception ex) {

                    return _reporte.limpiar()
                            .addLinea("Ha ocurrido un error inesperado, no se pudo generar el archivo '%s'.",
                                    archivoAsm.toFile())
                            .conError(true);

                }

                _reporte.addLinea("Archivo ensamblador: '%s'", archivoAsm.toFile());

                // nasm/nasm.exe -f win64 archivoAsm -o archivoObj
                var pb = new ProcessBuilder("nasm/nasm.exe",
                        "-f", "win64", archivoAsm.toString(),
                        "-o", archivoObj.toString());

                try {

                    var process = pb.start();

                    int exitCode = process.waitFor();

                    if (exitCode != 0) {

                        return _reporte.limpiar()
                                .addLinea("BUG!!: No se ha podido generar archivo objeto.")
                                .conError(true);

                    }

                    process.destroy();

                } catch (IOException e) {

                    return _reporte.limpiar()
                            .addLinea("Ha ocurrido un error inesperado, intenta de nuevo.")
                            .conError(true);

                } catch (InterruptedException e) {
                    return _reporte.addLinea("Se ha interrumpido la compilación.");
                }
                _reporte.addLinea("Archivo objeto: '%s'", archivoObj.toFile());

                // Golink/GoLink.exe /console archivoObj kernel32.dll msvcrt.dll /entry:main /fo
                // archivoExe
                pb = new ProcessBuilder("Golink/GoLink.exe",
                        "/console", archivoObj.toString(),
                        "kernel32.dll", "msvcrt.dll",
                        "/entry:main",
                        "/fo", archivoExe.toString());

                try {

                    var process = pb.start();

                    int exitCode = process.waitFor();

                    if (exitCode != 0) {

                        return _reporte.limpiar()
                                .addLinea("BUG!!: No se ha podido generar archivo exe.")
                                .conError(true);

                    }

                    process.destroy();

                } catch (IOException e) {

                    return _reporte.limpiar()
                            .addLinea("Ha ocurrido un error inesperado, intenta de nuevo.")
                            .conError(true);

                } catch (InterruptedException e) {
                    return _reporte.addLinea("Se ha interrumpido la compilación.");
                }

                _reporte.addLinea("Archivo exe: '%s'", archivoExe.toFile())
                        .addLinea("El archivo '%s' se compiló con éxito.", _archivo);
            }

        } catch (FileNotFoundException ex) {

            _reporte.addLinea("Archivo: Desconocido")
                    .addSalto()
                    .addLinea("ERROR: No se encontró el archivo.")
                    .conError(true);

        } catch (ParseException ex) {

            _reporte.limpiar()
                    .addLinea("Ha ocurrido un error inesperado, intente de nuevo.")
                    .conError(true);
        }

        return _reporte;
    }

    public void addError(ECError error) {
        _errores.add(error);
    }

}
