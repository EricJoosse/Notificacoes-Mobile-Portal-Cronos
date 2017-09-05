package pt.truewind.cronostest.util.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;

import pt.truewind.cronostest.R;

public class AlertPopupDialog {

    private AlertDialog popup;
    private final AlertDialog.Builder builder;
    private ButtonLogic buttonLogic;

    public interface ButtonLogic {
        void positiveButton();
        void negativeButton();
    }

    public AlertPopupDialog(Context context, int title, int message, ButtonLogic buttonLogic) {
        this(context, context.getString(title), context.getString(message), buttonLogic);
    }

    public AlertPopupDialog(Context context, String title, String message) {
        this(context, title, message, null);
    }

    public AlertPopupDialog(Context context, String title, String message, ButtonLogic buttonLogic) {

        this.buttonLogic = buttonLogic;

        //builder = new AlertDialog.Builder(context);
        builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCronos));
        builder.setMessage(message);
        builder.setTitle(title);

        addOkButton();

        // Create the AlertDialog
        popup = builder.create();
    }

    public void addOkButton() {

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                popup.dismiss();

                if(buttonLogic != null){
                    buttonLogic.positiveButton();
                }
            }
        });

        popup = builder.create();
    }

    public void addCancelButton() {

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                popup.dismiss();

                if(buttonLogic != null){
                    buttonLogic.negativeButton();
                }
            }

        });

        popup = builder.create();
    }

    public void show() {
        this.popup.show();
    }

    public void dismiss() {
        if(isShowing()) {
            this.popup.dismiss();
            this.popup = null;
        }
    }

    public boolean isShowing(){
        return this.popup.isShowing();
    }

    public AlertDialog.Builder getBuilder(){
        return this.builder;
    }
}
