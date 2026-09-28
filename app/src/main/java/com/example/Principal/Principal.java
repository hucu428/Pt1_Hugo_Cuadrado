package com.example.Principal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Clase principal que gestiona la lógica de la calculadora.
 * Mejorada con persistencia de estado ante cambios de configuración (rotación)
 * y uso de expresiones lambda.
 */
public class Principal extends AppCompatActivity {

    private static final String STATE_FIRST_OPERAND = "first_operand";
    private static final String STATE_CURRENT_OPERATOR = "current_operator";
    private static final String STATE_IS_NEW_OP = "is_new_op";

    // Referencia al TextView donde se muestran los números y resultados
    private TextView tvDisplay;
    
    // Almacena el primer número de la operación
    private double firstOperand = 0;
    
    // Almacena el operador actual ("+", "-", "*", "/")
    private String currentOperator = "";
    
    // Indica si estamos empezando a escribir un nuevo número (después de pulsar un operador)
    private boolean isNewOp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos el display buscando su ID en el layout
        tvDisplay = findViewById(R.id.tvDisplay);

        // Restaurar estado si la actividad fue recreada (ej. rotación de pantalla)
        if (savedInstanceState != null) {
            firstOperand = savedInstanceState.getDouble(STATE_FIRST_OPERAND, 0);
            currentOperator = savedInstanceState.getString(STATE_CURRENT_OPERATOR, "");
            isNewOp = savedInstanceState.getBoolean(STATE_IS_NEW_OP, true);
        }

        // Array con los IDs de todos los botones numéricos
        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        // Listener común para todos los botones numéricos usando lambdas
        View.OnClickListener numberListener = v -> {
            Button b = (Button) v;
            String currentText = tvDisplay.getText().toString();
            String errorMsg = getString(R.string.error_div_zero);
            
            // Si es un nuevo número o hay un mensaje de error, reseteamos el display
            if (isNewOp || currentText.equals(errorMsg)) {
                tvDisplay.setText(b.getText());
                isNewOp = false;
            } else {
                tvDisplay.append(b.getText());
            }
        };

        // Asignamos el listener numérico a cada botón del array
        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(numberListener);
        }

        // Asignamos el listener de operaciones a los botones correspondientes
        findViewById(R.id.btnAdd).setOnClickListener(opListener);
        findViewById(R.id.btnSub).setOnClickListener(opListener);
        findViewById(R.id.btnMul).setOnClickListener(opListener);
        findViewById(R.id.btnDiv).setOnClickListener(opListener);

        // Botón '=': Calcula el resultado final
        findViewById(R.id.btnEqual).setOnClickListener(v -> {
            calculate();
            currentOperator = "";
        });

        // Botón 'C': Limpia la pantalla y reinicia las variables
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            tvDisplay.setText("0");
            firstOperand = 0;
            currentOperator = "";
            isNewOp = true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(STATE_FIRST_OPERAND, firstOperand);
        outState.putString(STATE_CURRENT_OPERATOR, currentOperator);
        outState.putBoolean(STATE_IS_NEW_OP, isNewOp);
    }

    /**
     * Listener para los botones de operación (+, -, *, /)
     */
    private final View.OnClickListener opListener = v -> {
        Button b = (Button) v;
        String currentText = tvDisplay.getText().toString();
        String errorMsg = getString(R.string.error_div_zero);
        
        if (currentText.equals(errorMsg)) {
            return;
        }
        
        try {
            if (!currentOperator.isEmpty() && !isNewOp) {
                calculate();
                currentText = tvDisplay.getText().toString();
                if (currentText.equals(errorMsg)) return;
            }

            firstOperand = Double.parseDouble(currentText);
            currentOperator = b.getText().toString();
            isNewOp = true;
        } catch (NumberFormatException e) {
            tvDisplay.setText("0");
        }
    };

    /**
     * Realiza el cálculo matemático basándose en los operandos y el operador guardado.
     */
    private void calculate() {
        if (currentOperator.isEmpty()) return;
        String currentText = tvDisplay.getText().toString();
        String errorMsg = getString(R.string.error_div_zero);
        if (currentText.equals(errorMsg)) return;

        try {
            double secondOperand = Double.parseDouble(currentText);
            double result;

            switch (currentOperator) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "*":
                    result = firstOperand * secondOperand;
                    break;
                case "/":
                    if (secondOperand == 0) {
                        tvDisplay.setText(R.string.error_div_zero);
                        isNewOp = true;
                        return;
                    }
                    result = firstOperand / secondOperand;
                    break;
                default:
                    return;
            }

            // Mostramos el resultado. Si es entero, quitamos los decimales (.0)
            if (result == (long) result) {
                tvDisplay.setText(String.format("%d", (long) result));
            } else {
                tvDisplay.setText(String.valueOf(result));
            }
            isNewOp = true;
        } catch (NumberFormatException e) {
            tvDisplay.setText("0");
        }
    }
}
