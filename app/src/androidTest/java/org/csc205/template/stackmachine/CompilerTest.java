/*
 * Copyright (c) 2015-16 Annie Hui @ NVCC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.csc205.template.stackmachine;
import org.csc205.template.stackmachine.engine.Compiler;

import android.content.Context;
import android.test.InstrumentationTestCase;


public class CompilerTest extends InstrumentationTestCase {
    Context targetContext = null;
    Context testContext = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreate() {
        Compiler compiler = new Compiler();
        assertNotNull(compiler);
    }

    public void testGoodInput1() {
        String inputExpr = "X=A*B-(C+D)";
        String expectedPostfix = "X=AB*CD+-";
        String expectedProgram[] = new String[]{"push A", "push B", "mult", "push C", "push D", "add", "subt", "pop X"};
        String expectedSources[] = new String[]{"A", "B", "C", "D"};
        String expectedDestination = "X";
        String expectedError = null;

        checkGoodInput(inputExpr, expectedPostfix, expectedProgram, expectedSources, expectedDestination, expectedError);
    }

    public void testGoodInput2() {
        String inputExpr = "X=A+B";
        String expectedPostfix = "X=AB+";
        String expectedProgram[] = new String[]{"push A", "push B", "add", "pop X"};
        String expectedSources[] = new String[]{"A", "B"};
        String expectedDestination = "X";
        String expectedError = null;

        checkGoodInput(inputExpr, expectedPostfix, expectedProgram, expectedSources, expectedDestination, expectedError);
    }

    public void testGoodInput3() {
        String inputExpr = "X=(A)";
        String expectedPostfix = "X=A";
        String expectedProgram[] = new String[]{"push A", "pop X"};
        String expectedSources[] = new String[]{"A"};
        String expectedDestination = "X";
        String expectedError = null;

        checkGoodInput(inputExpr, expectedPostfix, expectedProgram, expectedSources, expectedDestination, expectedError);
    }


    public void testBadInput1() {
        String inputExpr = "X=A(";
        String expectedError = "Bad expression!";

        checkBadInput(inputExpr, expectedError);
    }

    public void testBadInput2() {
        String inputExpr = "X=()";
        String expectedError = "Bad expression!";

        checkBadInput(inputExpr, expectedError);
    }

    public void testBadInput3() {
        String inputExpr = "X=AB";
        String expectedError = "Bad expression!";

        checkBadInput(inputExpr, expectedError);
    }


    public void checkGoodInput(String inputExpr,
                               String expectedPostfix,
                               String expectedProgram[],
                               String expectedSources[],
                               String expectedDestination,
                               String expectedError) {
        Compiler compiler = new Compiler();
        Compiler.Result compileResult = null;


        compileResult = compiler.compile(inputExpr);
        // result must never be null
        assertNotNull(compileResult);

        // check input
        assertEquals(inputExpr, compileResult.getExpr());

        // check postfix
        assertEquals(expectedPostfix, compileResult.getPostfix());

        // check program
        // expecting the program to be the same in length and in content
        assertNotNull(compileResult.getProgram());
        assertEquals(expectedProgram.length, compileResult.getProgram().length);
        for (int index=0; index<expectedProgram.length; index++) {
            assertEquals(expectedProgram[index], compileResult.getProgram()[index]);
        }

        // check the variables
        // expecting the variables to be the same in number, name and order
        assertNotNull(compileResult.getSources());
        assertEquals(expectedSources.length, compileResult.getSources().length);
        for (int index=0; index<expectedSources.length; index++) {
            assertEquals(expectedSources[index], compileResult.getSources()[index]);
        }

        // check destination
        assertEquals(expectedDestination, compileResult.getDestination());

        // check error
        assertEquals(expectedError, compileResult.getError());
    }

    public void checkBadInput(String inputExpr, String expectedError) {
        Compiler compiler = new Compiler();
        Compiler.Result compileResult = null;

        compileResult = compiler.compile(inputExpr);
        // result must never be null
        assertNotNull(compileResult);

        // check input
        assertEquals(inputExpr, compileResult.getExpr());

        // check postfix
        assertNull(compileResult.getPostfix());

        // check program
        assertNull(compileResult.getProgram());

        // check the variables
        assertNull(compileResult.getSources());

        // check destination
        assertNull(compileResult.getDestination());

        // check error
        assertEquals(expectedError, compileResult.getError());
    }


}
