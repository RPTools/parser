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
package net.rptools.parser.ast;

import java.util.ArrayList;
import java.util.List;

public record FunctionCallNode(String function, List<ExpressionNode> parameters)
    implements ExpressionNode {

  @Override
  public List<String> getParts() {
    var result = new ArrayList<String>();
    result.add(function);
    for (var parameter : parameters) {
      result.add(parameter.toStringList());
    }
    return result;
  }
}
