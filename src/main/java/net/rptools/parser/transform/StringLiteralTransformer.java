/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * RPTools Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.parser.transform;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringLiteralTransformer {
  private int id = 0;
  private HashMap<String, String> strings = new HashMap<String, String>();

  public StringLiteralTransformer() {}

  private String getNextToken() {
    return String.format("StringLiteralTransformer%dTOKEN", id++);
  }

  private synchronized String removeStringsTransform(String str) {

    StringBuilder ret = new StringBuilder();

    StringBuilder currentString = new StringBuilder();
    boolean inString = false;
    char currentStringChar = '\'';

    for (char c : str.toCharArray()) {
      if (inString) {
        currentString.append(c);

        if (c == currentStringChar) {
          inString = false;

          String token = getNextToken();

          strings.put(token, currentString.toString());

          currentString = new StringBuilder();
          ret.append(token);
        }
      } else {
        switch (c) {
          case '\'':
          case '"':
            currentStringChar = c;
            currentString.append(c);
            inString = true;
            break;
          default:
            ret.append(c);
            break;
        }
      }
    }

    return ret.toString();
  }

  private Pattern tokenRegex = Pattern.compile("StringLiteralTransformer\\d+TOKEN");

  private synchronized String replaceStringsTransform(String str) {
    Matcher m = tokenRegex.matcher(str);
    StringBuffer sb = new StringBuffer();

    while (m.find()) {
      String token = m.group();

      m.appendReplacement(sb, strings.remove(token));
    }

    m.appendTail(sb);

    return sb.toString();
  }

  public Transformer getRemoveTransformer() {
    return new Transformer() {
      public String transform(String str) {
        return removeStringsTransform(str);
      }
    };
  }

  public Transformer getReplaceTransformer() {
    return new Transformer() {
      public String transform(String str) {
        return replaceStringsTransform(str);
      }
    };
  }
}
