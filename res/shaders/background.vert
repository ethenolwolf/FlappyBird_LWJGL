# version 130

in vec3 position;   // layout (location = 0)
in vec2 uv;         // layout (location = 1)

out vec2 UV;

uniform mat4 pr_matrix;     // projection
uniform mat4 vw_matrix;     // view
uniform mat4 ml_matrix;     // model

void main() {
    gl_Position = pr_matrix * vw_matrix * ml_matrix * vec4(position, 1.0f);

    UV = uv;
}
