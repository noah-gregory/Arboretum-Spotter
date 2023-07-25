package com.example.arboretumspotter;

import static android.Manifest.permission.CAMERA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arboretumspotter.api.RetrofitAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadPostFragment extends Fragment
{
    private Context context = getActivity();

    /**
     * Logging tag for this class
     */
    private final String TAG = UploadPostFragment.class.toString();

    /**
     * Member variable of this fragment
     */
    private String userId;
    private String username;

    private ImageView imageView;

    private TextView tagsTextView;

    private EditText captionEditText;
    private EditText newTagEditText;

    private Button addPictureButton;
    private Button addTagButton;
    private Button clearTagsButton;
    private Button uploadPostButton;
    private Button cancelPostButton;
    private final int MAX_NUM_TAGS = 3;

    private Bitmap selectedImage;

    private ArrayList<String> tags;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;
    private ActivityResultLauncher<Intent> takePictureActivityResultLauncher;
    private ActivityResultLauncher<Intent> choosePictureActivityResultLauncher;


    public UploadPostFragment(String userId, String userName)
    {
        // Required empty public constructor
        this.userId = userId;
        this.username = userName;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UploadPostFragment.
     */
    public static UploadPostFragment newInstance(String userId, String userName)
    {
        return new UploadPostFragment(userId, userName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get result of requestCameraPermissionsIntent
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted ->
                {
                    if (isGranted)
                    {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        Log.d(TAG, "Camera Permission granted");
                    }
                    else
                    {
                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.

                        Log.d(TAG, "Camera Permission denied");
                    }
                });

        // Get result of requestCameraPermissionsIntent
        requestStoragePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted ->
                {
                    if (isGranted)
                    {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        Log.d(TAG, "Storage Permission granted");
                    }
                    else
                    {
                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.

                        Log.d(TAG, "Storage Permission denied");
                    }
                });

        // Get result of takePictureIntent
        takePictureActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent data = result.getData();

                        if(data != null)
                        {
                            selectedImage = (Bitmap) data.getExtras().get("data");
                            imageView.setImageBitmap(selectedImage);
                        }
                    }
                });

        // Get result of choosePictureIntent
        choosePictureActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        // There are no request codes
                        Intent data = result.getData();

                        if(data != null)
                        {
                            Uri selectedImage = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            if (selectedImage != null && getContext() != null)
                            {
                                Cursor cursor = getContext().getContentResolver()
                                        .query(selectedImage, filePathColumn, null, null, null);

                                if (cursor != null)
                                {
                                    cursor.moveToFirst();

                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    String picturePath = cursor.getString(columnIndex);
                                    imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                    cursor.close();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_post, container, false);

        imageView = (ImageView) view.findViewById(R.id.image_view_new_post);

        // Initialize reference to each textView
        tagsTextView = (TextView) view.findViewById(R.id.text_view_tags);

        // Initialize reference to each editText
        captionEditText = (EditText) view.findViewById(R.id.edit_text_caption);
        newTagEditText = (EditText) view.findViewById(R.id.edit_text_new_tag);

        // Initialize reference to each button
        addPictureButton = (Button) view.findViewById(R.id.button_add_picture);
        addTagButton = (Button) view.findViewById(R.id.button_add_tag);
        clearTagsButton = (Button) view.findViewById(R.id.button_clear_tags);
        uploadPostButton = (Button) view.findViewById(R.id.button_upload_post);
        cancelPostButton = (Button) view.findViewById(R.id.button_cancel_post);

        // OnClick listeners for each button on the Upload Post page
        addPictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Add Picture button clicked");

                // Check if all needed permissions have been granted
                if(requestPermissions(getActivity()))
                {
                    // Go to method to select image for post
                    selectImage(getActivity());
                    Log.d(TAG, "Camera and storage permissions granted");
                }
                else
                {
                    Log.d(TAG, "Permissions for adding an image were denied");
                }
            }
        });

        addTagButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Add Tag button clicked");

                String newTag = newTagEditText.getText().toString();

                if(tags == null)
                {
                    tags = new ArrayList<String>();
                }
                if(tags.size() < MAX_NUM_TAGS)
                {
                    // Add tag to tags array
                    tags.add(newTag);

                    // Add new tag to current list of tags displayed in tags textView
                    String currentTagsText = tagsTextView.getText().toString();
                    String updatedTagsText = currentTagsText + "  " + newTag;
                    tagsTextView.setText(updatedTagsText);
                }
                else
                {
                    Log.d(TAG, "Max number of post tags reached, cannot add tag");
                }
            }
        });

        clearTagsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Clear Tags button clicked");

                tags = null;

                // Set remove tags from textView showing them
                tagsTextView.setText(getString(R.string.text_tags));
            }
        });

        uploadPostButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Upload Post button clicked");

                // Get user input caption from editText objects
                if(selectedImage != null)
                {
                    Log.d(TAG, "Calling prepare post for upload");

                    if(tags != null)
                    {
                        prepareForUploadPost();
                    }
                    else
                    {
                        Log.d(TAG, "At least one tag required");
                    }
                }
                else
                {
                    Log.d(TAG, "Image bitmap was null");
                }
            }
        });

        cancelPostButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Cancel Post button clicked");
                clearPostInputs();
            }
        });

        return view;
    }

    private boolean requestPermissions(final Activity context)
    {
        int writeExternalPermission = ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int cameraPermission = ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (cameraPermission != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            requestCameraPermissionLauncher.launch(CAMERA);
        }

//        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED)
//        {
//            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            requestStoragePermissionLauncher.launch(WRITE_EXTERNAL_STORAGE);
//        }
        if (!listPermissionsNeeded.isEmpty())
        {
            if(arePermissionsGranted())
            {
                Log.d(TAG, "Permissions granted for photo selection");
                return true;
            }

            Log.d(TAG, "Not all permissions were grant to allow photo selection");
            return false;
        }

        return true;
    }

    private boolean arePermissionsGranted()
    {
        int writeExternalPermission = ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int cameraPermission = ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA);

        if (cameraPermission != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }

//        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED)
//        {
//            return false;
//        }

        return true;
    }

    /**
     * Sets up dialog box for choosing to get image from camera or storage.
     * Starts corresponding activityResultLauncher to get image with chosen method.
     *
     * @param context context from activity that hosts the fragment
     */
    private void selectImage(Context context){

        Log.d(TAG, "Selecting image method");

        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array

        // create a dialog for showing the optionsMenu

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set the items in builder
        builder.setItems(optionsMenu, (dialogInterface, i) ->
        {
            if(optionsMenu[i].equals("Take Photo"))
            {
                // Create and send intent to open the camera to take the picture
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureActivityResultLauncher.launch(takePictureIntent);
            }
            else if(optionsMenu[i].equals("Choose from Gallery"))
            {
                // Create and send intent to choose picture from external storage (Photo Gallery)
                Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                choosePictureActivityResultLauncher.launch(choosePictureIntent);
            }
            else if (optionsMenu[i].equals("Exit"))
            {
                dialogInterface.dismiss();
            }

        });
        builder.show();
    }

    /**
     * Gets all elements required for post.
     * Creates JSON object with elements and sends it to method to call API.
     */
    private void prepareForUploadPost()
    {
        // Get bitmap image as base 64 string representation
        String imageBase64 = bitmapImageToBase64(selectedImage);
        String caption = captionEditText.getText().toString();

        String[] tagsArray = new String[tags.size()];

        for (int i = 0; i < tags.size(); i++)
        {
            tagsArray[i] = tags.get(i);
        }

        Log.d(TAG, "Got caption: " + caption);
        Log.d(TAG, "Got tags: " + Arrays.toString(tagsArray));

        JSONObject obj = new JSONObject();

        try
        {
            obj.put("poster", username);
            obj.put("image", imageBase64);
            obj.put("caption", caption);
            obj.put("tags", tags);
        }
        catch (JSONException e)
        {
            Log.d(TAG, "Upload post json could not be created");
        }

        Log.d(TAG, "Post JSON Object: " + obj);

        requestUploadPost(obj);
    }

    /**
     * Encodes a bitmap image into a Base64 string
     *
     * @param image image represented as Bitmap
     * @return the provided image encoded as a Base64 string
     */
    private String bitmapImageToBase64(Bitmap image)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.getEncoder().encodeToString(byteArray);

        Log.d(TAG, "Image as base 64:\n" + encoded + "\n");

        return encoded;
    }


    /**
     * Send POST request to remote API to upload a post
     * If login request return valid user id, calls loginSuccess method
     */
    private void requestUploadPost(JSONObject obj)
    {
        final String baseUrl = "https://arb-navigator-6c93ee5fc546.herokuapp.com/";

        // Creating a retrofit builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance for our retrofit api class.
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        // Create multipart body with a JSON object put in formData as string
        MultipartBody.Part multipartBody = MultipartBody.Part
                .createFormData("json", obj.toString());

        Call<Void> call = retrofitAPI.createUploadPost(multipartBody);

        // Asynchronously send the request and notify callback of its response
        call.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful())
                {
                    Log.d(TAG, "Got successful response from uploadPost API");
                    clearPostInputs();
                }
                else
                {
                    Log.d(TAG, "UploadPost POST response was not successful");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                Log.d(TAG, "UploadPost POST response failed: " + t.getMessage());
            }
        });
    }

    private void clearPostInputs()
    {
        // Remove image
        imageView.setImageResource(R.drawable.ic_feed_24);
        selectedImage = null;
        captionEditText.setText("");
        newTagEditText.setText("");
        tagsTextView.setText(getString(R.string.text_tags));
        tags = null;
    }
}