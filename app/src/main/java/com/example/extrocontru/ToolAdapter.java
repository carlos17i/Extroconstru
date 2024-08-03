package com.example.extrocontru;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Map;

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ToolViewHolder> {
    private List<Tool> tools;
    private Context context;

    public ToolAdapter(List<Tool> tools) {
        this.tools = tools;
    }

    @NonNull
    @Override
    public ToolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_tool, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolViewHolder holder, int position) {
        Tool tool = tools.get(position);
        holder.toolName.setText(tool.getToolName());
        holder.toolBrand.setText(tool.getBrand());

        // Formatea el precio para incluir el símbolo '$'
        String formattedPrice = "$" + tool.getPrice();
        holder.toolPrice.setText(formattedPrice);

        holder.toolDescription.setText(tool.getDescription());

        // Cargar la primera imagen si está disponible
        Map<String, String> images = tool.getImages();
        String firstImageUrl = null;
        if (images != null && !images.isEmpty()) {
            firstImageUrl = images.values().iterator().next(); // Obtener la primera imagen
            Log.d("ToolAdapter", "Image URL: " + firstImageUrl); // Agregar log para verificar la URL

            Glide.with(holder.itemView.getContext())
                    .load(firstImageUrl)
                    .placeholder(R.drawable.logo) // Imagen de placeholder
                    .error(R.drawable.logo) // Imagen de error
                    .into(holder.toolImage);
        } else {
            // Si no hay imágenes, mostrar una imagen de placeholder o vacío
            holder.toolImage.setImageResource(R.drawable.logo); // Imagen de placeholder
        }

        final String finalFirstImageUrl = firstImageUrl; // Variable final para la lambda
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ToolDetailActivity.class);
            intent.putExtra("toolId", tool.getToolId()); // Incluir toolId
            intent.putExtra("toolName", tool.getToolName());
            intent.putExtra("toolDescription", tool.getDescription());
            intent.putExtra("toolPrice", tool.getPrice());
            intent.putExtra("toolBrand", tool.getBrand());
            intent.putExtra("toolModel", tool.getModel());
            intent.putExtra("toolImageUrl", finalFirstImageUrl);
            intent.putExtra("toolSpecifications", tool.getSpecifications());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return tools.size();
    }

    public static class ToolViewHolder extends RecyclerView.ViewHolder {
        TextView toolName;
        TextView toolBrand;
        TextView toolPrice;
        TextView toolDescription;
        ImageView toolImage;

        public ToolViewHolder(@NonNull View itemView) {
            super(itemView);
            toolName = itemView.findViewById(R.id.toolName);
            toolBrand = itemView.findViewById(R.id.toolBrand);
            toolPrice = itemView.findViewById(R.id.toolPrice);
            toolDescription = itemView.findViewById(R.id.toolDescription);
            toolImage = itemView.findViewById(R.id.toolImage);
        }
    }
}
