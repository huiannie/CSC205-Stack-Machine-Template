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

package org.csc205.template.stackmachine.engine;

public class Compiler {
    public Compiler() {
    }

    public Result compile(String expr) {
        // The functions performed by this class to be automated:
        // step 1. build a tree
        // step 2. find postfix expression from tree traversal
        // step 3. build program based on postfix expression
        // step 4. compute the number of variables in the expression
        // Return an object of the class  if successful, null otherwise

        Result result = new Result();

        // TODO: compute all the fields based on expr
        // The following is a manual example for the case where  X=A*B-(C+D)
        // This manual approach need to be replaced by an automated algorithm
        // which is able to parse not just the example given, but also ALL possible inputs

        if (expr.equals("X=A*B-(C+D)")) {
            result.expr = "X=A*B-(C+D)";
            result.postfix = "X=AB*CD+-";
            result.program = new String[]{"push A", "push B", "mult", "push C", "push D", "add", "subt", "pop X"};
            result.sources = new String[]{"A", "B", "C", "D"};
            result.destination = "X";
            result.error = null;
        }
        else {
            result.expr = expr;
            result.postfix = null;
            result.program = null;
            result.sources = null;
            result.destination = null;
            result.error = "Bad expression!";

        }
        // End of example


        return result;
    }

    /*
     * expr: an assignment statement of the infix form. For example X=A*B-(C+D)
     *       The only valid input symbols are the first 10 characters in uppercase (A, B, C...)
     *       The only valid operators are +, -, *, /, (, ) and =
     *       There must be only one output symbol X.
     *       There must be only one assignment sign, which is =
     * postfix: The infix expression is converted to infix form. For example, X=AB*CD+-
     * program: The assignment is converted to a program for the stack machine.
     *          Every line is one instruction.
     *          Each instruction must have at most 1 operand separated by 1 space.
     *          The push and pop instructions have 0 operand.
     *          There should be no empty lines and no extra space.
     *          For example, the program corresponding to X=AB*CD+- is
     *            push A
     *            push B
     *            mult
     *            push C
     *            push D
     *            add
     *            subt
     *            pop X
     * sources: The list of input symbols in the assignment statement.
     *          The input symbols must appear in exactly the order shown in expr.
     *          For example, the list of input symbols is [A,B,C,D]
     * destination: The name of the output. In this example, the name of the output symbol is X
     * error: If the compilation is successful, then error is null.
     *        Else, error return a message that describes what has gone wrong.
     *
     * Requirements:
     *
     * expr must always be equal to the assignment statement provided by the input.
     * If compilation is successful, error must be null.
     * If compilation is unsuccessful, error must not be null, but all the other fields except expr must be null.
     */
    public class Result {
        private String expr = null;
        private String postfix = null;
        private String[] program = null;
        private String sources[] = null;
        private String destination = null;
        private String error = null;

        public boolean isSuccessful() {
            if (error==null) return true;
            else return false;
        }
        public String getExpr() {
            return expr;
        }
        public String getPostfix() {
            return postfix;
        }
        public String[] getProgram() {
            return program;
        }
        public String[] getSources() {
            return sources;
        }
        public String getDestination() {
            return destination;
        }
        public String getError() {
            return error;
        }
    }
}
