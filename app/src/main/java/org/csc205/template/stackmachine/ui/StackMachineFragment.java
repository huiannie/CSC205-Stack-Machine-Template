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

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.csc205.template.stackmachine.R;
import org.csc205.template.stackmachine.engine.StackMachine;
import org.csc205.template.stackmachine.app.AppSettings;
import org.csc205.template.stackmachine.io.Savelog;

public class StackMachineFragment extends Fragment {
    private static final String TAG = StackMachineFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final int ButtonColorId = R.color.button_very_dark;

    private static final int Mode_declare = 0;  // write expression
    private static final int Mode_assign = 1; // set values of variables
    private static final int Mode_execute = 2; // evaluate expression


    private static final String Default_expr = "X=";
    private static final String[] Symbols = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static final int maxNumberOfVariables = Symbols.length;
    private static final int maxInputLength = 40;


    private int mMode = Mode_declare;  // default
    private String mInput = Default_expr;
    private int mVariableCount = 0;
    private int mAssignmentCount = 0;
    private int mAssignedValues[] = null;

    private StackMachine mStackMachine = null;

    private GridView mStackGrid;
    private Keypad.Button[] buttonArray = Keypad.declareButtons;
    private KeypadAdapter mStackAdapter;
    private TextView mInputView;
    private TextView mDisplayView;
    private TextView mStackView;

    public static StackMachineFragment newInstance() {
        Bundle args = new Bundle();

        StackMachineFragment fragment = new StackMachineFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "onCreate()");

        mStackMachine = new StackMachine();

        setRetainInstance(true);
        setHasOptionsMenu(true);

    } // end to implementing onCreate()



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Savelog.d(TAG, debug, "onCreateView()");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tool_stackmachine, container, false);

        mInputView = (TextView) v.findViewById(R.id.fragmentToolStackMachine_input);
        this.displayInput();

        mStackView = (TextView) v.findViewById(R.id.fragmentToolStackMachine_stack);
        this.displayStack();

        mDisplayView = (TextView) v.findViewById(R.id.fragmentToolStackMachine_display);
        this.displayOutput();

        mStackGrid = (GridView) v.findViewById(R.id.fragmentToolStackMachine_grid);
        if (mStackAdapter==null) {
            mStackAdapter = new KeypadAdapter(this);
            mStackAdapter.setOnButtonClickListener(new OnButtonClickListener(this));
        }
        mStackGrid.setAdapter(mStackAdapter);

        return v;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mStackGrid != null) {
            mStackGrid.setAdapter(null);
            mStackGrid=null;
        }
        if (mInputView != null) {
            mInputView=null;
        }
        if (mDisplayView != null) {
            mDisplayView=null;
        }
        if (mStackView != null) {
            mStackView=null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mStackAdapter != null) {
            mStackAdapter = null;
        }
        if (mStackMachine != null) {
            mStackMachine = null;
        }
        if (mAssignedValues != null) {
            mAssignedValues = null;
        }
    }


    public static class KeypadAdapter extends BaseAdapter {
        OnButtonClickListener onButtonClickListener;
        private Context appContext;
        StackMachineFragment hostFragment;

        public KeypadAdapter(StackMachineFragment hostFragment) {
            appContext = hostFragment.getActivity().getApplicationContext();
            this.hostFragment = hostFragment;
        }

        public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
            this.onButtonClickListener = onButtonClickListener;
        }

        public int getCount() {
            return hostFragment.buttonArray.length;
        }

        public Object getItem(int position) {
            return hostFragment.buttonArray[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final Button button;
            if (convertView == null) {
                // new
                button = new Button(appContext);
                button.setTextColor(hostFragment.getResources().getColor(ButtonColorId));
                // One listener to be shared by all on this adapter
                button.setOnClickListener(onButtonClickListener);

            } else {
                // recycled
                button = (Button) convertView;

            }

            // Now adjust color. Need to do this for both new views and recycled views
            Keypad.Button keypadButton = hostFragment.buttonArray[position];

            Savelog.d(TAG, debug, "assigning button " + keypadButton.getLabel());

            button.setBackgroundResource(R.drawable.tool_keypad_active);
            button.setClickable(true);
            button.setVisibility(View.VISIBLE);
            button.setTag(Integer.valueOf(position));

            button.setText(hostFragment.buttonArray[position].getLabel());
            return button;
        }

    }

    private static class OnButtonClickListener implements View.OnClickListener {
        StackMachineFragment hostFragment;
        public OnButtonClickListener(StackMachineFragment hostFragment) {
            this.hostFragment = hostFragment;
        }

        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            int position = (Integer) button.getTag();
            Keypad.Button keypadButton = hostFragment.buttonArray[position];

            if (keypadButton.isExpr()) {
                if (keypadButton.isVariable()) {
                    if (hostFragment.mVariableCount<maxNumberOfVariables) {
                        if (hostFragment.mInput.length()<maxInputLength) {
                            hostFragment.mInput += Symbols[hostFragment.mVariableCount];
                            hostFragment.mVariableCount++;
                        }
                    }
                }
                else {
                    if (hostFragment.mInput.length()<maxInputLength) {
                        hostFragment.mInput += keypadButton.getLabel();
                    }
                }
                hostFragment.displayInput();
            }
            else if (keypadButton.isDigit()) {
                if (hostFragment.mInput.length()<maxInputLength) {
                    hostFragment.mInput += keypadButton.getDigit();
                }
                hostFragment.displayInput();
            }
            else if (keypadButton.isSign()) {
                int start = hostFragment.mInput.indexOf("=") + 1;
                if (start==hostFragment.mInput.length()) {
                    // ignore because there is no input digit yet.
                }
                else {
                    String inputString = hostFragment.mInput.substring(start);
                    // check if the input string is negative.
                    // If it is, remove the negative sign
                    // Else, add a negative sign.

                    if (inputString.startsWith("-")) {
                        // no need to check length when we want to shrink the input
                        hostFragment.mInput = hostFragment.mInput.substring(0, start) + inputString.substring(1);
                    }
                    else {
                        // must check length when expanding the input
                        if (hostFragment.mInput.length()<maxInputLength) {
                            hostFragment.mInput = hostFragment.mInput.substring(0, start) + "-" + inputString;
                        }
                    }
                    hostFragment.displayInput();
                }
            }
            else if (keypadButton.isControl()) {
                if (keypadButton.isCompile()) {

                    if (hostFragment.mMode==Mode_declare) {
                        hostFragment.mStackMachine.compile(hostFragment.mInput);

                        if (hostFragment.mStackMachine.isCompiled()) {
                            hostFragment.mMode = Mode_assign;
                            Savelog.d(TAG, debug, "Going to refresh buttons now!");
                            // Use a new set of buttons
                            hostFragment.buttonArray = Keypad.assignButtons;
                            hostFragment.mStackAdapter.notifyDataSetChanged();
                            // update variable count based on the compiled result
                            hostFragment.mVariableCount = hostFragment.mStackMachine.getVariableCount();
                            // Create array to be ready for assigning variables
                            hostFragment.mAssignmentCount = 0; // reset input count
                            hostFragment.mAssignedValues = new int[hostFragment.mVariableCount];
                            // set prompt for the first input
                            hostFragment.mInput = Symbols[hostFragment.mAssignmentCount] + "=";
                        }
                        else {
                            // Do not change mode
                        }

                        hostFragment.displayOutput();
                        hostFragment.displayInput();

                    }
                    else {
                        // no need to do anything
                    }
                }
                else if (keypadButton.isRun()) {

                    if (hostFragment.mMode==Mode_assign) {

                        int start = hostFragment.mInput.indexOf("=") + 1;
                        if (start==hostFragment.mInput.length()) {
                            // ignore because there is no input digit yet.
                        }
                        else {
                            int index = hostFragment.mAssignmentCount;

                            if (index<hostFragment.mVariableCount) {
                                String inputString = hostFragment.mInput.substring(start);

                                // Try to assign values
                                try {
                                    hostFragment.mAssignedValues[index] = Integer.parseInt(inputString);
                                    hostFragment.mAssignmentCount++;
                                    // Reset input to ask for the next variable
                                    hostFragment.mInput = Symbols[hostFragment.mAssignmentCount] + "=";
                                } catch (Exception e) {
                                    Savelog.d(TAG, debug, "Bad input " + inputString + " for variable " + Symbols[index]);
                                }

                                if (index==hostFragment.mVariableCount-1) {
                                    // move on to the next stage
                                    hostFragment.mMode = Mode_execute;

                                    // Assign values to the variables and get ready to run.
                                    hostFragment.mStackMachine.setVariables(hostFragment.mAssignedValues);

                                    Savelog.d(TAG, debug, "Going to refresh buttons now!");
                                    // Use a new set of buttons
                                    hostFragment.buttonArray = Keypad.executeButtons;
                                    hostFragment.mStackAdapter.notifyDataSetChanged();

                                    // Ready to start. Show it in display.
                                    hostFragment.displayOutput();

                                    // display the assigned values of all symbols
                                    hostFragment.mInput = "";
                                    for (int pos=0; pos<hostFragment.mVariableCount; pos++) {
                                        hostFragment.mInput += Symbols[pos] + "=" + hostFragment.mAssignedValues[pos] + " ";
                                    }
                                }
                            }
                            else {
                                // ignore
                            }
                            hostFragment.displayInput();
                        }
                    }
                    else if (hostFragment.mMode==Mode_execute) {
                        Savelog.d(TAG, debug, "clicked run to step");
                        // Start executing
                        if (hostFragment.mStackMachine.isCompiled()) {
                            hostFragment.mStackMachine.step();
                        }
                        hostFragment.displayStack();
                        hostFragment.displayOutput();

                    }
                    else {
                        Savelog.d(TAG, debug, "clicked run unrecognized");
                    }
                }
                else if (keypadButton.isClear()) {
                    // reset everything
                    hostFragment.mMode = Mode_declare;
                    hostFragment.mStackMachine.clear();
                    hostFragment.buttonArray = Keypad.declareButtons;
                    hostFragment.mInput = Default_expr;
                    hostFragment.mAssignedValues = null;
                    hostFragment.mAssignmentCount = 0;
                    hostFragment.mVariableCount = 0;

                    hostFragment.displayInput();
                    hostFragment.displayOutput();
                    hostFragment.displayStack();
                    hostFragment.mStackAdapter.notifyDataSetChanged();
                }
                else if (keypadButton.isDelete()) {
                    if (hostFragment.mMode==Mode_declare) {
                        // If the input string is longer than the default value, trim one character off the end
                        int length = hostFragment.mInput.length();
                        if (length>Default_expr.length()) {
                            char deletedChar = hostFragment.mInput.charAt(length-1);
                            // If we have deleted a symbol, then we down count the value
                            if (Character.isLetter(deletedChar)) {
                                hostFragment.mVariableCount--;
                            }
                            hostFragment.mInput = hostFragment.mInput.substring(0, length - 1);
                        }
                        else {
                            // no need to do anything
                        }
                    }
                    else if (hostFragment.mMode==Mode_assign) {
                        // If the last character is a =, then we cannot delete anything
                        // because that's the default part of the input
                        int length = hostFragment.mInput.length();
                        if (hostFragment.mInput.charAt(length-1) != '=') {
                            hostFragment.mInput = hostFragment.mInput.substring(0, length - 1);
                        }
                        else {
                            // no need to do anything
                        }
                    }
                    hostFragment.displayInput();
                }

                else {

                }
            }
        }
    }


    private void displayInput() {
        mInputView.setText(mInput);
    }

    private void displayStack() {

        String stackData = "";
        if (mMode==Mode_execute) {
            if (mStackMachine.isCompiled()) {
                // show the stack

                Savelog.d(TAG, debug, "Stack size=" + mStackMachine.getStack().size());

                int values[] = new int[mStackMachine.getStack().size()];
                for (int index=0; index<values.length; index++) {
                    values[index] = mStackMachine.getStack().get(index);

                    Savelog.d(TAG, debug, "item=" + values[index]);
                }
                // Create the stack from bottom up
                //

                for (int index=values.length-1; index>=0; index--) {
                    stackData += values[index] + "\n";
                }

            }
        }

        stackData += "stack";
        Savelog.d(TAG, debug, stackData);
        mStackView.setText(stackData);
    }

    private void displayOutput() {
        String newline = "<br>";
        String colorStart = "<font color='red'>";
        String colorEnd = "</font>";
        String displayData = "";

        if (mMode==Mode_declare) {
            String error = mStackMachine.getCompilationError();
            if (error!=null) {
                displayData = error;
            }
            else {
                displayData = "Enter expression and Compile";
            }
        }
        else if (mMode==Mode_assign) {
            if (mStackMachine.isCompiled()) {
                // double-check to make sure the expression is compiled
                String sourceExpr = mStackMachine.getExpr();
                String postfix = mStackMachine.getPostfixExpression();
                String program[] = mStackMachine.getProgram();

                displayData = "Expr = " + sourceExpr + newline;
                displayData += "Postfix form = " + postfix + newline;
                for (int index=0; index<program.length; index++) {
                    displayData += program[index] + newline;
                }
            }
        }
        else if (mMode==Mode_execute) {
            if (mStackMachine.isCompiled()) {
                // double-check to make sure the expression is compiled
                String sourceExpr = mStackMachine.getExpr();
                String postfix = mStackMachine.getPostfixExpression();
                String program[] = mStackMachine.getProgram();

                displayData = "Expr = " + sourceExpr + newline;
                displayData += "Postfix form = " + postfix + newline;
                for (int index=0; index<program.length; index++) {
                    if (index==mStackMachine.getProgramCounter()) {
                        displayData += colorStart + "PC> " + program[index] + colorEnd + newline;
                    }
                    else {
                        displayData += program[index] + newline;
                    }
                }
                if (mStackMachine.isCompleted()) {
                    displayData += colorStart + "Result: " + mStackMachine.getResult() + colorEnd;
                }
            }

        }

        mDisplayView.setText(Html.fromHtml(displayData), TextView.BufferType.SPANNABLE);
    }
}
