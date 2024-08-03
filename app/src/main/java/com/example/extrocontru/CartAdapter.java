package com.example.extrocontru;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private DatabaseReference cartRef;
    private boolean selectAll = false;
    private SelectionChangeListener selectionChangeListener;

    // Constructor
    public CartAdapter(List<CartItem> cartItems, SelectionChangeListener selectionChangeListener) {
        this.cartItems = cartItems;
        this.selectionChangeListener = selectionChangeListener;
        this.cartRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("cart");
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.productName.setText(item.getToolName());
        holder.productDescription.setText(item.getSpecifications());

        // Formatear el precio con el símbolo del dólar
        String priceString = item.getPrice();
        if (priceString != null) {
            try {
                priceString = priceString.replace("$", "").replace(",", "");
                BigDecimal price = new BigDecimal(priceString);
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
                currencyFormat.setRoundingMode(RoundingMode.HALF_UP);
                holder.productPrice.setText(currencyFormat.format(price));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.productPrice.setText("$0.00"); // Manejo de errores
            }
        } else {
            holder.productPrice.setText("$0.00"); // Manejo de errores
        }

        holder.productQuantity.setText(String.valueOf(item.getQuantity()));

        // Configuración de la imagen
        String imageUrl = item.getToolImageUrl();
        if (imageUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.logo);
        }

        // Configurar los botones de cantidad
        holder.increaseQuantityButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.productQuantity.getText().toString());
            if (currentQuantity < 99) { // Limitar a 99 para evitar problemas
                updateQuantity(item, holder.productQuantity, 1);
            }
        });

        holder.decreaseQuantityButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.productQuantity.getText().toString());
            if (currentQuantity > 1) { // Evitar que la cantidad sea 0
                updateQuantity(item, holder.productQuantity, -1);
            }
        });

        // Configurar el CheckBox
        holder.selectProductRadioButton.setChecked(item.isSelected());
        holder.selectProductRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChanged();
            }
        });

        // Si la selección global está activada, seleccionar todos los elementos
        holder.selectProductRadioButton.setChecked(selectAll);

        // Configurar el botón de eliminación
        holder.deleteProductButton.setOnClickListener(v -> {
            removeItem(item); // Pasar el CartItem en lugar del índice
        });
    }

    private void updateQuantity(CartItem item, TextView quantityTextView, int change) {
        String toolId = item.getToolId();
        if (toolId == null || toolId.isEmpty()) {
            Log.e("CartAdapter", "ToolId is null or empty for item: " + item.getToolName());
            return; // Salir del método si el ID del producto es nulo o vacío
        }

        int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());
        int newQuantity = currentQuantity + change;

        // Asegúrate de que la cantidad no sea menor a 1
        if (newQuantity < 1) return;

        quantityTextView.setText(String.valueOf(newQuantity));

        // Actualiza la cantidad en Firebase usando el ID del producto
        DatabaseReference itemRef = cartRef.child(toolId);

        itemRef.child("quantity").setValue(newQuantity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("CartAdapter", "Quantity updated successfully for item: " + item.getToolName());
            } else {
                Log.e("CartAdapter", "Failed to update quantity for item: " + item.getToolName(), task.getException());
            }
        });
    }

    public void removeItem(CartItem item) {
        String toolId = item.getToolId();
        if (toolId == null || toolId.isEmpty()) {
            Log.e("CartAdapter", "ToolId is null or empty for item: " + item.getToolName());
            return; // Salir del método si el ID del producto es nulo o vacío
        }

        DatabaseReference itemRef = cartRef.child(toolId);

        itemRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("CartAdapter", "Item removed successfully: " + item.getToolName());

                // Verificar si el elemento aún está en la lista antes de eliminar
                int position = cartItems.indexOf(item);
                if (position != -1) {
                    cartItems.remove(position);
                    notifyItemRemoved(position);
                } else {
                    Log.e("CartAdapter", "Item not found in list: " + item.getToolName());
                }

                // Mostrar un toast después de eliminar el producto
                Context context = cartRef.getDatabase().getApp().getApplicationContext(); // Obtener el contexto de la base de datos de Firebase
                Toast.makeText(context, "Producto quitado del carrito", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("CartAdapter", "Failed to remove item: " + item.getToolName(), task.getException());
            }
        });
    }



    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
        for (CartItem item : cartItems) {
            item.setSelected(selectAll);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity, productDescription;
        ImageView productImage;
        ImageButton decreaseQuantityButton, increaseQuantityButton, deleteProductButton;
        CheckBox selectProductRadioButton;

        CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productDescription = itemView.findViewById(R.id.productDescription);
            productImage = itemView.findViewById(R.id.productImage);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            deleteProductButton = itemView.findViewById(R.id.deleteProductButton);
            selectProductRadioButton = itemView.findViewById(R.id.selectProductRadioButton);
        }
    }

    public interface SelectionChangeListener {
        void onSelectionChanged();
    }
}
