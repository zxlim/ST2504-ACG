/*
**	AES Object for ACG Chat Program.
**
**	Copyright (C) 2017.
**	Written by Lim Zhao Xiang.
**	For educational use only.
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
