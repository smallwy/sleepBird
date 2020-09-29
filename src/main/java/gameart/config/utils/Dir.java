package gameart.config.utils;

//朝向
public enum Dir {
	None,
	Left,
	Right,
	Up,
	Down,
	;

	public static Dir valueOf(int dirNum) {
		for (Dir dir : values()) {
			if (dir.ordinal() == dirNum) {
				return dir;
			}
		}
		return null;
	}
}