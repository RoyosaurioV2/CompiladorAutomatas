section .data
    int_format db '%d', 0
    str_format db '%s', 0

    mensaje_num db 'Ingresa un número entero:', 0
    mensaje_str db 'Ingresa una cadena:', 0
    cadena db 0

section .bss
    numero resd 1  ; Variable para almacenar el número ingresado
    ; cadena resb 256  ; Buffer para almacenar la cadena ingresada

section .text
    extern printf, scanf

main:

    ; Imprimir el mensaje solicitando un número entero
    mov rcx, mensaje_num
    call printf

    ; Leer el número entero ingresado por el usuario
    mov rcx, int_format
    mov rdx, numero
    call scanf

    ; Imprimir el mensaje solicitando una cadena
    mov rcx, mensaje_str
    call printf

    ; Leer la cadena ingresada por el usuario
    mov rcx, str_format
    mov rdx, cadena
    call scanf

    ; Imprimir el número ingresado por el usuario
    mov rcx, int_format
    mov rdx, [numero]
    call printf

    ; Imprimir la cadena ingresada por el usuario
    mov rcx, formato_out_str
    mov rdx, cadena
    call printf

    ; Retornar 0
    xor eax, eax
    ret

