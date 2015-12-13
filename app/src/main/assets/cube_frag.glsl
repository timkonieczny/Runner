precision mediump float;

varying vec4 vColor;
varying vec3 vLightWeighting;

void main() {
    gl_FragColor = vec4(vColor.rgb * vLightWeighting, vColor.a);
}