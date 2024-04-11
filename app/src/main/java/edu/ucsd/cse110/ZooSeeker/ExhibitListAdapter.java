package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private List<ExhibitListItem> selectedExhibits = Collections.emptyList();
    private Consumer<ExhibitListItem> onCheckBoxClicked;
    private Consumer<ExhibitListItem> onDeleteBtnClicked;
    private BiConsumer<ExhibitListItem, String> onTextBoxChanged;

    public void setExhibitListItems(List<ExhibitListItem> newExhibitItems) {
        this.selectedExhibits.clear();
        this.selectedExhibits = newExhibitItems;
        notifyDataSetChanged();
    }

    public void setOnCheckBoxClickedHandler(Consumer<ExhibitListItem> onCheckBoxClicked) {
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    public void setOnDeleteBtnClickedHandler(Consumer<ExhibitListItem> onDeleteBtnClicked) {
        this.onDeleteBtnClicked = onDeleteBtnClicked;
    }

    public void setOnTextChangedHandler(BiConsumer<ExhibitListItem, String> onTextBoxChanged) {
        this.onTextBoxChanged = onTextBoxChanged;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibitListItem(selectedExhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return selectedExhibits.size();
    }

    @Override
    public long getItemId(int position) {
        return selectedExhibits.get(position).id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView, deleteBtn;
        private final CheckBox checkBox;
        private ExhibitListItem exhibitListItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_item_info);
            this.checkBox = itemView.findViewById(R.id.selected);
            this.deleteBtn = itemView.findViewById(R.id.delete_btn);

            this.checkBox.setOnClickListener(view -> {
                if (onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(exhibitListItem);
            });

            this.deleteBtn.setOnClickListener(view -> {
                if (onDeleteBtnClicked == null) return;
                onDeleteBtnClicked.accept(exhibitListItem);

                //Get data from Dao and update total selected count
                exhibitListItems = exhibitListItemDao.getAll();
                exhibitCountTextView.setText(SELECTED_TOTAL + " " + exhibitListItems.size());
            });
        }

        public void setExhibitListItem(ExhibitListItem exhibitListItem) {
            this.exhibitListItem = exhibitListItem;
            this.textView.setText(exhibitListItem.exhibitName);
            this.checkBox.setChecked(exhibitListItem.selected);
        }
    }
}
