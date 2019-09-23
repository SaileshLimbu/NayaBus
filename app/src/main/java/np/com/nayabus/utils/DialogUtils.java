package np.com.nayabus.utils;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogUtils {
    public static void showFailedDialog(Context context, String titleText, String errorMessage) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titleText)
                .setContentText(errorMessage)
                .show();
    }
    public static void showSuccessDialog(Context context, String titleText, String errorMessage) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titleText)
                .setContentText(errorMessage)
                .show();
    }
}
