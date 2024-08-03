package com.example.extrocontru;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
public class ToolDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private TextView quantityTextView;
    private Button addToCartButton;
    private FirebaseDatabase database;
    private DatabaseReference cartRef;
    private String userId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_detail);

        ImageView toolImageView = findViewById(R.id.toolImageView);
        TextView toolNameTextView = findViewById(R.id.toolNameTextView);
        TextView toolDescriptionTextView = findViewById(R.id.toolDescriptionTextView);
        TextView toolPriceTextView = findViewById(R.id.toolPriceTextView);
        TextView toolBrandTextView = findViewById(R.id.toolBrandTextView);
        TextView toolModelTextView = findViewById(R.id.toolModelTextView);
        TextView toolSpecificationsTextView = findViewById(R.id.toolDescriptionTextView); // Agregar este TextView
        quantityTextView = findViewById(R.id.quantityTextView);

        addToCartButton = findViewById(R.id.addToCartButton);

        // Firebase
        database = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = database.getReference("users").child(userId).child("cart");

        // Obtener los datos pasados desde el adaptador
        String toolId = getIntent().getStringExtra("toolId");
        String toolName = getIntent().getStringExtra("toolName");
        String toolDescription = getIntent().getStringExtra("toolDescription");
        String toolPrice = getIntent().getStringExtra("toolPrice");
        String toolBrand = getIntent().getStringExtra("toolBrand");
        String toolModel = getIntent().getStringExtra("toolModel");
        String toolImageUrl = getIntent().getStringExtra("toolImageUrl");
        String toolSpecifications = getIntent().getStringExtra("toolSpecifications"); // Obtener especificaciones

        // Mostrar los datos en la interfaz de usuario
        toolNameTextView.setText(toolName);
        toolDescriptionTextView.setText(toolDescription);

        // Formatea el precio para incluir el símbolo '$'
        String formattedPrice = "$" + toolPrice;
        toolPriceTextView.setText(formattedPrice);

        toolBrandTextView.setText(toolBrand);
        toolModelTextView.setText(toolModel);
        toolSpecificationsTextView.setText(toolSpecifications); // Mostrar especificaciones

        // Cargar la imagen usando Glide
        Glide.with(this)
                .load(toolImageUrl)
                .placeholder(R.drawable.logo) // Imagen de placeholder
                .error(R.drawable.logo) // Imagen de error
                .into(toolImageView);

        // Configurar botones de cantidad
        ImageButton decreaseButton = findViewById(R.id.decreaseButton);
        ImageButton increaseButton = findViewById(R.id.increaseButton);

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) { // Evitar que la cantidad sea menor a 1
                    quantity--;
                    updateQuantityDisplay();
                }
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                updateQuantityDisplay();
            }
        });

        // Configurar botón para añadir al carrito
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(toolId, toolName, toolDescription, toolPrice, toolBrand, toolModel, toolImageUrl, toolSpecifications, quantity);
            }
        });
    }


    private void updateQuantityDisplay() {
        quantityTextView.setText(String.valueOf(quantity));
    }

    private void addToCart(String toolId, String toolName, String toolDescription, String toolPrice, String toolBrand, String toolModel, String toolImageUrl, String toolSpecifications, int quantity) {
        // Crear un mapa con los datos del producto que queremos guardar
        Map<String, Object> toolMap = new HashMap<>();
        toolMap.put("toolId", toolId);
        toolMap.put("toolName", toolName);
        toolMap.put("description", toolDescription);
        toolMap.put("price", toolPrice);
        toolMap.put("brand", toolBrand);
        toolMap.put("model", toolModel);
        toolMap.put("specifications", toolSpecifications);
        toolMap.put("toolImageUrl", toolImageUrl);  // Añadir URL de la imagen
        toolMap.put("quantity", quantity);

        // Obtener una referencia al nodo del carrito para el usuario actual
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("cart");

        // Usar toolId como clave del nodo del carrito
        cartRef.child(toolId).setValue(toolMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Operación exitosa
                Toast.makeText(ToolDetailActivity.this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
            } else {
                // Error al guardar
                Toast.makeText(ToolDetailActivity.this, "Error al añadir al carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
