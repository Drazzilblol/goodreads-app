package com.training.dr.androidtraining.presentation.common.adapters;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.models.Book;
import com.training.dr.androidtraining.data.models.Item;
import com.training.dr.androidtraining.presentation.common.events.OnBookItemClickListener;
import com.training.dr.androidtraining.presentation.common.events.OnDataChangedListener;
import com.training.dr.androidtraining.presentation.common.holders.ItemViewHolder;
import com.training.dr.androidtraining.presentation.common.holders.ProgressViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class BooksCursorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_PROG = 1;
    private List<Item> items;
    private OnBookItemClickListener itemClickListener;
    private OnDataChangedListener dataChangedListener;

    private Cursor cursor;
    private ContentObserver contentObserver;
    private FavoredFilter filter;

    private volatile boolean loading;
    private String filterString;

    @IntDef({TYPE_ITEM, TYPE_PROG})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ViewTypes {
    }

    public BooksCursorAdapter(List<Item> items,
                              OnBookItemClickListener itemClickListener,
                              Cursor cursor,
                              OnDataChangedListener dataChangedListener) {

        this.cursor = cursor;
        this.itemClickListener = itemClickListener;
        this.items = items;
        this.dataChangedListener = dataChangedListener;

        contentObserver = new NotifiedContentObserver(new Handler());

        if (this.cursor != null) {
            this.cursor.registerContentObserver(contentObserver);
        }
        getBooksFromDB();
    }

    private void getBooksFromDB() {
        if (cursor.getCount() > 0) {
            while (!cursor.isLast()) {
                cursor.moveToNext();
                Book book = new Book();
                book.setId(cursor.getInt(0));
                book.setReviewId(cursor.getInt(2));
                book.setTitle(cursor.getString(3));
                book.setImageUrl(cursor.getString(4));
                book.setAuthor(cursor.getString(5));
                book.setRating(cursor.getFloat(7));
                book.setMyRating(cursor.getInt(8));
                book.setAuthor(cursor.getString(5));
                book.setDescription(cursor.getString(9));
                items.add(book);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @BooksCursorAdapter.ViewTypes int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_book, parent, false);
            return new ItemViewHolder(v, itemClickListener);
        }
        if (viewType == TYPE_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Book book = (Book) items.get(position);
            ((ItemViewHolder) holder).onBindData(book, filterString);
        }
        if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).onBindData(isLoading(), items.size());
        }
    }

    public void filter(String filter) {
        this.filterString = filter;
        getFilter().filter(filter);
    }

    private boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return false;
        }
        final Cursor oldCursor = cursor;
        if (oldCursor != null && contentObserver != null) {
            oldCursor.unregisterContentObserver(contentObserver);
        }
        cursor = newCursor;
        if (cursor != null) {
            if (contentObserver != null) {
                cursor.registerContentObserver(contentObserver);
            }
            items.clear();
            getBooksFromDB();
            notifyDataSetChanged();
            setLoading(false);
        }
        return true;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).cancelLoad();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.size() == position) {
            return TYPE_PROG;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FavoredFilter(this, items);
        }
        return filter;
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    private class NotifiedContentObserver extends ContentObserver {

        private NotifiedContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            dataChangedListener.onDataChanged();
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    }

}
