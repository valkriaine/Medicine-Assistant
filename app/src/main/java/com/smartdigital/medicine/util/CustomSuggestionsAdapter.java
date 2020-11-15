package com.smartdigital.medicine.util;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.smartdigital.medicine.MainActivity;
import com.smartdigital.medicine.R;
import com.smartdigital.medicine.model.SuggestionMedicine;
import com.smartdigital.medicine.model.UserMedicine;
import com.valkriaine.factor.HomePager;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;


public class CustomSuggestionsAdapter extends RecyclerView.Adapter<CustomSuggestionsAdapter.SuggestionViewHolder>
{

    private final List<SuggestionMedicine> suggestions = new ArrayList<>();
    private final HomePager homePager;
    private SuggestionMedicine selectedDrug;
    private final MaterialSearchBar searchBar;

    public CustomSuggestionsAdapter(HomePager homePager, MaterialSearchBar searchBar)
    {
        this.homePager = homePager;
        this.searchBar = searchBar;
    }

    @NonNull
    @NotNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_suggestion_item, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SuggestionViewHolder holder, int position) {
        SuggestionMedicine m = suggestions.get(position);
        holder.name.setText(m.getName());
        holder.targetName.setText(m.getTargetName());
    }


    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void addSuggestions(SuggestionMedicine u)
    {
        suggestions.add(u);
        notifyDataSetChanged();
    }

    public void clear()
    {
        suggestions.clear();
        notifyDataSetChanged();
    }

    public SuggestionMedicine getSelectedDrug()
    {
        if (selectedDrug != null)
            return this.selectedDrug;
        else
            return null;
    }


    public boolean contains(SuggestionMedicine u)
    {
        return suggestions.contains(u);
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private final TextView name;
        private final TextView targetName;
        public SuggestionViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.suggestion_title);
            targetName = itemView.findViewById(R.id.suggestion_targetName);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            selectedDrug = suggestions.get(getAdapterPosition());
            homePager.setCurrentItem(1, true);
            MainActivity.currentMed = selectedDrug.toUserMedicine();
            searchBar.clearFocus();

        }
    }
}