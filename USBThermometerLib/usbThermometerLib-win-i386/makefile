# Project: USBThermometerLib
# Makefile created by Dev-C++ 4.9.9.2

CPP  = g++.exe
CC   = gcc.exe
WINDRES = windres.exe
LINKOBJ-i386  = usbThermometer-win-i386.o
LINKOBJ-x64  = usbThermometer-win-x64.o
LIBS =  --no-export-all-symbols --add-stdcall-alias FTD2XX.lib  
CXXINCS =  -I"lib/gcc/mingw32/3.4.2/include"  -I"include/c++/3.4.2/backward"  -I"include/c++/3.4.2/mingw32"  -I"include/c++/3.4.2"  -I"include"  -I"C:/Program Files/Java/jdk1.6.0_18/include/win32"  -I"C:/Program Files/Java/jdk1.6.0_18/include" 
BIN-i386  = ./../usbThermometerLib-win-i386.dll
BIN-x64  = ./../usbThermometerLib-win-x64.dll
CXXFLAGS = $(CXXINCS) -DBUILDING_DLL=1
RM = rm -f

.PHONY: all all-before all-after clean clean-custom

all: all-before $(BIN-i386) $(BIN-x64) all-after

clean: clean-custom
	${RM} $(LINKOBJ-i386) $(LINKOBJ-x64) $(BIN-i386) $(BIN-x64)

DLLWRAP=dllwrap.exe

$(BIN-i386): $(LINKOBJ-i386)
	$(DLLWRAP) $(LINKOBJ-i386) $(LIBS) -o $(BIN-i386)

$(BIN-x64): $(LINKOBJ-x64)
	$(DLLWRAP) $(LINKOBJ-x64) $(LIBS) -o $(BIN-x64)	
	
$(LINKOBJ-i386): usbThermometer.cpp
	$(CC) -m32 -c usbThermometer.cpp -o $(LINKOBJ-i386) $(CXXFLAGS)
	
$(LINKOBJ-x64): usbThermometer.cpp
	$(CC) -m64 -c usbThermometer.cpp -o $(LINKOBJ-x64) $(CXXFLAGS)	
