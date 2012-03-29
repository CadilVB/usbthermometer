# Project: USBThermometerLib
# Makefile created by Dev-C++ 4.9.9.2

CPP  = g++.exe
CC   = gcc.exe
WINDRES = windres.exe
LINKOBJ_x64  = usbThermometer-win-x64.o
LINKOBJ_i386  = usbThermometer-win-i386.o
LIBS = --no-export-all-symbols --add-stdcall-alias
CXXINCS = -I"C:/Program Files/Java/jdk1.7.0_02/include/win32"  -I"C:/Program Files/Java/jdk1.7.0_02/include" 
BIN_x64  = ./../usbThermometerLib-win-x64.dll
BIN_i386  = ./../usbThermometerLib-win-i386.dll
CXXFLAGS = $(CXXINCS) -DBUILDING_DLL=1 
RM = rm -f

.PHONY: all all-before all-after clean clean-custom

all: all-before $(BIN_x64) $(BIN_i386) all-after

x64: $(BIN_x64) 

i386: $(BIN_i386)

clean: clean-custom
	${RM} $(OBJ_x64) $(BIN_x64) $(OBJ_i386) $(BIN_i386)

DLLWRAP = dllwrap.exe

$(BIN_x64): $(LINKOBJ_x64)
	$(DLLWRAP) $(LINKOBJ_x64) -o $(BIN_x64) $(LIBS) ftd2xx-x64.lib  

$(LINKOBJ_x64): usbThermometer.cpp
	$(CPP) -c usbThermometer.cpp -o $(LINKOBJ_x64) $(CXXFLAGS)
	
$(BIN_i386): $(LINKOBJ_i386)
	$(DLLWRAP) $(LINKOBJ_i386) -o $(BIN_i386) $(LIBS) ftd2xx-i386.lib  

$(LINKOBJ_i386): usbThermometer.cpp
	$(CPP) -c usbThermometer.cpp -o $(LINKOBJ_i386) $(CXXFLAGS)
