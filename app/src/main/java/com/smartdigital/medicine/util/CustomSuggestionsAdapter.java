package com.smartdigital.medicine.util;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.smartdigital.medicine.R;
import com.smartdigital.medicine.UserMedicine;
import com.valkriaine.factor.HomePager;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;


public class CustomSuggestionsAdapter extends RecyclerView.Adapter<CustomSuggestionsAdapter.SuggestionViewHolder>
{

    private final List<UserMedicine> suggestions = new ArrayList<>();
    private final HomePager homePager;
    private UserMedicine selectedDrug;

    public CustomSuggestionsAdapter(HomePager homePager)
    {
        this.homePager = homePager;
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
        UserMedicine m = suggestions.get(position);
        holder.name.setText(m.getName());
        holder.targetName.setText(m.getTargetName());
    }


    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void addSuggestions(UserMedicine u)
    {
        suggestions.add(u);
        notifyDataSetChanged();
    }

    public void clear()
    {
        suggestions.clear();
        notifyDataSetChanged();
    }

    public UserMedicine getSelectedDrug()
    {
        if (selectedDrug != null)
            return this.selectedDrug;
        else
            return null;
    }


    public boolean contains(UserMedicine u)
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

        }
    }
}