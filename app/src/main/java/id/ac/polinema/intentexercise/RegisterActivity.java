package id.ac.polinema.intentexercise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

//    Deklarasi variabel key

    public static final String FULLNAME_KEY = "fullname";
    public static final String EMAIL_KEY = "email";
    public static final String HOMEPAGE_KEY = "homepage";
    public static final String ABOUT_KEY = "about";
    public static final String IMAGE_KEY = "image";

    //    Deklarasi variabel
    private static final String TAG = RegisterActivity.class.getCanonicalName();
    private static final int GALLERY_REQUEST_CODE = 1;

    private EditText fullnameInput;
    private EditText passwordInput;
    private EditText confirmInput;
    private EditText emailInput;
    private EditText homeInput;
    private EditText aboutInput;
    private ImageView imageProfil;
    private ImageView changeImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullnameInput = findViewById(R.id.text_fullname);
        passwordInput = findViewById(R.id.text_password);
        confirmInput = findViewById(R.id.text_confirm_password);
        emailInput = findViewById(R.id.text_email);
        homeInput = findViewById(R.id.text_homepage);
        aboutInput = findViewById(R.id.text_about);
        imageProfil = findViewById(R.id.image_profile);
        changeImage = findViewById(R.id.image_profile);

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(RegisterActivity.this);
            }
        });

    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }




    public void submit(View view) {
        String about = aboutInput.getText().toString();
        String fullname = fullnameInput.getText().toString();
        String email = emailInput.getText().toString();
        String homepage = homeInput.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//      String img = imageProfil.getScaleType().toString();

        if (fullnameInput.getText().toString().length() == 0) {
            fullnameInput.setError("isi nama ");
        } else if (emailInput.getText().toString().length() == 0) {
            emailInput.setError("isi email");
        } else if (passwordInput.getText().toString().length() == 0) {
            passwordInput.setError("isi password");
        } else if (confirmInput.getText().toString().length() == 0) {
            confirmInput.setError("isi confirm password");
        } else if (homeInput.getText().toString().length() == 0) {
            homeInput.setError("isi homepage");
        } else if (aboutInput.getText().toString().length() == 0) {
            aboutInput.setError("isi about");
        } else if(!email.matches(emailPattern)) {
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show();
        }
        else {
            String password = passwordInput.getText().toString();

            if (password.equals(confirmInput.getText().toString())) {

                Intent intent = new Intent(this, ProfileActivity.class);
                imageProfil.buildDrawingCache();
                Bitmap image = imageProfil.getDrawingCache();
                Bundle extras = new Bundle();
                extras.putParcelable(IMAGE_KEY, image);
                intent.putExtras(extras);
//                 Bitmap bmp = null;
//                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                 bmp.compress(Bitmap.CompressFormat.PNG, 50, baos);
//                 intent.putExtra("img", img );
                intent.putExtra(ABOUT_KEY, about);
                intent.putExtra(FULLNAME_KEY, fullname);
                intent.putExtra(EMAIL_KEY, email);
                intent.putExtra(HOMEPAGE_KEY, homepage);
                startActivity(intent);

            } else
                Toast.makeText(this, "Password tidak sesuai", Toast.LENGTH_SHORT).show();
        }
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imageProfil.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageProfil.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                        if (requestCode == GALLERY_REQUEST_CODE) {
                            if (data != null) {
                                try {
                                    Uri imageUri = data.getData();
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                                    imageProfil.setImageBitmap(bitmap);
                                } catch (IOException e) {
                                    Toast.makeText(this, "Can't load image", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }

                    }

                    break;
            }
        }
    }

    public void handleChangeImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }
}





