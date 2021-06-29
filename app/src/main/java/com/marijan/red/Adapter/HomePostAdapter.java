package com.marijan.red.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.marijan.red.HomePostActivity;
import com.marijan.red.Model.HomePost;
import com.marijan.red.R;
import com.marijan.red.VideoPostActivity;

import java.util.List;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.ViewHolder>  {

    private Context mContext;
    private List<HomePost> mPost;

    public HomePostAdapter(Context mContext, List<HomePost> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public HomePostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_post_layout, viewGroup, false);
        return new HomePostAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HomePostAdapter.ViewHolder viewHolder, int i) {
        final HomePost homePost = mPost.get(i);
        try {
            Glide
                    .with(mContext)
                    .asBitmap()
                    .load(homePost.getPostimage())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(viewHolder.homePostItem);
            viewHolder.title.setText(mPost.get(i).getTitle());
            viewHolder.publisher.setText(mPost.get(i).getPublisher());
        }catch (Exception e){
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(homePost.getType().equals("video")){
                Intent intent = new Intent(mContext, VideoPostActivity.class);
                intent.putExtra("videourl", homePost.getPostimage());
                mContext.startActivity(intent);
            }else {
                    Intent intent = new Intent(mContext, HomePostActivity.class);
                    intent.putExtra("homepostuserid", homePost.getPostid());
                    mContext.startActivity(intent);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mPost.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView homePostItem;
        TextView title;
        TextView publisher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homePostItem = itemView.findViewById(R.id.home_post_image);
            title = itemView.findViewById(R.id.home_post_title);
            publisher = itemView.findViewById(R.id.home_post_publisher);
        }
    }

}
