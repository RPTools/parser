/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.rptools.parser.function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public abstract class AbstractComparisonFunction extends AbstractLogicalOperatorFunction {

	public AbstractComparisonFunction(String... aliases) {
		super(aliases);
	}

	public AbstractComparisonFunction(int minParameters, int maxParameters, String... aliases) {
		super(minParameters, maxParameters, aliases);
	}

	public AbstractComparisonFunction(int minParameters, int maxParameters, boolean deterministic, String... aliases) {
		super(minParameters, maxParameters, deterministic, aliases);
	}

	@Override
	public void checkParameters(List<Object> parameters) throws ParameterException {
		super.checkParameters(parameters);

		for (Object param : parameters) {
			if (!(param instanceof BigDecimal)
					&& !(param instanceof String))
				throw new ParameterException(String.format("Illegal argument type %s, expecting %s", param == null ? "null" : param.getClass().getName(), BigDecimal.class.getName()));
		}
	}

	protected boolean ConvertToBoolean(Object o) {
		if (o instanceof Boolean)
			return ((Boolean) o).booleanValue();

		if (o instanceof BigDecimal)
			return !BigDecimal.ZERO.equals((BigDecimal) o);

		if (o instanceof BigInteger)
			return !BigInteger.ZERO.equals((BigInteger) o);

		if (o instanceof String)
			return ((String) o).length() > 0;

		return false;
	}

	protected BigDecimal BooleanAsBigDecimal(boolean b) {
		return b ? BigDecimal.ONE : BigDecimal.ZERO;
	}
}
