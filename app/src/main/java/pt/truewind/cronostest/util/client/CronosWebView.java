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

    // O seguinte override não funcionou com Android 7.0: o teclado Android aparece numérico,
    // como deveria para 90 % dos campos, porém não exibe nenhuma opção para mudar o teclado
    // para alfabético no caso de campos texto:
//    @Override
//    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
//        InputConnection ic = super.onCreateInputConnection(outAttrs);
//     // Alterar teclado Android para default numérico:
//        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;
//        return ic;
//    }

}
