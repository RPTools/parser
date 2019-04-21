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

import java.util.Set;

/**
 * Interface that does variable resolution for the parser. If the parser is constructed with an
 * instance of this interface, then all variable resolution will go through that instance instead of
 * through the default {@link MapVariableResolver} instance.
 */
public interface VariableResolver {
  public boolean containsVariable(String name) throws ParserException;

  public void setVariable(String name, Object value) throws ParserException;

  public Object getVariable(String variableName) throws ParserException;

  public boolean containsVariable(String name, VariableModifiers vType) throws ParserException;

  public void setVariable(String name, VariableModifiers vType, Object value)
      throws ParserException;

  public Object getVariable(String variableName, VariableModifiers vType) throws ParserException;

  public Set<String> getVariables();
}
