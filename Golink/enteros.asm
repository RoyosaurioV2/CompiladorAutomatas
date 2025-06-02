section .data
    int_format db '%d',  0
    str_format db '%s', 0
    limpiar_format db "%c", 0
    
    salto_linea db 0xA, 0
    int_str db 0xA, 'Entero: %d', 0xA, 0

    suma_str db 0xA, 'Suma: %d', 0xA, 0
    resta_str db 0xA, 'Resta: %d', 0xA, 0
    multi_str db 0xA, 'Multiplicacion: %d', 0xA, 0
    div_str db 0xA, 'Division: %d', 0xA, 0
    mod_str db 0xA, 'Modulo: %d', 0xA, 0

    mensaje1 db 'Dame entero 1: ', 0  
    mensaje2 db 'Dame entero 2: ', 0  

    error_scanf db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se esperaba un "%s" en la lectura.', 0xA, 0

    my_int dd 10
    my_int2 dd 5

    basura db 0

; Formato: @%id_bloque%_%nombre%
@0_a:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000

@0_b:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000
t1:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000
t2:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000
t3:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000
t4:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000
t5:
    .tipoDato db 'Entero', 0
    .valor dd 0x00000000

section .text
    extern printf, scanf, exit, _flushall

main:

    push rbp
    mov rbp, rsp

    mov rcx, mensaje1
    call imprimir_cadena

    ; Leer entero
    ; sub RSP, 16
    ; mov rcx, int_format
    ; lea rdx, [RBP]
    ; call scanf
    call _flushall
    mov rcx, int_format
    mov rdx, @0_a.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_a.tipoDato
    call check_scanf

    mov ecx, [@0_a.valor]
    call imprimir_entero

    mov rcx, salto_linea
    call imprimir_cadena

    mov rcx, mensaje2
    call imprimir_cadena

    call _flushall
    mov rcx, int_format
    mov rdx, @0_b.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_b.tipoDato
    call check_scanf

    mov ecx, [@0_b.valor]
    call imprimir_entero

    mov rcx, salto_linea
    call imprimir_cadena

    ; Asignar constante
    mov dword [t1.valor], 0x00000001
    
    mov ecx, [t1.valor]
    call imprimir_entero

    ; Asignar un valor variable
    ;  mov eax, dword [RBP]
    ;  mov dword [RBP - 4], eax

    ; Asignar un valor constante
    ;  mov eax, dword [my_int]
    ;  mov dword [RBP - 8], eax

    ; Asignar un valor constante
    ;  mov eax, 0xF
    ;  mov dword [RBP - 12], eax

    ; Imprimir int
    ;  mov rcx, int_str
    ;  mov rdx, [RBP]
    ;  call printf

    ;  mov rcx, int_str
    ;  mov rdx, [RBP- 4]
    ;  call printf

    ;  mov rcx, int_str
    ;  mov rdx, [RBP - 8]
    ;  call printf

    ;  mov rcx, int_str
    ;  mov rdx, [RBP - 12]
    ;  call printf

    ; Suma int
    ;  mov eax, dword [my_int]
    ;  mov ecx, dword [my_int2]
    ;  add eax, ecx
    mov eax, [@0_a.valor]
    mov ecx, [@0_b.valor]
    add eax, ecx

    mov dword [t1.valor], eax

    mov rcx, salto_linea
    call imprimir_cadena

    mov ecx, [t1.valor]
    call imprimir_entero

    ;  mov dword [RBP], eax

    ; Imprimir int
    ;  mov rcx, suma_str
    ;  mov rdx, [RBP]
    ;  call printf

    ; Resta int
    ;  mov eax, dword [my_int]
    ;  mov ecx, dword [my_int2]
    ;  sub eax, ecx

    ;  mov dword [RBP], eax

    mov eax, [@0_a.valor]
    mov ecx, [@0_b.valor]
    sub eax, ecx

    mov dword [t2.valor], eax

    mov rcx, salto_linea
    call imprimir_cadena

    mov ecx, [t2.valor]
    call imprimir_entero

    ; Imprimir int
    ;  mov rcx, resta_str
    ;  mov rdx, [RBP]
    ;  call printf

    ; Multiplicación int
    ;  mov eax, dword [my_int]
    ;  mov ecx, dword [my_int2]
    ;  imul eax, ecx

    ;  mov dword [RBP], eax

    mov eax, [@0_a.valor]
    mov ecx, [@0_b.valor]
    imul eax, ecx

    mov dword [t3.valor], eax

    mov rcx, salto_linea
    call imprimir_cadena

    mov ecx, [t3.valor]
    call imprimir_entero

    ; Imprimir int
    ;  mov rcx, multi_str
    ;  mov rdx, [RBP]
    ;  call printf

    ; División:Modulo int
    ;  mov eax, dword [my_int]
    ;  mov ecx, dword [my_int2]
    ;  cdq
    
    ;  idiv ecx

    mov eax, [@0_a.valor]
    mov ecx, [@0_b.valor]
    cdq
    
    idiv ecx

    mov dword [t4.valor], eax ; Consiente
    mov dword [t5.valor], edx ; Residuo

    mov rcx, salto_linea
    call imprimir_cadena

    mov ecx, [t4.valor]
    call imprimir_entero

    mov rcx, salto_linea
    call imprimir_cadena

    mov ecx, [t5.valor]
    call imprimir_entero

    ;  mov dword [RBP], eax ; Division
    ;  mov dword [RBP - 4], edx ; Modulo

    ; Imprimir double
    ;  mov rcx, div_str
    ;  mov rdx, [RBP]
    ;  call printf

    ;  mov rcx, mod_str
    ;  mov rdx, [RBP - 4]
    ;  call printf

    mov rsp, rbp
    pop rbp

    ; Llamar a la función exit de C para salir con un código de salida específico
    mov     rcx, 0      ; Código de salida
    call    exit       ; Llamar a la función exit de la biblioteca C

    xor rax, rax    ; Limpiar el registro de retorno
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

imprimir_entero:
    ; ecx <- entero a imprimir
    mov edx, ecx
    mov rcx, int_format
    call imprimir
    ret