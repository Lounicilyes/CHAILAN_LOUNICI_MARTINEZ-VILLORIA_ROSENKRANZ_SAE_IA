@echo off
echo Compilation...
javac Test.java

echo.
echo ================= TESTS ET (0.5) =================
echo.
java Test 2,1 sigmoid ET 0.5
java Test 2,2 sigmoid ET 0.5 2d
java Test 2,2,1 sigmoid ET 0.5
java Test 2,2,2 sigmoid ET 0.5 2d
java Test 2,2,1 sigmoid ET 0.5 melange

java Test 2,1 tanh ET 0.5
java Test 2,2 tanh ET 0.5 2d
java Test 2,2,1 tanh ET 0.5
java Test 2,2,2 tanh ET 0.5 2d
java Test 2,2,1 tanh ET 0.5 melange

echo.
echo ================= TESTS OU (0.5) =================
echo.
java Test 2,1 sigmoid OU 0.5
java Test 2,2 sigmoid OU 0.5 2d
java Test 2,2,1 sigmoid OU 0.5
java Test 2,2,2 sigmoid OU 0.5 2d
java Test 2,2,1 sigmoid OU 0.5 melange

java Test 2,1 tanh OU 0.5
java Test 2,2 tanh OU 0.5 2d
java Test 2,2,1 tanh OU 0.5
java Test 2,2,2 tanh OU 0.5 2d
java Test 2,2,1 tanh OU 0.5 melange

echo.
echo ================= TESTS XOR (0.2) =================
echo.
REM --- Sigmoid ---
java Test 2,1 sigmoid XOR 0.2
java Test 2,1 sigmoid XOR 0.2 melange

java Test 2,2,1 sigmoid XOR 0.2
java Test 2,2,2 sigmoid XOR 0.2 2d
java Test 2,2,1 sigmoid XOR 0.2 melange

java Test 2,4,1 sigmoid XOR 0.2
java Test 2,4,2 sigmoid XOR 0.2 2d
java Test 2,4,1 sigmoid XOR 0.2 melange

java Test 2,8,1 sigmoid XOR 0.2
java Test 2,8,2 sigmoid XOR 0.2 2d
java Test 2,8,1 sigmoid XOR 0.2 melange

java Test 2,4,4,1 sigmoid XOR 0.2
java Test 2,4,4,2 sigmoid XOR 0.2 2d
java Test 2,4,4,1 sigmoid XOR 0.2 melange

REM --- Tanh ---
java Test 2,1 tanh XOR 0.2
java Test 2,1 tanh XOR 0.2 melange

java Test 2,2,1 tanh XOR 0.2
java Test 2,2,2 tanh XOR 0.2 2d
java Test 2,2,1 tanh XOR 0.2 melange

java Test 2,4,1 tanh XOR 0.2
java Test 2,4,2 tanh XOR 0.2 2d
java Test 2,4,1 tanh XOR 0.2 melange

java Test 2,8,1 tanh XOR 0.2
java Test 2,8,2 tanh XOR 0.2 2d
java Test 2,8,1 tanh XOR 0.2 melange

java Test 2,4,4,1 tanh XOR 0.2
java Test 2,4,4,2 tanh XOR 0.2 2d
java Test 2,4,4,1 tanh XOR 0.2 melange

echo.
echo Fin des tests.
pause
