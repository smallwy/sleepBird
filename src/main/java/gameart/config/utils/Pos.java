package gameart.config.utils;

import java.util.Objects;

public class Pos {
	/**
	 * 坐标X
	 */
	public int x;
	/**
	 * 坐标z
	 */
	public int z;

	/**
	 * 朝向
	 */
	public Dir dir;

	public Pos() {
	}

	public Pos(int x, int z, int dir) {
		this.x = x;
		this.z = z;
		this.dir = Dir.valueOf(dir);
	}

	public Pos(int x, int z, Dir dir) {
		this.x = x;
		this.z = z;
		this.dir = dir;
	}

	public Pos(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public Pos(Pos pos, Dir dir) {
		this.x = pos.x;
		this.z = pos.z;
		this.dir = dir;
	}

	public Pos(Pos pos) {
		this.x = pos.x;
		this.z = pos.z;
		this.dir = pos.dir;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Pos pos = (Pos) o;
		return x == pos.x && z == pos.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, z, dir);
	}

	public static int getDistance(Pos a, Pos b) {
		return Math.abs(a.x - b.x) + Math.abs(a.z - b.z);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "Pos{" +
				"x=" + x +
				", z=" + z +
				", dir=" + dir +
				'}';
	}
}
