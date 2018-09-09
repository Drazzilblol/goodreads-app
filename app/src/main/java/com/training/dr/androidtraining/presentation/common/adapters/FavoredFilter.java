package com.training.dr.androidtraining.presentation.common.adapters;

import android.widget.Filter;

import com.training.dr.androidtraining.data.models.Book;
import com.training.dr.androidtraining.data.models.Item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class FavoredFilter extends Filter {

    private final BooksCursorAdapter adapter;
    private final List<Item> originalList;
    private final List<Item> filteredList;

    public FavoredFilter(BooksCursorAdapter adapter, List<Item> originalList) {
        super();
        this.adapter = adapter;
        this.originalList = new LinkedList<>(originalList);
        this.filteredList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();
            for (Item item : originalList) {
                if (((Book) item).getTitle().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }

        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.getItems().clear();
        List<Item> list = (List<Item>) results.values;
        adapter.getItems().addAll(list);
        adapter.notifyDataSetChanged();
    }
}
