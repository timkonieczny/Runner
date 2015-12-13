attribute vec4 aPosition;
attribute vec4 aColor;
attribute vec3 aNormal;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat3 uNormalMatrix;
varying vec4 vColor;
varying vec3 vLightWeighting;

void main() {
    gl_Position = uProjectionMatrix * uViewMatrix * aPosition;
    vColor = aColor;

    vec3 transformedNormal = uNormalMatrix * aNormal;
    float directionalLightWeighting = max(dot(transformedNormal, vec3(1.0,0.0,0.0)), 0.0);
    vLightWeighting = vec3(0.3,0.3,0.3) + vec3(1.0,1.0,1.0) * directionalLightWeighting;
}