section .data

    salto_linea db 0xA, 0

    str_format db '%s', 0

    mensaje1 db 'Dame cadena 1: ', 0
    mensaje2 db 'Dame cadena 2: ', 0

    error_scanf db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se esperaba un "%s" en la lectura.', 0xA, 0

    str_constante db 'Contante', 0
; otra forma de manejar variables
; Formato: @%id_bloque%_%nombre%
@0_a:
    .tipoDato db 'Cadena', 0
    .valor db 0x00
@0_b:
    .tipoDato db 'Cadena', 0
    .valor db 0xFF
t1:
    .tipoDato db 'Cadena', 0
    .valor db 0x00
t2:
    .tipoDato db 'Cadena', 0
    .valor db 0x00
t3:
    .tipoDato db 'Cadena', 0
    .valor db 0xFF

section .text
    extern printf, scanf, exit, strcmp, strcpy, strlen, _flushall

main:

    push rbp
    mov rbp, rsp

    ; Imprimir
    mov rcx, mensaje1
    call imprimir_cadena

    call _flushall
    mov rcx, str_format
    mov rdx, @0_a.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_a.tipoDato
    call check_scanf

    mov rcx, @0_a.valor
    call imprimir_cadena

    mov rcx, salto_linea
    call imprimir_cadena

    mov rcx, mensaje2
    call imprimir_cadena

    call _flushall
    mov rcx, str_format
    mov rdx, @0_b.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_b.tipoDato
    call check_scanf

    mov rcx, @0_b.valor
    call imprimir_cadena

    mov rcx, salto_linea
    call imprimir_cadena

    ; Asignar constante
    mov rcx, t1.valor
    mov rdx, str_constante
    call copiar_cadena

    mov rcx, t1.valor
    call imprimir_cadena

    mov rcx, salto_linea
    call imprimir_cadena

    mov rsp, rbp
    pop rbp

    ; Llamar a la función exit de C para salir con un código de salida específico
    mov     rcx, 0      ; Código de salida
    call    exit       ; Llamar a la función exit de la biblioteca C

    xor eax, eax    ; Limpiar el registro de retorno
    ret ; Retornar del programa

success:
    mov rax, 0
    ret

fail:
    mov rax, 1
    ret

true:
    mov rax, 0xFF
    ret

false:
    mov rax, 0x00
    ret

error:
    mov rcx, 1 ; Código de salida 1 (error)
    call exit
    
imprimir:
    
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
    mov rcx, str_format
    call imprimir
    ret

copiar_cadena:
    ; rcx <- destino
    ; rdx <- fuente
    sub rsp, 32
    call strcpy
    add rsp, 32
    ret