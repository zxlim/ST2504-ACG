/*
**	[ST2504 Applied Cryptography Assignment]
**	[Encrypted Chat Program]
**
**	Aeron Teo (P1500725)
**	Aiman Abdul Rashid (P1529335)
**	Gerald Peh (P1445972)
**	Lim Zhao Xiang (P1529559)
*/

import java.io.Serializable;

public class Credentials implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    private byte[] username;
    private byte[] password;
    private byte[] rsaPub;
    private byte[] ecdsaPub;

    Credentials(final byte[] username, final byte[] password) {
        this.username = username;
        this.password = password;
    }

    Credentials(final byte[] username, final byte[] password, final byte[] rsaPub, final byte[] ecdsaPub) {
        this.username = username;
        this.password = password;
        this.rsaPub = rsaPub;
        this.ecdsaPub = ecdsaPub;
    }

    //Encrypted Username
    byte[] getUsername(){
        return username;
    }

    //Encrypted Password
    byte[] getPassword(){
        return password;
    }

    //Encrypted RSA Public Key
    byte[] getRsaPub(){
        return rsaPub;
    }

    //Encrypted ECDSA Public Key
    byte[] getEcdsaPub(){
        return ecdsaPub;
    }
} //Class
