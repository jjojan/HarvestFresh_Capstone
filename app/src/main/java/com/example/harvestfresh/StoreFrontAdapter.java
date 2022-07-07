package com.example.harvestfresh;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class StoreFrontAdapter extends RecyclerView.Adapter<StoreFrontAdapter.ViewHolder> {

    private Context context;
    private List<StoreFront> stores;

    public StoreFrontAdapter(Context context, List<StoreFront> stores) {
        this.context = context;
        this.stores = stores;
    }

    @NonNull
    @Override
    public StoreFrontAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreFrontAdapter.ViewHolder holder, int position) {
        StoreFront store = stores.get(position);
        holder.bind(store);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public void clear() {
        stores.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<StoreFront> list) {
        stores.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStoreName;
        private ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivStore);
        }

        public void bind(StoreFront store) {
            tvStoreName.setText(store.getName());
            ParseFile image = store.getImage();

            if (image == null) { return; }
            else if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivImage);

                ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goDetailActivity();
                    }
                });
            }
        }

        private void goDetailActivity() {
            int position = getAdapterPosition();
            StoreFront store = stores.get(position);
            Intent i = new Intent(context, DetailActivity.class);
            i.putExtra(StoreFront.class.getSimpleName(), Parcels.wrap(store));
            context.startActivity(i);
        }
    }
}
