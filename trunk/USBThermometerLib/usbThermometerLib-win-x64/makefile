# Project: USBThermometerLib
# Makefile created by Dev-C++ 4.9.9.2

CPP = g++.exe
DLLWRAP = dllwrap.exe

LIBS = --no-export-all-symbols --add-stdcall-alias
CXXINCS = -I'C:/Program Files/Java/jdk1.7.0_02/include/win32'  -I'C:/Program Files/Java/jdk1.7.0_02/include' 

LINKOBJ_x64  = usbThermometerLib-win-x64.o
BIN_x64  = ./../usbThermometerLib-win-x64.dll
LINKOBJ_i386  = usbThermometerLib-win-i386.o
BIN_i386  = ./../usbThermometerLib-win-i386.dll

CXXFLAGS = $(CXXINCS) -DBUILDING_DLL=1  
RM = rm -f

.PHONY: all all-before all-after clean clean-custom

x64: $(BIN_x64)
i386: $(BIN_i386)

clean: clean-custom
	${RM} $(LINKOBJ_x64) $(BIN_x64) $(LINKOBJ_i386) $(BIN_i386)

$(BIN_x64): $(LINKOBJ_x64)
	$(DLLWRAP) -m64 $(LINKOBJ_x64) $(LIBS) ftd2xx_x64.lib -o $(BIN_x64)

$(LINKOBJ_x64): usbThermometer.cpp
	$(CPP) -m64 -c usbThermometer.cpp -o $(LINKOBJ_x64) $(CXXFLAGS)
	
$(BIN_i386): $(LINKOBJ_i386)
	$(DLLWRAP) -m32 $(LINKOBJ_i386) $(LIBS) ftd2xx_i386.lib -o $(BIN_i386)

$(LINKOBJ_i386): usbThermometer.cpp
	$(CPP) -m32 -c usbThermometer.cpp -o $(LINKOBJ_i386) $(CXXFLAGS)	
