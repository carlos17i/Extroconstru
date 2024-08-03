package com.example.extrocontru;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Tool> tools;

    public ProductAdapter(List<Tool> tools) {
        this.tools = tools;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Tool tool = tools.get(position);
        holder.productName.setText(tool.getToolName());
        holder.productBrand.setText(tool.getBrand());
        holder.productPrice.setText(tool.getPrice());
        holder.productDescription.setText(tool.getDescription());


    }

    @Override
    public int getItemCount() {
        return tools.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productBrand;
        TextView productPrice;
        TextView productDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.toolImage);
            productName = itemView.findViewById(R.id.toolName);
            productBrand = itemView.findViewById(R.id.toolBrand);
            productPrice = itemView.findViewById(R.id.toolPrice);
            productDescription = itemView.findViewById(R.id.toolDescription);
        }
    }
}
