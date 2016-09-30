package com.therishka.androidlab_2;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.therishka.androidlab_2.DialogsActivity;
import com.therishka.androidlab_2.R;
import com.therishka.androidlab_2.models.VkAttachments;
import com.therishka.androidlab_2.models.VkLikes;
import com.therishka.androidlab_2.models.VkNewsItem;
import com.therishka.androidlab_2.models.VkPhoto;
import com.therishka.androidlab_2.network.RxVk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    ProgressBar mProgress;
    RecyclerView mRecyclerList;
    RecyclerNewsAdapter mNewsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mProgress = (ProgressBar) findViewById(R.id.loading);
        mRecyclerList = (RecyclerView) findViewById(R.id.news_list);
        mNewsAdapter = new RecyclerNewsAdapter(this);
        mRecyclerList.setAdapter(mNewsAdapter);
        mRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        getNewsAndShowThem();
    }

    private void getNewsAndShowThem() {
        showLoading();
        RxVk api = new RxVk();
        api.getNews(new RxVk.RxVkListener<LinkedList<VkNewsItem>>() {
            @Override
            public void requestFinished(LinkedList<VkNewsItem> requestResult) {
                mNewsAdapter.setNewsList(requestResult);
                showNews();
            }
        });
    }

    private void showLoading() {
        mRecyclerList.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void showNews() {
        mRecyclerList.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
    }

    private class RecyclerNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<VkNewsItem> mNewsList;
        private Context mContext;

        public RecyclerNewsAdapter(@NonNull Context context) {
            mContext = context;
        }

        public void setNewsList(@Nullable List<VkNewsItem> NewsList) {
            mNewsList = NewsList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NewsViewHolder) {
                VkNewsItem newsItem = mNewsList.get(position);
                ((NewsViewHolder) holder).bind(newsItem);
                Glide.with(mContext).load(newsItem.getPublisher().getPhoto_100())
                        .fitCenter()
                        .into(((NewsViewHolder) holder).avatar);
        List<VkAttachments> vkAttachmentsList = newsItem.getAttachments();
                if (vkAttachmentsList!=null) {
                    String image = getMaxPhoto(vkAttachmentsList.get(0).getPhoto());
                        try {
                            Glide.with(mContext).load(image).fitCenter().into(((NewsViewHolder) holder).news_image);
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            }


        @Override
        public int getItemCount() {

            return mNewsList != null ? mNewsList.size() : 0;
        }


    }

    private String getMaxPhoto(VkPhoto vkPhoto) {
        String getPhoto= null;
        if (vkPhoto.getPhoto_2560()!=null)
            getPhoto = vkPhoto.getPhoto_2560();
        if (getPhoto==null)
            getPhoto = vkPhoto.getPhoto_1280();
        if (getPhoto == null)
            getPhoto = vkPhoto.getPhoto_807();
        if (getPhoto==null)
            getPhoto = vkPhoto.getPhoto_604();
        if (getPhoto== null)
            getPhoto = vkPhoto.getPhoto_130();
        if (getPhoto==null)
            getPhoto = vkPhoto.getPhoto_75();
        return getPhoto;
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        ImageView avatar;
        TextView message;
        ImageView news_image;
        ImageView like_sign;
        TextView likes;
        TextView com_name;
        public NewsViewHolder(View itemView) {

            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
           com_name = (TextView) itemView.findViewById(R.id.community_name);
            message = (TextView) itemView.findViewById(R.id.news);
            avatar = (ImageView) itemView.findViewById(R.id.community_photo);
            news_image = (ImageView) itemView.findViewById(R.id.news_image);
            like_sign = (ImageView) itemView.findViewById(R.id.like_sign);
            likes = (TextView) itemView.findViewById(R.id.likes);
        }

        public void bind(VkNewsItem vkNewsItem) {
            date.setText(getDate(vkNewsItem.getDate()));
            message.setText(vkNewsItem.getText());
            VkLikes like = vkNewsItem.getLikes();
            if (like != null) {
                likes.setText("");
                likes.append(Integer.toString(like.getCount()));
            }
            com_name.setText(vkNewsItem.getPublisher().getName());


        }

        }


    private String getDate(long date){
        Date date1 = new Date(date*1000);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm dd:MM:yyyy");
        return dateFormat.format(date1);
    }
}