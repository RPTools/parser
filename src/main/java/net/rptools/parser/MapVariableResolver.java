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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.rptools.CaseInsensitiveHashMap;

/**
 * Default implementation of a {@link VariableResolver} that uses a Map<String, Object> as the
 * backing object.
 */
public class MapVariableResolver implements VariableResolver {
  private final Map<String, Object> variables = new CaseInsensitiveHashMap<Object>();

  private static final Map<String, Object> constants =
      Map.of(
          "true", BigDecimal.ONE,
          "false", BigDecimal.ZERO);

  public MapVariableResolver() {
    variables.putAll(constants);
  }

  public boolean containsVariable(String name) throws ParserException {
    return containsVariable(name, VariableModifiers.None);
  }

  public void setVariable(String name, Object value) throws ParserException {
    if (constants.containsKey(name)) {
      throw new ParserException(name + " can not be the target of assignment.");
    }
    setVariable(name, VariableModifiers.None, value);
  }

  public Object getVariable(String variableName) throws ParserException {
    return getVariable(variableName, VariableModifiers.None);
  }

  public boolean containsVariable(String name, VariableModifiers vType) throws ParserException {
    return variables.containsKey(name);
  }

  public void setVariable(String name, VariableModifiers vType, Object value)
      throws ParserException {
    variables.put(name, value);
  }

  public Object getVariable(String variableName, VariableModifiers vType) throws ParserException {
    return variables.get(variableName);
  }

  @Override
  public Set<String> getVariables() {
    return Collections.unmodifiableSet(variables.keySet());
  }
}
