/*
LICENSE of these Class:
MIT License

Copyright (c) 2020 Nico Mexis

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
© 2021 GitHub, Inc.

From Github: https://github.com/ThexXTURBOXx/McUUIDFetcher
(Latest acess: 06.06.2021)
 */

package de.freddy.money_system;//package your.package.here;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

/*
Uncomment this if you want the helper method for BungeeCord:
import net.md_5.bungee.api.connection.ProxiedPlayer;
*/

/*
Uncomment this if you want the helper method for Bukkit/Spigot:
import org.bukkit.entity.Player;
*/

/**
 * Helper-class for getting UUIDs of players.
 */
public final class UUIDFetcher {

    private static final String UUID_URL = "https://api.mojang.com/users"
            + "/profiles/minecraft/";

    private UUIDFetcher() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the UUID of the searched player.
     *
     * @param player The player.
     * @return The UUID of the given player.
     */
    //Uncomment this if you want the helper method for BungeeCord:
    /*
    public static UUID getUUID(ProxiedPlayer player) {
        return getUUID(player.getName());
    }
    */

    /**
     * Returns the UUID of the searched player.
     *
     * @param player The player.
     * @return The UUID of the given player.
     */
    //Uncomment this if you want the helper method for Bukkit/Spigot:
    /*
    public static UUID getUUID(Player player) {
        return getUUID(player.getName());
    }
    */

    /**
     * Returns the UUID of the searched player.
     *
     * @param playername The name of the player.
     * @return The UUID of the given player.
     */
    public static UUID getUUID(String playername) {
        String output = callURL(UUID_URL + playername);
        StringBuilder result = new StringBuilder();
        readData(output, result);
        String u = result.toString();
        StringBuilder uuid = new StringBuilder();
        for (int i = 0; i <= 31; i++) {
            uuid.append(u.charAt(i));
            if (i == 7 || i == 11 || i == 15 || i == 19) {
                uuid.append('-');
            }
        }
        return UUID.fromString(uuid.toString());
    }

    private static void readData(String toRead, StringBuilder result) {
        for (int i = toRead.length() - 3; i >= 0; i--) {
            if (toRead.charAt(i) != '"') {
                result.insert(0, toRead.charAt(i));
            } else {
                break;
            }
        }
    }

    private static String callURL(String urlStr) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn;
        InputStreamReader in;
        try {
            URL url = new URL(urlStr);
            urlConn = url.openConnection();
            if (urlConn != null) {
                urlConn.setReadTimeout(60 * 1000);
            }
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                int cp;
                while ((cp = bufferedReader.read()) != -1) {
                    sb.append((char) cp);
                }
                bufferedReader.close();
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
