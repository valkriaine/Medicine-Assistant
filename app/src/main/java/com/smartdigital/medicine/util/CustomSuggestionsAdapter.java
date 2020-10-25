package com.smartdigital.medicine.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.smartdigital.medicine.R;
import com.smartdigital.medicine.UserMedicine;
import org.jetbrains.annotations.NotNull;



public class CustomSuggestionsAdapter extends SuggestionsAdapter<UserMedicine, CustomSuggestionsAdapter.SuggestionHolder> {

    public CustomSuggestionsAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public void onBindSuggestionHolder(UserMedicine suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getName());
    }

    @Override
    public int getSingleViewHeight() {
        return 80;
    }

    @NotNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.medicine_suggestion_item, parent, false);
        return new SuggestionHolder(view);
    }


    static class SuggestionHolder extends RecyclerView.ViewHolder{
        protected TextView title;

        public SuggestionHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.suggestion_title);
        }
    }

    @Override
    public void addSuggestion(UserMedicine r) {
        super.addSuggestion(r);
        notifyDataSetChanged();
    }
}