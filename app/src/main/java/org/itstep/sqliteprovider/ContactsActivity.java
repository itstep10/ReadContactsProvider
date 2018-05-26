package org.itstep.sqliteprovider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

/**
 * Created by Koren Vitalii on 26.05.2018.
 */
public class ContactsActivity extends Activity
{
    private static final int READ_CONTACTS_REQUEST = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        if(isPermissionGranted())
            init();
    }

    private void init()
    {
        TextView contactView = (TextView) findViewById(R.id.contactview);

        Cursor cursor = getContacts();

        while(cursor.moveToNext())
        {

            String displayName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            contactView.append("Name: ");
            contactView.append(displayName);
            contactView.append("\n");
        }
    }

    public boolean isPermissionGranted()
    {
        boolean result;

        if(Build.VERSION.SDK_INT >= 23)
        {
            if((checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED))
            {
                result = true;
            }
            else
            {
                String[] permissions = {
                        Manifest.permission.READ_CONTACTS
                };
                ActivityCompat.requestPermissions(this, permissions, READ_CONTACTS_REQUEST);
                result = false;
            }
        }
        else
        {
            result = true;
        }

        return result;
    }

    private Cursor getContacts()
    {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";


        return managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case READ_CONTACTS_REQUEST:
            {
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    init();
                else
                    showInfoDialog();
            }
        }
    }

    private void showInfoDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.request_permission_failed).setTitle(R.string.warning)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> ContactsActivity.this.finish());
//                new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        ContactsActivity.this.finish();
//                    }
//                }

        builder.create().show();
    }

}