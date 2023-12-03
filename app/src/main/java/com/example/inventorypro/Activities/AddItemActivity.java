package com.example.inventorypro.Activities;

import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.inventorypro.DatabaseManager;
import com.example.inventorypro.Fragments.SelectTagsFragment;
import com.example.inventorypro.Helpers;
import com.example.inventorypro.Item;
import com.example.inventorypro.R;
import com.example.inventorypro.SliderAdapter;
import com.example.inventorypro.SliderItem;
import com.example.inventorypro.User;
import com.google.android.gms.common.api.OptionalModuleApi;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * AddItem Activity is responsible for gathering user input and re-creating the MainActivity with the parsed Item.
 */
public class AddItemActivity extends AppCompatActivity {
    private TextView header;
    private TextInputLayout name;
    private TextInputLayout date;
    private TextInputLayout make;
    private TextInputLayout model;
    private TextInputLayout serialNumber;
    private TextInputLayout description;
    private TextInputLayout comments;
    private TextInputLayout value;
    private ImageButton addTagButton, addImageButton, serialNumberScanButton;
    private Item originalEditingItem;
    private int selectedPosition;
    private boolean editMode = false;
    List<String> tags = new ArrayList<>();

    private ViewPager2 viewPager2;
    List<SliderItem> sliderItems = new ArrayList<>();

    private Button confirmButton;
    private Button cancelButton;
    private int PICK_IMAGES_REQUEST = 1;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        if(User.getInstance()==null){
            Intent addItemIntent = new Intent(getBaseContext(), SignInActivity.class);
            startActivity(addItemIntent);
            return;
        }

        // Gets values from the EditText
        name = findViewById(R.id.inputItemName);
        value = findViewById(R.id.inputValue);
        date = findViewById(R.id.inputDate);
        make = findViewById(R.id.inputMake);
        model= findViewById(R.id.inputModel);
        serialNumber = findViewById(R.id.inputSerialNumber);
        description = findViewById(R.id.inputDescription);
        comments = findViewById(R.id.comments);
        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);
        header = findViewById(R.id.add_header);
        viewPager2 = findViewById(R.id.viewPagerImageSlider);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (PICK_IMAGES_REQUEST == 1){
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Intent data = result.getData();
                                handleReceivedGalleryImages(data);
                            }
                        } else if (PICK_IMAGES_REQUEST == 2) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Intent data = result.getData();
                                Bundle bundle = result.getData().getExtras();
                                Bitmap bitmap = (Bitmap) bundle.get("data");

                                handleReceivedCameraImages(bitmap);

                            }
                        }
                    }
                });


        // Set the default date to the current date
        date.getEditText().setText(LocalDate.now().toString());

        addTagButton = findViewById(R.id.addTagButton);
        addImageButton = findViewById(R.id.addImageButton);
        serialNumberScanButton = findViewById(R.id.serialNumberScanButton);

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment selectTags = new SelectTagsFragment(null, tags);
                selectTags.show(getSupportFragmentManager(), "selectTags");
            }
        });
        addImageButton.setOnClickListener(Helpers.notImplementedClickListener);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddImageDialogue();
                //openImagePicker();
               // clickImage();

            }
        });
        serialNumberScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getBaseContext();
                ModuleInstallClient moduleInstallClient = ModuleInstall.getClient(context);
                OptionalModuleApi barcodeScanningModule = GmsBarcodeScanning.getClient(context);

                moduleInstallClient
                        .areModulesAvailable(barcodeScanningModule)
                        .addOnSuccessListener(
                                response -> {
                                    if (!response.areModulesAvailable()) {
                                        // Install barcode scanning module as it is not installed
                                        ModuleInstallRequest moduleInstallRequest =
                                                ModuleInstallRequest.newBuilder()
                                                        .addApi(barcodeScanningModule)
                                                        .build();

                                        moduleInstallClient.installModules(moduleInstallRequest);
                                        Helpers.toast(context, "");
                                    } else {
                                        GmsBarcodeScanning.getClient(context)
                                                .startScan()
                                                .addOnSuccessListener(barcode -> {
                                                    serialNumber.getEditText().setText(barcode.getRawValue());
                                                })
                                                .addOnCanceledListener(() -> {
                                                    Helpers.toast(getBaseContext(), getString(R.string.barcode_scan_cancelled_message));
                                                })
                                                .addOnFailureListener(e -> {
                                                    String failureMessage = getString(R.string.barcode_scan_failed_message) + e.getMessage();
                                                    Helpers.toast(getBaseContext(), failureMessage);
                                                });
                                    }
                                })
                        .addOnFailureListener(
                                e -> {
                                    Log.e(
                                            "SCANNING",
                                            String.format("Could not check if barcode module is installed due to %s", e.getMessage())
                                    );
                                });
            }
        });

        //calls sendItem if all inputs are valid
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    if (editMode) {
                        sendEditItem();
                    } else {
                        sendItem();
                    }
                }
            }
        });

        // Calls cancel if the user wants to return to the main activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        // Try to get a new item from the intent.
        Item potentialItem = tryGetItemFromIntent();
        String potentialSerialNumber = tryGetSerialNumberFromIntent();

        if (potentialItem != null) {
            editMode = true;
            originalEditingItem = potentialItem;
            // Set EditText values to the values of the selected Item
            name.getEditText().setText(potentialItem.getName());
            name.setHelperText("");
            value.getEditText().setText(String.valueOf(potentialItem.getValue()));
            date.getEditText().setText(potentialItem.getLocalDate().toString());
            make.getEditText().setText(potentialItem.getMake());
            model.getEditText().setText(potentialItem.getModel());
            serialNumber.getEditText().setText(potentialItem.getSerialNumber());
            description.getEditText().setText(potentialItem.getDescription());
            comments.getEditText().setText(potentialItem.getComment());
            tags.addAll(potentialItem.getTags());
            if (potentialItem.getImageUris().size() > 0) {
                DatabaseManager.getImageUris(potentialItem.getImageUris(), new OnSuccessListener<List<Uri>>() {
                    @Override
                    public void onSuccess(List<Uri> uris) {
                        int count = potentialItem.getImageUris().size();
                        for (int i = 0; i < count; i++) {
                            sliderItems.add(new SliderItem(uris.get(i)));
                        }
                        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, viewPager2,true);

                        viewPager2.setAdapter(sliderAdapter);
                        // Save the URI to the global variable if needed for later use
                        // this.uri = uri;
                        sliderAdapter.notifyDataSetChanged();
                        viewPager2.setBackground(null);

                        viewPager2.setClipToPadding(false);
                        viewPager2.setClipChildren(false);
                        viewPager2.setOffscreenPageLimit(3);
                        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


                        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                        compositePageTransformer.addTransformer(new MarginPageTransformer(5));
                        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                            @Override
                            public void transformPage(@NonNull View page, float position) {
                                float r = 1 - Math.abs(position);
                                page.setScaleY(0.85f + r * 0.15f);
                            }
                        });

                        viewPager2.setPageTransformer(compositePageTransformer);


                    }
                });
            }
        } else if (potentialSerialNumber != null) {
            serialNumber.getEditText().setText(potentialSerialNumber);
        }
    }



    private void showAddImageDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_options, null);
        Button galleryButton = dialogView.findViewById(R.id.galleryButton);
        Button cameraButton = dialogView.findViewById(R.id.cameraButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICK_IMAGES_REQUEST = 1;
                onGalleryButtonClick(); // Call your function to open the gallery here
                dialog.dismiss();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICK_IMAGES_REQUEST = 2;
                onCameraButtonClick(); // Call your function to open the camera here
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog when cancel is clicked
            }
        });

        dialog.show();
    }


    private void onCameraButtonClick(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager())!=null){
            imagePickerLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(AddItemActivity.this, "App does not support this action",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void onGalleryButtonClick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(intent);
    }

    private void handleReceivedGalleryImages(Intent data) {
        if (data.getClipData() != null) {
            // Handle multiple selected images here using the imageUris ArrayList

            int count = data.getClipData().getItemCount();
            ArrayList<Uri> imageUris = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                sliderItems.add(new SliderItem(uri));
            }
            viewPager2.setAdapter(new SliderAdapter(sliderItems,viewPager2,true));
            // Save the URI to the global variable if needed for later use
            // this.uri = uri;
            viewPager2.setBackground(null);

            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(5));
            compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                }
            });

            viewPager2.setPageTransformer(compositePageTransformer);


        } else if (data.getData() != null) {
            // Handle single selected image here using the imageUri

            Uri uri = data.getData();
            sliderItems.add(new SliderItem(uri));
            viewPager2.setAdapter(new SliderAdapter(sliderItems,viewPager2,true));
            // Save the URI to the global variable if needed for later use
            // this.uri = uri;
            viewPager2.setBackground(null);

            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(5));
            compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                }
            });

            viewPager2.setPageTransformer(compositePageTransformer);


        }
    }
    private void handleReceivedCameraImages(Bitmap bitmap){
        // Save the bitmap to a file
        Uri imageUri = saveBitmapToFile(bitmap);

        // Use the obtained URI as needed
        // For example, set the image to an ImageView
        sliderItems.add(new SliderItem(imageUri));
        viewPager2.setAdapter(new SliderAdapter(sliderItems,viewPager2,true));
        // Save the URI to the global variable if needed for later use
        // this.uri = uri;
        viewPager2.setBackground(null);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(5));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        // Get the content resolver
        ContentResolver resolver = getContentResolver();

        // Define a destination for the image file using MediaStore
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "image_file_name.jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // Insert an empty entry to MediaStore to get the URI
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            // Open an output stream using the resolver and the obtained URI
            OutputStream outputStream = resolver.openOutputStream(imageUri);

            // Compress and write the bitmap to the output stream as JPEG
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // Close the output stream
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the saved image URI
        return imageUri;
    }

    private Item parseItem() {
        // Create a date in LocalDate format from the user input
        LocalDate itemDate = Helpers.parseDate(date.getEditText().getText().toString());

        // These may be a mixture of download links and paths to local files in edit mode.
        // Database expects Firestore paths OR local file paths, download links are no no.
        String[] stringUris = new SliderAdapter(sliderItems, viewPager2,true).convertUrisToStringArray();

        List<String> correctUris = Arrays.asList(stringUris);
        if(editMode){
            // Replace download links with firestore paths.
            List<String> originalUris = originalEditingItem.getImageUris();

            for(int i = 0;i< correctUris.size();++i){
                String s = correctUris.get(i);
                if(Helpers.isInternetUri(s)){
                    correctUris.set(i,DatabaseManager.downloadUriToFirestorePath(s));
                }
            }

            // Apply any deletions.
            if(correctUris.size()<originalUris.size()){
                for(String s : originalUris){
                    if (!correctUris.contains(s))
                        DatabaseManager.deleteImage(originalEditingItem, Uri.parse(s));
                }
            }
        }

        Item editItem = new Item(
                name.getEditText().getText().toString(),
                Double.parseDouble(value.getEditText().getText().toString()),
                itemDate,
                make.getEditText().getText().toString(),
                model.getEditText().getText().toString(),
                serialNumber.getEditText().getText().toString(),
                description.getEditText().getText().toString(),
                comments.getEditText().getText().toString(),
                tags,
                correctUris,
                editMode ? originalEditingItem.getUid() : Item.generateNewUID());
        return editItem;
    }
    /**
     * Parses the item if this activity is in edit mode and starts MainActivity
     */
    private void sendEditItem() {
        // Intent to return to the main activity
        Intent sendEditIntent = new Intent(this, MainActivity.class);

        Item editItem = parseItem();

        // Send the edited item back to the main activity
        sendEditIntent.putExtra("edit Item", editItem);
        sendEditIntent.putExtra("edit Position", selectedPosition);
        startActivity(sendEditIntent);
    }

    /**
     * Creates a new item from the user inputs and sends it back to the main activity.
     */
    private void sendItem() {
        // Create a date in LocalDate format from the user input
        LocalDate itemDate = Helpers.parseDate(date.getEditText().getText().toString());
        String[] stringUris = new SliderAdapter(sliderItems,viewPager2,true).convertUrisToStringArray();
        Log.e("GAN", ""+stringUris.length);

        // Create a new input
        Item newItem =  parseItem();

        // Intent to return to the main activity
        Intent sendItemIntent = new Intent(this, MainActivity.class);

        // Send the new item back to the main activity
        sendItemIntent.putExtra("new Item", newItem);
        startActivity(sendItemIntent);
    }

    /**
     * Returns to the main activity.
     */
    private void cancel() {
        Intent cancelIntent = new Intent(this, MainActivity.class);
        startActivity(cancelIntent);
    }

    /**
     * Validates all the user inputs.
     *
     * @return true if all inputs pass validation, false otherwise.
     */
    private boolean validateInput() {
        // Validate name
        if (name.getEditText().length() == 0) {
            name.setError("This field is required!");
            return false;
        }

        // Validate date
        if (date.getEditText().length() == 0) {
            date.setError("This field is required!");
            return false;
        } else if (date.getEditText().length() != 10) {
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
            // Check for month
        } else if (parseInt(date.getEditText().getText().toString().substring(5, 7)) > 12 ||
                parseInt(date.getEditText().getText().toString().substring(5, 7)) < 1) {
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
            // Validate day
        } else if (parseInt(date.getEditText().getText().toString().substring(8)) < 1 ||
                parseInt(date.getEditText().getText().toString().substring(8)) > 31) {
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
        } else if (parseInt(date.getEditText().getText().toString().substring(8)) < 1 ||
                parseInt(date.getEditText().getText().toString().substring(8)) > 30) {
            List<Integer> thirtyDaymonths = new ArrayList<>(Arrays.asList(4, 6, 9, 11));
            if (thirtyDaymonths.contains(parseInt(date.getEditText().getText().toString().substring(5, 7)))) {
                date.setError("Enter (YYYY-MM-DD)!");
                return false;
            }
        } else if (parseInt(date.getEditText().getText().toString().substring(8)) < 1 ||
                parseInt(date.getEditText().getText().toString().substring(8)) > 29) {

            if (parseInt(date.getEditText().getText().toString().substring(5, 7)) == 2) {
                date.setError("Enter (YYYY-MM-DD)!");
                return false;
            }
        }

        // Validate value
        if (value.getEditText().length() == 0) {
            name.setError("This field is required!");
            return false;
        }
        return true;
    }

    /**
     * Receives a new item if created from the AddItem Fragment.
     *
     * @return New Item if created; otherwise, returns null.
     */
    private Item tryGetItemFromIntent() {
        Intent receiverIntent = getIntent();
        Item receivedItem = receiverIntent.getParcelableExtra(getString(R.string.edit_item_intent));
        selectedPosition = getIntent().getIntExtra(getString(R.string.edit_item_position_intent), -1);

        if (receivedItem == null) {
            return null;
        }

        return receivedItem;
    }

    private String tryGetSerialNumberFromIntent() {
        Intent receiverIntent = getIntent();
        return receiverIntent.getStringExtra(getString(R.string.serial_number_intent));
    }
}