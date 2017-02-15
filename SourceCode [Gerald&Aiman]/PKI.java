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
import java.util.Base64;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PKI implements Serializable {

  protected static final long serialVersionUID = 1112122200L;

  private PublicKey pubKey;
  private PrivateKey privKey;

  PKI(final PublicKey pubKey) {
    this.pubKey = pubKey;
  }

  PKI(final PrivateKey privKey) {
    this.privKey = privKey;
  }

  PKI(final PublicKey pubKey, final PrivateKey privKey) {
    this.pubKey = pubKey;
    this.privKey = privKey;
  }

  PublicKey getPublic() {
    return pubKey;
  }

  PrivateKey getPrivate() {
    return privKey;
  }

  byte[] getPubBytes() {
    return pubKey.getEncoded();
  }

  byte[] getPrivBytes() {
    return privKey.getEncoded();
  }
}
