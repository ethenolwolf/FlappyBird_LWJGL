# version 130

in vec2 UV;

out vec4 out_color;

uniform sampler2D tex;

void main() {
    out_color = texture(tex, UV);
}
