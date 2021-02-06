package achievements.misc;

public class Password {

	public final String salt;
	public final String hash;

	private Password(String salt, String hash) {
		this.salt = salt;
		this.hash = hash;
	}

	public static Password generate(String password) {
		// Generate the salt
		var salt = HashManager.generateBytes(16); // 128 bits

		return new Password(
			HashManager.encode(salt),
			HashManager.encode(HashManager.hash(salt, password.getBytes()))
		);
	}

	public static boolean validate(String salt, String password, String hash) {
		var srcHash    = HashManager.hash(HashManager.decode(salt), password.getBytes());
		var targetHash = HashManager.decode(hash);
		for (int i = 0; i < srcHash.length; ++i) {
			if (srcHash[i] != targetHash[i]) {
				return false;
			}
		}
		return true;
	}
}
