package it.gpgames.consigliaviaggi19.search.filters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.gpgames.consigliaviaggi19.R;

public class FilterTagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> tagsList;
    private String holdingTag;

    private TagSetter setter;

    private LayoutInflater inflater;

    private SetterCheckBoxListener listener;

    public FilterTagsAdapter(List<String> tags, Context context, TagSetter setter) {
        inflater= LayoutInflater.from(context);
        listener=new SetterCheckBoxListener();
        this.tagsList=tags;
        this.setter=setter;
    }


    @NonNull
    @Override
    public FilterTagsAdapter.TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.tag_container,parent, false);
        return new FilterTagsAdapter.TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final FilterTagsAdapter.TagViewHolder holder = (FilterTagsAdapter.TagViewHolder) h;
        holdingTag=tagsList.get(position);
        holder.box.setText(holdingTag);
        holder.box.setOnCheckedChangeListener(listener);
    }

    @Override
    public int getItemCount() {
        return tagsList.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {

        View mView;
        CheckBox box;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            box=itemView.findViewById(R.id.tagCheckBox);
        }
    }

    public interface TagSetter
    {
        void addTag(String tag);
        void removeTag(String tag);
    }

    private class SetterCheckBoxListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
            {
                Log.d("check","Il listener sta per inviare il tag: "+buttonView.getText().toString());
                setter.addTag(buttonView.getText().toString());
            }
            else
            {
                setter.removeTag(buttonView.getText().toString());
            }
        }
    }
}

