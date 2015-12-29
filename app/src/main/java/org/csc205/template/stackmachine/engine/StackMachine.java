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

import java.lang.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class StackMachine {
    public static final String push = "push";
    public static final String pop = "pop";
    public static final String add = "add";
    public static final String subt = "subt";
    public static final String mult = "mult";
    public static final String div = "div";

    private int programCounter = 0;
    private Stack<Integer> stack = null;
    private Compiler compiler;
    private Compiler.Result compileResult;

    private HashMap<String, Integer> inputs = null;
    private int output = 0;

    public StackMachine() {
        compiler = new Compiler();
        clear();
    }


    public void clear() {
        if (this.stack == null)
            this.stack = new Stack<>();
        else
            this.stack.clear();
        if (this.inputs == null)
            this.inputs = new HashMap<>();
        else
            this.inputs.clear();
        this.programCounter = 0;
        this.compileResult = null;
    }

    public void compile(String expr) {
        compileResult = compiler.compile(expr);
        // If compilation is successful, then prepare input
    }

    public boolean isCompiled() {
        if (compileResult == null) return false;
        return compileResult.isSuccessful();
    }

    public String getExpr() {
        if (isCompiled())
            return compileResult.getExpr();
        else
            return null;
    }

    public String getPostfixExpression() {
        if (isCompiled())
            return compileResult.getPostfix();
        else
            return null;
    }

    public String[] getProgram() {
        if (isCompiled())
            return compileResult.getProgram();
        else
            return null;
    }

    public String getCompilationError() {
        if (compileResult == null) return null;
        else return compileResult.getError();
    }

    public int getVariableCount() {
        // Count the number of variables needed
        if (isCompiled())
            return compileResult.getSources().length;
        else
            return 0;
    }

    public String getNextInstruction() {
        if (isReady() || isRunning()) {
            return compileResult.getProgram()[programCounter];
        } else
            return "";
    }

    public void setVariables(int assignedValues[]) {
        if (!isCompiled()) return;
        if (assignedValues == null) return;

        for (int index = 0; index < compileResult.getSources().length; index++) {
            String key = compileResult.getSources()[index];
            if (index < assignedValues.length) {
                this.inputs.put(key, assignedValues[index]);
            } else {
                this.inputs.put(key, 0);  // if not provided, set to 0
            }
        }
    }

    private int getValue(String operand) {
        if (this.inputs.containsKey(operand))
            return this.inputs.get(operand);
        else return 0;
    }

    public boolean step() {
        // 1. Execute the current instruction.
        // 2. Increment PC.
        // 3. Return the status of whether it was possible to increment PC.
        if (isReady() || isRunning()) {

            String instruction = compileResult.getProgram()[programCounter].trim();

            if (instruction.startsWith(push)) {
                // Get operand
                String[] parts = instruction.split("\\s");
                String operand = parts[1].trim();
                int value = getValue(operand);
                stack.push(value);
            } else if (instruction.startsWith(pop)) {
                // Get operand
                String[] parts = instruction.split("\\s");
                String operand = parts[1].trim();
                output = stack.pop();
            } else if (instruction.equals(add)) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                stack.push(operand1 + operand2);
            } else if (instruction.equals(subt)) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                stack.push(operand1 - operand2);
            } else if (instruction.equals(mult)) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                stack.push(operand1 * operand2);
            } else if (instruction.equals(div)) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                stack.push(operand1 / operand2);
            }
            programCounter++;
            return true;
        } else {
            // not possible to increment PC.
            return false;
        }
    }

    public Stack<Integer> getStack() {
        return stack;
    }


    public int getProgramCounter() {
        return programCounter;
    }


    public String getResult() {
        if (isCompleted()) {
            return compileResult.getDestination() + "=" + output;
        } else return "";
    }

    public boolean isReady() {
        if (!isCompiled()) return false;
        // program is available. PC is 0
        if (programCounter == 0 && programCounter < compileResult.getProgram().length) return true;
        return false;
    }

    public boolean isRunning() {
        if (!isCompiled()) return false;
        // program is available. PC is greater than 0
        if (programCounter > 0 && programCounter < compileResult.getProgram().length) return true;
        return false;
    }

    public boolean isCompleted() {
        if (!isCompiled()) return false;
        // program is available. PC exceeds the last line of the program
        if (programCounter >= compileResult.getProgram().length) return true;
        return false;
    }


    public static void main(String[] args) {
        String expr = "X=A*B-(C+D)";
        StackMachine stackMachine = new StackMachine();
        stackMachine.compile(expr);

        if (stackMachine.isCompiled()) {
            System.out.println("Infix = " + stackMachine.compileResult.getExpr());

            String postfix = stackMachine.getPostfixExpression();
            String[] program = stackMachine.getProgram();

            System.out.println("Postfix = " + postfix);
            for (int index = 0; index < program.length; index++) {
                System.out.println(" " + (index + 1) + "." + program[index]);
            }

            int countVariables = stackMachine.getVariableCount();
            int assignedValues[] = new int[countVariables];

            Scanner scanner = new Scanner(System.in);
            for (int index = 0; index < countVariables; index++) {
                System.out.print("Enter " + stackMachine.compileResult.getSources()[index] + ": ");
                assignedValues[index] = scanner.nextInt();
            }
            stackMachine.setVariables(assignedValues);

            if (stackMachine.isReady()) {
                System.out.println("Start:");
                while (!stackMachine.isCompleted()) {
                    System.out.print("Running " + stackMachine.getNextInstruction() + "  ");
                    stackMachine.step();
                    Stack<Integer> stack = stackMachine.getStack();
                    System.out.print("Stack=|");
                    for (int pos = 0; pos < stack.size(); pos++) {
                        System.out.print(Integer.toString(stack.get(pos)) + "|");
                    }
                    System.out.println();
                }
                System.out.println(stackMachine.getResult());
            }
        } else {
            System.out.println("Cannot compile " + expr);
            System.out.println(stackMachine.compileResult.getError());
        }
    }
}