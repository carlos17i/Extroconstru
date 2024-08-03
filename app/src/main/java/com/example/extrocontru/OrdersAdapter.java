package com.example.extrocontru;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orders;

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.orderDateTextView.setText("Date: " + order.getOrderDate()); // Asumiendo que has añadido este campo en Order
        holder.orderDetailsTextView.setText("Details: " + getOrderDetails(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String getOrderDetails(Order order) {
        // Personaliza este método para mostrar los detalles del pedido como prefieras
        return "Name: " + order.getNombre() + "\n" +
                "Duration: " + order.getRentalDuration();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView orderDateTextView;
        TextView orderDetailsTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            orderDetailsTextView = itemView.findViewById(R.id.orderDetailsTextView);
        }
    }
}
