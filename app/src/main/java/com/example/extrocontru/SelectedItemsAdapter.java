package com.example.extrocontru;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SelectedItemsAdapter extends RecyclerView.Adapter<SelectedItemsAdapter.SelectedItemsViewHolder> {
    private List<CartItem> selectedItems;

    public SelectedItemsAdapter(List<CartItem> selectedItems) {
        this.selectedItems = selectedItems;
    }

    @NonNull
    @Override
    public SelectedItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_product, parent, false);
        return new SelectedItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedItemsViewHolder holder, int position) {
        CartItem item = selectedItems.get(position);
        holder.productName.setText(item.getToolName());

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
    }

    @Override
    public int getItemCount() {
        return selectedItems.size();
    }

    class SelectedItemsViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;

        SelectedItemsViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameTextView);
            productPrice = itemView.findViewById(R.id.priceTextView);
            productImage = itemView.findViewById(R.id.productImageView);
        }
    }
}
