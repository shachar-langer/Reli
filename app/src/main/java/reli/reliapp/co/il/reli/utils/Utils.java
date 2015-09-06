package reli.reliapp.co.il.reli.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;

import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.dataStructures.ReliUserType;
import reli.reliapp.co.il.reli.main.MainActivity;

/**
 * The Class Utils is a common class that hold many kind of different useful
 * utility methods.
 */
public class Utils
{

	/**
	 * Show dialog.
	 * 
	 * @param ctx
	 *            the ctx
	 * @param msg
	 *            the msg
	 * @param btn1
	 *            the btn1
	 * @param btn2
	 *            the btn2
	 * @param listener1
	 *            the listener1
	 * @param listener2
	 *            the listener2
	 * @return the alert dialog
	 */
	public static AlertDialog showDialog(Context ctx, String title, String msg, String btn1,
			String btn2, DialogInterface.OnClickListener listener1,
			DialogInterface.OnClickListener listener2)
	{

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton(btn1, listener1);
		if (btn2 != null)
			builder.setNegativeButton(btn2, listener2);

		AlertDialog alert = builder.create();
		alert.show();
		return alert;

	}

    /* ========================================================================== */

	public static AlertDialog showDialog(Context ctx, int title, int msg, int btn1,
			int btn2, DialogInterface.OnClickListener listener1,
			DialogInterface.OnClickListener listener2)
	{

		return showDialog(ctx, ctx.getString(title), ctx.getString(msg), ctx.getString(btn1),
				ctx.getString(btn2), listener1, listener2);

	}

    /* ========================================================================== */

	public static AlertDialog showDialog(Context ctx, String title, String msg, String btn1,
			String btn2, DialogInterface.OnClickListener listener)
	{

		return showDialog(ctx, title, msg, btn1, btn2, listener,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id)
					{

						dialog.dismiss();
					}
				});

	}

    /* ========================================================================== */

	public static AlertDialog showDialog(Context ctx, int title, int msg, int btn1,
			int btn2, DialogInterface.OnClickListener listener)
	{

		return showDialog(ctx, ctx.getString(title), ctx.getString(msg), ctx.getString(btn1),
				ctx.getString(btn2), listener);

	}

    /* ========================================================================== */

	public static AlertDialog showDialog(Context ctx, String title, String msg,
			DialogInterface.OnClickListener listener)
	{

		return showDialog(ctx, title, msg, ctx.getString(android.R.string.ok), null,
				listener, null);
	}

    /* ========================================================================== */

	public static AlertDialog showDialog(Context ctx, int msg,
			DialogInterface.OnClickListener listener)
	{

		return showDialog(ctx, null, ctx.getString(msg),
				ctx.getString(android.R.string.ok), null, listener, null);
	}

    /* ========================================================================== */

	public static AlertDialog showDialog(Context ctx, String msg)
	{
    	return showDialog(ctx, null, msg, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id)
			{

				dialog.dismiss();
			}
		});

	}

    /* ========================================================================== */

	public static AlertDialog showDialog(Context ctx, int msg)
	{

		return showDialog(ctx, ctx.getString(msg));

	}

    /* ========================================================================== */

	public static void showDialog(Context ctx, int title, int msg,
			DialogInterface.OnClickListener listener)
	{

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton(android.R.string.ok, listener);
		builder.setTitle(title);
		AlertDialog alert = builder.create();
		alert.show();
	}

    /* ========================================================================== */

	public static final void hideKeyboard(Activity ctx)
	{
		if (ctx.getCurrentFocus() != null)
		{
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(), 0);
		}
	}

    /* ========================================================================== */

	public static final void hideKeyboard(Activity ctx, View v)
	{
		try
		{
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

    /* ========================================================================== */

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        // Convert to Byte Array
        return stream.toByteArray();
    }

    /* ========================================================================== */

    public static void setAvatar(final ImageView iv, final String senderID) {
        // Check if the image is already cached
        if (MainActivity.usersAvatars.containsKey(senderID)) {
            byte[] currentAvatar = MainActivity.usersAvatars.get(senderID);
            iv.setImageBitmap(BitmapFactory.decodeByteArray(currentAvatar, 0, currentAvatar.length));
            return;
        }

        ParseQuery<ReliUser> userQuery = ReliUser.getReliQuery();
        userQuery.getInBackground(senderID, new GetCallback<ReliUser>() {
            public void done(ReliUser reliUser, ParseException e) {
                if (e == null) {
                    // If this is anonymous user - return (the default resource is the one of Anonymous)
                    if (reliUser.getUserType() == ReliUserType.ANONYMOUS_USER) {
                        return;
                    }

                    // Handle the case of a Facebook user
                    ParseFile avatarFile = reliUser.getAvatar();
                    if (avatarFile != null) {
                        avatarFile.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    // Add to the cache
                                    MainActivity.usersAvatars.put(senderID, data);

                                    // Load the image
                                    iv.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}