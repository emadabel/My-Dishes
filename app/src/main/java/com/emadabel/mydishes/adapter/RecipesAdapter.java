package com.emadabel.mydishes.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.emadabel.mydishes.R;
import com.emadabel.mydishes.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder> {

    private RecipesAdapterOnClickHandler mClickHandler;
    private List<Recipe> mRecipeList;
    private Context mContext;
    private int mLayoutResource;

    public RecipesAdapter(@LayoutRes int resource, Context mContext, RecipesAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
        this.mContext = mContext;
        this.mLayoutResource = resource;
    }

    @NonNull
    @Override
    public RecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(mLayoutResource, parent, false);
        return new RecipesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesViewHolder recipesViewHolder, int position) {
        String posterUrl = mRecipeList.get(position).getImageUrl();
        String title = mRecipeList.get(position).getTitle();
        String publisher = mRecipeList.get(position).getPublisher();
        double rank = mRecipeList.get(position).getSocialRank();

        Glide.with(mContext)
                .load(posterUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(recipesViewHolder.mRecipePosterImageView);

        recipesViewHolder.mRecipeTitleTextView.setText(title);
        recipesViewHolder.mPublisherTextView.setText(publisher);
        recipesViewHolder.mRanking.setRating((float) (rank * 0.05));
    }

    @Override
    public int getItemCount() {
        if (mRecipeList == null) return 0;
        return mRecipeList.size();
    }

    public void setRecipeData(List<Recipe> recipeData) {
        mRecipeList = recipeData;
        notifyDataSetChanged();
    }

    public interface RecipesAdapterOnClickHandler {
        void onClick(String rId, String recipeTitle);
    }

    public class RecipesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_poster_iv)
        ImageView mRecipePosterImageView;
        @BindView(R.id.recipe_title_tv)
        TextView mRecipeTitleTextView;
        @BindView(R.id.publisher_tv)
        TextView mPublisherTextView;
        @BindView(R.id.ratingBar)
        RatingBar mRanking;

        RecipesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String recipeId = mRecipeList.get(adapterPosition).getRecipeId();
            String recipeTitle = mRecipeList.get(adapterPosition).getTitle();
            mClickHandler.onClick(recipeId, recipeTitle);
        }
    }
}
