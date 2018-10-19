package com.example.hp.countries;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.ArrayList;
import java.util.List;

public class countriesRecyclerViewAdapter extends RecyclerView.Adapter<countriesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Country> Countries = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;

    public countriesRecyclerViewAdapter(ArrayList<Country> countries,Context context, Activity activity) {
        this.Countries = countries;
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.name.setText(Countries.get(position).getName());
        holder.capital.setText(Countries.get(position).getCapital());
        GlideToVectorYou.justLoadImage(mActivity, Uri.parse(Countries.get(position).getFlag()), holder.flag);
    }

    @Override
    public int getItemCount() {
        return Countries.size();
    }

    public void clear() {
        Countries.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Country> data) {
        Countries.addAll(data);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,capital;
        ImageView flag;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            capital = (TextView) itemView.findViewById(R.id.capital);
            flag = (ImageView)itemView.findViewById(R.id.flag);
        }
    }
}
