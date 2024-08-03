package com.example.extrocontru;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private RecyclerView profilesRecyclerView;
    private List<String> categories = new ArrayList<>();
    private Map<String, List<Tool>> toolsByCategory = new HashMap<>();
    private CategoryAdapter categoryAdapter;
    private DatabaseReference databaseReference;
    private String userId;
    private String displayName;

    public ProfileFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Configurar RecyclerView
        profilesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("tools");

        // Configurar el adaptador para las categorías
        categoryAdapter = new CategoryAdapter(categories, toolsByCategory);
        profilesRecyclerView.setAdapter(categoryAdapter);

        // Cargar categorías y herramientas desde la base de datos
        loadCategoriesAndToolsFromDatabase();

        // Obtener el userId y displayName desde el Intent de HomeActivity
        if (getActivity() != null && getActivity().getIntent() != null) {
            userId = getActivity().getIntent().getStringExtra("userId");
            displayName = getActivity().getIntent().getStringExtra("displayName");

            // Configurar el TextView con el nombre del usuario
            TextView userNameTextView = view.findViewById(R.id.userName);
            userNameTextView.setText(displayName != null ? displayName : "Usuario");
        } else {
            Log.d(TAG, "User ID or Display Name not found in Intent.");
        }

        // Configurar el MaterialCardView para ir a la actividad de pedidos
        MaterialCardView ordersCard = view.findViewById(R.id.ordersCard);
        ordersCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PedidosListActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadCategoriesAndToolsFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                toolsByCategory.clear();

                for (DataSnapshot toolSnapshot : snapshot.getChildren()) {
                    Tool tool = toolSnapshot.getValue(Tool.class);
                    if (tool != null) {
                        String category = tool.getCategory();
                        if (!categories.contains(category)) {
                            categories.add(category);
                            toolsByCategory.put(category, new ArrayList<>());
                        }
                        toolsByCategory.get(category).add(tool);
                    }
                }

                if (categoryAdapter != null) {
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores
            }
        });
    }
}
