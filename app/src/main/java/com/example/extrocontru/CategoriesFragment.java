package com.example.extrocontru;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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

public class CategoriesFragment extends Fragment {

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<String> categories = new ArrayList<>();
    private Map<String, List<Tool>> toolsByCategory = new HashMap<>();

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        categoryRecyclerView = view.findViewById(R.id.toolsRecyclerView);

        // Configurar RecyclerView
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new CategoryAdapter(categories, toolsByCategory);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Cargar categorías y herramientas
        loadCategoriesAndTools();

        return view;
    }

    private void loadCategoriesAndTools() {
        DatabaseReference toolsRef = FirebaseDatabase.getInstance().getReference("tools");

        // Cargar categorías
        toolsRef.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categories.clear();
                toolsByCategory.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.getKey();
                    categories.add(category);
                    loadToolsForCategory(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadToolsForCategory(String category) {
        DatabaseReference toolsRef = FirebaseDatabase.getInstance().getReference("tools");
        toolsRef.orderByChild("category").equalTo(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Tool> tools = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tool tool = snapshot.getValue(Tool.class);
                    tools.add(tool);
                }
                toolsByCategory.put(category, tools);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error al cargar herramientas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
