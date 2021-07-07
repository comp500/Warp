package link.infra.warp.util;

import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;

public class RotatedBox {
	private final Box unrotated;
	private final Matrix4f rotation;
	private final Matrix4f rotationInverse;

	public RotatedBox(Vec3d center, double dx, double dy, double dz, Matrix4f rotation) {
		unrotated = Box.of(center, dx, dy, dz);
		this.rotation = rotation;
		this.rotationInverse = rotation.copy();
		rotationInverse.invert();
	}

	private Vec3d transformInverse(Vec3d src) {
		Vector4f vec = new Vector4f(new Vec3f(src));
		vec.transform(rotationInverse);
		return new Vec3d(new Vec3f(vec));
	}

	public boolean contains(Vec3d pos) {
		Vector4f vec = new Vector4f(new Vec3f(pos));
		vec.transform(rotationInverse);
		return unrotated.contains(new Vec3d(new Vec3f(vec)));
	}

	public boolean intersects(Box box) {
		return unrotated.intersects(
			transformInverse(new Vec3d(box.minX, box.minY, box.minZ)),
			transformInverse(new Vec3d(box.maxX, box.maxY, box.maxZ))
		);
	}

	public boolean intersects(Vec3d min, Vec3d max) {
		return unrotated.intersects(
			transformInverse(min),
			transformInverse(max)
		);
	}

	public List<ChunkSectionPos> coveredSections() {
		int minX = (int) unrotated.minX >> 4;
		int minY = (int) unrotated.minY >> 4;
		int minZ = (int) unrotated.minZ >> 4;
		int maxX = (int) unrotated.maxX >> 4;
		int maxY = (int) unrotated.maxY >> 4;
		int maxZ = (int) unrotated.maxZ >> 4;

		List<ChunkSectionPos> sections = new ArrayList<>();
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (intersects(
						new Vec3d(x << 4, y << 4, z << 4),
						new Vec3d((x << 4) + 15, (y << 4) + 15, (z << 4) + 15)
					)) {
						sections.add(ChunkSectionPos.from(x, y, z));
					}
				}
			}
		}
		return sections;
	}
}
