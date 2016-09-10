package com.ztech.fakecallm7;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectContactFragment extends Fragment {

    private EditText nameInput;

    private EditText numberInput;

    private ImageButton selectContactButton;

    final static int PICK_CONTACT = 1;

    private Activity activity;

    public SelectContactFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_select_contact, container, false);

        activity = getActivity();

        nameInput = (EditText)view.findViewById(R.id.nameInput);

        numberInput = (EditText)view.findViewById(R.id.numberInput);

        selectContactButton = (ImageButton)view.findViewById(R.id.selectContactButton);

        selectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

                startActivityForResult(intent, PICK_CONTACT);

            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) return;

        int index;

        String id, name = "", phone = "", photo = "";

        Cursor cursor = null;

        int hasPhone;

        switch (requestCode) {

            case PICK_CONTACT:

                Uri resultURI = data.getData();

                cursor = getActivity().getContentResolver().query(resultURI, null, null, null, null);

                if (cursor.moveToFirst()) {

                    index = cursor.getColumnIndex(ContactsContract.Contacts._ID);

                    id = cursor.getString(index);

                    index = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

                    name = cursor.getString(index);

                    index = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

                    hasPhone = cursor.getInt(index);

                    index = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

                    photo = cursor.getString(index);

                    if (hasPhone != 1) {
                        Toast.makeText(getActivity(), "Contact doesn't have a phone number!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Uri.Builder b = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id).buildUpon();

                    b.appendPath(ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);

                    Uri contactURI = b.build();

                    String[] projection = {
                            ContactsContract.Contacts.Entity.RAW_CONTACT_ID,
                            ContactsContract.Contacts.Entity.DATA1,
                            ContactsContract.Contacts.Entity.MIMETYPE,
                            ContactsContract.Contacts.Entity.PHOTO_URI
                    };

                    String sortOrder = ContactsContract.Contacts.Entity.RAW_CONTACT_ID + " ASC";

                    cursor = getActivity().getContentResolver().query(contactURI, projection, null, null, sortOrder);

                    String mime;

                    int mimeIndex = cursor.getColumnIndex(ContactsContract.Contacts.Entity.MIMETYPE);

                    int dataIndex = cursor.getColumnIndex(ContactsContract.Contacts.Entity.DATA1);

                    if (cursor.moveToFirst()) {
                        do {
                            mime = cursor.getString(mimeIndex);
                            if (mime.equalsIgnoreCase(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                phone = cursor.getString(dataIndex);
                            }

                        } while(cursor.moveToNext());

                    }

                }

                cursor.close();

                nameInput.setText(name);

                numberInput.setText(phone);

                ((IEventListener)activity).sendContactImage(photo);

                if (photo != null) {

                    Uri photoURI = Uri.parse(photo);

                    selectContactButton.setImageURI(photoURI);

                } else {

                    Drawable d = getActivity().getDrawable(R.drawable.img_no_image);

                    selectContactButton.setImageDrawable(d);

                }

                break;
        }
    }

    interface IEventListener {
        public void sendContactImage(String contactImage);
    }

}
