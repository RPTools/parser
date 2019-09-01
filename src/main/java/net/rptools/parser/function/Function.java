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
package net.rptools.parser.function;

import java.util.List;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;

public interface Function {
  public static final int UNLIMITED_PARAMETERS = -1;

  public String[] getAliases();

  public Object evaluate(Parser parser, String functionName, List<Object> parameters)
      throws ParserException;

  public void checkParameters(String functionName, List<Object> parameters)
      throws ParameterException;

  public int getMinimumParameterCount();

  public int getMaximumParameterCount();

  public boolean isDeterministic();
}
