;NSIS Modern User Interface
;Multilingual Example Script
;Written by Joost Verburg

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  !define MUI_PRODUCT "USB Thermometer"
  !define MUI_FILE "USBThermometer"
  !define MUI_VERSION "1.0.0"
  CRCCheck On

  !define MUI_ICON ".\graphics\kni - ikona.ico"
  !define MUI_UNICON ".\graphics\kni - ikona.ico"
  !define MUI_COMPONENTSPAGE_CHECKBITMAP "${NSISDIR}\Contrib\Graphics\Checks\modern.bmp"

  ;Name and file
  Name "${MUI_PRODUCT}"
  OutFile ".\..\${MUI_FILE}.${MUI_VERSION}.exe"
  BrandingText "KNI Electronics"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\${MUI_PRODUCT}"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\${MUI_FILE}" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel user

;--------------------------------
;Variables

  Var StartMenuFolder

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING
  !define MUI_COMPONENTSPAGE_NODESC

;--------------------------------
;Language Selection Dialog Settings

  ;Remember the installer language
  !define MUI_LANGDLL_REGISTRY_ROOT "HKCU" 
  !define MUI_LANGDLL_REGISTRY_KEY "Software\${MUI_FILE}" 
  !define MUI_LANGDLL_REGISTRY_VALUENAME "Installer Language"

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "license.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY

  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${MUI_FILE}" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder

  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages

  !insertmacro MUI_LANGUAGE "English"
  !insertmacro MUI_LANGUAGE "Polish"

;--------------------------------
;Reserve Files
  
  ;If you are using solid compression, files that are required before
  ;the actual installation should be stored first in the data block,
  ;because this will make your installer start faster.
  
  !insertmacro MUI_RESERVEFILE_LANGDLL

;--------------------------------
;Installer Sections

Section "${MUI_PRODUCT}" ps_prog

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  
  File USBThermometer.exe
  File USBThermometer(console).exe
  File sqlite3.exe
  File CDM20814_Setup.exe
  
  File sqlite4java-win32-x86.dll
  File ftd2xx.dll
  File msvcr100.dll
  File rxtxParallel.dll
  File rxtxSerial.dll
  File usbThermometerLib-win-i386.dll
  
  File index.php
  File license.txt
  
  StrCmp $LANGUAGE ${LANG_POLISH} 0 +3
    File config_pl_PL.xml
	Rename $INSTDIR\config_pl_PL.xml $INSTDIR\config.xml

  SetOutPath $INSTDIR\build\classes
  File /r build\classes\*.class
  File /r build\classes\*.properties

  SetOutPath $INSTDIR\jre7
  File /r jre7\*.*

  SetOutPath $INSTDIR\lib
  File /r /x .svn lib\*.*

  SetOutPath $INSTDIR\graphics
  File /r /x .svn graphics\*.* 

  SetOutPath $INSTDIR\help 
  File /r /x .svn help\*.*
  
  ;Store installation folder
  WriteRegStr HKCU "Software\${MUI_FILE}" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

  ;Add/Remove Programs entry
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_FILE}" "DisplayIcon" "$\"$INSTDIR\${MUI_FILE}.exe$\""
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_FILE}" "DisplayName" "${MUI_PRODUCT}"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_FILE}" "UninstallString" "$\"$INSTDIR\Uninstall.exe$\""

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
  ;Create shortcuts
  CreateShortCut "$DESKTOP\${MUI_PRODUCT}.lnk" "$INSTDIR\${MUI_FILE}.exe"
  CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
  CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${MUI_PRODUCT}.lnk" "$INSTDIR\${MUI_FILE}.exe"
  CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall ${MUI_PRODUCT}.lnk" "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

Section "Device drivers" ps_drv

  ExecWait "$INSTDIR\CDM20814_Setup.exe"

SectionEnd

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...

  Delete "$INSTDIR\Uninstall.exe"

  Delete "$DESKTOP\${MUI_PRODUCT}.lnk"

  RMDir /r "$INSTDIR\build"
  RMDir /r "$INSTDIR\jre7"
  RMDir /r "$INSTDIR\lib"
  RMDir /r "$INSTDIR\graphics"
  RMDir /r "$INSTDIR\help"
  RMDir /r "$INSTDIR\log"
  RMDir /r "$INSTDIR\src"

  Delete "$INSTDIR\USBThermometer.exe"
  Delete "$INSTDIR\USBThermometer(console).exe"
  Delete "$INSTDIR\sqlite3.exe"
  Delete "$INSTDIR\CDM20814_Setup.exe"
  
  Delete "$INSTDIR\sqlite4java-win32-x86.dll"
  Delete "$INSTDIR\ftd2xx.dll"
  Delete "$INSTDIR\msvcr100.dll"
  Delete "$INSTDIR\rxtxParallel.dll"
  Delete "$INSTDIR\rxtxSerial.dll"
  Delete "$INSTDIR\usbThermometerLib-win-i386.dll"
  
  Delete "$INSTDIR\index.php"
  Delete "$INSTDIR\license.txt"
  
  RMDir "$INSTDIR"

  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
    
  RMDir /r "$SMPROGRAMS\$StartMenuFolder"

  DeleteRegKey /ifempty HKCU "Software\${MUI_FILE}"

  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_FILE}"

SectionEnd

;--------------------------------
;Installer Functions

Function .onInit

  !insertmacro MUI_LANGDLL_DISPLAY

  !insertmacro SelectSection ${ps_prog}
  !insertmacro UnselectSection ${ps_drv}

FunctionEnd

;--------------------------------
;Uninstaller Functions1

Function un.onInit

  !insertmacro MUI_UNGETLANGUAGE
  
FunctionEnd