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

import junit.framework.TestCase;

public class StringLiteralTransformerTest extends TestCase {
  public void testRemove() {
    StringLiteralTransformer transformer = new StringLiteralTransformer();

    assertEquals(
        "This is a StringLiteralTransformer0TOKEN, dont you think?",
        transformer.getRemoveTransformer().transform("This is a \"test\", dont you think?"));
  }

  public void testReplace() {
    StringLiteralTransformer transformer = new StringLiteralTransformer();

    String input =
        "This is a \"test\", dont you think?.   Why does 'this' work? But not this '\"' ";

    assertEquals(
        input,
        transformer
            .getReplaceTransformer()
            .transform(transformer.getRemoveTransformer().transform(input)));
  }
}
