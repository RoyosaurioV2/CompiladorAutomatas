; Juntar todos los codigos asm aqui y luego hacer las operaciones relacionales
; entre tipos de dato
section .data
    salto_linea db 0xA, 0

    formato_bool db '%s', 0
    formato_cadena db '%s', 0
    formato_char db '%c', 0
    formato_int db '%d', 0
    formato_float db '%lf', 0

    booleano_verdadero db 'Verdadero', 0
    booleano_falso db 'Falso', 0

    verdadero db 0xFF
    falso db 0x00

    error_bool db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se ley', 0xA2,' "%s" y', 0xA, '    se esperaba Verdadero o Falso.', 0xA, 0

    error_scanf db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se esperaba un "%s" en la lectura.', 0xA, 0

    error_div db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, no se puede dividir por 0.', 0xA, 0

    entero1 db 'Entero 1: ', 0
    entero2 db 'Entero 2: ', 0

    flotante1 db 'Flotante 1: ', 0
    flotante2 db 'Flotante 2: ', 0

    caracter1 db 'Caracter 1: ', 0
    caracter2 db 'Caracter 2: ', 0

    cadena1 db 'Cadena 1: ', 0
    cadena2 db 'Cadena 2: ', 0

    booleano1 db 'Booleano 1: ', 0
    booleano2 db 'Booleano 2: ', 0

    eTitulo db '+---------------- Enteros ----------------+', 0xA, 0
    fTitulo db '+---------------- Flotante ----------------+', 0xA, 0
    cTitulo db '+---------------- Caracter ----------------+', 0xA, 0
    sTitulo db '+---------------- Cadena ----------------+', 0xA, 0
    bTitulo db '+---------------- Booleano ----------------+', 0xA, 0

    suma db 'Suma: ', 0
    resta db 'Resta: ', 0
    multi db 'Multiplicaci', 0xA2, 'n: ', 0
    divi db 'Divisi', 0xA2, 'n: ', 0
    mod db 'Modulo: ', 0
    nega db 'Negativo: ', 0
    
    cat db 'Concatenaci', 0xA2, 'n: ', 0
    
    bAnd db 'And: ', 0
    bOr db 'Or: ', 0
    bNot db 'Not: ', 0

    ; Todos tienen estos operadores
    igual db "Valor1 == Valor2: ", 0
    diferente db "Valor1 != Valor2: ", 0

    ; Solo enteros, flotantes, caracteres tienen estos operadores
    mayor db "Valor1 > Valor2: ", 0
    mayorIgual db "Valor1 >= Valor2: ", 0
    menor db "Valor1 < Valor2: ", 0
    menorIgual db "Valor1 <= Valor2: ", 0

    testStr db 'a', 0

; Delarar variables
; @%idBloque%_%nombre%:
;   .tipoDato db %tipoDato%, 0
;   .valor %tamaño% %valor%
;   .formato db %formato%, 0

@0_e1:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000
    .formato db '%d', 0

@0_e2:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000
    .formato db '%d', 0

@0_f1:
    .tipoDato db 'Flotante', 0
    .valor dq 0x0000000000000000
    .formato db '%lf', 0

@0_f2:
    .tipoDato db 'Flotante', 0
    .valor dq 0x0000000000000000
    .formato db '%lf', 0

@0_c1:
    .tipoDato db 'Caracter', 0
    .valor db 0x00
    .formato db '%c', 0

@0_c2:
    .tipoDato db 'Caracter', 0
    .valor db 0x00
    .formato db '%c', 0

@0_s1:
    .tipoDato db 'Cadena', 0
    .valor resb 1024
    .formato db '%[^', 0xA, ']', 0

@0_s2:
    .tipoDato db 'Cadena', 0
    .valor resb 1024
    .formato db '%[^', 0xA, ']', 0

@0_b1:
    .tipoDato db 'Booleano', 0
    .valor db 0x00
    .formato db '%s', 0

@0_b2:
    .tipoDato db 'Booleano', 0
    .valor db 0x00
    .formato db '%s', 0

; Declarar variables temporales
; t%consecutivo%:
;   .tipoDato db %tipoDato%, 0
;   .valor %tamaño% %valor%
;   .formato db %formato%, 0
t1:
    .tipoDato db 'Entero', 0
    .valor dd 0x00
    .formato db '%d', 0

t2:
    .tipoDato db 'Entero', 0
    .valor dd 0x00
    .formato db '%d', 0

t3:
    .tipoDato db 'Flotante', 0
    .valor dq 0x00
    .formato db '%lf', 0

t4:
    .tipoDato db 'Cadena', 0
    .valor resb 1024
    .formato db '%[^', 0xA, ']', 0

t5:
    .tipoDato db 'Caracter', 0
    .valor db 0x00
    .formato db '%c', 0

t6:
    .tipoDato db 'Booleano', 0
    .valor db 0x00
    .formato db '%s', 0

section .text
    extern printf, scanf, exit, strcmp, strcpy, strcat, _flushall

; Leer variable
;  call _flushall
;  mov rcx, %variable%.formato
;  mov rdx, %variable%.valor
;  sub rsp, 32
;  xor rax, rax
;  call scanf
;  sub rsp, 32

;  mov rcx, rax
;  mov rdx, %variable%.tipoDato
;  call check_scanf

main:

    push rbp
    mov rbp, rsp

    ; codigo

    ; +---------------- Enteros ----------------+

    mov rcx, eTitulo
    call imprimir_cadena

    mov rcx, entero1
    call imprimir_cadena

    call _flushall
    mov rcx, @0_e1.formato
    mov rdx, @0_e1.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_e1.tipoDato
    call check_scanf

    mov rcx, entero2
    call imprimir_cadena

    call _flushall
    mov rcx, @0_e2.formato
    mov rdx, @0_e2.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_e2.tipoDato
    call check_scanf

    mov rcx, salto_linea
    call imprimir_cadena

    ; Negativo
    mov rcx, nega
    call imprimir_cadena

    mov ecx, [@0_e1.valor]
    call entero_negativo

    mov dword [t1.valor], eax

    mov ecx, [t1.valor]
    call imprimir_entero

    mov rcx, salto_linea
    call imprimir_cadena

    ; Suma
    mov rcx, suma
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call suma_entera

    mov dword [t1.valor], eax

    mov ecx, [t1.valor]
    call imprimir_entero

    mov rcx, salto_linea
    call imprimir_cadena

    ; Resta
    mov rcx, resta
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call resta_entera

    mov dword [t1.valor], eax

    mov ecx, [t1.valor]
    call imprimir_entero

    mov rcx, salto_linea
    call imprimir_cadena

    ; Multiplicación
    mov rcx, multi
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call multiplicacion_entera

    mov dword [t1.valor], eax

    mov ecx, [t1.valor]
    call imprimir_entero

    mov rcx, salto_linea
    call imprimir_cadena

    ; División
    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call division_modulo_entera
    
    mov dword [t1.valor], eax ; Cociente
    mov dword [t2.valor], edx ; Reciduo

    mov rcx, divi
    call imprimir_cadena

    mov ecx, [t1.valor]
    call imprimir_entero
    
    mov rcx, salto_linea
    call imprimir_cadena

    ; Modulo
    mov rcx, mod
    call imprimir_cadena
    
    mov ecx, [t2.valor]
    call imprimir_entero
    
    mov rcx, salto_linea
    call imprimir_cadena

    ; Igual
    mov rcx, igual
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call igualdad_entera

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Diferente
    mov rcx, diferente
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call diferencia_entera

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Mayor
    mov rcx, mayor
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call mayor_entero

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; MayorIgual
    mov rcx, mayorIgual
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call mayor_igual_entero

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Menor
    mov rcx, menor
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call menor_entero

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; MenorIgual
    mov rcx, menorIgual
    call imprimir_cadena

    mov ecx, [@0_e1.valor] ; Guardar operacion izquierda en eax
    mov edx, [@0_e2.valor] ; Guardar operacion derecha en ecx
    call menor_igual_entero

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; +---------------- Flotante ----------------+

    mov rcx, fTitulo
    call imprimir_cadena

    mov rcx, flotante1
    call imprimir_cadena

    call _flushall
    mov rcx, @0_f1.formato
    mov rdx, @0_f1.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_f1.tipoDato
    call check_scanf

    mov rcx, flotante2
    call imprimir_cadena

    call _flushall
    mov rcx, @0_f2.formato
    mov rdx, @0_f2.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_f2.tipoDato
    call check_scanf

    mov rcx, salto_linea
    call imprimir_cadena

    ; Negativo
    mov rcx, nega
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operando en xmm0
    call flotante_negativo

    mov qword [t3.valor], rax

    mov rcx, [t3.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; Suma
    mov rcx, suma
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call suma_flotante

    mov qword [t3.valor], rax

    mov rcx, [t3.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; Resta
    mov rcx, resta
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call resta_flotante

    mov qword [t3.valor], rax

    mov rcx, [t3.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; Multiplicación
    mov rcx, multi
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call multiplicacion_flotante

    mov qword [t3.valor], rax

    mov rcx, [t3.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; División
    mov rcx, divi
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call division_flotante
    
    mov qword [t3.valor], rax

    mov rcx, [t3.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; Igual
    mov rcx, igual
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call igualdad_flotante
    
    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Diferente
    mov rcx, diferente
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call diferencia_flotante
    
    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Mayor
    mov rcx, mayor
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call mayor_flotante
    
    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; MayorIgual
    mov rcx, mayorIgual
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call mayor_igual_flotante
    
    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Menor
    mov rcx, menor
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call menor_flotante
    
    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; MenorIgual
    mov rcx, menorIgual
    call imprimir_cadena

    mov rcx, [@0_f1.valor] ; Guardar operacion izquierda en xmm0
    mov rdx, [@0_f2.valor] ; Guardar operacion derecha en xmm1
    call menor_igual_flotante
    
    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; +---------------- Cadena ----------------+
    
    mov rcx, sTitulo
    call imprimir_cadena

    mov rcx, cadena1
    call imprimir_cadena

    call _flushall
    mov rcx, @0_s1.formato
    mov rdx, @0_s1.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_s1.tipoDato
    call check_scanf

    mov rcx, cadena2
    call imprimir_cadena

    call _flushall
    mov rcx, @0_s2.formato
    mov rdx, @0_s2.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_s2.tipoDato
    call check_scanf

    mov rcx, salto_linea
    call imprimir_cadena

    ; Concatenación
    mov rcx, cat
    call imprimir_cadena

    mov rcx, @0_s1.valor 
    mov rdx, @0_s2.valor
    mov r8, t4.valor
    call concat_cadena

    mov rcx, t4.valor
    call imprimir_cadena

    mov rcx, salto_linea
    call imprimir_cadena

    ; Igual
    mov rcx, igual
    call imprimir_cadena

    mov rcx, @0_s2.valor 
    mov rdx, @0_s1.valor
    call igualdad_cadena

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Diferente
    mov rcx, diferente
    call imprimir_cadena

    mov rcx, @0_s2.valor 
    mov rdx, @0_s1.valor
    call diferencia_cadena

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; +---------------- Caracter ----------------+
    
    mov rcx, cTitulo
    call imprimir_cadena

    mov rcx, caracter1
    call imprimir_cadena

    call _flushall
    mov rcx, @0_c1.formato
    mov rdx, @0_c1.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_c1.tipoDato
    call check_scanf

    mov rcx, caracter2
    call imprimir_cadena

    call _flushall
    mov rcx, @0_c2.formato
    mov rdx, @0_c2.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_c2.tipoDato
    call check_scanf

    mov cl, [@0_c1.valor]
    call imprimir_caracter

    mov rcx, salto_linea
    call imprimir_cadena

    mov cl, [@0_c2.valor]
    call imprimir_caracter

    mov rcx, salto_linea
    call imprimir_cadena

    ; Igual
    mov rcx, igual
    call imprimir_cadena

    mov cl, [@0_c1.valor] ; Guardar operacion izquierda en eax
    mov dl, [@0_c2.valor] ; Guardar operacion derecha en ecx
    call igualdad_caracter

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Diferente
    mov rcx, diferente
    call imprimir_cadena

    mov cl, [@0_c1.valor] ; Guardar operacion izquierda en eax
    mov dl, [@0_c2.valor] ; Guardar operacion derecha en ecx
    call diferencia_caracter

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Mayor
    mov rcx, mayor
    call imprimir_cadena

    mov cl, [@0_c1.valor] ; Guardar operacion izquierda en eax
    mov dl, [@0_c2.valor] ; Guardar operacion derecha en ecx
    call mayor_caracter

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; MayorIgual
    mov rcx, mayorIgual
    call imprimir_cadena

    mov cl, [@0_c1.valor] ; Guardar operacion izquierda en eax
    mov dl, [@0_c2.valor] ; Guardar operacion derecha en ecx
    call mayor_igual_caracter

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Menor
    mov rcx, menor
    call imprimir_cadena

    mov cl, [@0_c1.valor] ; Guardar operacion izquierda en eax
    mov dl, [@0_c2.valor] ; Guardar operacion derecha en ecx
    call menor_caracter

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; MenorIgual
    mov rcx, menorIgual
    call imprimir_cadena

    mov cl, [@0_c1.valor] ; Guardar operacion izquierda en eax
    mov dl, [@0_c2.valor] ; Guardar operacion derecha en ecx
    call menor_igual_caracter

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; +---------------- Booleano ----------------+
    
    mov rcx, bTitulo
    call imprimir_cadena

    mov rcx, booleano1
    call imprimir_cadena

    call _flushall
    mov rcx, @0_b1.formato
    mov rdx, @0_b1.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_b1.tipoDato
    call check_scanf

    mov r8, @0_b1.valor
    call str_to_bool

    mov byte [@0_b1.valor], al

    mov cl, [@0_b1.valor]
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    mov rcx, booleano2
    call imprimir_cadena

    call _flushall
    mov rcx, @0_b2.formato
    mov rdx, @0_b2.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_b2.tipoDato
    call check_scanf

    mov r8, @0_b2.valor
    call str_to_bool

    mov byte [@0_b2.valor], al

    mov cl, [@0_b2.valor]
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; And
    mov rcx, bAnd
    call imprimir_cadena

    mov al, [@0_b1.valor]
    mov cl, [@0_b2.valor]
    and al, cl
    mov byte [t6.valor], al

    mov cl, [t6.valor]
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Or
    mov rcx, bOr
    call imprimir_cadena

    mov al, [@0_b1.valor]
    mov cl, [@0_b2.valor]
    or al, cl
    mov byte [t6.valor], al

    mov cl, [t6.valor]
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Not
    mov rcx, bNot
    call imprimir_cadena

    mov al, [@0_b1.valor]
    not al
    mov byte [t6.valor], al

    mov cl, [t6.valor]
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Igual
    mov rcx, igual
    call imprimir_cadena

    mov cl, [@0_b1.valor] ; Guardar operacion izquierda en eax
    mov dl, [@0_b2.valor] ; Guardar operacion derecha en ecx
    call igualdad_booleana

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Diferente
    mov rcx, diferente
    call imprimir_cadena

    mov cl, [@0_b1.valor] ; Guardar operacion izquierda en eax
    mov dl, [@0_b2.valor] ; Guardar operacion derecha en ecx
    call diferencia_booleana

    mov cl, al
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena

    ; Condicional
    
    ; Formato etiqueta condicional
    ; .%idBloque%_si%consecutivo%:
    ; .%idBloque%_sino%consecutivo%:
    ; .%idBloque%_fin_si%consecutivo%:

    .0_si0: nop ; No sirve para nada, solo es decoración
    mov al, [@0_b1.valor]
    cmp al, [falso]
    je .0_sino0

    ; bloqueVerdadero
    mov cl, [@0_b1.valor]
    call imprimir_bool
        
    mov rcx, salto_linea
    call imprimir_cadena

    jmp .0_fin_si0
    
    .0_sino0: nop ; bloqueFalso
    mov cl, [@0_b1.valor]
    call imprimir_bool

    mov rcx, salto_linea
    call imprimir_cadena
    
    .0_fin_si0: nop ; FinSi

    ; Mientras
    ; formato para ciclo mientras:
    ; .%idBloque%_mientras%consecutivo%:
    ; .%idBloque%_fin_mientras%consecutivo%:
    .0_mientras0: nop
    mov ecx, [@0_e1.valor]
    mov edx, [@0_e2.valor]   
    call menor_igual_entero ; e1 <= e2
    
    cmp al, [falso]
    je .0_fin_mientras0

    mov ecx, [@0_e1.valor]
    call imprimir_entero 

    mov rcx, salto_linea
    call imprimir_cadena

    mov ecx, [@0_e1.valor]
    mov edx, 1
    call suma_entera ; e1 + 1
    mov dword [@0_e1.valor], eax ; e1 = e1 + 1

    jmp .0_mientras0

    .0_fin_mientras0: nop

    mov rsp, rbp
    pop rbp
        
    mov rcx, 0
    call exit

    xor rax, rax
    ret

success:
    mov rax, 0
    ret

fail:
    mov rax, 1
    ret

true:
    xor rax, rax
    mov al, [verdadero]
    ret

false:
    xor rax, rax
    mov al, [falso]
    ret

error:
    mov rcx, 1 ; Código de salida 1 (error)
    call exit

imprimir:
    ; rcx: Formato
    ; rdx: Valor
    sub rsp, 32
    call printf
    add rsp, 32

    ret

check_scanf:
    ; rcx: retorno de scanf (rax)
    ; rdx: tipo de dato esperado como cadena (Entero, Flotante, Cadena, Caracter, Booleano)
    test rcx, rcx
    jnz success
    
    mov rcx, error_scanf
    mov rdx, rdx
    call imprimir

    mov rax, 1
    jmp error
    
imprimir_cadena:
    ; rcx <- cadena a imprimir
    mov rdx, rcx
    mov rcx, formato_cadena
    call imprimir
    ret

imprimir_bool:
    ; cl <- booleano a imprimir

    cmp cl, 0
    test cl, cl

    jz .false

    mov rcx, booleano_verdadero
    call imprimir_cadena
    ret

    .false:
        mov rcx, booleano_falso
        call imprimir_cadena
        ret
    
imprimir_caracter:
    ; cl <- caracter a imprimir
    xor rdx, rdx
    mov dl, cl
    mov rcx, formato_char
    call imprimir
    ret

imprimir_entero:
    ; ecx <- entero a imprimir
    mov edx, ecx
    mov rcx, formato_int
    call imprimir
    ret

imprimir_flotante:
    ; rcx <- flotante a imprimir
    mov rdx, rcx
    mov rcx, formato_float
    call imprimir
    ret

; Operaciones enteras

entero_negativo:
    ; ecx <- Operando
    ; eax <- Resultado
    xor rax, rax
    mov eax, ecx ; Guardar operando en eax
    neg eax

    ret

suma_entera:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; eax <- Resultado
    xor rax, rax
    mov eax, ecx ; Guardar operacion izquierda en eax
    mov ecx, edx ; Guardar operacion derecha en ecx
    add eax, ecx

    ret

resta_entera:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; eax <- Resultado
    xor rax, rax
    mov eax, ecx ; Guardar operacion izquierda en eax
    mov ecx, edx ; Guardar operacion derecha en ecx
    sub eax, ecx

    ret

multiplicacion_entera:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; eax <- Resultado
    xor rax, rax
    mov eax, ecx ; Guardar operacion izquierda en eax
    mov ecx, edx ; Guardar operacion derecha en ecx
    imul eax, ecx

    ret

division_modulo_entera:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; eax <- Cociente
    ; edx <- Residuo
    xor rax, rax
    mov eax, ecx ; Guardar operacion izquierda en eax
    mov ecx, edx ; Guardar operacion derecha en ecx
    cdq

    ; Hacer checkeo si edx es 0
    test edx, edx
    jz .error 
    
    idiv ecx
    ret

    .error:
        mov rcx, error_div
        call imprimir_cadena
        jmp error

    ret

igualdad_entera:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; al <- Resultado booleano
    xor rax, rax
    cmp ecx, edx
    je true

    jmp false
    ret

diferencia_entera:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; al <- Resultado booleano
    xor rax, rax
    cmp ecx, edx
    jne true

    jmp false
    ret

mayor_entero:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; al <- Resultado booleano
    xor rax, rax
    cmp ecx, edx
    jg true

    jmp false
    ret

mayor_igual_entero:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; al <- Resultado booleano
    xor rax, rax
    cmp ecx, edx
    jge true

    jmp false
    ret

menor_entero:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; al <- Resultado booleano
    xor rax, rax
    cmp ecx, edx
    jl true

    jmp false
    ret

menor_igual_entero:
    ; ecx <- Entero izq
    ; edx <- Entero der
    ; al <- Resultado booleano
    xor rax, rax
    cmp ecx, edx
    jle true

    jmp false
    ret


; Operaciones flotantes

flotante_negativo:
    ; rcx <- Operando
    ; rax <- Resultado
    xor rax, rax
    mov rax, 0x8000000000000000
    movq xmm0, rcx ; Guardar operando en xmm0
    movq xmm1, rax 
    xorpd xmm0, xmm1
    movq rax, xmm0

    ret

suma_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; rax <- Resultado
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    addsd xmm0, xmm1
    movq rax, xmm0

    ret

resta_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; rax <- Resultado
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    subsd xmm0, xmm1
    movq rax, xmm0
    
    ret

multiplicacion_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; rax <- Resultado
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    mulsd xmm0, xmm1
    movq rax, xmm0

    ret

division_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; rax <- Resultado
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 

    ; Hacer checkeo si xmm1 es 0
    pxor xmm2, xmm2
    ucomisd xmm1, xmm2
    je .error

    divsd xmm0, xmm1
    movq rax, xmm0
    ret

    .error:
        mov rcx, error_div
        call imprimir_cadena
        jmp error

    ret

igualdad_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; al <- Resultado booleano
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    ucomisd xmm0, xmm1
    je true

    jmp false
    ret

diferencia_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; al <- Resultado booleano
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    ucomisd xmm0, xmm1
    jne true

    jmp false
    ret

mayor_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; al <- Resultado booleano
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    ucomisd xmm0, xmm1
    ja true

    jmp false
    ret

mayor_igual_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; al <- Resultado booleano
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    ucomisd xmm0, xmm1
    jae true

    jmp false
    ret

menor_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; al <- Resultado booleano
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    ucomisd xmm0, xmm1
    jb true

    jmp false
    ret

menor_igual_flotante:
    ; rcx <- Flotante izq
    ; rdx <- Flotante der
    ; al <- Resultado booleano
    xor rax, rax
    movq xmm0, rcx ; Guardar operacion izquierda en xmm0
    movq xmm1, rdx ; Guardar operacion derecha en xmm1 
    ucomisd xmm0, xmm1
    jbe true

    jmp false
    ret

; Operaciones de cadenas

concat_cadena:
    ; rcx <- izq
    ; rdx <- der
    ; r8 <- Destino
    push rdx

    mov rdx, rcx
    mov rcx, r8
    call copiar_cadena

    mov rcx, r8
    pop rdx
    sub rsp, 32
    call strcat
    add rsp, 32

    ret

copiar_cadena:
    ; rcx <- destino
    ; rdx <- fuente
    sub rsp, 32
    call strcpy
    add rsp, 32
    ret

str_to_bool:
    ; Respaldar rcx, rdx, rax si es necesario
    ; r8: cadena a convertir
    ; rcx <- str
    mov rcx, r8
    mov rdx, booleano_verdadero
    sub rsp, 32
    call strcmp
    add rsp, 32
    cmp rax, 0
    je true
    
    mov rcx, r8
    mov rdx, booleano_falso
    sub rsp, 32
    call strcmp
    add rsp, 32
    cmp rax, 0
    je false
    
    mov rcx, error_bool
    mov rdx, r8
    call imprimir

    jmp error
    
igualdad_cadena:
    ; rcx <- Cadena izq
    ; rdx <- Cadena der
    ; al <- Resultado
    mov rdi, rcx
    mov rsi, rdx
    call strcmp
    cmp rax, 0
    
    je true

    jmp false
    ret

diferencia_cadena:
    ; rcx <- Cadena izq
    ; rdx <- Cadena der
    ; al <- Resultado
    mov rdi, rcx
    mov rsi, rdx
    call strcmp
    cmp rax, 0

    jne true

    jmp false
    ret

; Operaciones caracter

igualdad_caracter:
    ; cl <- Caracter izq
    ; dl <- Caracter der
    ; al <- Resultado booleano
    cmp cl, dl
    je true

    jmp false
    ret

diferencia_caracter:
    ; cl <- Caracter izq
    ; dl <- Caracter der
    ; al <- Resultado booleano
    cmp cl, dl
    jne true

    jmp false
    ret

mayor_caracter:
    ; cl <- Caracter izq
    ; dl <- Caracter der
    ; al <- Resultado booleano
    cmp cl, dl
    jg true

    jmp false
    ret

mayor_igual_caracter:
    ; cl <- Caracter izq
    ; dl <- Caracter der
    ; al <- Resultado booleano
    cmp cl, dl
    jge true

    jmp false
    ret

menor_caracter:
    ; cl <- Caracter izq
    ; dl <- Caracter der
    ; al <- Resultado booleano
    cmp cl, dl
    jl true

    jmp false
    ret

menor_igual_caracter:
    ; cl <- Caracter izq
    ; dl <- Caracter der
    ; al <- Resultado booleano
    cmp cl, dl
    jle true

    jmp false
    ret

; Operaciones Booleanos

igualdad_booleana:
    ; cl <- Caracter izq
    ; dl <- Caracter der
    ; al <- Resultado booleano
    cmp cl, dl
    je true

    jmp false
    ret

diferencia_booleana:
    ; cl <- Caracter izq
    ; dl <- Caracter der
    ; al <- Resultado booleano
    cmp cl, dl
    jne true

    jmp false
    ret