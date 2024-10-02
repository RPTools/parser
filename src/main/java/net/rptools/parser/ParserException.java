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
package net.rptools.parser;

import java.util.ArrayList;
import java.util.List;

public class ParserException extends Exception {

  private static final long serialVersionUID = 1959865440126054220L;

  /** The list of macro calls that resulted in the error. */
  private final List<String> macroStackTrace = new ArrayList<>();

  public ParserException(Throwable cause) {
    super(cause);
  }

  public ParserException(String msg) {
    super(msg);
  }

  /**
   * Add the macro / udf name to the stackTrace.
   *
   * @param name the macro or UDF name
   */
  public void addMacro(String name) {
    macroStackTrace.add(name);
  }

  /**
   * @return an array representing the macro stack trace.
   */
  public String[] getMacroStackTrace() {
    return macroStackTrace.toArray(new String[0]);
  }
}
