package org.activehome.context.data;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Embedded in Request, UserInfo provides the details on the user.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class UserInfo {

    /**
     * The user id.
     */
    private final String id;
    /**
     * The array of groups the user is in.
     */
    private final String[] groups;
    /**
     * The household name.
     */
    private final String household;
    /**
     * The user type (Java class).
     */
    private final String userType;

    /**
     * @param theId The user id
     * @param theGroups The array of groups the user is in
     * @param theHousehold The household name
     * @param theUserType The user type (Java class)
     */
    public UserInfo(final String theId, final String[] theGroups,
                    final String theHousehold, final String theUserType) {
        id = theId;
        groups = theGroups;
        household = theHousehold;
        userType = theUserType;
    }

    /**
     * @return The user id
     */
    public final String getId() {
        return id;
    }

    /**
     * @return The array of groups the user is in
     */
    public final String[] getGroups() {
        return groups;
    }

    /**
     * @return The household name
     */
    public final String getHousehold() {
        return household;
    }

    /**
     * @return The user type (Java class)
     */
    public final String getUserType() {
        return userType;
    }

    /**
     * @param json Json that can be map as UserInfo
     */
    public UserInfo(final JsonObject json) {
        id = json.get("id").asString();
        household = json.get("household").asString();
        userType = json.get("userType").asString();
        JsonArray array = json.get("groups").asArray();
        groups = new String[array.size()];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = array.get(i).asString();
        }
    }

    /**
     * Convert the UserInfo into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", UserInfo.class.getName());
        json.add("id", id);
        json.add("household", household);
        json.add("userType", userType);
        JsonArray array = new JsonArray();
        for (String group : groups) {
            array.add(group);
        }
        json.add("groups", array);
        return json;
    }

    /**
     * @return true if user is in the admin group
     */
    public final boolean isAdmin() {
        for (String group : getGroups()) {
            if (group.compareTo("admin") == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return The Json as String
     */
    @Override
    public final String toString() {
        return toJson().toString();
    }
}
