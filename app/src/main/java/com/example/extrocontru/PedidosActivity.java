package com.example.extrocontru;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PedidosActivity extends AppCompatActivity {
    private EditText nombreEditText, apellidoEditText, telefonoEditText, estadoEditText, ciudadEditText, codigoPostalEditText, calleEditText, cedulaEditText, referenciaEditText;
    private Spinner rentalDurationSpinner;
    private RecyclerView selectedItemsRecyclerView;
    private SelectedItemsAdapter selectedItemsAdapter;
    private List<CartItem> selectedItems;

    private static final int MAP_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        // Inicializa los campos del formulario
        nombreEditText = findViewById(R.id.nombreEditText);
        apellidoEditText = findViewById(R.id.apellidoEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);
        estadoEditText = findViewById(R.id.estadoEditText);
        ciudadEditText = findViewById(R.id.ciudadEditText);
        codigoPostalEditText = findViewById(R.id.codigoPostalEditText);
        calleEditText = findViewById(R.id.calleEditText);
        cedulaEditText = findViewById(R.id.cedulaEditText);
        referenciaEditText = findViewById(R.id.referenciaEditText);
        rentalDurationSpinner = findViewById(R.id.rentalDurationSpinner);

        // Configura el Spinner con opciones de duración
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.rental_durations,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rentalDurationSpinner.setAdapter(adapter);

        // Inicializa el RecyclerView para mostrar los productos seleccionados
        selectedItemsRecyclerView = findViewById(R.id.selectedItemsRecyclerView);
        selectedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedItems = getIntent().getParcelableArrayListExtra("selectedItems");
        if (selectedItems != null) {
            selectedItemsAdapter = new SelectedItemsAdapter(selectedItems);
            selectedItemsRecyclerView.setAdapter(selectedItemsAdapter);
        }

        // Configura el click listener para el campo calle
        calleEditText.setOnClickListener(v -> {
            Intent intent = new Intent(PedidosActivity.this, MapsActivity.class);
            startActivityForResult(intent, MAP_REQUEST_CODE);
        });

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            // Obtén los valores de los campos
            String nombre = nombreEditText.getText().toString();
            String apellido = apellidoEditText.getText().toString();
            String telefono = telefonoEditText.getText().toString();
            String estado = estadoEditText.getText().toString();
            String ciudad = ciudadEditText.getText().toString();
            String codigoPostal = codigoPostalEditText.getText().toString();
            String calle = calleEditText.getText().toString();
            String cedula = cedulaEditText.getText().toString();
            String referencia = referenciaEditText.getText().toString();
            String rentalDuration = rentalDurationSpinner.getSelectedItem().toString();
            saveOrderToFirebase(nombre, apellido, telefono, estado, ciudad, codigoPostal, calle, cedula, referencia, rentalDuration, selectedItems);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            // Puedes usar la latitud y longitud para obtener la dirección o almacenarla como coordenadas
            String address = "Lat: " + latitude + ", Lon: " + longitude; // Para demostración
            calleEditText.setText(address);
        }
    }

    private void saveOrderToFirebase(String nombre, String apellido, String telefono, String estado, String ciudad, String codigoPostal, String calle, String cedula, String referencia, String rentalDuration, List<CartItem> selectedItems) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("orders");
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");

        String orderId = ordersRef.push().getKey();
        if (orderId != null) {
            String currentDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()); // Obtén la fecha actual
            Order order = new Order(orderId, nombre, apellido, telefono, estado, ciudad, codigoPostal, calle, cedula, referencia, rentalDuration, selectedItems, currentDate);
            ordersRef.child(orderId).setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Borra los productos seleccionados del carrito después de guardar el pedido
                    for (CartItem item : selectedItems) {
                        String toolId = item.getToolId(); // Asegúrate de que el ID del producto sea el correcto
                        cartRef.child(toolId).removeValue().addOnCompleteListener(removeTask -> {
                            if (removeTask.isSuccessful()) {
                                // Regresa a la actividad anterior
                                finish(); // Cierra la actividad actual
                            }
                        });
                    }
                    Toast.makeText(PedidosActivity.this, "Pedido Enviado con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PedidosActivity.this, "Error al guardar el pedido", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
