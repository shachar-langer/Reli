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
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;

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

	public static AlertDialog showDialog(Context ctx, int title, int msg, int btn1,
			int btn2, DialogInterface.OnClickListener listener1,
			DialogInterface.OnClickListener listener2)
	{

		return showDialog(ctx, ctx.getString(title), ctx.getString(msg), ctx.getString(btn1),
				ctx.getString(btn2), listener1, listener2);

	}

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

	public static AlertDialog showDialog(Context ctx, int title, int msg, int btn1,
			int btn2, DialogInterface.OnClickListener listener)
	{

		return showDialog(ctx, ctx.getString(title), ctx.getString(msg), ctx.getString(btn1),
				ctx.getString(btn2), listener);

	}

	public static AlertDialog showDialog(Context ctx, String title, String msg,
			DialogInterface.OnClickListener listener)
	{

		return showDialog(ctx, title, msg, ctx.getString(android.R.string.ok), null,
				listener, null);
	}

	public static AlertDialog showDialog(Context ctx, int msg,
			DialogInterface.OnClickListener listener)
	{

		return showDialog(ctx, null, ctx.getString(msg),
				ctx.getString(android.R.string.ok), null, listener, null);
	}

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


	public static AlertDialog showDialog(Context ctx, int msg)
	{

		return showDialog(ctx, ctx.getString(msg));

	}


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


	public static final void hideKeyboard(Activity ctx)
	{
		if (ctx.getCurrentFocus() != null)
		{
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(), 0);
		}
	}

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




    public static void loadImageToParse(Resources res, int resourceID, Context ctx) {
        byte[] byteArray = convertBitmapToByteArray(res, resourceID);

        // Create the ParseFile
        ParseFile file = new ParseFile("androidbegin.png", byteArray);
        // Upload the image into Parse Cloud
        file.saveInBackground();

        // Create a New Class called "ImageUpload" in Parse
        ParseObject imgupload = new ParseObject("ImageUpload");

        // Create a column named "ImageName" and set the string
        imgupload.put("ImageName", "AndroidBegin Logo");

        // Create a column named "ImageFile" and insert the image
        imgupload.put("ImageFile", file);

        // Create the class and the columns
        imgupload.saveInBackground();

        // Show a simple toast message
        Toast.makeText(ctx, "Image Uploaded", Toast.LENGTH_SHORT).show();
    }

    public static byte[] convertBitmapToByteArray(Resources res, int resourceID) {
        // Locate the image in res > drawable-hdpi
        Bitmap bitmap = BitmapFactory.decodeResource(res, resourceID);

        // Convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        // Convert to Byte Array
        return stream.toByteArray();
    }
}