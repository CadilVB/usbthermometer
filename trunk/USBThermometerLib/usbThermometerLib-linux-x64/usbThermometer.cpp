/* Replace "dll.h" with the name of your header */

#include "USBThermometerLib_USBThermometerLib.h"

#include "ftd2xx.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>

#define DEBUG 1

#define printd if( DEBUG ) printf

#define MaksymalnaIloscUkladow 100

#define UNKNOWN_ERROR 0
#define DEVICE_ERROR 1
#define CRC_ERROR 2
#define SEARCH_ERROR 3
#define DEVICE_NOT_SUPPORTED 4

int ThrowError( JNIEnv *env, unsigned char errorCode );

void die( JNIEnv *env );

unsigned char owReset( JNIEnv *env, FT_HANDLE ftHandle );
unsigned char owWrite( JNIEnv *env, FT_HANDLE ftHandle, unsigned char data );
unsigned char owRead( JNIEnv *env, FT_HANDLE ftHandle, unsigned char *result );
unsigned char owWriteBit( JNIEnv *env, FT_HANDLE ftHandle, unsigned char data );
unsigned char owReadBit( JNIEnv *env, FT_HANDLE ftHandle, unsigned char *result );

unsigned char crc8(unsigned char *inData, unsigned char DataLength);

JNIEXPORT jintArray JNICALL Java_USBThermometerLib_USBThermometerLib_GetDevicesIds
  (JNIEnv *env, jobject obj)
{
    jintArray result;
    jint devIds[256];

	char SerialNumber[256], Description[256], Dummmy[256];
    FT_HANDLE ftHandle;
    FT_DEVICE ftDevice;
	FT_STATUS ftStatus;
	DWORD iNumDevs;
	DWORD Flags;
    DWORD ID;
    DWORD Type;
    DWORD LocId;

    int NoDevice = 0;

	printd("Get Chip IDs using handle\n");

	FT_DEVICE_LIST_INFO_NODE *devInfo;

	//
	// create the device information list
	//
	if( FT_CreateDeviceInfoList(&iNumDevs) ) { die(env); return NULL; }

	//
	// allocate storage for list based on numDevs
	//
	devInfo = (FT_DEVICE_LIST_INFO_NODE*)malloc(sizeof(FT_DEVICE_LIST_INFO_NODE)*iNumDevs);

	//
	// get the device information list
	//
	ftStatus = FT_GetDeviceInfoList(devInfo, &iNumDevs);
	printd("iNumDevs=%i\n", iNumDevs);

	if (ftStatus == FT_OK) {
		for (int i = 0; i < iNumDevs; i++) {

                printd("Open\n");

                if( FT_Open( i, &ftHandle ) == FT_OK ) {
				    ftStatus = FT_GetDeviceInfo( ftHandle, &ftDevice, &ID, SerialNumber, Description, Dummmy);
				    if( FT_Close((FT_HANDLE)ftHandle) ) { die(env);	return NULL; }

				    if(ftStatus == FT_OK) {
                      if( strcmp("USB Thermometer", Description) == 0 ) {
                        devIds[ NoDevice++ ] = i;
                      }
                      printd("Dev %i:\n", i);
                      printd("  Flags=0x%x\n",Flags);
                      printd("  Type=0x%x\n",Type);
                      printd("  ID=0x%x\n",ID);
                      printd("  LocId=0x%x\n",LocId);
                      printd("  SerialNumber=%s\n",SerialNumber);
                      printd("  Description=%s\n",Description);
                      printd("  ftHandle=%lu\n",ftHandle);
                    }
                }
		}

        result = env->NewIntArray(NoDevice);
        if (result == NULL) {
           ThrowError( env, UNKNOWN_ERROR );
           return NULL; /* out of memory error thrown */
        }

        // move from the temp structure to the java structure
        env->SetIntArrayRegion( result, 0, NoDevice, devIds );
	}

    return result;
}

JNIEXPORT jint JNICALL Java_USBThermometerLib_USBThermometerLib_OpenDevice
  (JNIEnv *env, jobject obj, jint i)
{
    FT_HANDLE ftHandle;
	printd("Open\n");  if( FT_Open( i, &ftHandle ) ) { die(env); return 0; }
	printd("  ftHandle=%lu\n",ftHandle);
	printd("SetDataCharacteristics\n");  if( FT_SetDataCharacteristics( ftHandle, FT_BITS_8, FT_STOP_BITS_2, FT_PARITY_NONE ) ) { die(env); return 0; }
	printd("SetLatency\n");  if( FT_SetLatencyTimer( ftHandle, 2 ) ) { die(env); return 0; }
    printd("SetTimeouts\n"); if( FT_SetTimeouts( ftHandle, 10, 10 ) ) { die(env); return 0; }
    return ((jlong)ftHandle);
}

JNIEXPORT void JNICALL Java_USBThermometerLib_USBThermometerLib_CloseDevice
  (JNIEnv *env, jobject obj, jint ftHandle)
{
   if( FT_ClrRts((FT_HANDLE)ftHandle) ) { die(env); return; }
   if( FT_Close((FT_HANDLE)ftHandle) ) { die(env); return; }
}

JNIEXPORT jstring JNICALL Java_USBThermometerLib_USBThermometerLib_GetDeviceSerial
  (JNIEnv *env, jobject obj, jint ftHandle)
{
    jstring result;

	DWORD ChipID = 0;
	char SerialNumber[256], Description[256], Dummy[256];
	FT_DEVICE ftDevice;
	
	printd("  ftHandle=%lu\n",ftHandle);
	printd("GetDeviceInfo\n"); FT_GetDeviceInfo( (FT_HANDLE)ftHandle, &ftDevice, &ChipID, SerialNumber, Description, Dummy );
	printd("  SerialNumber=%s\n",SerialNumber);

    result = env->NewStringUTF(SerialNumber);
    if (result == NULL) {
       ThrowError( env, UNKNOWN_ERROR );
       return NULL; /* out of memory error thrown */
    }

    return result;
}

JNIEXPORT jobjectArray JNICALL Java_USBThermometerLib_USBThermometerLib_GetSensorsIds
  (JNIEnv *env, jobject obj, jint handle)
{
    jobjectArray result;
    FT_HANDLE ftHandle = (FT_HANDLE)handle;

    jbyte dane[MaksymalnaIloscUkladow][8];
    unsigned char *ptDane;
    unsigned char device_counter;

    //zmienne szukania
    unsigned char id_bit,cmp_id_bit;
    unsigned char id_bit_number;
    int LastDeviceFlag;
    unsigned char LastDiscrepancy;
    unsigned char last_zero;
    unsigned char ROM_NO[64];
    unsigned char search_direction;

    //liczniki dodatkowe
    unsigned char i,j;

    //zerowanie poczatkowe
    ptDane = (unsigned char*)dane;
    device_counter = 0;
    LastDiscrepancy = 0;
    LastDeviceFlag = FALSE;

    if( FT_ClrRts(ftHandle) ) { die(env); return NULL; }

    //powtarzaj az znajdziesz wszystkie uklady 1-wire
    do {

      //przygotowania do odczytu kolejnego czujnika
      id_bit_number = 1;
      last_zero = 0;

      //wyslij kod szukania
      if( owReset( env, ftHandle ) ) { die(env); return NULL; }
      if( owWrite( env, ftHandle, 0xF0 ) ) { die(env); return NULL; }

      //powtazaj az znajdziesz wszystkie bity kolejnego czujnika
      do {
        //odzczyt bitu pierwszego czujnikow i bitu odwrotnego czujnikow
        if( owReadBit( env, ftHandle, &id_bit ) ) { die(env); return NULL; }
        if( owReadBit( env, ftHandle, &cmp_id_bit ) ) { die(env); return NULL; }

        printd("owReadBit %i\n", id_bit);
        printd("owReadBit %i\n", cmp_id_bit);

        //jesli id_bit = comp_id_bit = 1 to blad
        if ( (id_bit == 1) && (cmp_id_bit == 1) ) {
            ThrowError( env, SEARCH_ERROR );
            return NULL;
        }

        //bity rozne czyli id_bit = comp_id_bit = 0
        if ( (id_bit == 0) && (cmp_id_bit == 0) ) {
          //sprawdz czy ostatnia roznica ( przy poprzednim czujniku )
          //wystapila wlasnie tu
          if ( id_bit_number == LastDiscrepancy ) {
            printd("id_bit_number == LastDiscrepancy\n");
            search_direction = 1;
          } else {
            printd("id_bit_number != LastDiscrepancy\n");
            //jesli sprawdzany bit jest pozniejszy niz ten z ostatniej roznicy to
            if ( id_bit_number > LastDiscrepancy ) {
              printd("search_direction = 0\n");
              //wybierz kierunek 0
              search_direction = 0;
            } else {
              printd("search_direction = ROM_NO[id_bit_number]: %i\n",ROM_NO[id_bit_number]);
              search_direction = ROM_NO[id_bit_number];
            }
          }

          if ( search_direction == 0 ) {
            printd("search_direction == 0\n");
            last_zero = id_bit_number;
          }

        } else {
          printd("search_direction = id_bit: %i\n",id_bit);
          // jesli wszystkie bity sa rowne to przejdz do nastepnego bitu
          search_direction = id_bit;
        }

        //zapisz wybor i wyslij na 1-wire
        ROM_NO[id_bit_number] =  search_direction;
        if( owWriteBit( env, ftHandle, search_direction ) ) { die(env); return NULL; }

        //przejdz do kolejnego bitu
        id_bit_number++;

      }
      // dopuki nie sprawdzisz wszystkich bitow
      while( id_bit_number <= 64 );

      LastDiscrepancy = last_zero;

      //zamiana na postac hex i zapis do pamieci wskazanej przez uzytkownika
      for ( i = 0; i < 8; i++ ) {
        *ptDane = 0;
        for ( j = 0; j < 8; j++ ) {
            if( ROM_NO[i*8 + j + 1] == 1) {
                *ptDane = *ptDane + (1 << j);
            }
        }
        ptDane++;
      }

      //zapamietaj ze znaleziono kolejny uklad
      device_counter++;

      //sprawdz czy nie przekroczono liczby ukladow
      if ( device_counter == MaksymalnaIloscUkladow ) {
        LastDeviceFlag = TRUE;
      }

      //jesli nie bylo roznic to znak ze znaleziono oststni uklad
      if ( LastDiscrepancy == 0 ) {
        LastDeviceFlag = TRUE;
      }

    }
    while( LastDeviceFlag != TRUE );

    jclass intArrCls = env->FindClass("[B");
    if (intArrCls == NULL) {
       ThrowError( env, UNKNOWN_ERROR );
       return NULL; /* exception thrown */
    }
    result = env->NewObjectArray( device_counter, intArrCls, NULL);
    if (result == NULL) {
       ThrowError( env, UNKNOWN_ERROR );
       return NULL; /* out of memory error thrown */
    }

    for( i = 0; i < device_counter; i++ ) {
        jbyteArray iarr = env->NewByteArray(8);
        if (iarr == NULL) {
           ThrowError( env, UNKNOWN_ERROR );
           return NULL; /* out of memory error thrown */
        }

        if( crc8( (unsigned char*)&dane[i][0], 7 ) != (unsigned char)dane[i][7] ) {
            ThrowError( env, CRC_ERROR );
            return NULL;
        }

        env->SetByteArrayRegion(iarr, 0, 8, &dane[i][0]);
        env->SetObjectArrayElement(result, i, iarr);
        env->DeleteLocalRef(iarr);
    }

    return result;
}

JNIEXPORT void JNICALL Java_USBThermometerLib_USBThermometerLib_StartConversion
  (JNIEnv *env, jobject obj, jint handle)
{
    FT_HANDLE ftHandle = (FT_HANDLE)handle;

    if( FT_ClrRts(ftHandle) ) { die(env); return; }

	if( owReset( env, ftHandle) ) { die(env); return; }
    if( owWrite( env, ftHandle, 0xcc) ) { die(env); return; }
    if( FT_SetRts(ftHandle) ) { die(env); return; }
    if( owWrite( env, ftHandle, 0x44) ) { die(env); return; }
}

JNIEXPORT jdouble JNICALL Java_USBThermometerLib_USBThermometerLib_GetTemperture
  (JNIEnv *env, jobject obj, jint handle, jbyteArray arr)
{
    FT_HANDLE ftHandle = (FT_HANDLE)handle;

    jbyte *carr;
    carr = env->GetByteArrayElements(arr, NULL);
    if (carr == NULL) {
       ThrowError( env, UNKNOWN_ERROR );
       return 0; /* exception occurred */
    }

    unsigned char recData[8];
    unsigned char crc;

    if( FT_ClrRts(ftHandle) ) { die(env); return 0; }

    if( owReset( env, ftHandle) ) { die(env); return 0; }
    if( owWrite( env, ftHandle, 0x55) ) { die(env); return 0; }
    for (int i = 0; i < 8; i++)
    {
        if( owWrite( env, ftHandle, (unsigned char)carr[i]) ) { die(env); return 0; }
    }
    if( owWrite( env, ftHandle, 0xBE) ) { die(env); return 0; }
    for (int i = 0; i < 8; i++)
    {
        if( owRead( env, ftHandle, &recData[i] ) ) { die(env); return 0; }
    }
    if( owRead( env, ftHandle, &crc ) ) { die(env); return 0; }

    if( crc != crc8( recData, sizeof(recData) ) ) {
        ThrowError( env, CRC_ERROR );
        return 0;
    }

    double temperature = 0;
    switch( carr[0] ) {
        case 0x10:
            if( recData[1] & 0x80 ) {
               temperature = (double)(-((-(recData[1] * 0x100 + recData[0])) & 0xffff)) / 2;
            } else {
               temperature = (double)(recData[1] * 0x100 + recData[0]) / 2;
            }
            break;
        case 0x28:
            if( recData[1] & 0x80 ) {
               temperature = (double)(-((-(recData[1] * 0x100 + recData[0])) & 0xffff)) / 16;
            } else {
               temperature = (double)(recData[1] * 0x100 + recData[0]) / 16;
            }
            break;
        default:
            ThrowError( env, DEVICE_NOT_SUPPORTED );
            return 0;
    }

    return temperature;
}

//------------------------------------------------------------------------
// private functions
// -----------------------------------------------------------------------
int ThrowError( JNIEnv *env, unsigned char errorCode )
{
     jclass newExcCls;

     switch( errorCode ) {
        case UNKNOWN_ERROR:
             newExcCls = env->FindClass("USBThermometerLib/ExceptionErrors/UnknownError");
             break;
        case DEVICE_ERROR:
             newExcCls = env->FindClass("USBThermometerLib/ExceptionErrors/DeviceError");
             break;
        case CRC_ERROR:
             newExcCls = env->FindClass("USBThermometerLib/ExceptionErrors/CrcError");
             break;
        case SEARCH_ERROR:
             newExcCls = env->FindClass("USBThermometerLib/ExceptionErrors/SearchError");
             break;
        case DEVICE_NOT_SUPPORTED:
             newExcCls = env->FindClass("USBThermometerLib/ExceptionErrors/DeviceNotSupported");
             break;
     }

     if (newExcCls == NULL) {
        printf("Unable to find the exception class, give up.");
        return 1;
     }

     env->ThrowNew(newExcCls, "Usb Thermometer Error");
     return 0;
}

unsigned char owReset( JNIEnv *env, FT_HANDLE ftHandle )
{
    unsigned char readBuffer[64];
    unsigned char sendBuffer[1];
    DWORD bytesWritten;
    DWORD bytesReturned;
    DWORD rxBytes;
    int timeout;

    printd("__owReset__\n");

    if( FT_SetBaudRate( ftHandle, 9600 ) ) { die(env); return 1; }

    printd("owWrite: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    timeout = 10;
    while( ( rxBytes > 0 ) && (timeout-- > 0 ) ){
        printd("owWrite: read\n"); if( FT_Read( ftHandle, readBuffer, rxBytes, &bytesReturned ) ) { die(env); return 1; }
        printd("owWrite: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    }
    if( timeout == 0 ) { die(env); return 1; }

    sendBuffer[0] = 0x00;
    rxBytes = 0;
    printd("owReset: write\n"); if( FT_Write( ftHandle, sendBuffer, 1, &bytesWritten ) ) { die(env); return 1; }
    timeout = 10;
    while( ( rxBytes == 2 ) && (timeout-- > 0 ) ) {
        printd("owReset: queue\n");  if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    }
    if( timeout == 0 ) { die(env); return 1; }

    printd("owReset: read\n");  if( FT_Read( ftHandle, readBuffer, 2, &bytesReturned ) ) { die(env); return 1; }

    printd("owReset: byte returned: 0x%02x 0x%02x\n", readBuffer[0], readBuffer[1] );

    return 0;
}

unsigned char owWrite( JNIEnv *env, FT_HANDLE ftHandle, unsigned char data )
{
    unsigned char readBuffer[64];
    unsigned char sendBuffer[8];
    DWORD bytesWritten;
    DWORD bytesReturned;
    DWORD rxBytes;
    int timeout;

    printd("__owWrite__\n");

    if( FT_SetBaudRate( ftHandle, 115200 ) ) { die(env); return 1; }

    printd("owWrite: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    timeout = 10;
    while( ( rxBytes > 0 ) && (timeout-- > 0 ) ) {
        printd("owWrite: read\n"); if( FT_Read( ftHandle, readBuffer, rxBytes, &bytesReturned ) ) { die(env); return 1; }
        printd("owWrite: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    }
    if( timeout == 0 ) { die(env); return 1; }

    for (int i = 0; i < 8; i++) {
        if ((data & (1 << i)) != 0) {
            sendBuffer[i] = 0xff;
        } else {
            sendBuffer[i] = 0xf8;
        }
    }

    printd("owWrite: write\n"); if( FT_Write( ftHandle, sendBuffer, 8, &bytesWritten ) ) { die(env); return 1; }

    return 0;
}

unsigned char owRead( JNIEnv *env, FT_HANDLE ftHandle, unsigned char *result )
{
    *result = 0;
    unsigned char readBuffer[64];
    unsigned char sendBuffer[8] = { 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff };
    DWORD bytesWritten;
    DWORD bytesReturned;
    DWORD rxBytes;
    int timeout;

    printd("__owRead__\n");

    if( FT_SetBaudRate( ftHandle, 115200 ) ) { die(env); return 1; }

    printd("owRead: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    timeout = 10;
    while( ( rxBytes > 0 ) && (timeout-- > 0 ) ) {
        printd("owRead: read\n"); if( FT_Read( ftHandle, readBuffer, rxBytes, &bytesReturned ) ) { die(env); return 1; }
        printd("owRead: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    }
    if( timeout == 0 ) { die(env); return 1; }

    printd("owRead: write\n"); if( FT_Write( ftHandle, sendBuffer, 8, &bytesWritten ) ) { die(env); return 1; }

    timeout = 10;
    printd("owRead: queue \n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    while( ( rxBytes < 8 ) && (timeout-- > 0 ) ) {
       usleep(1000);
       printd("owRead: queue %i\n", 1000 - timeout); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    }
    if( timeout == 0 ) { die(env); return 1; }

    for (int i = 0; i < 8; i++)
    {
        bytesReturned = 0;
        printd("owRead: read\n");  if( FT_Read( ftHandle, readBuffer, 1, &bytesReturned ) ) { die(env); return 1; }

        if (readBuffer[0] == 0xff) {
            *result |= (char)(1 << i);
        }
    }

    return 0;
}

unsigned char owWriteBit( JNIEnv *env, FT_HANDLE ftHandle, unsigned char data )
{
    unsigned char readBuffer[64];
    unsigned char sendBuffer[1];
    DWORD bytesWritten;
    DWORD bytesReturned;
    DWORD rxBytes;
    int timeout;

    printd("__owWriteBit__\n");

    if( FT_SetBaudRate( ftHandle, 115200 ) ) { die(env); return 1; }

    printd("owWriteBit: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    timeout = 10;
    while( ( rxBytes > 0 ) && (timeout-- > 0 ) ) {
        printd("owWriteBit: read\n"); if( FT_Read( ftHandle, readBuffer, rxBytes, &bytesReturned ) ) { die(env); return 1; }
        printd("owWriteBit: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    }
    if( timeout == 0 ) { die(env); return 1; }

    if (data != 0) {
        sendBuffer[0] = 0xff;
    } else {
        sendBuffer[0] = 0xf8;
    }

    printd("owWriteBit: write %i\n", (data ? 1 : 0) ); if( FT_Write( ftHandle, sendBuffer, 1, &bytesWritten ) ) { die(env); return 1; }

    return 0;
}

unsigned char owReadBit( JNIEnv *env, FT_HANDLE ftHandle, unsigned char *result )
{
    *result = 1;
    unsigned char readBuffer[64];
    unsigned char sendBuffer[1];
    DWORD bytesWritten;
    DWORD bytesReturned;
    DWORD rxBytes;
    int timeout;

    printd("__owReadBit__\n");

    if( FT_SetBaudRate( ftHandle, 115200 ) ) { die(env); return 1; }

    printd("owReadBit: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    timeout = 10;
    while( ( rxBytes > 0 ) && (timeout-- > 0 ) ) {
        printd("owReadBit: read\n"); if( FT_Read( ftHandle, readBuffer, rxBytes, &bytesReturned ) ) { die(env); return 1; }
        printd("owReadBit: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    }
    if( timeout == 0 ) { die(env); return 1; }

    sendBuffer[0] = 0xff;
    printd("owReadBit: write\n"); if( FT_Write( ftHandle, sendBuffer, 1, &bytesWritten ) ) { die(env); return 1; }

    timeout = 10;
    printd("owReadBit: queue\n"); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    while( ( rxBytes == 0 ) && (timeout-- > 0 ) ) {
        usleep(1000);
        printd("owReadBit: queue %i\n", 1000 - timeout); if( FT_GetQueueStatus( ftHandle, &rxBytes ) ) { die(env); return 1; }
    }
    if( timeout == 0 ) { die(env); return 1; }

    bytesReturned = 0;
    printd("owRead: read\n");  if( FT_Read( ftHandle, readBuffer, 1, &bytesReturned ) ) { die(env); return 1; }

    printd("owReadBit: bytesReturned %i: ", bytesReturned );
    for(int i = 0; i < bytesReturned; i++) {
        printd("0x%02x ", readBuffer[i]);
    }
    printd("\n");

    if( ( readBuffer[0] != 0xff ) && ( readBuffer[0] != 0xfe ) ) {
        *result = 0;
    }

    return 0;
}

unsigned char crc8(unsigned char *inData, unsigned char len)
{
  unsigned char crc;

   crc = 0;
   for(; len; len--)
   {
      crc ^= *inData++;
      crc ^= (crc << 3) ^ (crc << 4) ^ (crc << 6);
      crc ^= (crc >> 4) ^ (crc >> 5);
   }
   return crc;
}

void die( JNIEnv *env )
{
     ThrowError(env, DEVICE_ERROR );
}
