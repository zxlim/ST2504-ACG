import java.io.Serializable;
import java.util.Base64;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Credentials implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    private byte[] username;
    private byte[] password;
    private byte[] RSApub;
    private byte[] ECDSApub;

    Credentials(byte[] username, byte[] password, byte[] RSApub, byte[] ECDSApub) {
        this.username = username;
        this.password = password;
        this.RSApub = RSApub;
        this.ECDSApub = ECDSApub;
    }
    //Username
    protected byte[] getUsername(){
        return username;
    }

    protected void setUsername(byte[] username){
        this.username=username;
    }
    //Password
    protected byte[] getPassword(){
        return password;
    }

    protected void setPassword(byte[] password){
        this.password=password;
    }
    //RSA Public
    protected byte[] getRSApub(){
        return RSApub;
    }

    protected void setRSApub(byte[] RSApub){
        this.RSApub=RSApub;
    }
    //ECDSA Public
    protected byte[] getECDSApub(){
        return ECDSApub;
    }

    protected void setECDSApub(byte[] ECDSApub){
        this.ECDSApub=ECDSApub;
    }

}
