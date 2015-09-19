/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.parser.function.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;
import junit.framework.TestCase;

public class MedianTest extends TestCase {
    public void testEvaluate() throws EvaluationException, ParameterException {
        Median median = new Median();
        
        assertEquals(new BigDecimal(10), median.childEvaluate(null, null, createArgs(new BigDecimal(10))));
        assertEquals(new BigDecimal(6), median.childEvaluate(null, null, createArgs(new BigDecimal(10), new BigDecimal(2))));
        assertEquals(new BigDecimal(3), median.childEvaluate(null, null, createArgs(new BigDecimal(10), new BigDecimal(2), new BigDecimal(3))));
        assertEquals(new BigDecimal(5), median.childEvaluate(null, null, createArgs(new BigDecimal(10), new BigDecimal(2), new BigDecimal(3), new BigDecimal(5), new BigDecimal(-100), new BigDecimal(7), new BigDecimal(100))));
    }
    
    private List<Object> createArgs(Object... arguments)
    {
        List<Object> ret = new ArrayList<Object>();
        for (Object o : arguments)
            ret.add(o);
        
        return ret;
    }
}
