package net.madz.security.login.code;

import net.madz.security.login.interfaces.IPasswordEncryptor;

public class Base64 implements IPasswordEncryptor {

    private static final byte[] encodingTable = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I',
            (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's',
            (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
            (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };
    private static final byte[] decodingTable;
    static {
        decodingTable = new byte[128];
        for ( int i = 0; i < 128; i++ ) {
            decodingTable[i] = (byte) -1;
        }
        for ( int i = 'A'; i <= 'Z'; i++ ) {
            decodingTable[i] = (byte) ( i - 'A' );
        }
        for ( int i = 'a'; i <= 'z'; i++ ) {
            decodingTable[i] = (byte) ( i - 'a' + 26 );
        }
        for ( int i = '0'; i <= '9'; i++ ) {
            decodingTable[i] = (byte) ( i - '0' + 52 );
        }
        decodingTable['+'] = 62;
        decodingTable['/'] = 63;
    }

    public String encrypt(String password) {
        byte[] data = password.getBytes();
        byte[] bytes;
        int modulus = data.length % 3;
        if ( modulus == 0 ) {
            bytes = new byte[( 4 * data.length ) / 3];
        } else {
            bytes = new byte[4 * ( ( data.length / 3 ) + 1 )];
        }
        int dataLength = ( data.length - modulus );
        int a1;
        int a2;
        int a3;
        for ( int i = 0, j = 0; i < dataLength; i += 3, j += 4 ) {
            a1 = data[i] & 0xff;
            a2 = data[i + 1] & 0xff;
            a3 = data[i + 2] & 0xff;
            bytes[j] = encodingTable[( a1 >>> 2 ) & 0x3f];
            bytes[j + 1] = encodingTable[( ( a1 << 4 ) | ( a2 >>> 4 ) ) & 0x3f];
            bytes[j + 2] = encodingTable[( ( a2 << 2 ) | ( a3 >>> 6 ) ) & 0x3f];
            bytes[j + 3] = encodingTable[a3 & 0x3f];
        }
        int b1;
        int b2;
        int b3;
        int d1;
        int d2;
        switch (modulus) {
        case 0: /* nothing left to do */
            break;
        case 1:
            d1 = data[data.length - 1] & 0xff;
            b1 = ( d1 >>> 2 ) & 0x3f;
            b2 = ( d1 << 4 ) & 0x3f;
            bytes[bytes.length - 4] = encodingTable[b1];
            bytes[bytes.length - 3] = encodingTable[b2];
            bytes[bytes.length - 2] = (byte) '=';
            bytes[bytes.length - 1] = (byte) '=';
            break;
        case 2:
            d1 = data[data.length - 2] & 0xff;
            d2 = data[data.length - 1] & 0xff;
            b1 = ( d1 >>> 2 ) & 0x3f;
            b2 = ( ( d1 << 4 ) | ( d2 >>> 4 ) ) & 0x3f;
            b3 = ( d2 << 2 ) & 0x3f;
            bytes[bytes.length - 4] = encodingTable[b1];
            bytes[bytes.length - 3] = encodingTable[b2];
            bytes[bytes.length - 2] = encodingTable[b3];
            bytes[bytes.length - 1] = (byte) '=';
            break;
        }
        return new String(bytes);
    }
}
