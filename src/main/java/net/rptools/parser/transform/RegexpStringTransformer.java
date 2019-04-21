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

import java.util.regex.Pattern;

public class RegexpStringTransformer implements Transformer {

  private final Pattern[] patterns;
  private final String[] replacements;

  public RegexpStringTransformer(String[][] regexps) {
    this.patterns = new Pattern[regexps.length];
    this.replacements = new String[regexps.length];

    for (int i = 0; i < regexps.length; i++) {
      this.patterns[i] = Pattern.compile(regexps[i][0]);
      this.replacements[i] = regexps[i][1];
    }
  }

  public RegexpStringTransformer(String[] patterns, String[] replacements) {
    this.patterns = new Pattern[patterns.length];

    for (int i = 0; i < patterns.length; i++) this.patterns[i] = Pattern.compile(patterns[i]);

    this.replacements = new String[replacements.length];
    System.arraycopy(replacements, 0, this.replacements, 0, replacements.length);
  }

  public String transform(String str) {
    String ret = str;

    for (int i = 0; i < patterns.length; i++) {
      Pattern p = patterns[i];
      ret = p.matcher(ret).replaceAll(replacements[i]);
    }

    return ret;
  }
}
