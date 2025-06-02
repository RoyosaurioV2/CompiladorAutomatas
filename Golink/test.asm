section .data
    int_format db '%d', 0
    str_format db '%s', 0
    char_format db '%c', 0
    float_format db '%lf', 0

    mensaje db 'Dame algo: ', 0  ; Cadena de caracteres terminada en null
    cadena db 0

section .text
    extern printf, scanf, exit

main:

    PUSH RBP
    MOV RBP, RSP

    ; Imprimir el mensaje solicitando un número entero
    mov rcx, str_format
    mov rdx, mensaje
    call printf
    
    sub RSP, 8
    ; mov rcx, float_format
    ; mov rdx, [RBP]  ; Cargar la dirección base del string en rax
    ; lea rdx, [RBP] ; <- Si no es cadena
    ; call scanf

    ; mov dword[RBP], 0x2 ; <- Enteros
    ; movsd xmm0, 0x40091EB851EB851F
    mov dword [RBP], __float32__(3.14) ; <- Flotante 
    cvtss2sd xmm0, [RBP]
    movsd qword [RBP], xmm0

    ; Imprimir el mensaje solicitando un número entero
    
    mov rcx, float_format 
    mov rdx, [RBP] ; Float
    ; mov rdx, [RBP]
    call printf
    
    MOV RSP, RBP
    POP RBP

    ; Llamar a la función exit de C para salir con un código de salida específico
    mov     rcx, 0      ; Código de salida
    call    exit       ; Llamar a la función exit de la biblioteca C

    xor eax, eax    ; Limpiar el registro de retorno
    ret ; Retornar del programa

