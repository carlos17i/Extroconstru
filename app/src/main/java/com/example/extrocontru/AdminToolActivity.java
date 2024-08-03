package com.example.extrocontru;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
public class AdminToolActivity extends AppCompatActivity {

    private static final int PICK_IMAGES_REQUEST = 1;
    private static final int MAX_IMAGES = 4;
    private List<Uri> imageUris = new ArrayList<>();
    private StorageReference storageReference;

    private EditText etToolId, etToolName, etBrand, etModel, etDescription, etPrice, etStock, etSpecifications, etComments;
    private Spinner spinnerCategory, spinnerSubCategory;
    private Button btnAddImages, btnSaveTool;

    private Map<String, String> imageUrls = new HashMap<>();
    private int totalImages;
    private int imagesUploaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tool);

        etToolId = findViewById(R.id.etToolId);
        etToolName = findViewById(R.id.etToolName);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSubCategory = findViewById(R.id.spinnerSubCategory);
        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etSpecifications = findViewById(R.id.etSpecifications);
        etComments = findViewById(R.id.etComments);
        btnAddImages = findViewById(R.id.btnAddImages);
        btnSaveTool = findViewById(R.id.btnSaveTool);

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize Spinners
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set listener for category spinner to update subcategories
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = (String) parent.getItemAtPosition(position);
                updateSubCategories(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnAddImages.setOnClickListener(v -> openImagePicker());

        btnSaveTool.setOnClickListener(v -> saveTool());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                imageUris.clear(); // Clear previous images
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count && i < MAX_IMAGES; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                }
                totalImages = imageUris.size();
                if (totalImages > 0) {
                    uploadImages();
                }
            }
        }
    }

    private void uploadImages() {
        String toolId = etToolId.getText().toString();
        if (toolId.isEmpty()) {
            Toast.makeText(this, "Debe guardar la herramienta primero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUris.isEmpty()) {
            Toast.makeText(this, "No se han seleccionado imágenes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mapa para guardar URLs de imágenes
        Map<String, String> imageUrls = new HashMap<>();
        AtomicInteger imageCounter = new AtomicInteger(1); // Contador para la clave de las imágenes

        for (Uri imageUri : imageUris) {
            // Crea una referencia de almacenamiento en Firebase Storage
            StorageReference fileReference = storageReference.child("tools/" + toolId + "/" + System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Guarda la URL en el mapa con una clave como "image1", "image2", etc.
                            String imageKey = "image" + imageCounter.getAndIncrement();
                            imageUrls.put(imageKey, imageUrl);

                            // Si todas las imágenes se han subido, guarda las URLs en la base de datos
                            if (imageUrls.size() == imageUris.size()) {
                                saveImageUrlsToDatabase(toolId, imageUrls);
                            }
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(AdminToolActivity.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }



    private void saveImageUrlsToDatabase(String toolId, Map<String, String> imageUrls) {
        DatabaseReference toolRef = FirebaseDatabase.getInstance().getReference("tools").child(toolId);
        toolRef.child("images").setValue(imageUrls)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminToolActivity.this, "Imágenes subidas con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminToolActivity.this, "Error al guardar URLs de imágenes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void saveTool() {
        String toolId = etToolId.getText().toString();
        String toolName = etToolName.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String subCategory = spinnerSubCategory.getSelectedItem().toString();
        String brand = etBrand.getText().toString();
        String model = etModel.getText().toString();
        String description = etDescription.getText().toString();
        String price = etPrice.getText().toString();
        String stock = etStock.getText().toString();
        String specifications = etSpecifications.getText().toString();
        String comments = etComments.getText().toString();

        if (toolId.isEmpty() || toolName.isEmpty()) {
            Toast.makeText(this, "El ID y el nombre de la herramienta son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tools").child(toolId);

        Tool tool = new Tool(toolId, toolName, category, subCategory, brand, model, description, price, stock, specifications, comments);

        databaseReference.setValue(tool).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminToolActivity.this, "Herramienta guardada correctamente", Toast.LENGTH_SHORT).show();
                // Limpiar los campos después de guardar
                clearFields();
            } else {
                Toast.makeText(AdminToolActivity.this, "Error al guardar la herramienta: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        etToolId.setText("");
        etToolName.setText("");
        spinnerCategory.setSelection(0);
        spinnerSubCategory.setSelection(0);
        etBrand.setText("");
        etModel.setText("");
        etDescription.setText("");
        etPrice.setText("");
        etStock.setText("");
        etSpecifications.setText("");
        etComments.setText("");
    }

    private void updateSubCategories(String category) {
        List<String> subCategories;

        switch (category) {
            case "Encofrados":
                subCategories = Arrays.asList(getResources().getStringArray(R.array.subcategories_encofrados_array));
                break;
            case "Columnas Metálicas":
                subCategories = Arrays.asList(getResources().getStringArray(R.array.subcategories_columnas_array));
                break;
            case "Herramientas Eléctricas":
                subCategories = Arrays.asList(getResources().getStringArray(R.array.subcategories_herramientas_array));
                break;
            default:
                subCategories = new ArrayList<>();
                break;
        }

        ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subCategories);
        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubCategory.setAdapter(subCategoryAdapter);
    }
}
