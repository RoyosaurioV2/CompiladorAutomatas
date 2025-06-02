section .data
    char_format db '%c', 0
    str_format db '%s', 0
    char_str db 0xA, 'Char: %c', 0xA, 0
    hex_format db '0x%X', 0

    salto_linea db 0xA, 0

    mensaje1 db 'Dame caracter 1: ', 0
    mensaje2 db 'Dame caracter 2: ', 0  

    error_scanf db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se esperaba un "%s" en la lectura.', 0xA, 0

    my_char db 'l'
    my_char2 db 'h'

    c db 0

@0_a:
    .tipoDato db 'Caracter', 0
    .valor db 0x00
@0_b:
    .tipoDato db 'Caracter', 0
    .valor db 0x00
t1:
    .tipoDato db 'Caracter', 0
    .valor db 0x00
t2:
    .tipoDato db 'Caracter', 0
    .valor db 0x00
t3:
    .tipoDato db 'Caracter', 0
    .valor db 0x00
    
section .text
    extern printf, scanf, exit, _flushall

main:

    push rbp
    mov rbp, rsp
    
    mov rcx, mensaje1
    call imprimir_cadena
    
    ; Leer caracter
    ;  call _flushall
    ;  sub rsp, 1
    ;  mov rcx, char_format
    ;  lea rdx, [rbp]
    ;  sub rsp, 32
    ;  call scanf
    ;  sub rsp, 32

    call _flushall
    mov rcx, char_format
    mov rdx, @0_a.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_a.tipoDato
    call check_scanf
    
    mov cl, byte [@0_a.valor]
    call imprimir_caracter

    mov rcx, salto_linea
    call imprimir_cadena

    mov rcx, mensaje2
    call imprimir_cadena

    call _flushall
    mov rcx, char_format
    mov rdx, @0_b.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_b.tipoDato
    call check_scanf

    mov cl, byte [@0_b.valor]
    call imprimir_caracter

    mov rcx, salto_linea
    call imprimir_cadena
    
    ; Asignar constante
    mov byte [t1.valor], 'A'
    mov byte [t2.valor], 0x42

    mov cl, byte [t1.valor]
    call imprimir_caracter

    mov rcx, salto_linea
    call imprimir_cadena

    mov cl, byte [t2.valor]
    call imprimir_caracter

    mov rcx, salto_linea
    call imprimir_cadena

    ; Asignar un valor variable
    ;  mov ah, byte [my_char]
    ;  mov byte [RBP - 1], ah
    
    ; Asignar un valor constante
    ;  mov byte [RBP - 2], 'A'
    
    ; Asignar un valor constante
    ;  mov ah, 0x26
    ;  mov byte [RBP - 3], ah
    
    ;  sub rsp, 16+16 ; Alineacion de la pila
    ; Imprimir char
    ;  mov rcx, char_str
    ;  movzx rdx, byte [RBP]
    ;  call printf
    ;  add rsp, 16+16 ; Alineacion de la pila

    ;  sub rsp, 16+16 ; Alineacion de la pila
    ;  mov rcx, char_str
    ;  movzx rdx, byte [RBP - 1]
    ;  call printf
    ;  add rsp, 16+16 ; Alineacion de la pila

    ;  sub rsp, 16+16 ; Alineacion de la pila
    ;  mov rcx, char_str
    ;  movzx rdx, byte [RBP - 2]
    ;  call printf
    ;  add rsp, 16+16 ; Alineacion de la pila

    ;  sub rsp, 16+16 ; Alineacion de la pila
    ;  mov rcx, char_str
    ;  movzx rdx, byte [RBP - 3]
    ;  call printf
    ;  add rsp, 16+16 ; Alineacion de la pila

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

imprimir:
    sub rsp, 32
    call printf
    add rsp, 32

    ret

imprimir_cadena:
    ; rcx <- cadena a imprimir
    mov rdx, rcx
    mov rcx, str_format
    call imprimir
    ret

imprimir_caracter:
    ; cl <- caracter a imprimir
    xor rdx, rdx
    mov dl, cl
    mov rcx, char_format
    call imprimir
    ret
    