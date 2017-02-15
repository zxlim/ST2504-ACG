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

    //Username
    protected byte[] getUsername(){
        return username;
    }

    protected void setUsername(final byte[] username){
        this.username = username;
    }

    //Password
    protected byte[] getPassword(){
        return password;
    }

    protected void setPassword(final byte[] password){
        this.password = password;
    }

    //RSA Public
    protected byte[] getRsaPub(){
        return rsaPub;
    }

    protected void setRsaPub(final byte[] rsaPub){
        this.rsaPub = rsaPub;
    }

    //ECDSA Public Key
    protected byte[] getEcdsaPub(){
        return ecdsaPub;
    }

    protected void setEcdsaPub(final byte[] ecdsaPub){
        this.ecdsaPub = ecdsaPub;
    }
} //Class
