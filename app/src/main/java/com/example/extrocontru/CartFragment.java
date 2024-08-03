package com.example.extrocontru;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.SelectionChangeListener {
    private RecyclerView recyclerView;
    private TextView itemCountTextView, totalCostTextView;
    private CheckBox selectAllCheckBox;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private DatabaseReference cartRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        itemCountTextView = view.findViewById(R.id.itemCountTextView);
        totalCostTextView = view.findViewById(R.id.totalCostTextView);
        selectAllCheckBox = view.findViewById(R.id.selectAllCheckBox);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(cartAdapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                BigDecimal totalCost = BigDecimal.ZERO;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartItem item = dataSnapshot.getValue(CartItem.class);
                    if (item != null) {
                        String priceString = item.getPrice();
                        int quantity = item.getQuantity();

                        if (priceString != null) {
                            try {
                                priceString = priceString.replace("$", "").replace(",", "");
                                BigDecimal price = new BigDecimal(priceString);
                                BigDecimal quantityBigDecimal = new BigDecimal(quantity);
                                totalCost = totalCost.add(price.multiply(quantityBigDecimal));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("CartFragment", "Price is null for item: " + item.getToolId());
                        }
                        cartItems.add(item);
                    } else {
                        Log.e("CartFragment", "Item is null.");
                    }
                }

                for (CartItem item : cartItems) {
                    item.setSelected(true);
                }
                cartAdapter.notifyDataSetChanged();
                itemCountTextView.setText("Herramientas en tu Carrito: " + cartItems.size());

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
                currencyFormat.setRoundingMode(RoundingMode.HALF_UP);
                totalCostTextView.setText(currencyFormat.format(totalCost.setScale(2, RoundingMode.HALF_UP)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CartFragment", "Database error: " + error.getMessage());
            }
        });

        selectAllCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartAdapter.setSelectAll(isChecked);
            updateTotalCost();
        });

        Button placeOrderButton = view.findViewById(R.id.placeOrderButton);
        placeOrderButton.setOnClickListener(v -> {
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }

            Intent intent = new Intent(getActivity(), PedidosActivity.class);
            // Serializa la lista de productos seleccionados en un ArrayList
            intent.putParcelableArrayListExtra("selectedItems", new ArrayList<>(selectedItems));
            startActivity(intent);
        });

        return view;
    }

    private void updateTotalCost() {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                String priceString = item.getPrice();
                int quantity = item.getQuantity();
                if (priceString != null) {
                    try {
                        priceString = priceString.replace("$", "").replace(",", "");
                        BigDecimal price = new BigDecimal(priceString);
                        BigDecimal quantityBigDecimal = new BigDecimal(quantity);
                        totalCost = totalCost.add(price.multiply(quantityBigDecimal));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        currencyFormat.setRoundingMode(RoundingMode.HALF_UP);
        totalCostTextView.setText(currencyFormat.format(totalCost.setScale(2, RoundingMode.HALF_UP)));
    }

    @Override
    public void onSelectionChanged() {
        updateTotalCost();
    }
}
