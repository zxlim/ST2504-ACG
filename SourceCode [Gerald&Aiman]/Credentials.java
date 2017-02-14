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

    protected byte[] setUsername(byte[] username){
        this.username=username;
    }
    //Password
    protected byte[] getPassword(){
        return password;
    }

    protected byte[] setPassword(byte[] password){
        this.password=password;
    }
    //RSA Public
    protected byte[] getRSApub(){
        return RSApub;
    }
    
    protected byte[] setRSApub(byte[] RSApub){
        this.RSApub=RSApub;
    }
    //ECDSA Public
    protected byte[] getECDSApub(){
        return ECDSApub;
    }

    protected byte[] setECDSApub(byte[] ECDSApub){
        this.ECDSApub=ECDSApub;
    }

}
