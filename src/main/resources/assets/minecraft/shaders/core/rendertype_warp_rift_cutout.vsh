#version 150

#moj_import <light.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;

// Added by Warp
uniform vec2 RiftCastDir;
uniform vec2 RiftCastOrigin;
uniform float RiftSourceDist;
uniform float RiftTargetDist;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;

void main() {
	vec2 vecFromOrigin = Position.xz - RiftCastOrigin;
	float perpDistFromOrigin = dot(vecFromOrigin, RiftCastDir);

	if (perpDistFromOrigin > RiftSourceDist) {
		vec2 shiftVec = RiftCastDir * (RiftSourceDist - RiftTargetDist);
		vec3 warpedCoord = Position - vec3(shiftVec.x, 0.0, shiftVec.y);
		gl_Position = ProjMat * ModelViewMat * vec4(warpedCoord + ChunkOffset, 1.0);
	} else if (perpDistFromOrigin < 0.0) {
		gl_Position = ProjMat * ModelViewMat * vec4(Position + ChunkOffset, 1.0);
	} else {
		vec2 shiftVec = RiftCastDir * (perpDistFromOrigin - ((perpDistFromOrigin / RiftSourceDist) * RiftTargetDist));
		//vec2 shiftVec = RiftCastDir * (perpDistFromOrigin - (smoothstep(0.0, RiftSourceDist, perpDistFromOrigin) * RiftTargetDist));
		vec3 warpedCoord = Position - vec3(shiftVec.x, 0.0, shiftVec.y);
		gl_Position = ProjMat * ModelViewMat * vec4(warpedCoord + ChunkOffset, 1.0);
	}

    vertexDistance = length((ModelViewMat * vec4(Position + ChunkOffset, 1.0)).xyz);
    vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
