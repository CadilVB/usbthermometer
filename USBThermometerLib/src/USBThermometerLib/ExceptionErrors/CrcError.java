package USBThermometerLib.ExceptionErrors;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pawelkn
 */
public class CrcError extends Exception {
    public CrcError(){}

    public CrcError(String txt) {
        super(txt);
    }
}
