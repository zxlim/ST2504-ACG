/*
**	PKI Object for ACG Chat Program.
**
**	Copyright (C) 2017.
**	Written by Lim Zhao Xiang.
**	For educational use only.
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
