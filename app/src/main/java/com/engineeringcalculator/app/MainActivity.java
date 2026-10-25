
package com.engineeringcalculator.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {

    LinearLayout root;
    EditText inputA, inputB;
    TextView result;

    int navy = Color.rgb(18, 28, 48);
    int blue = Color.rgb(35, 105, 210);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);

        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 20, 18, 24);
        root.setBackgroundColor(Color.rgb(245, 247, 251));
        scroll.addView(root);
        setContentView(scroll);

        TextView title = new TextView(this);
        title.setText("ENGINEERING CALCULATOR");
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(navy);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Scientific • Electrical • Resistance");
        subtitle.setTextSize(14);
        subtitle.setTextColor(Color.DKGRAY);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 6, 0, 22);
        root.addView(subtitle);

        addHeading("SCIENTIFIC CALCULATOR");
        inputA = addInput("Enter number or expression");
        addButton("Calculate", v -> calculate());
        addButton("sin(x)", v -> scientific("sin"));
        addButton("cos(x)", v -> scientific("cos"));
        addButton("tan(x)", v -> scientific("tan"));
        addButton("√x", v -> scientific("sqrt"));
        addButton("x²", v -> scientific("square"));
        addButton("log₁₀(x)", v -> scientific("log"));
        addButton("Clear", v -> {
            inputA.setText("");
            result.setText("Result will appear here");
        });

        addHeading("OHM'S LAW");
        inputB = addInput("Enter second value");
        TextView hint = new TextView(this);
        hint.setText("Enter voltage (V) and resistance (Ω) to find current.");
        hint.setTextColor(Color.DKGRAY);
        hint.setTextSize(13);
        root.addView(hint);

        addButton("Find Current (I = V / R)", v -> ohm("I"));
        addButton("Find Voltage (V = I × R)", v -> ohm("V"));
        addButton("Find Resistance (R = V / I)", v -> ohm("R"));

        addHeading("RESISTANCE CALCULATOR");
        addButton("Series: R1 + R2", v -> resistance(true));
        addButton("Parallel: R1 × R2 / (R1 + R2)",
                v -> resistance(false));

        result = new TextView(this);
        result.setText("Result will appear here");
        result.setTextColor(navy);
        result.setTextSize(19);
        result.setTypeface(null, Typeface.BOLD);
        result.setGravity(Gravity.CENTER);
        result.setPadding(12, 22, 12, 22);
        result.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                -1, -2);
        rp.topMargin = 20;
        root.addView(result, rp);
    }

    void addHeading(String text) {
        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextSize(17);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setTextColor(blue);
        heading.setPadding(0, 22, 0, 10);
        root.addView(heading);
    }

    EditText addInput(String hint) {
        EditText edit = new EditText(this);
        edit.setSingleLine(true);
        edit.setHint(hint);
        edit.setTextSize(16);
        edit.setPadding(14, 8, 14, 8);
        edit.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = 10;
        root.addView(edit, p);
        return edit;
    }

    void addButton(String text, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(blue));
        button.setOnClickListener(listener);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = 6;
        root.addView(button, p);
    }

    double number(EditText edit) {
        String s = edit.getText().toString().trim();
        if (s.isEmpty()) throw new IllegalArgumentException(
                "Please enter a number.");
        double n = Double.parseDouble(s);
        if (Double.isNaN(n) || Double.isInfinite(n))
            throw new IllegalArgumentException("Invalid number.");
        return n;
    }

    void show(double value, String unit) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            result.setText("Invalid calculation");
            return;
        }
        result.setText(String.format(Locale.US, "%.6g %s",
                value, unit));
    }

    void calculate() {
        try {
            String s = inputA.getText().toString().trim();
            if (s.isEmpty()) {
                result.setText("Enter a number first.");
                return;
            }
            show(Double.parseDouble(s), "");
        } catch (Exception e) {
            result.setText("Enter a valid number.");
        }
    }

    void scientific(String op) {
        try {
            double x = number(inputA);
            double y;
            switch (op) {
                case "sin":
                    y = Math.sin(Math.toRadians(x));
                    break;
                case "cos":
                    y = Math.cos(Math.toRadians(x));
                    break;
                case "tan":
                    y = Math.tan(Math.toRadians(x));
                    break;
                case "sqrt":
                    if (x < 0) throw new IllegalArgumentException(
                            "Square root needs a non-negative number.");
                    y = Math.sqrt(x);
                    break;
                case "square":
                    y = x * x;
                    break;
                case "log":
                    if (x <= 0) throw new IllegalArgumentException(
                            "Log requires a number greater than zero.");
                    y = Math.log10(x);
                    break;
                default:
                    return;
            }
            show(y, "");
        } catch (Exception e) {
            result.setText(e.getMessage() == null
                    ? "Enter a valid number." : e.getMessage());
        }
    }

    void ohm(String mode) {
        try {
            double a = number(inputA);
            double b = number(inputB);
            if (a < 0 || b < 0)
                throw new IllegalArgumentException(
                        "Values cannot be negative.");

            if (mode.equals("I")) {
                if (b == 0) throw new IllegalArgumentException(
                        "Resistance cannot be zero.");
                show(a / b, "A");
            } else if (mode.equals("V")) {
                show(a * b, "V");
            } else {
                if (b == 0) throw new IllegalArgumentException(
                        "Current cannot be zero.");
                show(a / b, "Ω");
            }
        } catch (Exception e) {
            result.setText(e.getMessage() == null
                    ? "Enter both values." : e.getMessage());
        }
    }

    void resistance(boolean series) {
        try {
            double a = number(inputA);
            double b = number(inputB);
            if (a < 0 || b < 0)
                throw new IllegalArgumentException(
                        "Resistance cannot be negative.");

            if (series) {
                show(a + b, "Ω");
            } else {
                if (a + b == 0)
                    throw new IllegalArgumentException(
                            "Both resistances cannot be zero.");
                show((a * b) / (a + b), "Ω");
            }
        } catch (Exception e) {
            result.setText(e.getMessage() == null
                    ? "Enter R1 in the first box and R2 in the second."
                    : e.getMessage());
        }
    }
}
