package org.activehome.tools.file;

/**
 * List of Type Mine and their description.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public enum TypeMime {

    html("text/html"),
    css("text/css"),
    js("application/javascript"),
    png("image/png"),
    ico("image/ico"),
    jpg("image/jpeg"),
    svg("image/svg+xml"),
    gif("image/gif");

    private final String desc;

    TypeMime(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
