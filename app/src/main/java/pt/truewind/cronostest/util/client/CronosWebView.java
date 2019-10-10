package pt.truewind.cronostest.util.client;

import android.webkit.WebView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.text.InputType;
import pt.truewind.cronostest.log.Logger;

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
        // para decidir o tipo de teclado automaticamente.
        // Outra coisa, foi testado que <input type="tel"/> não adiantou para exibir um teclado
        // com apenas números inteiros sem decimais, pois Android 7.0 nostrou um ponto (.) no teclado...
        // Se apertar nas teclas "Próximo campo" ou "Campo anterior", Android ajusta automaticamente
        // o tipo de teclado de alfanumérico para numérico e de volta.
     // outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;


        // Para resolver o bug que o botão "Próximo campo" não aparece no teclado em alguns casos:
        // (veja https://stackoverflow.com/questions/16686681/soft-keyboard-in-webview-no-next-button-to-tab-between-input-fields)
     // if (outAttrs != null) {
//         // Remover outros botões que não se aplicam nas telas exibidas dentro do WebView para liberar espaço no teclado:
//            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_GO;
//            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_SEARCH;
//            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_SEND;
//            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_DONE;
//            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_NONE;
//
//         // Adicionar o botão "Próximo":
//            outAttrs.imeOptions |= EditorInfo.IME_ACTION_NEXT;
//
//         // Para resolver o bug que o botão "Fechar teclado" também não aparece no teclado em alguns casos:
//            outAttrs.imeOptions |= EditorInfo.IME_ACTION_DONE;

            // O acima foi testado e não funcionou usando Android 7.0:
            //	    - A tecla para ir para o próximo campo não está sendo mais exibida;
            //    	- A tecla habilitada no teclado numérico serve para voltar para o
            //        campo anterior e não para fechar o teclado, ou seja, o botão que fecha o teclado não apareceu;

         // Logger.d(this.getContext(), "CronosWebView: outAttrs.inputType = " + Integer.toString(outAttrs.inputType));
         // Logger.d(this.getContext(), "CronosWebView: outAttrs.imeOptions = " + Integer.toString(outAttrs.imeOptions));
     // }

        return ic;
    }

}
