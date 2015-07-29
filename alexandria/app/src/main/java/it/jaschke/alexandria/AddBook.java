package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;

public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public String LOG_TAG = AddBook.class.getSimpleName() ;

    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";

    private final int LOADER_ID = 1;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    /*
    * UI Components
    */
    private View rootView;
    private EditText eanInputField;
    private TextView mBookTitleTextView;
    private TextView mBookSubTitleTextView;
    private TextView mAuthorsTextView;
    private ImageView mBookCoverImageView;
    private TextView mCategoriesTextView;
    private ImageButton mSaveButton;
    private ImageButton mDeleteButton;
    private Button mScanButton;


    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(eanInputField !=null) {
            outState.putString(EAN_CONTENT, eanInputField.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);

        eanInputField = (EditText) rootView.findViewById(R.id.ean);
        mBookTitleTextView = ((TextView) rootView.findViewById(R.id.bookTitle));
        mBookSubTitleTextView = ((TextView) rootView.findViewById(R.id.bookSubTitle));
        mAuthorsTextView = ((TextView) rootView.findViewById(R.id.authors));
        mBookCoverImageView = (ImageView) rootView.findViewById(R.id.bookCover);
        mCategoriesTextView = (TextView) rootView.findViewById(R.id.categories);
        mScanButton = (Button) rootView.findViewById(R.id.scan_button);
        mSaveButton = (ImageButton) rootView.findViewById(R.id.save_button);
        mDeleteButton = (ImageButton) rootView.findViewById(R.id.delete_button);


        eanInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    /*
                    * Once a book's detail is loaded let is stay on the screen until
                    * a new valid ISBN is entered.
                    */
//                    clearFields();
                    return;
                }

                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.
                //when you're done, remove the toast below.

//                Context context = getActivity();
//                CharSequence text = "This button should let you scan a book for its barcode!";
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

                Intent intent = new Intent(Intents.Scan.ACTION);
                intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                startActivityForResult(intent, 0);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eanInputField.setText("");
                clearFields();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, eanInputField.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                eanInputField.setText("");
                clearFields();
            }
        });

        if(savedInstanceState!=null){
            eanInputField.setText(savedInstanceState.getString(EAN_CONTENT));
            eanInputField.setHint("");
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0)  {
            if ((resultCode == Activity.RESULT_CANCELED) || (resultCode == -1000))  {
                Toast.makeText(getActivity(), getString(R.string.nothing_captured), Toast.LENGTH_SHORT).show();
            } else  {

                Bundle bundle = data.getExtras();

                String upc_ean_extension = bundle.getString("SCAN_RESULT_UPC_EAN_EXTENSION");
                String result = bundle.getString("SCAN_RESULT");
                int orientation = bundle.getInt("SCAN_RESULT_ORIENTATION");
                String format = bundle.getString("SCAN_RESULT_FORMAT");

                for (String key : bundle.keySet())  {
                    Object value = bundle.get(key);
                    Log.d(TAG, String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
                }

                eanInputField.setText(result);
            }
        }
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(eanInputField.getText().length()==0){
            return null;
        }
        String eanStr= eanInputField.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitleTextView.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mBookSubTitleTextView.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));

        /*
        * Fixes a NullPointerException if there is no author for the current record.
        */
        if (authors == null)  authors = "";

        String[] authorsArr = authors.split(",");
        mAuthorsTextView.setLines(authorsArr.length);
        mAuthorsTextView.setText(authors.replace(",", "\n"));

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            new DownloadImage(mBookCoverImageView).execute(imgUrl);
            mBookCoverImageView.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mCategoriesTextView.setText(categories);

        mSaveButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        mBookTitleTextView.setText("");
        mBookSubTitleTextView.setText("");
        mAuthorsTextView.setText("");
        mCategoriesTextView.setText("");
        mBookCoverImageView.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }
}
