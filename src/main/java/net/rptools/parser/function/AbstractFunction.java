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

public abstract class AbstractFunction implements Function {
  private final String[] aliases;
  private final int minParameters;
  private final int maxParameters;
  private final boolean deterministic;

  // Could use i18n instead, but would require additional dependency
  private static final String WRONG_NUM_PARAM =
      "Function '%s' requires exactly %d parameters; %d were provided.";
  private static final String NOT_ENOUGH_PARAM =
      "Function '%s' requires at least %d parameters; %d were provided.";
  private static final String TOO_MANY_PARAM =
      "Function '%s' requires no more than %d parameters; %d were provided.";

  public AbstractFunction(String... aliases) {
    this(0, UNLIMITED_PARAMETERS, aliases);
  }

  public AbstractFunction(int minParameters, int maxParameters, String... aliases) {
    this(minParameters, maxParameters, true, aliases);
  }

  public AbstractFunction(
      int minParameters, int maxParameters, boolean deterministic, String... aliases) {
    this.minParameters = minParameters;
    this.maxParameters = maxParameters;
    this.deterministic = deterministic;

    this.aliases = aliases;
  }

  public final String[] getAliases() {
    return aliases;
  }

  public final Object evaluate(Parser parser, String functionName, List<Object> parameters)
      throws ParserException {
    checkParameters(functionName, parameters);

    return childEvaluate(parser, functionName, parameters);
  }

  public final int getMinimumParameterCount() {
    return minParameters;
  }

  public final int getMaximumParameterCount() {
    return maxParameters;
  }

  public final boolean isDeterministic() {
    return deterministic;
  }

  /**
   * Default implementation only checks count. Override this to implement more complex parameter
   * checking.
   *
   * @param functionName the name of the function
   * @param parameters the list of parameters
   */
  public void checkParameters(String functionName, List<Object> parameters)
      throws ParameterException {
    int pCount = parameters == null ? 0 : parameters.size();

    if (minParameters == maxParameters) {
      if (pCount != maxParameters)
        throw new ParameterException(
            String.format(WRONG_NUM_PARAM, functionName, maxParameters, pCount));
    } else {
      if (pCount < minParameters)
        throw new ParameterException(
            String.format(NOT_ENOUGH_PARAM, functionName, minParameters, pCount));
      if (maxParameters != UNLIMITED_PARAMETERS && pCount > maxParameters)
        throw new ParameterException(
            String.format(TOO_MANY_PARAM, functionName, maxParameters, pCount));
    }
  }

  public void checkParameterTypes(List<Object> parameters, List<Class> allowedTypes) {}

  protected boolean containsString(List<Object> parameters) {
    for (Object param : parameters) {
      if (param instanceof String) return true;
    }

    return false;
  }

  public abstract Object childEvaluate(Parser parser, String functionName, List<Object> parameters)
      throws ParserException;
}
