section .data
    float_format db '%lf', 0
    str_format db '%s', 0
    float_str db 0xA, 'Flotante: %lf', 0xA, 0

    salto_linea db 0xA, 0
        
    suma_str db 0xA, 'Suma: %lf', 0xA, 0
    resta_str db 0xA, 'Resta: %lf', 0xA, 0
    multi_str db 0xA, 'Multiplicacion: %lf', 0xA, 0
    div_str db 0xA, 'Division: %lf', 0xA, 0

    mensaje1 db 'Dame flotante 1: ', 0  ; Cadena de caracteres terminada en null
    mensaje2 db 'Dame flotante 2: ', 0

    error_scanf db 0xA, 'Error en tiempo de ejecuci', 0xA2, 'n, se esperaba un "%s" en la lectura.', 0xA, 0

    my_double dq 3.25
    my_double2 dq 0x400921fb54442d18 ; 3.141593

@0_a:
    .tipoDato db 'Flotante', 0
    .valor dq 0x0000000000000000

@0_b:
    .tipoDato db 'Flotante', 0
    .valor dq 0x0000000000000000
t1:
    .tipoDato db 'Flotante', 0
    .valor dq 0x0000000000000000
t2:
    .tipoDato db 'Flotante', 0
    .valor dq 0x0000000000000000
t3:
    .tipoDato db 'Flotante', 0
    .valor dq 0x0000000000000000
t4:
    .tipoDato db 'Flotante', 0
    .valor dq 0x0000000000000000

section .text
    extern printf, scanf, exit, _flushall

main:

    push rbp
    mov rbp, rsp

    mov rcx, mensaje1
    call imprimir

    ; Leer double
    ;  sub RSP, 32
    ;  mov rcx, float_format
    ;  lea rdx, [RBP]
    ;  call scanf
    call _flushall
    mov rcx, float_format
    mov rdx, @0_a.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_a.tipoDato
    call check_scanf

    mov rcx, [@0_a.valor] 
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    mov rcx, mensaje2
    call imprimir

    call _flushall
    mov rcx, float_format
    mov rdx, @0_b.valor
    sub rsp, 32
    xor rax, rax
    call scanf
    sub rsp, 32

    mov rcx, rax
    mov rdx, @0_b.tipoDato
    call check_scanf

    mov rcx, [@0_b.valor] 
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; Asignar un valor constante
    mov rax, 0x400921fb54442d18
    movq xmm0, rax
    movsd qword [t1.valor], xmm0

    mov rcx, [t1.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena
    ; Asignar un valor variable
    ;  movsd xmm0, qword [RBP]
    ;  movsd qword [RBP - 0x8], xmm0

    ; Asignar un valor constante
    ;  movsd xmm0, qword [my_double]
    ;  movsd qword [RBP - 0x10], xmm0

    ; Asignar un valor constante
    ;  mov rax, 0x400921fb54442d18
    ;  movq xmm0, rax
    ;  movsd qword [RBP - 0x18], xmm0

    ; Imprimir double
    ;  mov rcx, float_str
    ;  mov rdx, [RBP]
    ;  call printf

    ;  mov rcx, float_str
    ;  mov rdx, [RBP - 0x8]
    ;  call printf

    ;  mov rcx, float_str
    ;  mov rdx, [RBP - 0x10]
    ;  call printf

    ;  mov rcx, float_str
    ;  mov rdx, [RBP - 0x18]
    ;  call printf

    ; Suma double
    ;  movsd xmm0, qword [my_double]
    ;  movsd xmm1, qword [my_double2]
    ;  addsd xmm0, xmm1

    ;  movsd qword [RBP], xmm0
    
    movsd xmm0, [@0_a.valor]
    movsd xmm1, [@0_b.valor]
    addsd xmm0, xmm1

    movsd qword [t1.valor], xmm0
    
    mov rcx, [t1.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; Imprimir double
    ;  mov rcx, suma_str
    ;  mov rdx, [RBP]
    ;  call printf

    ; Resta double
    ;  movsd xmm0, qword [my_double]
    ;  movsd xmm1, qword [my_double2]
    ;  subsd xmm0, xmm1

    ;  movsd qword [RBP], xmm0

    movsd xmm0, [@0_a.valor]
    movsd xmm1, [@0_b.valor]
    subsd xmm0, xmm1

    movsd qword [t2.valor], xmm0
    
    mov rcx, [t2.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; Imprimir double
    ;  mov rcx, resta_str
    ;  mov rdx, [RBP]
    ;  call printf

    ; Multiplicación double
    ;  movsd xmm0, qword [my_double]
    ;  movsd xmm1, qword [my_double2]
    ;  mulsd xmm0, xmm1

    ;  movsd qword [RBP], xmm0

    movsd xmm0, [@0_a.valor]
    movsd xmm1, [@0_b.valor]
    mulsd xmm0, xmm1

    movsd qword [t3.valor], xmm0
    
    mov rcx, [t3.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena


    ; Imprimir double
    ;  mov rcx, multi_str
    ;  mov rdx, [RBP]
    ;  call printf

    ; División double
    ;  movsd xmm0, qword [my_double]
    ;  movsd xmm1, qword [my_double2]
    ;  divsd xmm0, xmm1

    ;  movsd qword [RBP], xmm0

    movsd xmm0, [@0_a.valor]
    movsd xmm1, [@0_b.valor]
    divsd xmm0, xmm1

    movsd qword [t4.valor], xmm0
    
    mov rcx, [t4.valor]
    call imprimir_flotante

    mov rcx, salto_linea
    call imprimir_cadena

    ; Imprimir double
    ;  mov rcx, div_str
    ;  mov rdx, [RBP]
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

imprimir_flotante:
    ; rcx <- flotante a imprimir
    mov rdx, rcx
    mov rcx, float_format
    call imprimir
    ret
