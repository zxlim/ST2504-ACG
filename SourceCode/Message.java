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

public class Message implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	static final int MESSAGE = 0, WHISPER = 1, WHOISIN = 2, LOGIN = 3, LOGOUT = 4;
	private int type;
	private byte[] sender;
	private byte[] receiver;
	private byte[] message;
	private AES encryptedMessage;
	private byte[] signature;

	//WHOISIN and LOGOUT Message object
	Message(final int type) {
		this.type = type;
	}

	//Unencrypted Message object
	Message(final int type, final byte[] message, final byte[] signature) {
		this.type = type;
		this.message = message;
		this.signature = signature;
	}

	//Encrypted Message object
	Message(final int type, final AES encryptedMessage, final byte[] signature) {
		this.type = type;
		this.encryptedMessage = encryptedMessage;
		this.signature = signature;
	}

	//Whisper (Encrypted) Message object
	Message(final int type, final byte[] sender, final byte[] receiver, final AES encryptedMessage, final byte[] signature) {
		this.type = type;
		this.sender = sender;
		this.receiver = receiver;
		this.encryptedMessage = encryptedMessage;
		this.signature = signature;
	}

	int getType() {
		return type;
	}

	byte[] getSender() {
		return sender;
	}

	byte[] getReceiver() {
		return receiver;
	}

	byte[] getMessage() {
		return message;
	}

	AES getEncrypted() {
		return encryptedMessage;
	}

	byte[] getSignature() {
		return signature;
	}
} //Class
