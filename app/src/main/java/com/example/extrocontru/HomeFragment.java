package com.example.extrocontru;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView categoriesRecyclerView;
    private List<String> categories = new ArrayList<>();
    private Map<String, List<Tool>> toolsByCategory = new HashMap<>();
    private CategoryAdapter categoryAdapter;  // Necesitarás crear un adaptador para categorías
    private DatabaseReference databaseReference;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Inicializa Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("tools");
        // Configura el adaptador para las categorías
        categoryAdapter = new CategoryAdapter(categories, toolsByCategory); // Crear un adaptador para las categorías
        categoriesRecyclerView.setAdapter(categoryAdapter);
        // Carga categorías y herramientas desde la base de datos
        loadCategoriesAndToolsFromDatabase();
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

                // Notificar al adaptador que los datos han cambiado
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
