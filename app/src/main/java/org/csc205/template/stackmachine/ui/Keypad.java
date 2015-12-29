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

package org.csc205.template.stackmachine.ui;

public class Keypad {
    private static final int Type_blank = 0;
    private static final int Type_expr = 1;
    private static final int Type_digit = 2;
    private static final int Type_sign = 3;
    private static final int Type_control = 4;

    public enum Button {
        Variable("VAR", Type_expr),
        OpenBracket("(", Type_expr),
        CloseBracket(")", Type_expr),
        Plus("+", Type_expr),
        Minus("-", Type_expr),
        Multiply("*", Type_expr),
        Divide("/", Type_expr),

        ZERO("0", Type_digit),
        ONE("1", Type_digit),
        TWO("2", Type_digit),
        THREE("3", Type_digit),
        FOUR("4", Type_digit),
        FIVE("5", Type_digit),
        SIX("6", Type_digit),
        SEVEN("7", Type_digit),
        EIGHT("8", Type_digit),
        NINE("9", Type_digit),
        sign("±", Type_sign),
        delete("DEL", Type_control),
        clear("CLR", Type_control),
        run("RUN", Type_control),
        compile("compile", Type_control),
        blank("", Type_blank);


        CharSequence label = "";
        int type = 0;

        Button(CharSequence label, int type) {
            if (label != null) this.label = label;
            if (type==Type_expr || type==Type_digit || type==Type_sign || type== Type_control) this.type = type;
        }
        public CharSequence getLabel() {
            return label;
        }

        public boolean isExpr() { return type==Type_expr; }
        public boolean isDigit() { return type==Type_digit; }
        public boolean isControl() { return type==Type_control; }
        public boolean isVariable() { return label.equals(Variable.label); }

        public CharSequence getDigit() {
            if (type==Type_digit) {
                return label;
            }
            else {
                return "";
            }
        }
        public int getDigitValue() {
            if (type == Type_digit) {
                return getValue(label);
            }
            return 0;
        }
        public static int getValue(CharSequence label) {
            if (label.equals(ZERO.getLabel())) return 0;
            else if (label.equals(ONE.getLabel())) return 1;
            else if (label.equals(TWO.getLabel())) return 2;
            else if (label.equals(THREE.getLabel())) return 3;
            else if (label.equals(FOUR.getLabel())) return 4;
            else if (label.equals(FIVE.getLabel())) return 5;
            else if (label.equals(SIX.getLabel())) return 6;
            else if (label.equals(SEVEN.getLabel())) return 7;
            else if (label.equals(EIGHT.getLabel())) return 8;
            else if (label.equals(NINE.getLabel())) return 9;
            else return 0;
        }

        public boolean isSign() {
            return (type==Type_sign && label.equals(sign.getLabel()));
        }
        public boolean isClear() {
            return (type==Type_control && label.equals(clear.getLabel()));
        }
        public boolean isRun() {
            return (type==Type_control && label.equals(run.getLabel()));
        }
        public boolean isCompile() {
            return (type==Type_control && label.equals(compile.getLabel()));
        }
        public boolean isDelete() {
            return (type== Type_control && label.equals(delete.getLabel()));
        }
    }

    public static final Button[] declareButtons = new Button[] {
            Button.Variable,
            Button.OpenBracket,
            Button.CloseBracket,
            Button.compile,

            Button.Plus,
            Button.Minus,
            Button.Multiply,
            Button.Divide,

            Button.delete,
            Button.clear
    };

    public static final Button[] assignButtons = new Button[] {
            Button.ONE,
            Button.TWO,
            Button.THREE,
            Button.FOUR,

            Button.FIVE,
            Button.SIX,
            Button.SEVEN,
            Button.EIGHT,

            Button.NINE,
            Button.ZERO,
            Button.sign,
            Button.run,

            Button.delete,
            Button.clear
    };

    public static final Button[] executeButtons = new Button[] {
            Button.clear,
            Button.run
    };
}
