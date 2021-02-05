package achievements.misc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Password {

	private static final Random RANDOM = new SecureRandom();

	public final String salt;
	public final String hash;

	private Password(String salt, String hash) {
		this.salt = salt;
		this.hash = hash;
	}

	public static Password generate(String password) {
		// Generate the salt
		var salt = new byte[16]; // 128 bits
		RANDOM.nextBytes(salt);

		return new Password(
			encode(salt),
			encode(hash(salt, password.getBytes()))
		);
	}

	public static boolean validate(String salt, String password, String hash) {
		System.out.println(salt + ", " + password);
		var srcHash    = hash(decode(salt), password.getBytes());
		var targetHash = decode(hash);
		for (int i = 0; i < srcHash.length; ++i) {
			if (srcHash[i] != targetHash[i]) {
				return false;
			}
		}
		return true;
	}

	private static byte[] hash(byte[] salt, byte[] password) {
		try {
			var concat = new byte[salt.length + password.length];
			int i = 0;
			for (; i < salt.length; ++i) {
				concat[i] = salt[i];
			}
			for (int j = 0; j < password.length; ++j) {
				concat[i + j] = password[j];
			}

			var md = MessageDigest.getInstance("SHA-256");
			return md.digest(concat);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String encode(byte[] bytes) {
		var chars = new char[bytes.length << 1];
		for (int i = 0; i < bytes.length; ++i) {
			chars[(i << 1)    ] = toHex(bytes[i] >> 0);
			chars[(i << 1) + 1] = toHex(bytes[i] >> 4);
		}
		return new String(chars);
	}

	private static byte[] decode(String data) {
		var decoded = new byte[data.length() >> 1];
		for (int i = 0; i < data.length(); i += 2) {
			int currentByte =
				(fromHex(data.charAt(i    ))     ) |
				(fromHex(data.charAt(i + 1)) << 4);
			decoded[i >> 1] = (byte) (currentByte & 0xFF);
		}
		return decoded;
	}

	private static char toHex(int halfByte) {
		halfByte = halfByte & 0xF;
		if (0  <= halfByte && halfByte <= 9 ) return (char) (halfByte + '0'     );
		if (10 <= halfByte && halfByte <= 15) return (char) (halfByte + 'a' - 10);
		return '0';
	}

	private static int fromHex(char c) {
		if ('0' <= c && c <= '9') return c - '0';
		if ('A' <= c && c <= 'F') return c - 'A' + 10;
		if ('a' <= c && c <= 'f') return c - 'a' + 10;
		return 0;
	}
}
