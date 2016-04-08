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

import java.util.List;

import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;

public abstract class AbstractFunction implements Function {
	private final String[] aliases;
	private final int minParameters;
	private final int maxParameters;
	private final boolean deterministic;

	public AbstractFunction(String... aliases) {
		this(0, UNLIMITED_PARAMETERS, aliases);
	}

	public AbstractFunction(int minParameters, int maxParameters, String... aliases) {
		this(minParameters, maxParameters, true, aliases);
	}

	public AbstractFunction(int minParameters, int maxParameters, boolean deterministic, String... aliases) {
		this.minParameters = minParameters;
		this.maxParameters = maxParameters;
		this.deterministic = deterministic;

		this.aliases = aliases;
	}

	public final String[] getAliases() {
		return aliases;
	}

	public final Object evaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		checkParameters(parameters);

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
	 * Default implementation only checks count.  Override this to implement
	 * more complex parameter checking.
	 *
	 * @param parameters
	 */
	public void checkParameters(List<Object> parameters) throws ParameterException {
		int pCount = parameters == null ? 0 : parameters.size();

		if (pCount < minParameters || (maxParameters != UNLIMITED_PARAMETERS && parameters.size() > maxParameters))
			throw new ParameterException(String.format("Invalid number of parameters %d, expected %s", pCount,
					formatExpectedParameterString()));
	}

	public void checkParameterTypes(List<Object> parameters, List<Class> allowedTypes) {
	}

	private String formatExpectedParameterString() {
		if (minParameters == maxParameters)
			return String.format("exactly %d parameter(s)", maxParameters);

		if (maxParameters == UNLIMITED_PARAMETERS)
			return String.format("at least %d parameters", minParameters);

		return String.format("between %d and %d parameters", minParameters, maxParameters);
	}

	protected boolean containsString(List<Object> parameters) {
		for (Object param : parameters) {
			if (param instanceof String)
				return true;
		}

		return false;
	}

	public abstract Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException;

}
