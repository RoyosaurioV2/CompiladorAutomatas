nasm.exe -f win64 -gcv8 todos.asm

GoLink.exe /console todos.obj kernel32.dll msvcrt.dll /entry:main