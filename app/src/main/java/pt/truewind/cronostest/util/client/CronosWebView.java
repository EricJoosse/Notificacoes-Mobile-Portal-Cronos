package pt.truewind.cronostest.util.client;

import android.webkit.WebView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.text.InputType;

/**
 * Created by Eric Joosse on 28/08/2018.
 */

public class CronosWebView extends WebView {

    public CronosWebView(Context context) {
        this(context, null);
    }

    public CronosWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CronosWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /* any initialisation works here */
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);

        // O seguinte não funcionou com Android 7.0: o teclado Android fica numérico,
        // como deveria para 90 % dos campos, porém não exibe nenhuma tecla para mudar o teclado
        // para alfanumérico no caso de campos texto. A solução melhor é usar HTML5 alterando
        // <input type="text"/> para <input type="number"/>. Foi testado que Android usa isso
        // para decidir o tipo de teclado automaticamente:
    //  outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;

        return ic;
    }

}
