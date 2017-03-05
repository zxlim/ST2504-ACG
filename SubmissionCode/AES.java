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

public class AES implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	private byte[] message;
	private byte[] iv;

	AES(final byte[] message, final byte[] iv) {
		this.message = message;
		this.iv = iv;
	}

	byte[] getMessage() {
		return message;
	}

	byte[] getIV() {
		return iv;
	}
}
