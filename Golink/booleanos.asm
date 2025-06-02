section .data

    salto_linea db 0xA, 0

    bool_format db '%s', 0
    str_format db '%s', 0

    mensaje db 'Dame logico: ', 0
    booleano_verdadero db 'Verdadero', 0
    booleano_falso db 'Falso', 0

    error_bool db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se ley', 0xA2,' "%s" y', \
    0xA, '    se esperaba Verdadero o Falso.', 0xA, 0

    error_scanf db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se esperaba un "%s" en la lectura.', 0xA, 0

; otra forma de manejar variables
; Formato: @%id_bloque%_%nombre%
@0_a:
    .tipoDato db 'Booleano', 0
    .valor db 0x00
@0_b:
    .tipoDato db 'Booleano', 0
    .valor db 0xFF
t1:
    .tipoDato db 'Booleano', 0
    .valor db 0x00
t2:
    .tipoDato db 'Booleano', 0
    .valor db 0x00
t3:
    .tipoDato db 'Booleano', 0
    .valor db 0xFF

section .text
    extern printf, scanf, exit, strcmp, strcpy, strlen, _flushall

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

main:

    push rbp
    mov rbp, rsp

    ; sub rsp, 32+8 ; Alineacion de la pila

    ; mov rcx, int_format
    ; mov rdx, [@0_a.valor]
    ; call imprimir

    ; Imprimir
    mov rcx, mensaje
    call imprimir_cadena

    ; Leer y checar que el dato es el correcto
    ; sub rsp, 8
    ; mov rax, booleano_verdadero
    ; mov rbp, rax 
    ; lea rdx, [rbp]
    call _flushall
    mov rcx, bool_format
    mov rdx, t1.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, t1.tipoDato
    call check_scanf
    
    ; Chequeo
    ; mov rcx, rbp
    ; mov r8, rbp
    mov r8, t1.valor
    call str_to_bool

    mov byte [@0_a.valor], al

    mov cl, [@0_a.valor]
    call imprimir_bool

    ; mov rcx, int_format
    ; mov rdx, [@0_a.valor]
    ; call imprimir

    ; sub rsp, 1
    ; mov byte [RBP - 1], al
    ; movzx rcx, byte [RBP - 1]
    
    ; Asignar constante
    mov byte [t1.valor], 0xFF

    ; AND
    mov rcx, salto_linea
    call imprimir_cadena

    mov al, [@0_a.valor]
    mov cl, [@0_b.valor]
    and al, cl
    mov byte [t1.valor], al

    mov cl, [t1.valor]
    call imprimir_bool

    ; OR
    mov rcx, salto_linea
    call imprimir_cadena

    mov al, [@0_a.valor]
    mov cl, [@0_b.valor]
    or al, cl

    mov byte [t2.valor], al

    mov cl, [t2.valor]
    call imprimir_bool

    ; NOT
    mov rcx, salto_linea
    call imprimir_cadena

    mov al, [@0_a.valor]
    not al

    mov byte [t3.valor], al

    mov cl, [t3.valor]
    call imprimir_bool

    mov rsp, rbp
    pop rbp

    ; Llamar a la función exit de C para salir con un código de salida específico
    mov     rcx, 0      ; Código de salida
    call    exit       ; Llamar a la función exit de la biblioteca C

    xor eax, eax    ; Limpiar el registro de retorno
    ret ; Retornar del programa

str_to_bool:
    ; Respaldar rcx, rdx, rax si es necesario
    ; r8: cadena izq
    ; rcx <- str
    
    mov rcx, r8
    mov rdx, booleano_verdadero
    call strcmp
    cmp rax, 0
    je true
    
    mov rcx, r8
    mov rdx, booleano_falso
    call strcmp
    cmp rax, 0
    je false
 
    mov rcx, error_bool
    mov rdx, r8
    call imprimir

    jmp error

imprimir_bool:
    ; rcx <- Valor

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