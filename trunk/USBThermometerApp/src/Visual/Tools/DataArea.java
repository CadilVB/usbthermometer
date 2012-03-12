/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Tools;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import javax.swing.JTextArea;

/**
 *
 * @author pawelkn
 */
public class DataArea extends JTextArea {

    public static final int DATASTATE_HEX = 0;
    public static final int DATASTATE_ASCI = 1;

    private int dataState = DATASTATE_ASCI;

    public DataArea() {
        super();

        setFont(new Font("Tahoma", 0, 11));

        getDocument().addDocumentListener( new DocumentAdapter() {
            @Override
            public void update() {
                if(dataState == DATASTATE_HEX) {
                    if( tryParseHexToBytes( getText() ) ) {
                        setForeground(Color.black);
                    } else {
                        setForeground(Color.red);
                    }
                } else {
                    setForeground(Color.black);
                }
            }
        });
    }
    
    public void setDataState(int state) {
        if ( (state == DATASTATE_ASCI) && (dataState == DATASTATE_HEX)) {
            dataState = DATASTATE_ASCI;
            byte[] bytes = parseHexToBytes(getText());
            setText(parseBytesToAsci(bytes));
        } else if ( (state == DATASTATE_HEX) && (dataState == DATASTATE_ASCI)) {
            dataState = DATASTATE_HEX;
            byte[] bytes = parseAsciToBytes(getText());
            setText(parseBytesToHex(bytes));
        }        
    }

    public void setData(byte[] data) {
        if( dataState == DATASTATE_HEX ) {
            setText(parseBytesToHex(data));
        } else {
            setText(parseBytesToAsci(data));
        }
    }

    public byte[] getData() {
        if( dataState == DATASTATE_HEX ) {
            return parseHexToBytes( getText() );
        } else {
            return parseAsciToBytes( getText() );
        }
    }

    private String parseBytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if( bytes != null ) {
            for(byte b : bytes) {
                sb.append("<0x");
                String hexString = Integer.toHexString((int)b & 0xff);
                if( hexString.length() == 1 ) {
                    sb.append('0');
                }
                sb.append(hexString);
                sb.append('>');
            }
        }
        return sb.toString();
    }

    private String parseBytesToAsci(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if( bytes != null ) {
            for(byte b : bytes) {
                sb.append((char)b);
            }
        }
        return sb.toString();
    }

    private boolean tryParseHexToBytes(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if( text != null ) {
            char[] textChars = text.toCharArray();

            for(int i = 0; i < textChars.length; i++ ) {
                if( textChars[i] == '<' ) {
                    if( ( i + 5 >= textChars.length ) ||
                        ( textChars[i + 1] != '0' ) ||
                        ( textChars[i + 2] != 'x' ) ||
                        ( !isHexNumber(textChars[i + 3]) ) ||
                        ( !isHexNumber(textChars[i + 4]) ) ||
                        ( textChars[i + 5] != '>' ) ) {
                        return false;
                    } else {
                        char[] number = new char[] { textChars[i + 3], textChars[i + 4] };
                        byte b = (byte)Integer.parseInt( new String(number), 16 );
                        baos.write(b);
                        i += 5;
                    }
                } else {
                    if(( textChars[i] != 0x0a ) && ( textChars[i] != 0x0a ) && ( textChars[i] != ' ' ) ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isHexNumber(char c) {
        if ( ( Character.isDigit(c) ) ||
             ( Character.toUpperCase(c) == 'A' ) ||
             ( Character.toUpperCase(c) == 'B' ) ||
             ( Character.toUpperCase(c) == 'C' ) ||
             ( Character.toUpperCase(c) == 'D' ) ||
             ( Character.toUpperCase(c) == 'E' ) ||
             ( Character.toUpperCase(c) == 'F' ) ) {
            return true;
        } else {
            return false;
        }
    }

    private byte[] parseHexToBytes(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if( text != null ) {
            char[] textChars = text.toCharArray();

            for(int i = 0; i < textChars.length; i++ ) {
                if( textChars[i] == '<' ) {
                    if( ( i + 5 >= textChars.length ) ||
                        ( textChars[i + 1] != '0' ) ||
                        ( textChars[i + 2] != 'x' ) ||
                        ( !isHexNumber(textChars[i + 3]) ) ||
                        ( !isHexNumber(textChars[i + 4]) ) ||
                        ( textChars[i + 5] != '>' ) ) {
                        // nothing to do
                    } else {
                        char[] number = new char[] { textChars[i + 3], textChars[i + 4] };
                        byte b = (byte)Integer.parseInt( new String(number), 16 );
                        baos.write(b);
                        i += 5;
                    }
                }
            }
        }
        return baos.toByteArray();
    }

    private byte[] parseAsciToBytes(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if( text != null ) {
            char[] textChars = text.toCharArray();

            for(int i = 0; i < textChars.length; i++ ) {
                baos.write(textChars[i]);
            }
        }
        return baos.toByteArray();
    }
}
