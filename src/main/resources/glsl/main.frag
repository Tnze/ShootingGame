#version 330 core
out vec4 FragColor;

in vec3 Normal;
in vec3 Position;

uniform float mRatio;
uniform vec3 cameraPos;
uniform samplerCube skybox;

void main()
{
    //    vec3 refI = normalize(Position - cameraPos);
    //    vec3 refR = reflect(refI, normalize(Normal));
    //    vec4 refFragColor = vec4(texture(skybox, refR).rgb, 1.0);

    float ratio = 1.00 / mRatio;
    vec3 I = normalize(Position - cameraPos);
    vec3 R = refract(I, normalize(Normal), ratio);
    FragColor = vec4(texture(skybox, R).rgb, 1.0);

}